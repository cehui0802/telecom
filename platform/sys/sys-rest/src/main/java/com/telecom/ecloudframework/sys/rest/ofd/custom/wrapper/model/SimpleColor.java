/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ public class SimpleColor {
/*    */   private int a;
/*    */   private int r;
/*    */   private int g;
/*    */   private int b;
/*    */   
/*    */   public SimpleColor(int a, int r, int g, int b) {
/* 10 */     this.a = a;
/* 11 */     this.r = r;
/* 12 */     this.g = g;
/* 13 */     this.b = b;
/*    */   }
/*    */   public SimpleColor(int r, int g, int b) {
/* 16 */     this.a = 255;
/* 17 */     this.r = r;
/* 18 */     this.g = g;
/* 19 */     this.b = b;
/*    */   }
/*    */   
/*    */   public String toHex(int i) {
/* 23 */     String hex = "";
/* 24 */     if (i < 0) {
/* 25 */       i = 0;
/* 26 */     } else if (i > 255) {
/* 27 */       i = 255;
/* 28 */     }  if (i < 16) {
/* 29 */       hex = "0" + Integer.toHexString(i);
/*    */     } else {
/* 31 */       hex = Integer.toHexString(i);
/* 32 */     }  return hex;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 37 */     return toHex(this.a) + toHex(this.r) + toHex(this.g) + toHex(this.b);
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\SimpleColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */