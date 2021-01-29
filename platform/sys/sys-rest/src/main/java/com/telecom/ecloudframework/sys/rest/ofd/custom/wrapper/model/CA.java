/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackEntry;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CA
/*    */ {
/*    */   private String digestMethod;
/*    */   private String signMethod;
/*    */   private PackEntry pfx;
/*    */   private String pfxPassword;
/*    */   private PackEntry certificate;
/*    */   
/*    */   public CA(String digestMethod, String signMethod, InputStream pfx, String pfxPassword, InputStream certificate) {
/* 27 */     this(digestMethod, signMethod, PackEntry.wrap(pfx), pfxPassword, PackEntry.wrap(certificate));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CA(String digestMethod, String signMethod, PackEntry pfx, String pfxPassword, PackEntry certificate) {
/* 40 */     this.digestMethod = digestMethod;
/* 41 */     this.signMethod = signMethod;
/* 42 */     this.pfx = pfx;
/* 43 */     this.pfxPassword = pfxPassword;
/* 44 */     this.certificate = certificate;
/*    */   }
/*    */   
/*    */   public String getDigestMethod() {
/* 48 */     return this.digestMethod;
/*    */   }
/*    */   
/*    */   public String getSignMethod() {
/* 52 */     return this.signMethod;
/*    */   }
/*    */   
/*    */   public PackEntry getPfx() {
/* 56 */     return this.pfx;
/*    */   }
/*    */   
/*    */   public String getPfxPassword() {
/* 60 */     return this.pfxPassword;
/*    */   }
/*    */   
/*    */   public PackEntry getCertificate() {
/* 64 */     return this.certificate;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\CA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */