package com.telecom.ecloudframework.base.db.model.query;

import com.telecom.ecloudframework.base.api.query.QueryField;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import cn.hutool.core.collection.CollectionUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 默认条件接口实现类。
 *
 * @author
 */
public class DefaultQueryField implements QueryField {
    /**
     * 字段
     */
    private String field;
    /**
     * 比较符
     */
    private QueryOP compare;
    /**
     * 比较值
     */
    private Object value;

    public DefaultQueryField() {
    }

    public DefaultQueryField(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public DefaultQueryField(String field, QueryOP compare, Object value) {
        this.value = value;
        this.field = field;
        this.compare = compare;

        if (QueryOP.IN == compare || QueryOP.NOT_IN == compare) {
            this.value = getInValueSql();
        }
    }

    /**
     * 针对in查询方式，根据传入的value的类型做不同的处理。 value 是 string，则分隔字符串，然后合并为符合in规范的字符串。
     * value 是 list，则直接合并为符合in规范的字符串
     *
     * @return
     */
    private String getInValueSql() {
        List listValue = null;
        if (value instanceof String) {
            listValue = Arrays.asList(((String) value).split(","));
        }
        if (value instanceof List) {
            listValue = (List) value;
        }

        if (CollectionUtil.isEmpty(listValue)) {
            return "";
        }

        StringBuilder inSqlStr = new StringBuilder("(");
        for (Object obj : listValue) {
            if (obj == null || "".equals(obj)) {
                continue;
            }
            inSqlStr.append("'");
            inSqlStr.append(obj.toString());
            inSqlStr.append("'");
            inSqlStr.append(",");
        }
        inSqlStr = new StringBuilder(inSqlStr.substring(0, inSqlStr.length() - 1));
        inSqlStr.append(")");
        return inSqlStr.toString();
    }

    @Override
    public String getField() {
        return field;
    }

    public int getValueHashCode(){
        if (value == null){
            return 0;
        }
        int hashCode = value.hashCode();
        if (hashCode < 0){
            return -hashCode;
        }
        return hashCode;
    }
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public QueryOP getCompare() {
        return compare;
    }

    public void setCompare(QueryOP compare) {
        this.compare = compare;
    }

    @Override
    public String getSql() {
        if (compare == null) {
            compare = QueryOP.EQUAL;
        }
        String fieldParam;
//        // OR 关系运算符
//        if (field.contains("~")) {
//            return getOrSql();
//        }
        fieldParam = "#{%s}";
        String sql = field;
        if (sql.lastIndexOf("^") != -1) {
            sql = sql.substring(0, sql.lastIndexOf("^"));
        }
        switch (compare) {
            case EQUAL:
                sql += " = " + fieldParam;
                break;
            case EQUAL_IGNORE_CASE:
                sql = "upper(" + sql + ") = " + fieldParam;
                break;
            case LESS:
                sql += " < " + fieldParam;
                break;
            case LESS_EQUAL:
                sql += " <= " + fieldParam;
                break;
            case GREAT:
                sql += " > " + fieldParam;
                break;
            case GREAT_EQUAL:
                sql += " >= " + fieldParam;
                break;
            case NOT_EQUAL:
                sql += " != " + fieldParam;
                break;
            case LEFT_LIKE:
                this.setValue("%" + this.value);
                sql += " like " + fieldParam;
                break;
            case RIGHT_LIKE:
                this.setValue(this.value + "%");
                sql += " like  " + fieldParam;
                break;
            case LIKE:
                this.setValue("%" + this.value + "%");
                sql += " like  " + fieldParam;
                break;
            case NOT_LIKE:
                this.setValue("%" + this.value + "%");
                sql += " not like  " + fieldParam;
                break;
            case IS_NULL:
                sql += " is null";
                break;
            case NOTNULL:
                sql += " is not null";
                break;
            case IN:
                sql += " in  " + this.value;
                break;
            case NOT_IN:
                sql += " not in  " + this.value;
                break;
            case BETWEEN:
                if (field.endsWith("-end")) {
                    sql = field.substring(0, field.length() - 4) + " <= " + fieldParam;
                } else {
                    sql += " >= " + fieldParam;
                }
                break;
            default:
                sql += " =  " + fieldParam;
                break;
        }
        if (field.contains(".")) {
            fieldParam =  field.replace(".", "") + "_" + getValueHashCode() ;
        } else {
            fieldParam = field + "_" + getValueHashCode() ;
        }
        return String.format(sql,fieldParam);
    }

    private String getOrSql() {
        if (field.endsWith("~")) {
            field = field.substring(0, field.length() - 1);
        }
        String[] fields = field.split("~");
        StringBuilder fieldParam = new StringBuilder("#{");
        for (String field : fields) {
            if (field.contains(".")) {
                // -- 修改 去掉别名会导致同名参数间覆盖
                fieldParam.append(field.replace(".", "")).append("~");
            } else {
                fieldParam.append(field).append("~");
            }
        }
        fieldParam = new StringBuilder(fieldParam.substring(0, fieldParam.length() - 1) + "_" + getValueHashCode() + "}");

        StringBuilder sql = new StringBuilder("(");
        for (String field : fields) {
            if (StringUtil.isEmpty(field)) {
                continue;
            }
            if (field.contains("^")) {
                field = field.substring(0, field.lastIndexOf("^"));
            }
        }
        switch (compare) {
            case EQUAL:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" = ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" = ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case EQUAL_IGNORE_CASE:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append("upper(").append(fields[i]).append(") = ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append("upper(").append(fields[i]).append(") = ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case LESS:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" < ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" < ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case LESS_EQUAL:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" <= ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" <= ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case GREAT:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" > ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" > ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case GREAT_EQUAL:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" >= ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" >= ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case NOT_EQUAL:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" != ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" != ").append(fieldParam).append(" OR ");
                    }
                }
                break;
            case LEFT_LIKE:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" OR ");
                    }
                }
                this.setValue("%" + this.value);
                break;
            case RIGHT_LIKE:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" OR ");
                    }
                }
                this.setValue(this.value + "%");
                break;
            case LIKE:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" like ").append(fieldParam).append(" OR ");
                    }
                }
                this.setValue("%" + this.value + "%");
                break;
            case IS_NULL:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" is null )");
                    } else {
                        sql.append(fields[i]).append(" is null OR ");
                    }
                }
                break;
            case IN:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" in ").append(this.value).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" in ").append(this.value).append(" OR ");
                    }
                }
                break;
            case NOT_IN:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" not in ").append(this.value).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" not in ").append(this.value).append(" OR ");
                    }
                }
                break;
            default:
                for (int i = 0; i < fields.length; i++) {
                    if (i == fields.length - 1) {
                        sql.append(fields[i]).append(" = ").append(fieldParam).append(" ) ");
                    } else {
                        sql.append(fields[i]).append(" = ").append(fieldParam).append(" OR ");
                    }
                }
        }
        return sql.toString();
    }

}