/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*     */ 
/*     */ import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MarkPosition
/*     */ {
/*  10 */   public static final int[] INDEX_ALL = new int[0];
/*     */   
/*     */   private float x;
/*     */   
/*     */   private float y;
/*     */   
/*     */   private float width;
/*     */   
/*     */   private float height;
/*     */   private int[] index;
/*     */   private Pair<Integer, Integer>[] pages;
/*     */   private boolean isPattern;
/*     */   
/*     */   public MarkPosition(int[] index) {
/*  24 */     this.index = index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MarkPosition(int[] index, Const.PatternType type) {
/*  34 */     this.index = index;
/*  35 */     this.isPattern = true;
/*  36 */     if (type.value() != null && (type.value()).length == 2) {
/*  37 */       this.width = type.value()[0];
/*  38 */       this.height = type.value()[1];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MarkPosition(float width, float height, int[] index) {
/*  49 */     this.width = width;
/*  50 */     this.height = height;
/*  51 */     this.index = index;
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
/*     */   public MarkPosition(float x, float y, float width, float height, int[] index) {
/*  63 */     this.x = x;
/*  64 */     this.y = y;
/*  65 */     this.width = width;
/*  66 */     this.height = height;
/*  67 */     this.index = index;
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
/*     */   public MarkPosition(float x, float y, float width, float height, Pair<Integer, Integer>... pages) {
/*  80 */     this.x = x;
/*  81 */     this.y = y;
/*  82 */     this.width = width;
/*  83 */     this.height = height;
/*  84 */     this.pages = pages;
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
/*     */   public MarkPosition(float x, float y, float width, float height, int[] index, Pair<Integer, Integer>... pages) {
/*  98 */     this.x = x;
/*  99 */     this.y = y;
/* 100 */     this.width = width;
/* 101 */     this.height = height;
/* 102 */     this.index = index;
/* 103 */     this.pages = pages;
/*     */   }
/*     */   public float getX() {
/* 106 */     return this.x;
/*     */   }
/*     */   
/*     */   public float getY() {
/* 110 */     return this.y;
/*     */   }
/*     */   
/*     */   public float getWidth() {
/* 114 */     return this.width;
/*     */   }
/*     */   
/*     */   public float getHeight() {
/* 118 */     return this.height;
/*     */   }
/*     */   
/*     */   public int[] getIndex() {
/* 122 */     return this.index;
/*     */   }
/*     */   
/*     */   public Pair<Integer, Integer>[] getPages() {
/* 126 */     return this.pages;
/*     */   }
/*     */   
/*     */   public boolean isPattern() {
/* 130 */     return this.isPattern;
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\MarkPosition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */