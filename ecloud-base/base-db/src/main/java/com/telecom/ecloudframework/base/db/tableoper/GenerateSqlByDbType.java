package com.telecom.ecloudframework.base.db.tableoper;

import com.telecom.ecloudframework.base.db.model.table.Table;

import java.util.Map;

public interface GenerateSqlByDbType {
    boolean getCreateTableSql(StringBuilder sql, Table table);

    boolean getUpdateTableSql(StringBuilder sql, Table table);

    boolean checkUpdateTableColumn(Map<String, Object> columns, Table table);
}
