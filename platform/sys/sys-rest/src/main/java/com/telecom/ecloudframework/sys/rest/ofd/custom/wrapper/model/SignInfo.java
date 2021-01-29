/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackEntry;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SignInfo
/*    */ {
/*    */   private String certID;
/*    */   private String password;
/*    */   private TextInfo textInfo;
/*    */   private MarkPosition position;
/*    */   private boolean printable;
/*    */   private boolean visible;
/*    */   private PackEntry image;
/*    */   private String type;
/*    */   
/*    */   public SignInfo(String certID, String password) {
/* 20 */     this.certID = certID;
/* 21 */     this.password = password;
/*    */   }
/*    */   
/*    */   public SignInfo(String certID, String password, TextInfo textInfo, MarkPosition position) {
/* 25 */     this(certID, password);
/* 26 */     this.textInfo = textInfo;
/* 27 */     this.position = position;
/*    */   }
/*    */   
/*    */   public SignInfo(String certID, String password, TextInfo textInfo, MarkPosition position, boolean printable, boolean visible) {
/* 31 */     this(certID, password, textInfo, position);
/* 32 */     this.printable = printable;
/* 33 */     this.visible = visible;
/*    */   }
/*    */   public SignInfo(String certID, String password, PackEntry image, MarkPosition position, String type) {
/* 36 */     this(certID, password);
/* 37 */     this.image = image;
/* 38 */     this.position = position;
/*    */   }
/*    */   public SignInfo(String certID, String password, PackEntry image, MarkPosition position, String type, boolean printable, boolean visible) {
/* 41 */     this(certID, password, image, position, type);
/* 42 */     this.printable = printable;
/* 43 */     this.visible = visible;
/*    */   }
/*    */   
/*    */   public String getCertID() {
/* 47 */     return this.certID;
/*    */   }
/*    */   
/*    */   public String getPassword() {
/* 51 */     return this.password;
/*    */   }
/*    */   
/*    */   public TextInfo getTextInfo() {
/* 55 */     return this.textInfo;
/*    */   }
/*    */   
/*    */   public MarkPosition getPosition() {
/* 59 */     return this.position;
/*    */   }
/*    */   
/*    */   public boolean isPrintable() {
/* 63 */     return this.printable;
/*    */   }
/*    */   
/*    */   public boolean isVisible() {
/* 67 */     return this.visible;
/*    */   }
/*    */   
/*    */   public PackEntry getImage() {
/* 71 */     return this.image;
/*    */   }
/*    */   
/*    */   public String getType() {
/* 75 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\SignInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */