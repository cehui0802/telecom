/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.MessageDigest;
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
/*     */ public class DigestInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   protected MessageDigest digest;
/*     */   private boolean isOn = true;
/*     */   
/*     */   public DigestInputStream(InputStream stream, MessageDigest digest) {
/*  31 */     super(stream);
/*  32 */     this.digest = digest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageDigest getMessageDigest() {
/*  41 */     return this.digest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMessageDigest(MessageDigest digest) {
/*  50 */     this.digest = digest;
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
/*     */   public int read() throws IOException {
/*  65 */     int byteRead = this.in.read();
/*     */ 
/*     */ 
/*     */     
/*  69 */     if (this.isOn && byteRead != -1) {
/*  70 */       this.digest.update((byte)byteRead);
/*     */     }
/*     */     
/*  73 */     return byteRead;
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
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  94 */     int bytesRead = this.in.read(b, off, len);
/*     */ 
/*     */ 
/*     */     
/*  98 */     if (this.isOn && bytesRead != -1) {
/*  99 */       this.digest.update(b, off, bytesRead);
/*     */     }
/*     */     
/* 102 */     return bytesRead;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void on(boolean on) {
/* 113 */     this.isOn = on;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 124 */     return super.toString() + ", " + this.digest.toString() + (this.isOn ? ", is on" : ", is off");
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\io\DigestInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */