package com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {
	private static Logger log = LoggerFactory.getLogger(Utils.class);

	public static String UUID() {
		return UUID.randomUUID().toString();
	}

	public static boolean toBoolean(Object v) {
		if (v instanceof Boolean)
			return ((Boolean) v).booleanValue();
		return Boolean.valueOf(String.valueOf(v)).booleanValue();
	}

	public static int toInteger(Object v) {
		if (v instanceof Integer)
			return ((Integer) v).intValue();
		return Integer.valueOf(String.valueOf(v)).intValue();
	}

	public static float toFloat(Object v) {
		if (v instanceof Float)
			return ((Float) v).floatValue();
		return Float.valueOf(String.valueOf(v)).floatValue();
	}

	public static String toString(int[] array) {
		if (array == null || array.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i : array)
			sb.append(i).append(",");
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public static Date toDate(Object v) {
		if (v instanceof Date)
			return (Date) v;
		if (v instanceof String) {
			String d = (String) v;
			d = d.replace('/', '-');
			try {
				return (new SimpleDateFormat("yyyy-MM-dd")).parse(d);
			} catch (ParseException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	@Deprecated
	public static XMLGregorianCalendar toXGC(Object v) {
		if (v instanceof String) {
			try {
				return DatatypeFactory.newInstance().newXMLGregorianCalendar((String) v);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else if (v instanceof Date) {
			try {
				Date d = (Date) v;
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar();
				xgc.setYear(c.get(1));
				xgc.setMonth(c.get(2) + 1);
				xgc.setDay(c.get(5));
				xgc.setHour(c.get(11));
				xgc.setMinute(c.get(12));
				return xgc;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	public static <K, V> Pair<K, V>[] toPairs(Map<K, V> map) {
		int size = (map == null) ? 0 : map.size();
		Pair[] arrayOfPair = (Pair[]) Array.newInstance(Pair.class, size);
		if (size > 0) {
			int i = 0;
			for (Map.Entry<K, V> en : map.entrySet())
				arrayOfPair[i++] = new Pair(en.getKey(), en.getValue());
		}
		return (Pair<K, V>[]) arrayOfPair;
	}

	public static <K, V> Map<K, V> toMap(Pair<K, V>... pairs) {
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
		if (pairs != null)
			for (Pair<K, V> p : pairs)
				map.put((K) p.key(), (V) p.value());
		return map;
	}

	public static String toString(Pair<Integer, Integer>... pairs) {
		StringBuilder sb = new StringBuilder();
		if (pairs != null)
			for (Pair<Integer, Integer> p : pairs) {
				int k = ((Integer) p.key()).intValue();
				int v = ((Integer) p.value()).intValue();
				if (k < v) {
					sb.append(k).append("-").append(v).append(",");
				} else {
					sb.append(v).append("-").append(k).append(",");
				}
			}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static URI toURI(File file) {
		return file.toURI();
	}

	public static URI toURI(String uri) throws IllegalArgumentException {
		return URI.create(uri);
	}
}
