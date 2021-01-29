
package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.CA;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.CipherProvider;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Common;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Group;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.MarkPosition;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Pair;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SealImage;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SealInfo;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SignInfo;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SimpleColor;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SimpleTextInfo;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Template;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.TextInfo;

public final class Packet implements Closeable {
	private static final String PREFIX = Packet.class.getSimpleName() + "-SF-";
	private static final String META_NAME = PREFIX + "Meta.xml";
	private Document main;
	private Document metadata;
	private Element root;
	private Map<String, PackEntry> entites;
	private int index;
	private boolean packed;
	private boolean closed;

	public Packet(Const.PackType type, Const.Target target) {
		this(type.value(), target);
	}

	public Packet(String custom, Const.Target target) {
		if (target == null) {
			target = Const.Target.OFD;
		}
		this.entites = new LinkedHashMap<String, PackEntry>();
		this.main = DocumentHelper.createDocument();
		this.main.setXMLEncoding("UTF-8");
		this.root = DocumentHelper.createElement((String) "FileRoot");
		this.main.setRootElement(this.root);
		this.root.addAttribute("Target", target.value());
		this.root.addAttribute("Type", custom);
	}

	public Packet(Const.Target target, boolean decryptSeal, boolean decryptContent, boolean decryptImage) {
		if (target == null) {
			target = Const.Target.OFD;
		}
		this.entites = new LinkedHashMap<String, PackEntry>();
		this.main = DocumentHelper.createDocument();
		this.main.setXMLEncoding("UTF-8");
		this.root = DocumentHelper.createElement((String) "FileRoot");
		this.main.setRootElement(this.root);
		this.root.addAttribute("Target", target.value());
		this.root.addAttribute("Type", "Native.parser");
		this.root.addAttribute("DecryptSeal", decryptSeal + "");
		this.root.addAttribute("DecryptContent", decryptContent + "");
		this.root.addAttribute("DecryptImage", decryptImage + "");
	}

	public static Packet common() {
		return new Packet(Const.PackType.COMMON, Const.Target.OFD);
	}

	public static Packet html() {
		return new Packet(Const.PackType.COMMON, Const.Target.OFD);
	}

	private Element findOrNew(Element parent, String name) {
		if (parent == null || name == null) {
			return null;
		}
		Element e = parent.element(name);
		if (e == null) {
			e = parent.addElement(name);
		}
		return e;
	}

