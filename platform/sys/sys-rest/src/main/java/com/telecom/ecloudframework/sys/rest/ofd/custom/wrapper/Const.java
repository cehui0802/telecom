package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper;

public final class Const {
	public enum PackType {
		COMMON {
			public String toString() {
				return "common.all";
			}
		},
		HTML {
			public String toString() {
				return "common.html";
			}
		},
		IMAGE {
			public String toString() {
				return "custom.ofd2image";
			}
		},
		CATALOG {
			public String toString() {
				return "common.catalog";
			}
		},
		NATIVE {
			public String toString() {
				return "Native.parser";
			}
		};

		public String value() {
			return toString();
		}
	}

	public enum Target {
		OFD, PDF, TIFF, IMAGE, MERGE, OAP, CEB;

		public String value() {
			return toString().toLowerCase();
		}
	}

	public enum Meta {
		DOC_ID("DocID"), TITLE("Title"), AUTHOR("Author"), SUBJECT("Subject"), ABSTRACT("Abstract"),
		MOD_DATE("ModDate"), DOC_USAGE("DocUsage"), KEYWORDS("Keywords"), KEYWORD("Keyword"),
		CUSTOM_DATAS("CustomDatas"), CUSTOM_DATA("CustomData"), EXTEND_FILE("ExtendFile"), CREATOR("Creator");

		private String name;

		Meta(String xmlName) {
			this.name = xmlName;
		}

		public String xmlName() {
			return this.name;
		}

		public String value() {
			String v = toString();
			return v.replace("_", "").toLowerCase();
		}

		public static Meta of(String v) {
			if (v != null)
				v = v.toLowerCase();
			for (Meta m : values()) {
				if (m.value().equals(v))
					return m;
			}
			return null;
		}
	}

	public enum Perm {
		PRINT("Print"), PRINT_COPIES("Copies"), ANNOT("Annot"), COPY("Copy"), SAVE_AS("SaveAs"), EXPORT("Export"),
		VALID_START("StartDate"), VALID_END("EndDate"), ASSEM("Assem"), SIGNATURE("Signature"), WATERMARK("Watermark");

		private String name;

		Perm(String xmlName) {
			this.name = xmlName;
		}

		public String xmlName() {
			return this.name;
		}
		
		public String value() {
			String v = toString();
			return v.replace("_", "").toLowerCase();
		}
		
		public static Perm of(String v) {
			if (v != null)
				v = v.toLowerCase();
			for (Perm m : values()) {
				if (m.value().equals(v))
					return m;
			}
			return null;
		}
	}

	public enum View {
		PAGE_LAYOUT("PageLayout"), PAGE_MODE("PageMode"), ZOOM_MODE("ZoomMode"), ZOOM("Zoom"),
		TAB_DISPLAY("TabDisplay"), HIDE_WINDOW("HideWindowUI"), HIDE_MENU("HideMenubar"), HIDE_TOOLBAR("HideToolbar");

		private String name;

		View(String xmlName) {
			this.name = xmlName;
		}

		public String xmlName() {
			return this.name;
		}
	}

	public enum PageLayout {
		OnePage, OneColumn, TwoPageL, TwoColumnL, TwoPageR, TwoColumnR;
	}

	public enum PageMode {
		None, FullScreen, UseOutlines, UseThumbs, UseAttatchs, UseLayers;
	}

	public enum ZoomMode {
		Default, FitHeight, FitWidth, FitVisible;
	}

	public enum TabDisplay {
		DocTitle, FileName;
	}

	public enum XAlign {
		Absolute, Right, Center, Left;
	}

	public enum YAlign {
		Top, Middle, Bottom, Absolute;
	}

	public enum EnvelopeMeta {
		DocId, DocTitle, MiJi, StartDate, EndDate, TimeStamp, Sender, Receiver;

		public String xmlName() {
			return toString();
		}
	}

	public enum EnvelopePerm {
		Copy, SaveAs, Print;

		public String xmlName() {
			return toString();
		}
	}

	public enum EnvelopeType {
		Custom, MacAddress, IPAddress, MachineCode, DefaultPwd;
	}

	public enum SealMode {
		Signature, Eseal, Encryption, Timestamp;

		public String xmlName() {
			return toString();
		}
	}

	public enum MergeType {
		Image, Path, Text, ImageAndPath;
	}

	public enum EntryScope {
		Contents, Resources, Attaches, Tags;

		public String xmlName() {
			return toString();
		}
	}

	public enum EntryType {
		CustomTag;
	}

	public enum RemoveAttachType {
		All, Name, Format;
	}

	public enum ClipColorType {
		CROP_ALL {
			public int value() {
				return -1;
			}
		},
		CROP_TEXT {
			public int value() {
				return 1;
			}
		},
		CROP_PATH {
			public int value() {
				return 2;
			}
		};

		public abstract int value();
	}

	public enum PatternType {
		Tile {
			public float[] value() {
				return null;
			}
		},
		Stretch {
			public float[] value() {
				return new float[] { -1.0F, -1.0F };
			}
		},
		Adapt {
			public float[] value() {
				return new float[] { -2.0F, -2.0F };
			}
		},
		Center {
			public float[] value() {
				return new float[] { -3.0F, -3.0F };
			}
		};

		public abstract float[] value();
	}
}
