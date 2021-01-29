/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleTextInfo
/*    */ {
/*    */   private String fontName;
/*    */   private float fontSize;
/*    */   private String color;
/*    */   
/*    */   public SimpleTextInfo(String fontName, float fontSize, String color) {
/* 12 */     this.fontName = fontName;
/* 13 */     this.fontSize = fontSize;
/* 14 */     this.color = color;
/*    */   }
/*    */   
/*    */   public String getFontName() {
/* 18 */     return this.fontName;
/*    */   }
/*    */   
/*    */   public float getFontSize() {
/* 22 */     return this.fontSize;
/*    */   }
/*    */   
/*    */   public String getColor() {
/* 26 */     return this.color;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\SimpleTextInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */