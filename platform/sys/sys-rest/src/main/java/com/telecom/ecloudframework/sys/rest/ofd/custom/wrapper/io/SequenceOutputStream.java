/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class SequenceOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private Block block;
/*    */   
/*    */   protected static class Block
/*    */   {
/*    */     final OutputStream out;
/*    */     long capacity;
/*    */     
/*    */     public Block(OutputStream out, long capacity) {
/* 19 */       this.out = out;
/* 20 */       this.capacity = capacity;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected SequenceOutputStream(Block block) {
/* 30 */     this.block = block;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 35 */     while (len > 0) {
/* 36 */       int sz = (int)Math.min(len, this.block.capacity);
/* 37 */       this.block.out.write(b, off, sz);
/* 38 */       this.block.capacity -= sz;
/* 39 */       len -= sz;
/* 40 */       off += sz;
/* 41 */       swapIfNeeded();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void write(int b) throws IOException {
/* 46 */     this.block.out.write(b);
/* 47 */     this.block.capacity--;
/* 48 */     swapIfNeeded();
/*    */   }
/*    */   
/*    */   private void swapIfNeeded() throws IOException {
/* 52 */     if (this.block.capacity > 0L)
/* 53 */       return;  this.block.out.close();
/* 54 */     this.block = next(this.block);
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 59 */     this.block.out.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 64 */     this.block.out.close();
/* 65 */     this.block = null;
/*    */   }
/*    */   
/*    */   protected abstract Block next(Block paramBlock) throws IOException;
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\io\SequenceOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */