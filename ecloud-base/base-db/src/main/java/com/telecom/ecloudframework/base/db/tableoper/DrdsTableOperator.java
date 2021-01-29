package com.telecom.ecloudframework.base.db.tableoper;

import com.telecom.ecloudframework.base.api.constant.ColumnType;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.api.constant.DbType;
import com.telecom.ecloudframework.base.db.model.table.Column;
import com.telecom.ecloudframework.base.db.model.table.Table;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * <pre>
 * mysql的实现类
 * </pre>
 *
 * @author aschs
 */
public class DrdsTableOperator extends TableOperator {

	/**
	 * @param table
	 * @param jdbcTemplate
	 */
	public DrdsTableOperator(Table<? extends Column> table, JdbcTemplate jdbcTemplate) {
		super(table, jdbcTemplate);
	}

	@Override
	public String type() {
		return DbType.DRDS.getKey();
	}

	@Override
	public void createTable() {
		if (isTableCreated()) {
			logger.debug("表[" + table.getName() + "(" + table.getComment() + ")]已存在数据库中，无需再次生成");
			return;
		}

		// 建表语句
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + table.getName() + " (" + "\n");
		for (Column column : table.getColumns()) {
			sql.append(columnToSql(column) + ",\n");
		}
		sql.append("PRIMARY KEY (" + table.getPkColumn().getName() + ")" + "\n)");
		//默认设置字符集
		sql.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin ");
		if (StringUtil.isNotEmpty(table.getComment())) {
			sql.append(" COMMENT='" + table.getComment() + "'");
		}
		List<GenerateSqlByDbType> generateSqlByDbTypes = AppUtil.getImplInstanceArray(GenerateSqlByDbType.class);
		for (GenerateSqlByDbType generateSqlByDbType : generateSqlByDbTypes){
			if (generateSqlByDbType.getCreateTableSql(sql,table)){
				break;
			}
		}
		// 建表结束
		sql.append(";");
		jdbcTemplate.execute(sql.toString());
	}

	@Override
	public boolean isTableCreated() {
		String sql = "select count(1) from information_schema.TABLES t where table_name =?";
		return jdbcTemplate.queryForObject(sql, Integer.class, table.getName()) > 0 ? true : false;
	}

	@Override
	public void addColumn(Column column) {
		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE " + table.getName() + "");
		sql.append(" ADD COLUMN " + columnToSql(column) + ";");
		jdbcTemplate.execute(sql.toString());
	}

	@Override
	public void updateColumn(Column column) {
		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE " + table.getName() + "");
		sql.append(" MODIFY COLUMN " + columnToSql(column) + ";");
		jdbcTemplate.execute(sql.toString());
	}

	@Override
	public void dropColumn(String columnName) {
		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE " + table.getName() + "");
		sql.append(" DROP COLUMN " + columnName + ";");
		jdbcTemplate.execute(sql.toString());
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
		sb.append("" + column.getName() + "");
		if (ColumnType.CLOB.equalsWithKey(column.getType())) {
			sb.append(" text");
		} else if (ColumnType.DATE.equalsWithKey(column.getType())) {
			sb.append(" datetime");
		} else if (ColumnType.NUMBER.equalsWithKey(column.getType())) {
			sb.append(" decimal(" + column.getLength() + "," + column.getDecimal() + ")");
		} else if (ColumnType.VARCHAR.equalsWithKey(column.getType())) {
			sb.append(" varchar(" + column.getLength() + ")");
		}

		if (column.isRequired() || column.isPrimary()) {
			sb.append(" NOT NULL");
		} else {
			sb.append(" NULL");
		}
		if (StringUtil.isNotEmpty(column.getDefaultValue())) {
			//sb.append(" DEFAULT " + column.getDefaultValue() + "");
		}
		sb.append(" COMMENT '" + column.getComment() + "'");
		return sb.toString();
	}
}
