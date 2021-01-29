/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Argument
/*    */ {
/* 13 */   private Map<String, String> map = new LinkedHashMap<String, String>();
/*    */ 
/*    */   
/*    */   public Argument put(String key, String val) {
/* 17 */     if (key == null) {
/* 18 */       return this;
/*    */     }
/* 20 */     this.map.put(key, val);
/* 21 */     return this;
/*    */   }
/*    */   
/*    */   public Argument put(Map<String, String> map) {
/* 25 */     if (map == null) {
/* 26 */       return this;
/*    */     }
/* 28 */     this.map.putAll(map);
/* 29 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Argument retain(Set<Integer> index) {
/* 39 */     if (index == null || index.isEmpty()) {
/* 40 */       return this;
/*    */     }
/* 42 */     StringBuilder sb = new StringBuilder();
/* 43 */     for (Integer i : index) {
/* 44 */       if (sb.length() > 0) {
/* 45 */         sb.append(".");
/*    */       }
/* 47 */       sb.append(i);
/*    */     } 
/* 49 */     put("retain", sb.toString());
/* 50 */     return this;
/*    */   }
/*    */   
/*    */   public String value() {
/* 54 */     if (this.map == null || this.map.isEmpty()) {
/* 55 */       return null;
/*    */     }
/*    */     
/* 58 */     StringBuilder sb = new StringBuilder();
/*    */     
/* 60 */     for (String key : this.map.keySet()) {
/* 61 */       if (sb.length() > 0) {
/* 62 */         sb.append(";");
/*    */       }
/* 64 */       String val = this.map.get(key);
/* 65 */       sb.append(key);
/* 66 */       if (val != null) {
/* 67 */         sb.append("=").append(val);
/*    */       }
/*    */     } 
/*    */     
/* 71 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\Argument.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */