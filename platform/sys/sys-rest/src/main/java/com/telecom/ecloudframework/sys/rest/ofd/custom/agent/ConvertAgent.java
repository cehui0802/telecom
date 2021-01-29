package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackException;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io.AutoDeleteFileInputStream;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io.EmptyOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.EntryScope;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.EntryType;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.Meta;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.PackType;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.SealMode;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const.Target;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Packet;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.CA;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.CipherProvider;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Common;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Group;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.MarkPosition;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Pair;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SealInfo;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.SimpleTextInfo;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Template;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.TextInfo;

public abstract class ConvertAgent implements Closeable {

	private static Logger log = LoggerFactory.getLogger(ConvertAgent.class);

	protected ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

	public static OutputStream emptyOutputStream() {
		return new EmptyOutputStream();
	}

	@Override
	public void close() throws IOException {
		this.executor.shutdownNow();
	}

	/**
	 * 添加元数据
	 *
	 * @param packet 打包
	 * @param metas  元数据集合
	 */
	protected void metadata(Packet packet, Map<String, String> metas) {
		if (metas == null || metas.isEmpty()) {
			return;
		}
		for (Map.Entry<String, String> en : metas.entrySet()) {
			Meta name = Meta.of(en.getKey());
			if (name != null) {
				packet.metadata(name, en.getValue());
			} else {
				packet.metadata(Meta.CUSTOM_DATA, en.getValue());
			}
		}
	}

	/**
	 * 添加转换文件
	 *
	 * @param packet
	 * @param file
	 * @param w
	 * @param dpi
	 * @throws PackException
	 * @throws IOException
	 */
	protected void append(Packet packet, File file, float w, int dpi) throws PackException, IOException {
		String name = file.getName();
		String ext = FilenameUtils.getExtension(name);
		if (ext == null || ext.trim().length() == 0) {
			throw new PackException("Not found extendsion from file : " + file.getAbsolutePath());
		}
		FileInputStream in = FileUtils.openInputStream(file);
		if (w > 0) {
			packet.file(new Common(name, ext, w, in));
		} else if (dpi > 0) {
			packet.file(new Common(name, ext, dpi, in));
		} else {
			packet.file(new Common(name, ext, in));
		}
	}

	protected void append(Packet packet, String fileName, InputStream in, float w, int dpi)
			throws PackException, IOException {

		String ext = FilenameUtils.getExtension(fileName);
		if (ext == null || ext.trim().length() == 0) {
			throw new PackException("Not found extendsion from file : " + fileName);
		}
		if (w > 0) {
			packet.file(new Common(fileName, ext, w, in));
		} else if (dpi > 0) {
			packet.file(new Common(fileName, ext, dpi, in));
		} else {
			packet.file(new Common(fileName, ext, in));
		}
	}

	/**
	 * 将单个办公文件（Office文件如doc等、版式文件如pdf、xps、ceb等）转换为OFD文件
	 *
	 * @param srcFile Office文件如doc、版式文件如pdf、xps、ceb等
	 * @param out     转换后OFD文件
	 * @throws ConvertException
	 * @throws IOException
	 * @throws PackException
	 */
	public void officeToOFD(File srcFile, OutputStream out) throws IOException, ConvertException, PackException {
		this.officeToOFD(srcFile, out, null);
	}

	/**
	 * 将单个办公文件（Office文件如doc等、版式文件如pdf、xps、ceb等）转换为OFD文件并附加元数据
	 *
	 * @param srcFile Office文件如doc、版式文件如pdf、xps、ceb等
	 * @param out     转换后OFD文件
	 * @param metas   欲附加到生成后OFD文件中的元数据
	 * @throws ConvertException
	 * @throws IOException
	 * @throws PackException
	 */
	public void officeToOFD(File srcFile, OutputStream out, Map<String, String> metas)
			throws IOException, ConvertException, PackException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			this.append(packet, srcFile, 0, 0);
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	public void addTextMark(InputStream inputStream, String fileName, OutputStream out, TextInfo text, MarkPosition mk,
                            boolean printable, boolean visible) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.textMark(text, mk, printable, visible);
			this.append(packet, fileName, inputStream, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	public void officeToOFD(InputStream inputStream, String fileName, OutputStream out, Map<Const.Perm, Object> ps)
			throws IOException, ConvertException, PackException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (Map.Entry<Const.Perm, Object> entry : ps.entrySet()) {
				packet.permission(entry.getKey(), entry.getValue());
			}
			this.append(packet, fileName, inputStream, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 将多个办公文件（Office文件如doc等、版式文件如pdf、xps、ceb等）转换为OFD文件
	 *
	 * @param srcFiles Office文件如doc、版式文件如pdf、xps、ceb等
	 * @param out      转换后OFD文件存放位置
	 * @throws ConvertException
	 * @throws IOException
	 * @throws PackException
	 */
	public void officesToOFD(List<File> srcFiles, OutputStream out)
			throws PackException, IOException, ConvertException {
		this.officesToOFD(srcFiles, out, null);
	}

	/**
	 * 将多个办公文件（Office文件如doc等、版式文件如pdf、xps、ceb等）转换为OFD文件并附加元数据
	 *
	 * @param srcFiles Office文件如doc、版式文件如pdf、xps、ceb等
	 * @param out      转换后OFD文件存放位置
	 * @param metas    欲附加到生成后OFD文件中的元数据
	 * @throws IOException
	 * @throws PackException
	 * @throws ConvertException
	 */
	public void officesToOFD(List<File> srcFiles, OutputStream out, Map<String, String> metas)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, 0, 0);
			}
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 将多个图片合并转换为OFD文件
	 *
	 * @param srcFiles 图片文件
	 * @param pgWidth  文件宽度
	 * @throws IOException
	 * @throws PackException
	 * @throws ConvertException
	 */
	public void imagesToOFD(List<File> srcFiles, OutputStream out, float pgWidth)
			throws PackException, IOException, ConvertException {
		this.imagesToOFD(srcFiles, out, null, pgWidth);
	}

	/**
	 * 将多个图片合并转换为OFD文件
	 *
	 * @param srcFiles
	 * @param dpi
	 * @throws IOException
	 * @throws PackException
	 * @throws ConvertException
	 */
	public void imagesToOFD(List<File> srcFiles, OutputStream out, int dpi)
			throws PackException, IOException, ConvertException {
		this.imagesToOFD(srcFiles, out, null, dpi);
	}

	/**
	 * 将多个图片合并转换为OFD文件并附加元数据
	 *
	 * @param srcFiles
	 * @param metas
	 * @param pgWidth
	 * @throws IOException
	 * @throws PackException
	 * @throws ConvertException
	 */
	public void imagesToOFD(List<File> srcFiles, OutputStream out, Map<String, String> metas, float pgWidth)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, pgWidth, 0);
			}
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 将多个图片合并转换为OFD文件并附加元数据
	 *
	 * @param srcFiles
	 * @param metas
	 * @param dpi
	 */
	public void imagesToOFD(List<File> srcFiles, OutputStream out, Map<String, String> metas, int dpi)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, 0, dpi);
			}
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 将单个ofd转换为PDF
	 *
	 * @param srcFiles
	 * @param out
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToPDF(File srcFiles, OutputStream out) throws PackException, IOException, ConvertException {
		this.OFDToPDF(srcFiles, out, null);

	}

	/**
	 * 将单个ofd转换为PDF并附件元数据
	 *
	 * @param srcFiles ofd源文件
	 * @param out      输出流
	 * @param metas    元数据集合
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToPDF(File srcFiles, OutputStream out, Map<String, String> metas)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.PDF);
		try {
			this.append(packet, srcFiles, 0, 0);
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}

	}

	/**
	 * 将多个ofd转换为PDF并附件元数据
	 *
	 * @param srcFiles 源文件
	 * @param out      输出流
	 * @param metas    元数据集合
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToPDF(List<File> srcFiles, OutputStream out, Map<String, String> metas)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.PDF);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, 0, 0);
			}
			this.metadata(packet, metas);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}

	}

	/**
	 * 将多个ofd转换为PDF
	 *
	 * @param srcFiles ofd源文件
	 * @param out      输出文件
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToPDF(List<File> srcFiles, OutputStream out) throws PackException, IOException, ConvertException {
		this.OFDToPDF(srcFiles, out, null);

	}

	/**
	 * 模板转换
	 *
	 * @param title
	 * @param data     数据源 data.xml
	 * @param out      输出流
	 * @throws PackException
	 * @throws ConvertException
	 * @throws IOException
	 */
	public void templateToOFD(String title, InputStream template, InputStream data, OutputStream out)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			Template t = new Template(title, template, data);
			packet.data(t);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 网页转ofd
	 *
	 * @param uri
	 * @throws PackException
	 * @throws ConvertException
	 * @throws IOException
	 */
	public void htmlToOFD(String uri, OutputStream out) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.HTML, Target.OFD);
		try {
			packet.file(new Common(null, "html", new URI(uri)));
			// packet.pack(out);
			this.convert(packet, out);

		} catch (IllegalArgumentException e) {
			throw new PackException(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 多网页转ofd
	 *
	 * @param uris
	 * @throws PackException
	 * @throws ConvertException
	 * @throws IOException
	 */
	public void htmlToOFD(List<String> uris, OutputStream out) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.HTML, Target.OFD);
		try {
			for (String uri : uris) {
				packet.file(new Common(null, "html", new URI(uri)));
			}
			this.convert(packet, out);
		} catch (IllegalArgumentException e) {
			throw new PackException(e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并添加签名
	 *
	 * @param srcFile  源文件
	 * @param out      输出流
	 * @param provider 安全服务提供者标识
	 * @param oesClass 提供者实现类名，全类名
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addSignature(File srcFile, OutputStream out, String provider, String oesClass)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			this.append(packet, srcFile, 0, 0);
			packet.signature(provider, oesClass);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并添加签名
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param ca      内置签名
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addSignature(File srcFile, OutputStream out, CA ca)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			this.append(packet, srcFile, 0, 0);
			packet.signature(ca);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并添加签章<b>外部签章<b>
	 *
	 * @param srcFile  源文件
	 * @param out      输出流
	 * @param provider 安全服务提供者标识
	 * @param oesClass 提供者实现类名，全类名
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addSeal(File srcFile, OutputStream out, String provider, String oesClass, SealInfo info)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			this.append(packet, srcFile, 0, 0);
			packet.seal(provider, oesClass, info);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并添加签章
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param ca      封装的CA证书,<b>不能为空</b>
	 * @param esl     标准签章输入流,<b>可以为空</b>
	 * @param info    签章的章图像信息,<b>可以为空<b>
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addSeal(File srcFile, OutputStream out, CA ca, InputStream esl, SealInfo info)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			this.append(packet, srcFile, 0, 0);
			packet.seal(ca, esl, info);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并附加图片水印
	 *
	 * @param srcFile   源文件
	 * @param out       输出流
	 * @param imge      插入的图片流
	 * @param imageType 图片格式
	 * @param mk        水印的必要的一些属性想想x,y,h,w
	 * @param printable 是否可打印
	 * @param visible   是否可显示
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addImageMark(File srcFile, OutputStream out, InputStream imge, String imageType, MarkPosition mk,
			boolean printable, boolean visible) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.imageMark(imge, imageType, mk, printable, visible);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd并附加文字水印
	 *
	 * @param srcFile   源文件
	 * @param out       输出流
	 * @param text      文本属性
	 * @param mk        水印的必要的一些属性x,y,h,w
	 * @param printable 是否可打印
	 * @param visible   是否可显示
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addTextMark(File srcFile, OutputStream out, TextInfo text, MarkPosition mk, boolean printable,
			boolean visible) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.textMark(text, mk, printable, visible);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd添加权限
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param ps      权限集合
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addPermission(File srcFile, OutputStream out, Map<Const.Perm, Object> ps)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (Map.Entry<Const.Perm, Object> entry : ps.entrySet()) {
				packet.permission(entry.getKey(), entry.getValue());
			}
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd添加附件
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param attachs 附件
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void addAttachment(File srcFile, OutputStream out, Group<String, String, InputStream>... attachs)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (Group<String, String, InputStream> param : attachs) {
				packet.attach(param.key(), param.value(), param.type());
			}
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd添加后处理器
	 *
	 * @param srcFile    源文件
	 * @param out        输出流
	 * @param name       后处理的名称
	 * @param fhClass    后处理的类名称
	 * @param parameters 后处理的参数
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	protected void addFileHandler(File srcFile, OutputStream out, String name, String fhClass,
			Pair<String, String>... parameters) throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.fileHandler(name, fhClass, parameters);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单文件ofd转图片
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param w       图片宽度
	 * @param m       输出格式 当为true时 输出格式为zip 为false时输出格式为tiff
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToImge(File srcFile, OutputStream out, float w, boolean m)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.IMAGE, m ? Target.IMAGE : Target.TIFF);
		try {
			this.append(packet, srcFile, w, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单文件ofd转图片
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param dpi     dpi
	 * @param dpi     图片像素
	 * @param m       输出格式 当为true时 输出格式为zip 为false时输出格式为tiff
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToImge(File srcFile, OutputStream out, int dpi, boolean m)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.IMAGE, m ? Target.IMAGE : Target.TIFF);
		try {
			this.append(packet, srcFile, 0, dpi);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 多文件ofd转图片
	 *
	 * @param out     输出流
	 * @param w       图片宽度
	 * @param m       输出格式 当为true时 输出格式为zip 为false时输出格式为tiff
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToImge(List<File> srcFiles, OutputStream out, float w, boolean m)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.IMAGE, m ? Target.IMAGE : Target.TIFF);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, w, 0);
			}
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 多文件ofd转图片
	 *
	 * @param out     输出流
	 * @param dpi     图片像素
	 * @param m       输出格式 当为true时 输出格式为zip 为false时输出格式为tiff
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void OFDToImge(List<File> srcFiles, OutputStream out, int dpi, boolean m)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.IMAGE, m ? Target.IMAGE : Target.TIFF);
		try {
			for (File f : srcFiles) {
				this.append(packet, f, -1, dpi);
			}
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd插入页面
	 *
	 * @param srcFile  源文件
	 * @param out      输出流
	 * @param source、  待插入的文档，必须是OFD格式
	 * @param srcStart 待插入的范围，起始位置，1表示第1页
	 * @param srcEnd   待插入的范围，结束位置，1表示第1页
	 * @param index    插入的位置，0表示第一页前，1表示第1页后
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void insertPage(File srcFile, OutputStream out, InputStream source, int srcStart, int srcEnd, int index)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.insertPage(source, srcStart, srcEnd, index);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd插入页面
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param pages   删除的页面
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void deletePage(File srcFile, OutputStream out, int... pages)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.deletePage(pages);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd插入页面
	 *
	 * @param srcFile 源文件
	 * @param out     输出流
	 * @param swap    带交换的页面页码
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void swapPage(File srcFile, OutputStream out, Pair<Integer, Integer>... swap)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.swapPage(swap);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd移除签章
	 *
	 * @param srcFile 源文件
	 * @param out     完成输出流
	 * @param wipe    去除信息的参数
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void removeSeal(File srcFile, OutputStream out, Pair<SealMode, Boolean>... wipe)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			for (Pair<SealMode, Boolean> pair : wipe) {
				packet.removeSeal(pair.key(), pair.value());
			}
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 单办公文件转换为ofd添加页面旋转
	 *
	 * @param srcFile 源文件
	 * @param out     文件输出流
	 * @param rotate  旋转的角度
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void rotate(File srcFile, OutputStream out, float rotate, int... pages)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.rotate(rotate, pages);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 加密设置
	 *
	 * @param srcFile  原文件
	 * @param out      文件输出流
	 * @param provider 加密提供者
	 * @param scope    加密范围 ,<b>可以为null<b>
	 * @param info     加密内容预定义提示信息,<b>可以为null<b>
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */

	public void encrypt(File srcFile, OutputStream out, CipherProvider provider, Map<EntryScope, Boolean> scope,
			SimpleTextInfo info) throws PackException, IOException, ConvertException {

		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.encrypt(provider, scope, info);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 加密设置
	 *
	 * @param srcFile  原文件
	 * @param out      文件输出流
	 * @param provider 解密信息提供者
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void encrypt(File srcFile, OutputStream out, CipherProvider provider)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.encrypt(provider, null, null);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 解密设置
	 *
	 * @param srcFile  原文件
	 * @param out      文件输出流
	 * @param provider 加密提供者
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */

	public void decrypt(File srcFile, OutputStream out, CipherProvider provider)
			throws PackException, IOException, ConvertException {

		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.decrypt(provider);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * copy语义
	 *
	 * @param srcFile 原文件
	 * @param out     文件输出流
	 * @param ofd     提供语义的ofd文档
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */

	public void copyCustomTag(File srcFile, OutputStream out, InputStream ofd)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.copyEntry(EntryType.CustomTag, null, ofd);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	/**
	 * 添加模板
	 *
	 * @param srcFile 原文件
	 * @param out     文件输出流
	 * @param index   添加模板的页数
	 * @param form    表单文件
	 * @throws PackException
	 * @throws IOException
	 * @throws ConvertException
	 */
	public void appendTemplate(File srcFile, OutputStream out, int index, InputStream form)
			throws PackException, IOException, ConvertException {
		Packet packet = new Packet(PackType.COMMON, Target.OFD);
		try {
			packet.appendTemplate(index, form);
			this.append(packet, srcFile, 0, 0);
			this.convert(packet, out);
		} finally {
			IOUtils.closeQuietly(packet);
		}
	}

	public static InputStream store(final Packet packet) throws IOException {
		/*
		 * PipedInputStream in = new PipedInputStream(); final PipedOutputStream out =
		 * new PipedOutputStream(in); this.executor.execute(new Runnable() {
		 * 
		 * @Override public void run() { try { packet.pack(out); } catch (IOException e)
		 * { log.error(e.getMessage(), e); } } }); return in;
		 */
		DeferredFileOutputStream dfos = new DeferredFileOutputStream((int) (FileUtils.ONE_MB * 5), "Agent-", ".packet",
				FileUtils.getTempDirectory());
		try {
			packet.pack(dfos);
		} finally {
			IOUtils.closeQuietly(packet);
			IOUtils.closeQuietly(dfos);
		}
		if (dfos.isInMemory()) {
			return new ByteArrayInputStream(dfos.getData());
		} else {
			return new AutoDeleteFileInputStream(dfos.getFile());
		}
	}

	public String submit(final Packet packet) throws IOException {
		return this.submit(store(packet));
	}

	public String submit(InputStream packet) throws IOException {
		return this.submit(packet, null, false);
	}

	public abstract String submit(InputStream packet, String token, boolean raise) throws IOException;

	public void convert(final Packet packet, OutputStream result) throws IOException, ConvertException {
		this.convert(store(packet), result);
	}

	public void convert(final Packet packet, OutputStream result, String token, boolean raise)
			throws IOException, ConvertException {
		this.convert(store(packet), result, token, raise);
	}

	public void convert(InputStream packet, OutputStream result) throws ConvertException, IOException {
		this.convert(packet, result, null, false);
	}

	public abstract void convert(InputStream packet, OutputStream result, String token, boolean raise)
			throws ConvertException, IOException;

	public void convertNoWait(final Packet packet, ConvertCallback callback) throws IOException {
		this.convertNoWait(packet, callback, null, false);
	}

	public void convertNoWait(InputStream packet, ConvertCallback callback) throws IOException {
		this.convertNoWait(packet, callback, null, false);
	}

	public void convertNoWait(Packet packet, ConvertCallback callback, String token, boolean raise) throws IOException {
		InputStream src;
		boolean itc = callback instanceof TransferCallback;
		if (itc) {
			((TransferCallback) callback).onPackStart();
		}
		src = store(packet);
		if (itc) {
			((TransferCallback) callback).onPackEnd();
		}
		this.convertNoWait(src, callback, token, raise);
	}

	public abstract void convertNoWait(InputStream packet, ConvertCallback callback, String token, boolean raise)
			throws IOException;

}
