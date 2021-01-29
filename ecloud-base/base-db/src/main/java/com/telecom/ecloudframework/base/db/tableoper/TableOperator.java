package com.telecom.ecloudframework.base.db.tableoper;

import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.db.dboper.DbOperator;
import com.telecom.ecloudframework.base.db.dboper.DbOperatorFactory;
import com.telecom.ecloudframework.base.db.model.table.Column;
import com.telecom.ecloudframework.base.db.model.table.Table;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.Map.Entry;

/**
 * <pre>
 * 针对一张表的操作者，系统表的操作有以下几种
 * 1 针对表本身的：建表，删表
 * 2 针对字段：增字段，改字段，删字段
 * 3 针对数据：增，删，查，改
 * </pre>
 *
 * @author aschs
 */
public abstract class TableOperator {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonColumn commonColumn;
	/**
	 * 表对象
	 */
	protected Table<? extends Column> table;
	/**
	 * jdbc
	 */
	protected JdbcTemplate jdbcTemplate;

	/**
	 * @param table
	 * @param jdbcTemplate
	 */
	public TableOperator(Table<? extends Column> table, JdbcTemplate jdbcTemplate) {
		super();
		this.table = table;
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * <pre>
	 * 返回的数据库类型
	 * 枚举：DbType
	 * </pre>
	 *
	 * @return
	 */
	public abstract String type();

	/**
	 * <pre>
	 * 创建表
	 * </pre>
	 */
	public void createTable() {
	}

	/**
	 * <pre>
	 * 删除表
	 * </pre>
	 */
	public void dropTable() {
		if (!isTableCreated()) {
			return;
		}
		String sql = "drop table " + table.getName() + "";
		jdbcTemplate.execute(sql);
	}

	/**
	 * <pre>
	 * 表是否被生成过
	 * 或者说，表是否已存在数据库
	 * </pre>
	 *
	 * @return
	 */
	public boolean isTableCreated() {
		return false;
	}

	/**
	 * <pre>
	 * 增加字段
	 * </pre>
	 *
	 * @param column
	 *            字段
	 */
	public void addColumn(Column column) {

	}

	/**
	 * <pre>
	 * 更新字段
	 * </pre>
	 *
	 * @param column
	 *            字段
	 */
	public void updateColumn(Column column) {

	}

	/**
	 * <pre>
	 * 删除字段
	 * </pre>
	 *
	 */
	public void dropColumn(String columnName) {

	}

	/**
	 * <pre>
	 * 插入数据
	 * </pre>
	 *
	 * @param data
	 *            数据
	 */
	public void insertData(Map<String, Object> data) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + table.getName());
		StringBuilder columnNameSql = new StringBuilder();// 字段名字sql
		StringBuilder paramNameSql = new StringBuilder();// 参数sql
		List<Object> param = new ArrayList<>();// 参数
		//增加指定的公共字段
		addCreateColumn(data);
		for (Entry<String, Object> entry : data.entrySet()) {
			if (columnNameSql.length() > 0) {
				columnNameSql.append(",");
				paramNameSql.append(",");
			}
			columnNameSql.append(entry.getKey());
			paramNameSql.append("?");
			param.add(entry.getValue());
		}
		sql.append("(" + columnNameSql + ") VALUES(" + paramNameSql + ")");
		jdbcTemplate.update(sql.toString(), param.toArray());
	}

	/**
	 * 填充指定创建人创建时间字段
	 * @param data
	 */
	private void addCreateColumn(Map<String, Object> data){
		if (null == commonColumn){
			commonColumn = AppUtil.getBean(CommonColumn.class);
		}
		if (data.containsKey(commonColumn.getCreateUser().toUpperCase())
				&& table.getColumn(commonColumn.getCreateUser().toUpperCase()) != null){
			data.put(commonColumn.getCreateUser().toUpperCase(),ContextUtil.getCurrentUserId());
		}else if (data.containsKey(commonColumn.getCreateUser())
				&& table.getColumn(commonColumn.getCreateUser()) != null){
			data.put(commonColumn.getCreateUser(),ContextUtil.getCurrentUserId());
		}else if (table.getColumn(commonColumn.getCreateUser()) != null){
			data.put(commonColumn.getCreateUser(),ContextUtil.getCurrentUserId());
		}
		if (data.containsKey(commonColumn.getCreateTime().toUpperCase())
				&& table.getColumn(commonColumn.getCreateTime().toUpperCase()) != null){
			data.put(commonColumn.getCreateTime().toUpperCase(),new Date());
		}else if (data.containsKey(commonColumn.getCreateTime())
				&& table.getColumn(commonColumn.getCreateTime()) != null){
			data.put(commonColumn.getCreateTime(),new Date());
		}else if (table.getColumn(commonColumn.getCreateTime()) != null){
			data.put(commonColumn.getCreateTime(), new Date());
		}
	}

	/**
	 * 填充指定修改人、修改时间字段
	 * @param data
	 */
	private void addUpdateColumn(Map<String, Object> data){
		if (null == commonColumn){
			commonColumn = AppUtil.getBean(CommonColumn.class);
		}
		if (data.containsKey(commonColumn.getUpdateUser().toUpperCase())
				&& table.getColumn(commonColumn.getUpdateUser().toUpperCase()) != null){
			data.put(commonColumn.getUpdateUser().toUpperCase(),ContextUtil.getCurrentUserId());
		}else if (data.containsKey(commonColumn.getUpdateUser())
				&& table.getColumn(commonColumn.getUpdateUser()) != null){
			data.put(commonColumn.getUpdateUser(),ContextUtil.getCurrentUserId());
		}else if (table.getColumn(commonColumn.getUpdateUser()) != null){
			data.put(commonColumn.getUpdateUser(),ContextUtil.getCurrentUserId());
		}
		if (data.containsKey(commonColumn.getUpdateTime().toUpperCase())
				&& table.getColumn(commonColumn.getUpdateTime().toUpperCase()) != null){
			data.put(commonColumn.getUpdateTime().toUpperCase(),new Date());
		}else if (data.containsKey(commonColumn.getUpdateTime())
				&& table.getColumn(commonColumn.getUpdateTime()) != null){
			data.put(commonColumn.getUpdateTime(),new Date());
		}else if (table.getColumn(commonColumn.getUpdateTime()) != null){
			data.put(commonColumn.getUpdateTime(), new Date());
		}
	}


	/**
	 * <pre>
	 * 根据主键删除数据
	 * </pre>
	 *
	 * @param id
	 *            主键值
	 */
	public void deleteData(Object id) {
		String sql = "DELETE FROM " + table.getName() + " where " + table.getPkColumn().getName() + " = ?";
		jdbcTemplate.update(sql, id);
	}

	/**
	 * <pre>
	 * 根据参数删除数据
	 * </pre>
	 *
	 * @param param
	 *            参数
	 */
	public void deleteData(Map<String, Object> param) {
		if (param.isEmpty()) {
			throw new RuntimeException("操作删除表[" + table.getComment() + "(" + table.getName() + ")]时，条件参数为空(会导致全表数据清空)");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM " + table.getName() + " where ");
		List<Object> paramList = new ArrayList<>();// 参数
		for (Entry<String, Object> entry : param.entrySet()) {
			if (sql.toString().endsWith("?")) {
				sql.append(" and ");
			}
			sql.append(entry.getKey() + " = ?");
			paramList.add(entry.getValue());
		}
		jdbcTemplate.update(sql.toString(), paramList.toArray());
	}

	/**
	 * <pre>
	 * 更新数据
	 * 获取数据中的主键来进行更新
	 * </pre>
	 *
	 * @param data
	 *            数据
	 */
	public void updateData(Map<String, Object> data) {
		Object id = data.get(table.getPkColumn().getName());
		if (id == null) {
			throw new RuntimeException("操作更新表[" + table.getComment() + "(" + table.getName() + ")]时，参数中有没主键[" + table.getPkColumn().getComment() + "(" + table.getPkColumn().getName() + ")]");
		}
		//填充修改人、修改时间字段
		addUpdateColumn(data);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE " + table.getName() + " SET ");
		List<Object> param = new ArrayList<>();// 参数

		for (Entry<String, Object> entry : data.entrySet()) {
			// 主键跳过
			if (entry.getKey().equals(table.getPkColumn().getName())) {
				continue;
			}
			if (sql.toString().endsWith("?")) {
				sql.append(" , ");
			}
			param.add(entry.getValue());
			sql.append(entry.getKey() + " = ?");
		}
		if (param.size() == 0){// 因权限问题导致当前用户没有更改任何字段权限
			return;
		}
		// 主键设置为过滤条件
		sql.append(" WHERE " +  table.getPkColumn().getName() + " = ?");
		param.add(id);
		jdbcTemplate.update(sql.toString(), param.toArray());
	}

	/**
	 * <pre>
	 * 根据主键获取唯一数据
	 * </pre>
	 *
	 * @param columnName
	 *            要查的字段
	 * @param id
	 *            主键值
	 * @return
	 */
	public Map<String, Object> selectData(List<String> columnName, Object id) {
		Map<String, Object> param = new HashMap<>();
		param.put(table.getPkColumn().getName(), id);
		List<Map<String, Object>> list = selectData(columnName, param);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * <pre>
	 * 根据主键获取唯一数据
	 * </pre>
	 *
	 * @param id
	 *            主键值
	 * @return
	 */
	public Map<String, Object> selectData(Object id) {
		Map<String, Object> param = new HashMap<>();
		param.put(table.getPkColumn().getName(), id);
		List<Map<String, Object>> list = selectData(param);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * <pre>
	 * </pre>
	 *
	 * @param param
	 *            参数
	 * @return
	 */
	public List<Map<String, Object>> selectData(Map<String, Object> param) {
		return selectData(null, param);
	}

	/**
	 * <pre>
	 * </pre>
	 *
	 * @param columnName
	 *            要查的字段
	 * @param param
	 *            参数列表
	 * @return
	 */
	public List<Map<String, Object>> selectData(List<String> columnName, Map<String, Object> param) {
		StringBuilder sql = new StringBuilder();
		if (CollectionUtil.isEmpty(columnName)) {
			sql.append("SELECT * FROM " + table.getName());
		} else {
			sql.append("SELECT");
			for (String cn : columnName) {
				if (!sql.toString().endsWith("SELECT")) {
					sql.append(",");
				}
				sql.append(" " + cn);
			}
			sql.append(" FROM " + table.getName());
		}

		sql.append(" WHERE ");

		List<Object> paramList = new ArrayList<>();// 参数
		for (Entry<String, Object> entry : param.entrySet()) {
			if (sql.toString().endsWith("?")) {
				sql.append(" and ");
			}
			sql.append(entry.getKey() + " = ?");
			paramList.add(entry.getValue());
		}

		return jdbcTemplate.queryForList(sql.toString(), paramList.toArray());
	}

	/**
	 * <pre>
	 * 同步table和数据库中的字段信息
	 * 目前只处理字段的增删
	 * </pre>
	 */
	public void syncColumn() {
		// 未生成表，不处理
		if (!isTableCreated()) {
			return;
		}
		Set<String> dbColumnNames = new HashSet<>();// 数据库中存在的字段名
		Table<Column> dbTable = getDbTable();
		for (Column c : dbTable.getColumns()) {
			dbColumnNames.add(c.getName());
		}

		for (String columnName : dbColumnNames) {
			if (this.table.getColumn(columnName) == null) {// 数据库表内有，但是结构没有，删除
				dropColumn(columnName);
			}
		}

		for (Column column : table.getColumns()) {
			boolean exits = false;
			for (String columnName : dbColumnNames) {
				if (columnName.equalsIgnoreCase(column.getName())) {
					exits = true;
					break;
				}
			}
			if (!exits) {// 结构有，数据库表内没有，增加
				addColumn(column);
			} else if (!dbTable.getColumn(column.getName()).equals(column)) {
				updateColumn(column);// 更新一遍结构
			}
		}
	}

	public Table<Column> getDbTable() {
		DbOperator dbOperator = DbOperatorFactory.newOperator(type(), jdbcTemplate);
		return dbOperator.getTable(table.getName());
	}
	/**
	 * <pre>
	 * 为字段保存索引
	 * 如果字段索引已存在，则不会创建
	 * </pre>
	 *
	 * @param columnNames
	 */
	public void saveIndex(List<String> columnNames) {
		if (columnNames.isEmpty()) {
			return;
		}

		/**
		 * 1 先判断是不是已经有了对应索引
		 */
		Map<String, String> indexMap = this.selectIndex();

		if (indexMap.containsValue(String.join(",", columnNames))) {
			logger.info("表【{}】已存在字段{}的索引", table.getName(), columnNames);
			return;
		}

		/**
		 * 2 建立索引
		 */
		this.createIndex(columnNames);
	}

	/**
	 * <pre>
	 * 查找表的索引
	 * </pre>
	 *
//	 * @param columnNames
	 * @return Map<索引名字,字段> eg:Map<"index_1","a,b,c,...">
	 */
	public Map<String, String> selectIndex() {
		logger.info("请实现【{}】数据库类型的查找索引方法（selectIndex）", type());
		return new HashMap<>();
	}

	/**
	 * <pre>
	 * 为字段创建索引
	 * 命名规则为：表名_index_字段1_字段2
	 * </pre>
	 *
	 * @param columnNames
	 */
	public void createIndex(List<String> columnNames) {
		logger.info("请实现【{}】数据库类型的创建索引方法（createIndex）", type());
	}

}
