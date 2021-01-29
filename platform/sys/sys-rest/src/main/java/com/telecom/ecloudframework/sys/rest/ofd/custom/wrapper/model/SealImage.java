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
/*    */ public class SealImage
/*    */ {
/*    */   private String sealID;
/*    */   private PackEntry data;
/*    */   private float width;
/*    */   private float height;
/*    */   
/*    */   public SealImage(String sealID, InputStream data, float width, float height) {
/* 25 */     this(sealID, PackEntry.wrap(data), width, height);
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
/*    */   public SealImage(String sealID, PackEntry data, float width, float height) {
/* 37 */     this.sealID = sealID;
/* 38 */     this.data = data;
/* 39 */     this.width = width;
/* 40 */     this.height = height;
/*    */   }
/*    */   
/*    */   public String getSealID() {
/* 44 */     return this.sealID;
/*    */   }
/*    */   
/*    */   public PackEntry getData() {
/* 48 */     return this.data;
/*    */   }
/*    */   
/*    */   public float getWidth() {
/* 52 */     return this.width;
/*    */   }
/*    */   
/*    */   public float getHeight() {
/* 56 */     return this.height;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\SealImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */