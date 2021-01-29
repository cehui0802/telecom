/*    */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model;
/*    */ 
/*    */ 
/*    */ public class Group<K, V, T>
/*    */   extends Pair<K, V>
/*    */ {
/*    */   private T type;
/*    */   
/*    */   public Group(K key, V value, T type) {
/* 10 */     super(key, value);
/* 11 */     this.type = type;
/*    */   }
/*    */   
/*    */   public T type() {
/* 15 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\model\Group.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */