package com.telecom.ecloudframework.base.db.model.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.telecom.ecloudframework.base.api.Page;
import com.telecom.ecloudframework.base.api.query.Direction;
import com.telecom.ecloudframework.base.api.query.FieldLogic;
import com.telecom.ecloudframework.base.api.query.FieldSort;
import com.telecom.ecloudframework.base.api.query.QueryField;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.query.WhereClause;

/**
 * @author lxy
 */
public class DefaultQueryFilter implements QueryFilter {
    /**
     * 分页组件
     */
    private Page page = new DefaultPage();
    /**
     * 排序字段
     */
    private List<FieldSort> fieldSortList = new ArrayList<>();
    /**
     * 字段参数构建列表
     */
    private Map<String, Object> params = new LinkedHashMap<>();
    /**
     * 字段参数组合关系列表
     */
    private FieldLogic fieldLogic = new DefaultFieldLogic();

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public Map<String, Object> getParams() {
        initParams(this.fieldLogic);
        return params;
    }

    public DefaultQueryFilter() {
    }

    public DefaultQueryFilter(boolean noPage) {
        if (noPage) {
            this.setPage(null);
        }
    }

    public DefaultQueryFilter(FieldLogic fieldLogic) {
        this.fieldLogic = fieldLogic;
    }

    @Override
    public FieldLogic getFieldLogic() {
        return fieldLogic;
    }

    public void setFieldLogic(FieldLogic fieldLogic) {
        this.fieldLogic = fieldLogic;
    }

    /**
     * <pre>
     * 初始化参数
     * </pre>
     *
     * @param fedLog
     */
    private void initParams(FieldLogic fedLog) {
        List<WhereClause> wcs = fedLog.getWhereClauses();
        for (WhereClause wc : wcs) {
            if (wc instanceof QueryField) {
                QueryField qf = (QueryField) wc;
                if (qf.getCompare() == QueryOP.IS_NULL || qf.getCompare() == QueryOP.NOTNULL) {
                    continue;
                }

                String fn = qf.getField();
                // 如果查询字段包含数据库别名，参数设置去掉别名
                if (fn.contains(".")) {
                    // -- 修改 去掉别名会导致同名参数间覆盖
                    if (fn.contains("~")) {
                        String[] fields = fn.split("~");
                        StringBuilder fieldParam = new StringBuilder();
                        for (String field : fields) {
                            if (field.contains(".")) {
                                fieldParam.append(field.replace(".", "")).append("~");
                            } else {
                                fieldParam.append(field).append("~");
                            }
                        }
                        fn = fieldParam.substring(0, fieldParam.length() - 1);
                    } else {
                        fn = fn.replace(".", "");
                    }
                }
//                this.params.put(fn, qf.getValue());
                this.params.put(fn + "_" + qf.getValueHashCode(), qf.getValue());
            } else if (wc instanceof FieldLogic) {
                FieldLogic fl = (FieldLogic) wc;
                initParams(fl);
            }
        }
    }

    @Override
    public List<FieldSort> getFieldSortList() {
        return fieldSortList;
    }

    public void setFieldSortList(List<FieldSort> fieldSortList) {
        this.fieldSortList = fieldSortList;
    }

    /**
     * 添加排序配置。
     *
     * @param orderField 排序字段
     * @param orderSeq   排序
     */
    @Override
    public void addFieldSort(String orderField, String orderSeq) {
        fieldSortList.add(new DefaultFieldSort(orderField, Direction.fromString(orderSeq)));
    }

    @Override
    public void addFilter(String name, Object obj, QueryOP queryType) {
        fieldLogic.getWhereClauses().add(new DefaultQueryField(name, queryType, obj));
    }

    @Override
    public void addParamsFilter(String key, Object obj) {
        this.params.put(key, obj);
    }

    @Override
    public void addParams(Map<String, Object> params) {
        this.params.putAll(params);
    }

    @Override
    public String getWhereSql() {
        String dynamicWhereSql = this.getFieldLogic().getSql();

        String defaultWhere = params.containsKey("defaultWhere") ? params.get("defaultWhere").toString() : "";
        if (StringUtils.isNotEmpty(defaultWhere)) {
            dynamicWhereSql = StringUtils.isNotEmpty(dynamicWhereSql) ? dynamicWhereSql + " and " + defaultWhere : defaultWhere;
        }
        return dynamicWhereSql;
    }

    @Override
    public String getOrderBySql() {
        if (this.getFieldSortList().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (FieldSort fieldSort : this.getFieldSortList()) {
                sb.append(fieldSort.getField()).append(" ").append(fieldSort.getDirection()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }
}
