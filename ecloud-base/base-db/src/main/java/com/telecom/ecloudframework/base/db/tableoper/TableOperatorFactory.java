package com.telecom.ecloudframework.base.db.tableoper;

import org.springframework.jdbc.core.JdbcTemplate;

import com.telecom.ecloudframework.base.api.constant.DbType;
import com.telecom.ecloudframework.base.db.model.table.Table;

/**
 * TableOperator的工厂类
 *
 * @author aschs
 */
public class TableOperatorFactory {
	private TableOperatorFactory() {

	}

	/**
	 * <pre>
	 * 构建一个TableOperator
	 * </pre>
	 *
	 * @param type
	 * @param table
	 * @param jdbcTemplate
	 * @return
	 */
	public static TableOperator newOperator(String type, Table<?> table, JdbcTemplate jdbcTemplate) {
		if (DbType.MYSQL.equalsWithKey(type)) {
			return new MysqlTableOperator(table, jdbcTemplate);
		}
		if (DbType.ORACLE.equalsWithKey(type)) {
			return new OracleTableOperator(table, jdbcTemplate);
		}
		if (DbType.DMSQL.equalsWithKey(type)){
			return new DmsqlTableOperator(table, jdbcTemplate);
		}
		if (DbType.DRDS.equalsWithKey(type)){
			return new DrdsTableOperator(table, jdbcTemplate);
		}
		if (DbType.KINGBASE.equalsWithKey(type)){
			return new KingBaseTableOperator(table, jdbcTemplate);
		}
		throw new RuntimeException("找不到类型[" + type + "]的数据库处理者(TableOperator)");
	}

}
