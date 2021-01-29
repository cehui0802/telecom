/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class EmptyOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private long count;
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 12 */     this.count += len;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 17 */     this.count++;
/*    */   }
/*    */   
/*    */   public long count() {
/* 21 */     return this.count;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 26 */     this.count = 0L;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\io\EmptyOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */