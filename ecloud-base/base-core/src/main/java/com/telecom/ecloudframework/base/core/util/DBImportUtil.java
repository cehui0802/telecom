package com.telecom.ecloudframework.base.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBImportUtil {
    public static void importTable(String filePath, String tableName) throws Exception {
        importTable(filePath, tableName, "ID_", null);
    }

    public static void importTable(String filePath, String tableName, String keyColumn) throws Exception {
        importTable(filePath, tableName, keyColumn, null);
    }

    public static void importTable(String filePath, String tableName, Map<String, Object> coverValue) throws Exception {
        importTable(filePath, tableName, "ID_", coverValue);
    }

    public static void importTable(String filePath, String tableName, String keyColumn, Map<String, Object> coverValue) throws Exception {
        File file = new File(filePath + "/" + tableName + ".txt");
        if (!file.exists()) {
            return;
        }
        JdbcTemplate jdbcTemplate = AppUtil.getBean(JdbcTemplate.class);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String deleteSql = "DELETE FROM " + tableName + " WHERE " + keyColumn + "  = ? ";
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        in.close();
        String bpmDef = buffer.toString();
        JSONArray jsonArray = JSON.parseArray(bpmDef);
        List<Map> params = jsonArray.toJavaList(Map.class);
        StringBuffer sbInsert = new StringBuffer();
        StringBuffer sbUpdate = new StringBuffer();
        StringBuffer value = new StringBuffer();
        StringBuffer keyV = new StringBuffer();
        Map<Integer, String> blobIndex = new HashMap<>();
        params.forEach(map -> {
            boolean init = false;
            List<Object> param = new ArrayList<>();
            if (sbInsert.length() == 0) {
                sbInsert.append("INSERT INTO " + tableName + " ( ");
                sbUpdate.append("UPDATE " + tableName + " SET ");
                init = true;
            }
            for (Object key : map.keySet()) {
                if (init) {
                    if (StringUtils.startsWith(key.toString(), "BLOB#")) {
                        blobIndex.put(param.size(), map.get(key).toString());
                        sbInsert.append(key.toString().split("#")[1] + ",");
                        sbUpdate.append(key.toString().split("#")[1] + " = ? ,");
                    } else {
                        sbInsert.append(key + ",");
                        sbUpdate.append(key + " = ? ,");
                    }
                    value.append("?,");
                }
                if (coverValue != null && coverValue.containsKey(key.toString())) {
                    param.add(coverValue.get(key.toString()));
                } else {
                    param.add(map.get(key));
                }
                if (StringUtils.equalsIgnoreCase(keyColumn, key.toString())) {
                    try {
                        jdbcTemplate.update(deleteSql, map.get(key));
                    } catch (Exception e) {
                        keyV.append(key.toString());
                    }
                }
            }
            if (init) {
                sbInsert.delete(sbInsert.length() - 1, sbInsert.length()).append(")").
                        append("values (").append(value.substring(0, value.length() - 1)).append(")");
                sbUpdate.delete(sbUpdate.length() - 1, sbUpdate.length()).append(" WHERE ").append(keyColumn).append(" = ?");
            }
            String sql = null;
            if (keyV.length() > 0) {
                param.add(keyV.toString());
                sql = sbUpdate.toString();
            } else {
                sql = sbInsert.toString();
            }
            jdbcTemplate.update(sql, ps -> {
                try {
                    for (int i = 0; i < param.size(); i++) {
                        String blobColumn = blobIndex.get(i);
                        if (StringUtil.isNotEmpty(blobColumn)) {
                            InputStream inputStream = new FileInputStream(filePath + "/" + tableName + "/" + param.get(i));
                            ps.setBinaryStream(i + 1,inputStream, inputStream.available());
                        } else {
                            ps.setObject(i + 1, param.get(i));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public static String checkTable(String filePath, String tableName, String keyColumn, String showColumn) throws Exception {
        File file = new File(filePath + "/" + tableName + ".txt");
        if (!file.exists()) {
            return "";
        }
        JdbcTemplate jdbcTemplate = AppUtil.getBean(JdbcTemplate.class);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        StringBuffer selectSql = new StringBuffer("SELECT ").append(showColumn).append(" FROM ")
                .append(tableName).append(" WHERE ").append(keyColumn + " in ( ");
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        in.close();
        String bpmDef = buffer.toString();
        JSONArray jsonArray = JSON.parseArray(bpmDef);
        List<Map> params = jsonArray.toJavaList(Map.class);
        List<Object> param = new ArrayList<>();
        params.forEach(map -> {
            Object value = map.get(keyColumn);
            if (value != null) {
                param.add(value);
                selectSql.append("?,");
            }
        });
        if (param.size() > 0) {
            selectSql.delete(selectSql.length() - 1, selectSql.length()).append(")");
            List<Map<String, Object>> exists = jdbcTemplate.queryForList(selectSql.toString(), param.toArray());
            if (exists.size() > 0) {
                StringBuffer showExist = new StringBuffer(tableName).append(" 已存在：");
                exists.forEach(value -> showExist.append(value.get(showColumn)).append(","));
                return showExist.delete(showExist.length() - 1, showExist.length()).toString();
            }
        }
        return "";
    }
}
