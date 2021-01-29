/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CipherProvider
/*    */ {
/*    */   private String name;
/*    */   private String oecClassName;
/*    */   private Group<String, String, String>[] parameters;
/*    */   
/*    */   public CipherProvider(String name) {
/* 15 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CipherProvider(String name, String oecClassName) {
/* 26 */     this.name = name;
/* 27 */     this.oecClassName = oecClassName;
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
/*    */   public CipherProvider(String name, String oecClassName, Group<String, String, String>... parameters) {
/* 39 */     this.name = name;
/* 40 */     this.oecClassName = oecClassName;
/* 41 */     this.parameters = parameters;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 45 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getOecClassName() {
/* 49 */     return this.oecClassName;
/*    */   }
/*    */   
/*    */   public Group<String, String, String>[] getParameters() {
/* 53 */     return this.parameters;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\CipherProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */