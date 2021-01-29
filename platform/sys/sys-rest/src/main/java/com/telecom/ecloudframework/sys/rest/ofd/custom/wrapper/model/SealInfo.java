/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
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
/*     */ public class SealInfo
/*     */ {
/*     */   public static final int TYPE_ALL = -1;
/*     */   public static final int TYPE_FIRST = 0;
/*     */   public static final int TYPE_LAST = 2147483647;
/*     */   private int type;
/*     */   private float x;
/*     */   private float y;
/*     */   private float width;
/*     */   private float height;
/*     */   private String sealID;
/*     */   private String password;
/*     */   private int pageIndex;
/*     */   private String typeC;
/*     */   private StampAnnot[] stampAnnot;
/*     */   
/*     */   public SealInfo(int type, String sealID, float x, float y, float width, float height) {
/*  49 */     this.type = type;
/*  50 */     this.sealID = sealID;
/*  51 */     this.x = x;
/*  52 */     this.y = y;
/*  53 */     this.width = width;
/*  54 */     this.height = height;
/*     */   }
/*     */ 
/*     */   
/*     */   public SealInfo(NativeType type, String sealID, float x, float y, float width, float height, String password, int pageIndex, StampAnnot[] stampAnnot) {
/*  59 */     this.typeC = type.toString();
/*  60 */     this.sealID = sealID;
/*  61 */     this.x = x;
/*  62 */     this.y = y;
/*  63 */     this.width = width;
/*  64 */     this.height = height;
/*  65 */     this.pageIndex = pageIndex;
/*  66 */     this.password = password;
/*  67 */     this.stampAnnot = stampAnnot;
/*     */   }
/*     */   
/*     */   public SealInfo(NativeType type, String sealID, float x, float y, float width, float height, String password, int pageIndex) {
/*  71 */     this.typeC = type.toString();
/*  72 */     this.sealID = sealID;
/*  73 */     this.x = x;
/*  74 */     this.y = y;
/*  75 */     this.width = width;
/*  76 */     this.height = height;
/*  77 */     this.pageIndex = pageIndex;
/*  78 */     this.password = password;
/*     */   }
/*     */   public String getTypeC() {
/*  81 */     return this.typeC;
/*     */   }
/*     */   
/*     */   public int getType() {
/*  85 */     return this.type;
/*     */   }
/*     */   
/*     */   public float getX() {
/*  89 */     return this.x;
/*     */   }
/*     */   
/*     */   public float getY() {
/*  93 */     return this.y;
/*     */   }
/*     */   
/*     */   public float getWidth() {
/*  97 */     return this.width;
/*     */   }
/*     */   
/*     */   public float getHeight() {
/* 101 */     return this.height;
/*     */   }
/*     */   
/*     */   public String getSealID() {
/* 105 */     return this.sealID;
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 109 */     return this.password;
/*     */   }
/*     */   
/*     */   public int getPageIndex() {
/* 113 */     return this.pageIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public StampAnnot[] getStampAnnot() {
/* 118 */     return this.stampAnnot;
/*     */   }
/*     */ 
/*     */   
/*     */   public enum NativeType
/*     */   {
/* 124 */     Last, First, All, Check, Normal; }
/*     */   
/*     */   public static class StampAnnot {
/*     */     private SealInfo info;
/*     */     private Type type;
/*     */     
/*     */     public StampAnnot(Type type, float x, float y, float width, float height) {
/* 131 */       this.info = new SealInfo(0, null, x, y, width, height);
/* 132 */       this.type = type;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Type getType() {
/* 138 */       return this.type;
/*     */     }
/*     */     
/*     */     public float getX() {
/* 142 */       return this.info.getX();
/*     */     }
/*     */     
/*     */     public float getY() {
/* 146 */       return this.info.getY();
/*     */     }
/*     */     
/*     */     public float getWidth() {
/* 150 */       return this.info.getWidth();
/*     */     }
/*     */     
/*     */     public float getHeight() {
/* 154 */       return this.info.height;
/*     */     }
/*     */     
/*     */     public enum Type {
/* 158 */       Last, First, Middle;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\SealInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */