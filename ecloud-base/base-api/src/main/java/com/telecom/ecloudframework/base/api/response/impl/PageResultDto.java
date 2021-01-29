package com.telecom.ecloudframework.base.api.response.impl;

import java.util.List;

/**
 * <pre>
 * 描述：分页结果
 * </pre>
 *
 * @author 谢石
 * @date 2020-9-24
 */
public class PageResultDto<T> extends BaseResult {
    private static final long serialVersionUID = 1L;
    /**
     * 分页大小
     */
    private Integer pageSize = 0;
    /**
     * 当前页
     */
    private Integer page = 1;
    /**
     * 总条数
     */
    private Integer total = 0;

    /**
     * 分页列表数据
     */
    private List rows = null;

    public PageResultDto() {

    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
