/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.io;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import org.apache.commons.io.FileUtils;
/*    */ 
/*    */ public class AutoDeleteFileInputStream extends FileInputStream {
/*    */   private File file;
/*    */   
/*    */   public AutoDeleteFileInputStream(File file) throws FileNotFoundException {
/* 13 */     super(file);
/* 14 */     this.file = file;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 19 */     super.close();
/* 20 */     FileUtils.forceDelete(this.file);
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\io\AutoDeleteFileInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */