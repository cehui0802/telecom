/*     */ package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class Hex
/*     */ {
/*     */   public static final String DEFAULT_CHARSET_NAME = "UTF-8";
/*  23 */   private static final char[] DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  28 */   private static final char[] DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   
/*     */   public static byte[] decodeHexString(String data) throws IOException {
/*  31 */     if (data == null) {
/*  32 */       return null;
/*     */     }
/*  34 */     return decodeHex(data.toCharArray());
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
/*     */   public static byte[] decodeHex(char[] data) throws IOException {
/*  53 */     int len = data.length;
/*     */     
/*  55 */     if ((len & 0x1) != 0) {
/*  56 */       throw new IOException("Odd number of characters.");
/*     */     }
/*     */     
/*  59 */     byte[] out = new byte[len >> 1];
/*     */ 
/*     */     
/*  62 */     for (int i = 0, j = 0; j < len; i++) {
/*  63 */       int f = toDigit(data[j], j) << 4;
/*  64 */       j++;
/*  65 */       f |= toDigit(data[j], j);
/*  66 */       j++;
/*  67 */       out[i] = (byte)(f & 0xFF);
/*     */     } 
/*     */     
/*  70 */     return out;
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
/*     */   public static char[] encodeHex(byte[] data) {
/*  84 */     return encodeHex(data, true);
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
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase) {
/* 102 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
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
/*     */   protected static char[] encodeHex(byte[] data, char[] toDigits) {
/* 119 */     int l = data.length;
/* 120 */     char[] out = new char[l << 1];
/*     */     
/* 122 */     for (int i = 0, j = 0; i < l; i++) {
/* 123 */       out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
/* 124 */       out[j++] = toDigits[0xF & data[i]];
/*     */     } 
/* 126 */     return out;
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
/*     */   public static String encodeHexString(byte[] data) {
/* 141 */     return new String(encodeHex(data));
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
/*     */   protected static int toDigit(char ch, int index) throws IOException {
/* 156 */     int digit = Character.digit(ch, 16);
/* 157 */     if (digit == -1) {
/* 158 */       throw new IOException("Illegal hexadecimal charcter " + ch + " at index " + index);
/*     */     }
/* 160 */     return digit;
/*     */   }
/*     */ }


/* Location:              C:\Users\19\Desktop\AgentHttpDemo\lib\packet-wrapper-1.13.19.1021.jar!\com\suwell\ofd\custom\wrapper\Hex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */