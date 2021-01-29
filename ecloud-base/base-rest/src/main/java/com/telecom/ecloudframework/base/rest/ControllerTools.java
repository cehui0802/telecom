package com.telecom.ecloudframework.base.rest;

import com.telecom.ecloudframework.base.api.query.Direction;
import com.telecom.ecloudframework.base.api.query.FieldSort;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.RequestContext;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultFieldSort;
import com.telecom.ecloudframework.base.db.model.query.DefaultPage;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ControllerTools {
    protected Logger LOG = LoggerFactory.getLogger(getClass());

    protected <T> ResultMsg<T> getSuccessResult(T data, String msg) {
        ResultMsg<T> resultMsg = new ResultMsg<T>();
        resultMsg.setOk(true);
        resultMsg.setMsg(msg);
        resultMsg.setData(data);
        return resultMsg;
    }

    protected <T> ResultMsg<T> getSuccessResult(T data) {
        ResultMsg<T> resultMsg = new ResultMsg<T>();
        resultMsg.setOk(true);
        resultMsg.setData(data);
        return resultMsg;
    }


    protected ResultMsg<String> getSuccessResult(String msg) {
        ResultMsg<String> resultMsg = new ResultMsg<String>();
        resultMsg.setOk(true);
        resultMsg.setMsg(msg);
        resultMsg.setData(msg);
        return resultMsg;
    }

    protected ResultMsg<String> getWarnResult(String msg) {
        ResultMsg<String> resultMsg = new ResultMsg<String>();
        resultMsg.setOk(false);
        resultMsg.setMsg(msg);
        return resultMsg;
    }

    protected ResultMsg<String> getSuccessResult() {
        ResultMsg<String> resultMsg = new ResultMsg<String>();
        resultMsg.setOk(true);
        resultMsg.setMsg("操作成功");
        return resultMsg;
    }


    public QueryFilter getCommonQueryFilter(HttpServletRequest request, DefaultQueryFilter queryFilter) {
        try {
            String offset = request.getParameter("offset");
            String limit = request.getParameter("limit");
            if (StringUtil.isNotEmpty(offset) && StringUtil.isNotEmpty(limit)) {
                RowBounds rowBounds = new RowBounds(Integer.parseInt(offset), Integer.parseInt(limit));
                DefaultPage page = new DefaultPage(rowBounds);
                queryFilter.setPage(page);
            }
            String noPage = request.getParameter("noPage");
            if ("true".equals(noPage)) {
                queryFilter.setPage(null);
            }


            // 设置排序字段
            String sort = request.getParameter("sort");
            String order = request.getParameter("order");
            if (StringUtil.isNotEmpty(sort) && StringUtil.isNotEmpty(order)) {
                List<FieldSort> fieldSorts = new ArrayList<FieldSort>();
                fieldSorts.add(new DefaultFieldSort(sort, Direction.fromString(order)));
                queryFilter.setFieldSortList(fieldSorts);
            }
        } catch (Exception e) {

        }
        return queryFilter;
    }


    /**
     * 返回构建的QueryFilter
     *
     * @param request
     * @return QueryFilter
     * @throws
     * @since 1.0.0
     */
    protected QueryFilter getQueryFilter(HttpServletRequest request) {
        if(request == null){
            LOG.error("获取request失败。");
            return null ;
        }
        String noPage = request.getParameter("noPage");
        DefaultQueryFilter queryFilter;
        if (StringUtils.isNotEmpty(noPage)) {
            queryFilter = new DefaultQueryFilter(true);
        } else {
            queryFilter = new DefaultQueryFilter();
        }
        try {
            RequestUtil.handleRequestParam(request, queryFilter);
            getCommonQueryFilter(request, queryFilter);
        } catch (Exception e) {
        }
        return queryFilter;
    }

    protected QueryFilter getQueryFilter() {
       return getQueryFilter(RequestContext.getHttpServletRequest());
    }

}