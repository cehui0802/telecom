/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*     */ 
/*     */ import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackEntry;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public final class Template
/*     */ {
/*     */   private String title;
/*     */   private String handler;
/*     */   private PackEntry template;
/*     */   private PackEntry data;
/*     */   private Map<String, PackEntry> sheet;
/*     */   
/*     */   public Template(String title, String handler, InputStream data) {
/*  28 */     this(title, handler, PackEntry.wrap(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Template(String title, String handler, PackEntry data) {
/*  39 */     this.title = title;
/*  40 */     this.handler = handler;
/*  41 */     this.data = data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Template(String title, InputStream template, InputStream data) {
/*  52 */     this(title, PackEntry.wrap(template), PackEntry.wrap(data), (Map<String, PackEntry>)null);
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
/*     */   public Template(String title, InputStream template, InputStream data, Map<String, InputStream> sheet) {
/*  64 */     this(title, PackEntry.wrap(template), PackEntry.wrap(data), to(sheet));
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
/*     */   public Template(String title, PackEntry template, PackEntry data, Map<String, PackEntry> sheet) {
/*  76 */     this.title = title;
/*  77 */     this.template = template;
/*  78 */     this.data = data;
/*  79 */     this.sheet = sheet;
/*     */   }
/*     */   
/*     */   private static Map<String, PackEntry> to(Map<String, InputStream> map) {
/*  83 */     if (map == null || map.isEmpty()) {
/*  84 */       return null;
/*     */     }
/*  86 */     Map<String, PackEntry> nm = new HashMap<String, PackEntry>(map.size());
/*  87 */     for (String key : map.keySet()) {
/*  88 */       nm.put(key, PackEntry.wrap(map.get(key)));
/*     */     }
/*  90 */     return nm;
/*     */   }
/*     */   
/*     */   public String getTitle() {
/*  94 */     return this.title;
/*     */   }
/*     */   
/*     */   public String getHandler() {
/*  98 */     return this.handler;
/*     */   }
/*     */   
/*     */   public PackEntry getTemplate() {
/* 102 */     return this.template;
/*     */   }
/*     */   
/*     */   public PackEntry getData() {
/* 106 */     return this.data;
/*     */   }
/*     */   
/*     */   public Map<String, PackEntry> getSheet() {
/* 110 */     return this.sheet;
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\Template.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */