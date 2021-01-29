package com.telecom.ecloudframework.base.db.tableoper;

import com.telecom.ecloudframework.base.api.constant.ColumnType;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.api.constant.DbType;
import com.telecom.ecloudframework.base.db.model.table.Column;
import com.telecom.ecloudframework.base.db.model.table.Table;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 达梦数据库表操作类
 *
 * @author 谢石
 * @date 2020-11-12
 */
public class DmsqlTableOperator extends TableOperator {

    /**
     * @param table
     * @param jdbcTemplate
     */
    public DmsqlTableOperator(Table<? extends Column> table, JdbcTemplate jdbcTemplate) {
        super(table, jdbcTemplate);
    }

    @Override
    public String type() {
        return DbType.DMSQL.getKey();
    }

    @Override
    public void createTable() {
        if (isTableCreated()) {
            logger.debug("表[" + table.getName().toUpperCase() + "(" + table.getComment() + ")]已存在数据库中，无需再次生成");
            return;
        }

        // 建表语句
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE \"").append(table.getName().toUpperCase()).append("\" (").append("\n");
        for (Column column : table.getColumns()) {
            sql.append(columnToSql(column)).append(",\n");
        }
        sql.append("NOT CLUSTER PRIMARY KEY (\"").append(table.getPkColumn().getName().toUpperCase()).append("\")").append("\n)");
        // 建表结束
        jdbcTemplate.execute(sql.toString());

        // 开始处理注释
        if (StringUtil.isNotEmpty(table.getComment())) {
            String str = "COMMENT ON TABLE \"" + table.getName().toUpperCase() + "\" IS '" + table.getComment() + "'";
            // 表注解
            jdbcTemplate.execute(str);
        }

        // 字段注解
        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            updateComment(column);
        }
    }

    @Override
    public boolean isTableCreated() {
        String sql = "select count(1) from user_tables t where table_name =?";
        return jdbcTemplate.queryForObject(sql, Integer.class, table.getName().toUpperCase()) > 0;
    }

    @Override
    public void addColumn(Column column) {
        String sql = "ALTER TABLE \"" + table.getName().toUpperCase() + "\"" + " ADD ( " + columnToSql(column) + " )";
        jdbcTemplate.execute(sql);
        updateComment(column);
    }

    @Override
    public void updateColumn(Column column) {
        String sql = "ALTER TABLE \"" + table.getName().toUpperCase() + "\"" + " MODIFY( " + columnToSql(column) + " )";
        jdbcTemplate.execute(sql);
        updateComment(column);

    }

    @Override
    public void dropColumn(String columnName) {
        String sql = "ALTER TABLE \"" + table.getName().toUpperCase() + "\"" + " DROP(\"" + columnName + "\")";
        jdbcTemplate.execute(sql);
    }

    /**
     * <pre>
     * 把column解析成Sql
     * </pre>
     *
     * @param column
     * @return
     */
    private String columnToSql(Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(column.getName().toUpperCase()).append("\"");
        if (ColumnType.CLOB.equalsWithKey(column.getType())) {
            sb.append(" CLOB");
        } else if (ColumnType.DATE.equalsWithKey(column.getType())) {
            sb.append(" TIMESTAMP");
        } else if (ColumnType.NUMBER.equalsWithKey(column.getType())) {
            sb.append(" NUMBER(").append(column.getLength()).append(",").append(column.getDecimal()).append(")");
        } else if (ColumnType.VARCHAR.equalsWithKey(column.getType())) {
            sb.append(" VARCHAR2(").append(column.getLength()).append(")");
        }

        if (column.isRequired() || column.isPrimary()) {
            sb.append(" NOT NULL");
        } else {
            sb.append(" NULL");
        }
        return sb.toString();
    }

    /**
     * 更新字段注释
     *
     * @param column
     */
    private void updateComment(Column column) {
        if (!StringUtil.isEmpty(column.getComment())) {
            String str = "COMMENT ON COLUMN \"" + table.getName().toUpperCase() + "\".\"" + column.getName().toUpperCase() + "\"  IS '" + column.getComment() + "'";
            jdbcTemplate.execute(str);
        }
    }
}