	private void writeXML(Document doc, OutputStream os) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		format.setIndent(true);
		XMLWriter xw = new XMLWriter(os, format);
		xw.write(doc);
		xw.flush();
	}

	public PackEntry.Name presetEntry(PackEntry in) {
		return this.presetEntry(in, null);
	}

	public PackEntry.Name presetEntry(PackEntry in, String suffix) {
		return this.presetEntry(in, null, suffix);
	}

	public PackEntry.Name presetEntry(PackEntry in, String name, String suffix) {
		if (in instanceof PackEntry.Name) {
			return (PackEntry.Name) in;
		}
		String string = name = name == null ? Utils.UUID() : name;
		if (suffix != null) {
			name = suffix.startsWith(".") ? name + suffix : name + "." + suffix;
			this.entites.put(name, in);
			return new PackEntry.Name(this, name);
		}
		this.entites.put(name, in);
		return new PackEntry.Name(this, name);
	}

	PackEntry findEntry(String name) {
		return this.entites.get(name);
	}

	public Packet fileHandler(String name, String fhClass, Pair<String, String>... parameters) {
		Element fh = this.findOrNew(this.root, "FileHandler");
		fh.addAttribute("Name", name);
		fh.addAttribute("ClassName", fhClass);
		if (parameters != null) {
			for (Pair<String, String> pair : parameters) {
				Element param = fh.addElement("Param");
				param.addAttribute("Name", pair.key());
				param.setText(pair.value());
			}
		}
		return this;
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	public void pack(OutputStream out) throws IOException {
		if (this.packed) {
			throw new IOException("Already packed");
		}
		this.packed = true;
		if (this.index == 0) {
			throw new IOException("Need at least one file or data!");
		}
		long time = 1531549140000L;
		ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(out);
		try {
			for (Map.Entry<String, PackEntry> en : this.entites.entrySet()) {
				PackEntry e = en.getValue();
				if (e == null || e instanceof PackEntry.Name)
					continue;
				ZipArchiveEntry zae = new ZipArchiveEntry(en.getKey());
				zae.setTime(time);
				zaos.putArchiveEntry((ArchiveEntry) zae);
				IOUtils.copy((InputStream) new AutoCloseInputStream(e.open()), (OutputStream) zaos);
				zaos.closeArchiveEntry();
			}
			if (this.entites.containsKey(META_NAME)) {
				this.root.addElement("Metadata").setText(META_NAME);
			} else if (this.metadata != null) {
				String metaName = "_Metadata.xml";
				if (this.entites.containsKey(metaName)) {
					metaName = "_" + metaName;
				}
				ZipArchiveEntry zae = new ZipArchiveEntry(metaName);
				zae.setTime(time);
				zaos.putArchiveEntry((ArchiveEntry) zae);
				this.root.addElement("Metadata").setText(metaName);
				this.writeXML(this.metadata, (OutputStream) zaos);
				zaos.closeArchiveEntry();
			}
			ZipArchiveEntry zae = new ZipArchiveEntry("Main.xml");
			zae.setTime(time);
			zaos.putArchiveEntry((ArchiveEntry) zae);
			this.writeXML(this.main, (OutputStream) zaos);
			zaos.closeArchiveEntry();
			zaos.finish();
		} finally {
			IOUtils.closeQuietly((OutputStream) zaos);
			IOUtils.closeQuietly((Closeable) this);
		}
	}

	public String data(Template tpl) throws PackException {
		Map<String, PackEntry> sheet;
		PackEntry in;
		Element component = this.findOrNew(this.root, "DocBody").addElement("Component");
		String id = String.valueOf(this.index++);
		component.addAttribute("ID", id);
		Element data = component.addElement("Data");
		data.addAttribute("Title", tpl.getTitle());
		String handler = tpl.getHandler();
		if (handler != null) {
			data.addAttribute("DataHandler", tpl.getHandler());
		} else {
			in = tpl.getTemplate();
			if (in != null) {
				data.addElement("Template").setText(this.presetEntry(in).value());
			}
		}
		in = tpl.getData();
		if (in != null) {
			data.addElement("FileLoc").setText(this.presetEntry(in).value());
		}
		if ((sheet = tpl.getSheet()) != null) {
			for (Map.Entry<String, PackEntry> pair : sheet.entrySet()) {
				this.entites.put(pair.getKey(), pair.getValue());
			}
		}
		return id;
	}

	public String data(PackEntry data, String tplName, String... relationNames) {
		Element component = this.findOrNew(this.root, "DocBody").addElement("Component");
		String id = String.valueOf(this.index++);
		component.addAttribute("ID", id);
		Element ed = component.addElement("Data");
		StringBuilder name = new StringBuilder(tplName);
		if (relationNames != null) {
			for (String rel : relationNames) {
				name.append(";").append(rel);
			}
		}
		ed.addElement("Template").setText("Template://" + name);
		ed.addElement("FileLoc").setText(this.presetEntry(data).value());
		return id;
	}

	public String file(Common common) throws PackException {
		Element component = this.findOrNew(this.root, "DocBody").addElement("Component");
		String id = String.valueOf(this.index++);
		component.addAttribute("ID", id);
		Element file = component.addElement("File");
		if (common.getTitle() != null) {
			file.addAttribute("Title", common.getTitle());
		}
		if (common.getFormat() != null) {
			file.addAttribute("Format", common.getFormat());
		}
		if (common.getDPI() > 0) {
			file.addAttribute("ImageDPI", String.valueOf(common.getDPI()));
		}
		if (common.getWidth() > 0.0f) {
			file.addAttribute("PageWidth", String.valueOf(common.getWidth()));
		}
		if (common.getHeight() > 0.0f) {
			file.addAttribute("PageHeight", String.valueOf(common.getHeight()));
		}
		if (common.getArgument() != null) {
			String args = common.getArgument().value();
			file.addAttribute("Argument", args);
		}
		URI uri = common.getURI();
		Element fileLoc = file.addElement("FileLoc");
		if (uri != null) {
			fileLoc.setText(uri.toString());
		} else {
			PackEntry in = common.getData();
			if (in != null) {
				fileLoc.setText(this.presetEntry(in, PREFIX + id, common.getFormat()).value());
			}
		}
		return id;
	}

	public Outline outline(String title, String refID) {
		Element outline = this.findOrNew(this.root, "Outline");
		return new Outline(outline).append(title, refID);
	}

	public Packet metadata(InputStream in) {
		this.entites.put(META_NAME, PackEntry.wrap(in));
		return this;
	}

	public Packet metadata(Const.Meta key, Object value) {
		if (key == null || value == null) {
			return this;
		}
		if (this.metadata == null) {
			this.metadata = DocumentHelper.createDocument((Element) DocumentHelper.createElement((String) "MetaRoot"));
		}
		Element md = this.metadata.getRootElement();
		switch (key) {
		case KEYWORDS: {
			String[] ks = ((String) value).split(",");
			md.addElement(key.xmlName());
			for (String v : ks) {
				this.metadata(Const.Meta.KEYWORD, v.trim());
			}
			break;
		}
		case KEYWORD: {
			Element kse = this.findOrNew(md, Const.Meta.KEYWORDS.xmlName());
			kse.addElement(Const.Meta.KEYWORD.xmlName()).setText((String) value);
			break;
		}
		case CUSTOM_DATAS: {
			String[] cds = ((String) value).split(",");
			md.addElement(Const.Meta.CUSTOM_DATAS.xmlName());
			for (String v : cds) {
				this.metadata(Const.Meta.CUSTOM_DATA, v);
			}
			break;
		}
		case CUSTOM_DATA: {
			String[] kv = value instanceof String[] ? (String[]) value : ((String) value).split("=");
			if (kv.length != 2)
				break;
			Element cde = this.findOrNew(md, Const.Meta.CUSTOM_DATAS.xmlName());
			cde.addElement(Const.Meta.CUSTOM_DATA.xmlName()).addAttribute("Name", kv[0]).setText(kv[1]);
			break;
		}
		case MOD_DATE: {
			this.findOrNew(md, key.xmlName()).setText(new SimpleDateFormat("yyyy-MM-dd").format(Utils.toDate(value)));
			break;
		}
		case EXTEND_FILE: {
			if (value instanceof PackEntry) {
				this.findOrNew(md, key.xmlName()).setText(this.presetEntry((PackEntry) value).value());
				break;
			}
			if (value instanceof InputStream) {
				this.metadata(key, PackEntry.wrap((InputStream) value));
				break;
			}
			if (!(value instanceof File))
				break;
			this.metadata(key, PackEntry.wrap((File) value));
			break;
		}
		default: {
			this.findOrNew(md, key.xmlName()).setText((String) value);
		}
		}
		return this;
	}

	public Packet permission(Const.Perm name, Object value) throws PackException {
		Element ep = this.findOrNew(this.root, "Permissions");
		switch (name) {
		case PRINT_COPIES: {
			this.findOrNew(ep, Const.Perm.PRINT.xmlName()).addAttribute("Copies",
					String.valueOf(Utils.toInteger(value)));
			break;
		}
		case COPY:
		case SAVE_AS:
		case EXPORT: {
			this.findOrNew(ep, "Export").addAttribute(name.xmlName(), String.valueOf(Utils.toBoolean(value)));
			break;
		}
		case VALID_START:
		case VALID_END: {
			this.findOrNew(ep, "ValidPeriod").addAttribute(name.xmlName(), String.valueOf(Utils.toDate(value)));
			break;
		}
		case PRINT:
		case ANNOT:
		case ASSEM:
		case SIGNATURE:
		case WATERMARK: {
			this.findOrNew(ep, name.xmlName()).addAttribute("Value", String.valueOf(Utils.toBoolean(value)));
		}
		}
		return this;
	}

	public Packet view(Const.View name, Object value) throws PackException {
		if (name == null || value == null) {
			return this;
		}
		Element vp = this.findOrNew(this.root, "VPreferences");
		this.findOrNew(vp, name.xmlName()).setText(value.toString());
		return this;
	}

	public Packet attach(String title, String type, InputStream in) throws PackException {
		return this.attach(title, type, PackEntry.wrap(in));
	}

	public Packet attach(String title, String type, InputStream in, boolean visible) throws PackException {
		return this.attach(title, type, PackEntry.wrap(in), visible);
	}

	public Packet attach(String title, String type, PackEntry in) throws PackException {
		this.attach(title, type, in, true);
		return this;
	}

	public Packet attach(String title, String type, PackEntry in, boolean visible) throws PackException {
		Element as = this.findOrNew(this.root, "Attachs");
		Element attach = as.addElement("Attach");
		attach.addAttribute("AttachTitle", title);
		attach.addAttribute("LogicType", type);
		if (!visible) {
			attach.addAttribute("Visible", visible + "");
		}
		attach.addElement("FileLoc").setText(this.presetEntry(in).value());
		return this;
	}

	public Packet customTeg(String tagroot, String prefix, String href) {
		Element ct = this.findOrNew(this.root, "CustomTag");
		if (tagroot != null) {
			ct.addElement("TagRoot").setText(tagroot);
		}
		if (prefix != null) {
			Element ns = ct.addElement("NameSpace");
			ns.addAttribute("Prefix", prefix);
			if (href != null) {
				ns.addAttribute("Href", href);
			}
		}
		return this;
	}

	public Packet imageMark(InputStream image, String imageType, MarkPosition pos, boolean printable, boolean visible,
                            String name) {
		this.addImageMark(PackEntry.wrap(image), imageType, pos, Const.XAlign.Absolute, Const.YAlign.Absolute,
				printable, visible, name);
		return this;
	}

	public Packet addImageMark(PackEntry image, String imageType, MarkPosition pos, Const.XAlign xAlign,
			Const.YAlign yAlign, boolean printable, boolean visible, String flag) {
		this.addImageMark(image, null, imageType, pos, xAlign, yAlign, printable, visible, flag, 255, pos.isPattern());
		return this;
	}

	public Packet addImageMark(InputStream image, String imageType, MarkPosition position, boolean printable,
			boolean visible, String flag, int alpha) {
		this.addImageMark(PackEntry.wrap(image), null, imageType, position, Const.XAlign.Absolute,
				Const.YAlign.Absolute, printable, visible, flag, alpha, true);
		return this;
	}

	public Packet addImageMark(URI uri, String imageType, MarkPosition position, boolean printable, boolean visible,
			String flag, int alpha) {
		this.addImageMark(null, uri, imageType, position, Const.XAlign.Absolute, Const.YAlign.Absolute, printable,
				visible, flag, alpha, true);
		return this;
	}

	public Packet addImageMark(URI uri, String imageType, MarkPosition pos, boolean printable, boolean visible,
			String flag, int alpha, boolean isTile) {
		this.addImageMark(null, uri, imageType, pos, Const.XAlign.Absolute, Const.YAlign.Absolute, printable, visible,
				flag, alpha, isTile);
		return this;
	}

	public Packet addImageMark(InputStream image, String imageType, MarkPosition pos, boolean printable,
			boolean visible, String flag, int alpha, boolean isTile) {
		this.addImageMark(PackEntry.wrap(image), null, imageType, pos, Const.XAlign.Absolute, Const.YAlign.Absolute,
				printable, visible, flag, alpha, isTile);
		return this;
	}

	private Element tranferImageMark(PackEntry image, URI uri, String imageType, MarkPosition pos, Const.XAlign xAlign,
			Const.YAlign yAlign, int alpha, boolean isTile) {
		Element mark = DocumentHelper.createElement((String) "ImageMark");
		if (uri != null) {
			mark.setText(uri.toString());
		} else {
			mark.setText(this.presetEntry(image, imageType).value());
		}
		if (pos.getX() != -3.4028235E38f && pos.getY() != -3.4028235E38f) {
			mark.addAttribute("PosX", String.valueOf(pos.getX()));
			mark.addAttribute("PosY", String.valueOf(pos.getY()));
		}
		if (xAlign == null) {
			mark.addAttribute("XPosType", Const.XAlign.Absolute.toString());
		} else {
			mark.addAttribute("XPosType", xAlign.toString());
		}
		if (yAlign == null) {
			mark.addAttribute("YPosType", Const.YAlign.Absolute.toString());
		} else {
			mark.addAttribute("YPosType", yAlign.toString());
		}
		if (pos.getWidth() != 0.0f) {
			mark.addAttribute("Width", String.valueOf(pos.getWidth()));
		}
		if (pos.getHeight() != 0.0f) {
			mark.addAttribute("Height", String.valueOf(pos.getHeight()));
		}
		String pages = null;
		if (pos.getIndex() != null || pos.getIndex() != MarkPosition.INDEX_ALL) {
			pages = Utils.toString(pos.getIndex());
		}
		if (pos.getPages() != null && pos.getPages().length != 0) {
			String string = pages = pages == null ? Utils.toString(pos.getPages())
					: pages + "," + Utils.toString(pos.getPages());
		}
		if (pages != null) {
			mark.addAttribute("Pages", pages);
		}
		if (alpha >= 0 && alpha <= 255) {
			mark.addAttribute("Alpha", alpha + "");
		}
		if (isTile || pos.isPattern()) {
			mark.addAttribute("Tiled", isTile + "");
		}
		return mark;
	}

	private void addImageMark(PackEntry image, URI uri, String imageType, MarkPosition pos, Const.XAlign xAlign,
			Const.YAlign yAlign, boolean printable, boolean visible, String flag, int alpha, boolean isTile) {
		Element watermark = this.addMaker();
		watermark.addAttribute("Printable", String.valueOf(printable));
		watermark.addAttribute("Visible", String.valueOf(visible));
		if (flag != null) {
			watermark.addAttribute("Name", flag);
		}
		watermark.add(this.tranferImageMark(image, uri, imageType, pos, xAlign, yAlign, alpha, isTile));
	}

	public Packet imageMark(InputStream image, String imageType, MarkPosition pos) {
		return this.imageMark(image, imageType, pos, true, true);
	}

	public Packet imageMark(InputStream image, String imageType, MarkPosition pos, int a, boolean tile) {
		return this.addImageMark(image, imageType, pos, true, true, "tile", a, tile);
	}

	public Packet imageMark(InputStream image, String imageType, Const.XAlign xa, Const.YAlign ya, MarkPosition pos) {
		return this.imageMark(image, imageType, xa, ya, pos, true, true);
	}

	public Packet imageMark(InputStream image, String imageType, MarkPosition pos, boolean printable, boolean visible) {
		this.addImageMark(PackEntry.wrap(image), imageType, pos, Const.XAlign.Absolute, Const.YAlign.Absolute,
				printable, visible, null);
		return this;
	}

	public Packet imageMark(InputStream image, String imageType, Const.XAlign xa, Const.YAlign ya, MarkPosition pos,
			boolean printable, boolean visible) {
		this.addImageMark(PackEntry.wrap(image), imageType, pos, xa, ya, printable, visible, null);
		return this;
	}

	private Element addMaker() {
		Element ws = this.findOrNew(this.root, "Watermarks");
		return ws.addElement("Watermark");
	}

	public Packet textMark(TextInfo text, MarkPosition pos, boolean printable, boolean visible, String name) {
		this.addTextMark(text, pos, printable, visible, name);
		return this;
	}

	private Element tranferTextMark(TextInfo text, MarkPosition pos) {
		Element mark = DocumentHelper.createElement((String) "TextMark");
		mark.setText(text.getText());
		mark.addAttribute("FontName", text.getFontName());
		if (text.getFontSize() > 0.0f) {
			mark.addAttribute("FontSize", String.valueOf(text.getFontSize()));
		}
		mark.addAttribute("Color", text.getColor());
		if (text.getRotate() % 45 == 0) {
			mark.addAttribute("Rotate", String.valueOf(text.getRotate()));
		}
		if (text.getXAlign() == null) {
			mark.addAttribute("XPosType", Const.XAlign.Absolute.toString());
		} else {
			mark.addAttribute("XPosType", text.getXAlign().toString());
		}
		if (text.getYAlign() == null) {
			mark.addAttribute("YPosType", Const.YAlign.Absolute.toString());
		} else {
			mark.addAttribute("YPosType", text.getYAlign().toString());
		}
		mark.addAttribute("X", String.valueOf(pos.getX()));
		mark.addAttribute("Y", String.valueOf(pos.getY()));
		mark.addAttribute("Width", String.valueOf(pos.getWidth()));
		mark.addAttribute("Height", String.valueOf(pos.getHeight()));
		String pages = null;
		if (pos.getIndex() != null || pos.getIndex() != MarkPosition.INDEX_ALL) {
			pages = Utils.toString(pos.getIndex());
		}
		if (pos.getPages() != null) {
			String string = pages = pages == null ? Utils.toString(pos.getPages())
					: pages + "," + Utils.toString(pos.getPages());
		}
		if (pages != null) {
			mark.addAttribute("Pages", pages);
		}
		return mark;
	}

	private void addTextMark(TextInfo text, MarkPosition pos, boolean printable, boolean visible, String flag) {
		Element watermark = this.addMaker();
		watermark.addAttribute("Printable", String.valueOf(printable));
		watermark.addAttribute("Visible", String.valueOf(visible));
		if (flag != null) {
			watermark.addAttribute("Name", flag);
		}
		watermark.add(this.tranferTextMark(text, pos));
	}

	public Packet textMark(TextInfo text, MarkPosition pos) {
		return this.textMark(text, pos, true, true);
	}

	public Packet textMark(TextInfo text, MarkPosition pos, boolean printable, boolean visible) {
		this.addTextMark(text, pos, printable, visible, null);
		return this;
	}

	public Packet signature(CA ca) throws PackException {
		this.newProvider("Signature").add(this.tranferCA(ca));
		return this;
	}

	public Packet signature(String provider, boolean lockSign) throws PackException {
		Element external = this.newProvider("Signature").addAttribute("LockSign", lockSign + "").addElement("External");
		external.addAttribute("Name", provider);
		return this;
	}

	public Packet signature(String provider, boolean lockSign, SignInfo signInfo) throws PackException {
		Element signature = this.newSignature("Signature").addAttribute("LockSign", lockSign + "");
		signature.addElement("Provider").addElement("External").addAttribute("Name", provider);
		if (signInfo != null) {
			Element watermark = signature.addElement("SignInfo").addAttribute("CertID", signInfo.getCertID())
					.addAttribute("Password", signInfo.getPassword()).addElement("Watermark");
			watermark.addAttribute("Printable", String.valueOf(signInfo.isPrintable()));
			watermark.addAttribute("Visible", String.valueOf(signInfo.isVisible()));
			if (signInfo.getTextInfo() != null) {
				watermark.add(this.tranferTextMark(signInfo.getTextInfo(), signInfo.getPosition()));
			} else if (signInfo.getImage() != null) {
				watermark.add(this.tranferImageMark(signInfo.getImage(), null, signInfo.getType(),
						signInfo.getPosition(), null, null, -1, false));
			}
		}
		return this;
	}

	public Packet signature(String provider, String oesClass) throws PackException {
		Element external = this.newProvider("Signature").addElement("External");
		external.addAttribute("Name", provider);
		external.addAttribute("OESClassName", oesClass);
		return this;
	}

	private Element newProvider(String parentName) {
		Element es = this.findOrNew(this.root, "Security");
		Element ep = es.addElement(parentName);
		return ep.addElement("Provider");
	}

	private Element newSignature(String parentName) {
		Element es = this.findOrNew(this.root, "Security");
		Element ep = es.addElement(parentName);
		return ep;
	}

	private Element tranferCA(CA ca) {
		PackEntry cer;
		Element buildin = DocumentHelper.createElement((String) "Buildin");
		buildin.addAttribute("DigestMethod", ca.getDigestMethod());
		buildin.addAttribute("SignaturMethod", ca.getSignMethod());
		PackEntry pfx = ca.getPfx();
		if (pfx != null) {
			Element loc = buildin.addElement("P12FileLoc");
			loc.setText(this.presetEntry(pfx).value());
			loc.addAttribute("Password", ca.getPfxPassword());
		}
		if ((cer = ca.getCertificate()) != null) {
			buildin.addElement("CerFileLoc").setText(this.presetEntry(cer).value());
		}
		return buildin;
	}

	public Packet seal(CA ca, SealImage image, SealInfo info) throws PackException {
		Element provider = this.newProvider("EleSeal");
		Element buildin = this.tranferCA(ca);
		provider.add(buildin);
		Element path = buildin.addElement("IconPath");
		path.addAttribute("SealID", image.getSealID());
		path.addAttribute("Width", String.valueOf(image.getWidth()));
		path.addAttribute("Height", String.valueOf(image.getHeight()));
		path.addAttribute("Name", "AutoSealName");
		PackEntry img = image.getData();
		if (img != null) {
			path.setText(this.presetEntry(img).value());
		}
		this.transferSealInfo(info, provider.getParent());
		return this;
	}

	public Packet seal(CA ca, InputStream esl, SealInfo info) throws PackException {
		return this.seal(ca, PackEntry.wrap(esl), info);
	}

	public Packet seal(CA ca, PackEntry esl, SealInfo info) throws PackException {
		Element provider = this.newProvider("EleSeal");
		Element buildin = this.tranferCA(ca);
		provider.add(buildin);
		if (esl != null) {
			buildin.addElement("StandardESL").setText(this.presetEntry(esl).value());
		}
		this.transferSealInfo(info, provider.getParent());
		return this;
	}

	public Packet seal(String provider, String oesClass, SealInfo info) throws PackException {
		Element ep = this.newProvider("EleSeal");
		Element external = ep.addElement("External");
		external.addAttribute("Name", provider);
		external.addAttribute("OESClassName", oesClass);
		this.transferSealInfo(info, ep.getParent());
		return this;
	}

	private void transferSealInfo(SealInfo info, Element eSeal) {
		Element e = eSeal.addElement("SealInfo");
		e.addAttribute("SealID", info.getSealID());
		e.addAttribute("PosX", String.valueOf(info.getX()));
		e.addAttribute("PosY", String.valueOf(info.getY()));
		e.addAttribute("SealWidth", String.valueOf(info.getWidth()));
		e.addAttribute("SealHeight", String.valueOf(info.getHeight()));
		e.addAttribute("PageIndex", String.valueOf(info.getPageIndex()));
		if (info.getPassword() != null) {
			e.addAttribute("Password", info.getPassword());
		}
		if (info.getTypeC() != null) {
			e.addAttribute("Type", info.getTypeC());
			if (info.getTypeC().equals(SealInfo.NativeType.All.toString()) && info.getStampAnnot() != null) {
				for (SealInfo.StampAnnot sa : info.getStampAnnot()) {
					Element el = e.addElement("StampAnnot");
					el.addAttribute("Type", sa.getType().toString());
					el.addAttribute("Boundary",
							sa.getX() + " " + sa.getY() + " " + sa.getWidth() + " " + sa.getHeight());
				}
			}
		} else {
			switch (info.getType()) {
			case 0: {
				e.addAttribute("Type", "First");
				break;
			}
			case 0x7FFFFFFF: {
				e.addAttribute("Type", "Last");
				break;
			}
			case -1: {
				e.addAttribute("Type", "All");
				break;
			}
			default: {
				e.addAttribute("Type", String.valueOf(info.getType()));
			}
			}
		}
	}

	public Packet envelope(Const.EnvelopeType type, String password, Map<Const.EnvelopePerm, Boolean> perms,
			Pair<Const.EnvelopeMeta, Object>... meta) {
		return this.envelope(type, password, perms, (String) null, meta);
	}

	public Packet envelope(Const.EnvelopeType type, String value, String provider,
			Pair<Const.EnvelopeMeta, Object>... meta) {
		return this.envelope(type, value, null, provider, meta);
	}

	private Packet envelope(Const.EnvelopeType type, String password, Map<Const.EnvelopePerm, Boolean> perms,
			String provider, Pair<Const.EnvelopeMeta, Object>... meta) {
		Element env = this.findOrNew(this.root, "Envelope");
		this.transferEnvMeta(env, meta);
		this.transferEnvPerm(env, perms);
		if (provider != null) {
			env.addElement("Provider").addAttribute("Name", provider);
		}
		Element pwd = env.addElement("Password");
		pwd.addAttribute("Type", type.toString());
		if (password != null) {
			pwd.addAttribute("Val", password);
		}
		return this;
	}

	private void transferEnvPerm(Element env, Map<Const.EnvelopePerm, Boolean> perms) {
		if (perms == null || perms.isEmpty()) {
			return;
		}
		Element pm = env.addElement("Permission");
		for (Const.EnvelopePerm perm : Const.EnvelopePerm.values()) {
			this.findOrNew(pm, String.valueOf((Object) perm)).setText(String.valueOf(false));
		}
		for (Map.Entry<Const.EnvelopePerm, Boolean> en : perms.entrySet()) {
			Boolean v = en.getValue();
			if (v == null)
				continue;
			this.findOrNew(pm, en.getKey().xmlName()).setText(String.valueOf(v));
		}
	}

	private void transferEnvMeta(Element env, Pair<Const.EnvelopeMeta, Object>... meta) {
		if (meta == null || meta.length == 0) {
			return;
		}
		Element m = env.addElement("Meta");
		for (Pair<Const.EnvelopeMeta, Object> pair : meta) {
			if (pair.key() == null)
				continue;
			m.addAttribute(pair.key().xmlName(), String.valueOf(pair.value()));
		}
	}

	public Packet encrypt(CipherProvider provider, Map<Const.EntryScope, Boolean> scope, SimpleTextInfo textInfo) {
		Element encrypt = this.findOrNew(this.root, "Encrypt");
		this.analysisProvider(provider, encrypt.addElement("Provider"));
		if (scope != null) {
			Element es = encrypt.addElement("EntryScope");
			for (Map.Entry<Const.EntryScope, Boolean> p : scope.entrySet()) {
				es.addAttribute(p.getKey().xmlName(), String.valueOf(p.getValue()));
			}
		}
		if (textInfo != null) {
			Element text = encrypt.addElement("PredefineText");
			text.addAttribute("TextFont", textInfo.getFontName());
			text.addAttribute("TextSize", String.valueOf(textInfo.getFontSize()));
			text.addAttribute("TextColor", textInfo.getColor());
		}
		return this;
	}

	private void analysisProvider(CipherProvider provider, Element ep) {
		Group<String, String, String>[] paras;
		ep.addAttribute("Name", provider.getName());
		String className = provider.getOecClassName();
		if (className != null) {
			ep.addAttribute("OECClassName", className);
		}
		if ((paras = provider.getParameters()) != null) {
			for (Group<String, String, String> para : paras) {
				Element pm = ep.addElement("Parameter");
				pm.setText((String) para.value());
				pm.addAttribute("Name", (String) para.key());
				pm.addAttribute("Value", (String) para.value());
			}
		}
	}

	public Packet decrypt(CipherProvider provider) {
		Element decrypt = this.findOrNew(this.root, "Decrypt");
		this.analysisProvider(provider, decrypt.addElement("Provider"));
		return this;
	}

	public Packet envelope(CipherProvider provider, Map<Const.EnvelopePerm, Boolean> perms,
			Pair<Const.EnvelopeMeta, Object>... meta) throws PackException {
		Element env = this.findOrNew(this.root, "Envelope");
		this.transferEnvMeta(env, meta);
		this.transferEnvPerm(env, perms);
		Element key = env.addElement("Key");
		this.analysisProvider(provider, key.addElement("Provider"));
		return this;
	}

	public Packet attachImage(MarkPosition position, int alpha, boolean isTop, PackEntry image, String type) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element sc = ep.addElement("AttachImage");
		sc.addAttribute("X", String.valueOf(position.getX()));
		sc.addAttribute("Y", String.valueOf(position.getY()));
		sc.addAttribute("W", String.valueOf(position.getWidth()));
		sc.addAttribute("H", String.valueOf(position.getHeight()));
		String pages = null;
		if (position.getIndex() != null || position.getIndex() != MarkPosition.INDEX_ALL) {
			pages = Utils.toString(position.getIndex());
		}
		if (position.getPages() != null && position.getPages().length != 0) {
			pages = pages == null ? Utils.toString(position.getPages())
					: pages + "," + Utils.toString(position.getPages());
		}
		sc.addAttribute("Pages", pages);
		if (alpha >= 0 && alpha <= 255) {
			sc.addAttribute("Alpha", String.valueOf(alpha));
		} else {
			sc.addAttribute("Alpha", String.valueOf(255));
		}
		if (isTop) {
			sc.addAttribute("Layer", "Top");
		} else {
			sc.addAttribute("Layer", "Bottom");
		}
		Element mark = sc.addElement("FileLoc");
		mark.setText(this.presetEntry(image, type).value());
		return this;
	}

	public Packet seceret(int index, float x, float y, float width, float height) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element sc = ep.addElement("Seceret");
		sc.addAttribute("PageIndex", String.valueOf(index));
		sc.addAttribute("PosX", String.valueOf(x));
		sc.addAttribute("PosY", String.valueOf(y));
		sc.addAttribute("Width", String.valueOf(width));
		sc.addAttribute("Height", String.valueOf(height));
		return this;
	}

	public Packet rotate(float rotate, int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element er = ep.addElement("Rotate");
		er.addAttribute("Rotate", String.valueOf(rotate));
		er.addAttribute("Pages", Utils.toString(pages));
		return this;
	}

	public Packet deletePage(int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element dp = this.findOrNew(ep, "DeletePage");
		dp.addAttribute("Pages", Utils.toString(pages));
		return this;
	}

	public Packet swapPage(Pair<Integer, Integer>... swap) {
		Element ep = this.findOrNew(this.root, "Operate");
		if (swap != null) {
			for (Pair<Integer, Integer> pair : swap) {
				Element page = ep.addElement("SwapPage");
				page.addAttribute("PageIndex1", String.valueOf(pair.key()));
				page.addAttribute("PageIndex2", String.valueOf(pair.value()));
			}
		}
		return this;
	}

	public Packet removeSeal(Const.SealMode mode, boolean delete) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element RemoveSeal = this.findOrNew(ep, "RemoveSeal");
		RemoveSeal.addAttribute(mode.xmlName(), String.valueOf(delete));
		return this;
	}

	public Packet insertPage(InputStream source, int srcStart, int srcEnd, int index) {
		return this.insertPage(PackEntry.wrap(source), srcStart, srcEnd, index);
	}

	public Packet insertPage(InputStream source, int srcStart, int srcEnd, int index, String format) {
		return this.insertPage(PackEntry.wrap(source), srcStart, srcEnd, index, format);
	}

	public Packet insertPage(PackEntry source, int srcStart, int srcEnd, int index) {
		return this.insertPage(source, srcStart, srcEnd, index, "ofd");
	}

	public Packet insertPage(PackEntry source, int srcStart, int srcEnd, int index, String format) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element page = this.findOrNew(ep, "InsertPage");
		page.addAttribute("FileLoc", this.presetEntry(source).value());
		if (srcStart > 0) {
			page.addAttribute("From", String.valueOf(srcStart));
		}
		if (srcEnd > 0) {
			page.addAttribute("To", String.valueOf(srcEnd));
		}
		page.addAttribute("Index", String.valueOf(index));
		if (format != null) {
			page.addAttribute("Format", format);
		}
		return this;
	}

	public Packet mergeGU(Const.MergeType type, float widthLimit, float heightLimit, int countLimit) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element gu = this.findOrNew(ep, "MergeGU");
		gu.addAttribute("Type", type.toString());
		if (widthLimit > 0.0f) {
			gu.addAttribute("CountLimit", String.valueOf(widthLimit));
		}
		if (heightLimit > 0.0f) {
			gu.addAttribute("HeightLimit", String.valueOf(heightLimit));
		}
		if (countLimit > 0) {
			gu.addAttribute("WidthLimit", String.valueOf(countLimit));
		}
		return this;
	}

	public Packet copyEntry(Const.EntryType type, String name, InputStream ofd) {
		return this.copyEntry(type, name, PackEntry.wrap(ofd), false);
	}

	public Packet addEntry(Const.EntryType type, String name, InputStream ofd) {
		return this.copyEntry(type, name, PackEntry.wrap(ofd), true);
	}

	public Packet copyEntry(Const.EntryType type, String name, PackEntry ofd, boolean onlyadd) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element coen = ep.addElement("CopyEntry");
		if (type != null) {
			coen.addAttribute("EntryType", type.toString());
		}
		if (name != null) {
			coen.addAttribute("EntryName", name);
		}
		if (onlyadd) {
			coen.addAttribute("OnlyAdd", onlyadd + "");
		}
		coen.addElement("FileLoc").setText(this.presetEntry(ofd).value());
		return this;
	}

	public Packet appendTemplate(int index, InputStream form) {
		return this.appendTemplate(index, PackEntry.wrap(form));
	}

	public Packet appendTemplate(int index, PackEntry form) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element at = ep.addElement("AppendTemplate");
		at.addAttribute("Index", index + "");
		at.addElement("Template").setText(this.presetEntry(form).value());
		return this;
	}

	public Packet removeAttach(Const.RemoveAttachType type, String... attach) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element ra = ep.addElement("RemoveAttach");
		ra.addAttribute("Type", type.toString());
		if (attach != null) {
			for (String string : attach) {
				ra.addElement("Attach").setText(string);
			}
		}
		return this;
	}

	public Packet clip(float x, float y, float w, float h, int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element clip = ep.addElement("Clip");
		clip.addAttribute("X", x + "");
		clip.addAttribute("Y", y + "");
		clip.addAttribute("W", w + "");
		clip.addAttribute("H", h + "");
		clip.addAttribute("Pages", Utils.toString(pages));
		return this;
	}

	public Packet nativeClip(float left, float right, float top, float bottom, SimpleColor color,
			Const.ClipColorType type, int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		ep.addAttribute("UseNative", "true");
		Element clip = ep.addElement("Clip");
		clip.addAttribute("Left", left + "");
		clip.addAttribute("Right", right + "");
		clip.addAttribute("Top", top + "");
		clip.addAttribute("Bottom", bottom + "");
		if (color != null) {
			clip.addAttribute("Color", color.toString());
			clip.addAttribute("Flag", type.value() + "");
		}
		clip.addAttribute("Pages", Utils.toString(pages));
		return this;
	}

	public Packet nativeClip(float left, float right, float top, float bottom, int... pages) {
		return this.nativeClip(left, right, top, bottom, null, null, pages);
	}

	public Packet nativeClip(SimpleColor color, Const.ClipColorType type, int... pages) {
		return this.nativeClip(-1.0f, -1.0f, -1.0f, -1.0f, color, type, pages);
	}

	public Packet addMask(float x, float y, float w, float h, boolean readOnly, int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element mask = ep.addElement("AddMask");
		mask.addAttribute("X", x + "");
		mask.addAttribute("Y", y + "");
		mask.addAttribute("W", w + "");
		mask.addAttribute("H", h + "");
		mask.addAttribute("ReadOnly", readOnly + "");
		mask.addAttribute("Pages", Utils.toString(pages));
		return this;
	}

	public Packet addMask(float x, float y, float w, float h, int... pages) {
		return this.addMask(x, y, w, h, true, pages);
	}

	public Packet RemoveAnnot(String name, int... pages) {
		Element ep = this.findOrNew(this.root, "Operate");
		Element ra = ep.addElement("RemoveAnnot");
		ra.addAttribute("PageIndex", Utils.toString(pages));
		if (name != null) {
			ra.addAttribute("Name", name);
		}
		return this;
	}

	public Packet embedFont(boolean exclude, String... names) {
		block5: {
			Element ef;
			block4: {
				if (!exclude && names == null) {
					return this;
				}
				Element ep = this.findOrNew(this.root, "Operate");
				ef = this.findOrNew(ep, "EmbedFont");
				if (!exclude)
					break block4;
				Element ex = this.findOrNew(ef, "Exclude");
				if (names == null)
					break block5;
				for (String name : names) {
					ex.addElement("Font").setText(name);
				}
				break block5;
			}
			Element in = this.findOrNew(ef, "Include");
			if (names != null) {
				for (String name : names) {
					in.addElement("Font").setText(name);
				}
			}
		}
		return this;
	}

	@Override
	public void close() throws IOException {
		if (this.closed) {
			return;
		}
		this.closed = true;
		for (PackEntry e : this.entites.values()) {
			e.close();
		}
		this.entites.clear();
	}

	public static class RefName {
		private String name;

		RefName(String id) {
			this.name = id;
		}

		public String value() {
			return this.name;
		}

		public String toString() {
			return this.name;
		}
	}

	public static class Outline {
		private Element node;

		private Outline(Element node) {
			this.node = node;
		}

		public Outline append(String title, String refID) {
			Element e = this.node.addElement("Node");
			e.addAttribute("Title", title);
			e.addAttribute("ComponentID", refID);
			return new Outline(e);
		}
	}
}
