/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*     */ 
/*     */ import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TextInfo
/*     */   extends SimpleTextInfo
/*     */ {
/*     */   private String text;
/*     */   private int rotate;
/*     */   private Const.XAlign xAlign;
/*     */   private Const.YAlign yAlign;
/*     */   
/*     */   public TextInfo(String text, String fontName, float fontSize) {
/*  30 */     super(fontName, fontSize, null);
/*  31 */     this.text = text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextInfo(String text, String fontName, float fontSize, String color) {
/*  47 */     super(fontName, fontSize, color);
/*  48 */     this.text = text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextInfo(String text, String fontName, float fontSize, String color, int rotate) {
/*  67 */     super(fontName, fontSize, color);
/*  68 */     this.text = text;
/*  69 */     this.rotate = rotate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextInfo(String text, String fontName, float fontSize, String color, int rotate, Const.XAlign xAlign, Const.YAlign yAlign) {
/*  92 */     super(fontName, fontSize, color);
/*  93 */     this.text = text;
/*  94 */     this.rotate = rotate;
/*  95 */     this.xAlign = xAlign;
/*  96 */     this.yAlign = yAlign;
/*     */   }
/*     */   
/*     */   public String getText() {
/* 100 */     return this.text;
/*     */   }
/*     */   
/*     */   public int getRotate() {
/* 104 */     return this.rotate;
/*     */   }
/*     */   
/*     */   public Const.XAlign getXAlign() {
/* 108 */     return this.xAlign;
/*     */   }
/*     */   
/*     */   public Const.YAlign getYAlign() {
/* 112 */     return this.yAlign;
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\TextInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */