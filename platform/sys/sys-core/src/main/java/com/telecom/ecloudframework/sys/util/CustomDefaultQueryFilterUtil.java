package com.telecom.ecloudframework.sys.util;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.db.model.query.DefaultPage;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import org.apache.ibatis.session.RowBounds;


public class CustomDefaultQueryFilterUtil {


    /**
     * 修改默认查询过滤 查询记录全部返回
     * @return
     */
    public static QueryFilter setDefaultQueryFilter(){
        QueryFilter filter = new DefaultQueryFilter();
        DefaultPage page = new DefaultPage(new RowBounds());
        filter.setPage(page);
        return filter;
    }
}
