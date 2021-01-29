/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Pair<K, V>
/*    */ {
/*    */   private K k;
/*    */   private V v;
/*    */   
/*    */   public Pair(K key, V value) {
/* 12 */     this.k = key;
/* 13 */     this.v = value;
/*    */   }
/*    */   
/*    */   public K key() {
/* 17 */     return this.k;
/*    */   }
/*    */   
/*    */   public V value() {
/* 21 */     return this.v;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\Pair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */