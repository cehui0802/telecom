package com.telecom.ecloudframework.base.dao.baseinterceptor;

import com.telecom.ecloudframework.base.api.context.ICurrentContext;
import com.telecom.ecloudframework.base.api.model.CreateInfoModel;
import com.telecom.ecloudframework.base.api.model.IDModel;
import com.telecom.ecloudframework.base.api.model.IGBaseModel;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.model.BaseModel;
import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * 更新设置更新人
 *
 * @author Jeff
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SaveInterceptor implements Interceptor {
    @Autowired
    ICurrentContext currentContext;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (ArrayUtil.isEmpty(args) || args.length < 2) {
            return invocation.proceed();
        }

        Object param = args[1];
        MappedStatement statement = (MappedStatement) args[0];
        String currentUserId = currentContext.getCurrentUserId();

        // 更新逻辑
        if (statement.getId().endsWith(".update")) {
            if (param instanceof CreateInfoModel) {
                CreateInfoModel model = (CreateInfoModel) param;
                model.setUpdateTime(new Date());
                model.setUpdateBy(currentUserId);
//                 model.setUpdateUser(currentContext.getCurrentUserName());
                if (param instanceof BaseModel) {
                    BaseModel baseModel = (BaseModel) param;
                    if (null != baseModel.getVersion()) {
                        baseModel.setVersion(baseModel.getVersion() + 1);
                    } else {
                        baseModel.setVersion(1);
                    }
                }
            }

            //如果是gofficeModel赋值updateUser
            if (param instanceof IGBaseModel) {
                ((IGBaseModel) param).setUpdateUser(currentContext.getCurrentUserName());
            }
        }

        //新增逻辑
        else if (StringUtils.endsWithAny(statement.getId(), ".create", ".insertSelective")) {
            //为ID赋值
            if (param instanceof IDModel) {
                IDModel model = (IDModel) param;
                if (model.getId() == null) {
                    model.setId(IdUtil.getSuid());
                }
            }
            //创建信息赋值
            if (param instanceof CreateInfoModel) {
                CreateInfoModel model = (CreateInfoModel) param;
                if (model.getCreateTime() == null) {
                    model.setCreateTime(new Date());
                    model.setCreateBy(currentUserId);
                }
                if (model.getUpdateTime() == null) {
                    model.setUpdateTime(new Date());
                    model.setUpdateBy(currentUserId);
                }
                if (param instanceof BaseModel) {
                    BaseModel baseModel = (BaseModel) param;
                    baseModel.setDelete(false);
                    baseModel.setVersion(0);
                }
            }

            //如果是gofficeModel赋值createUser或者updateUser
            if (param instanceof IGBaseModel) {
                IGBaseModel model = (IGBaseModel) param;
                String currentUserName = currentContext.getCurrentUserName();
                model.setCreateUser(currentUserName);
                model.setUpdateUser(currentUserName);
            }
        }

        // 批量新增
        else if (StringUtils.endsWithAny(statement.getId(), ".insertBatch")) {
            Object params = ((Map) param).get("param1");
            if (params instanceof Collection) {
                Collection collection = (Collection) params;
                for (Object object : collection) {
                    if (object instanceof IDModel) {
                        IDModel model = (IDModel) object;
                        if (model.getId() == null) {
                            model.setId(IdUtil.getSuid());
                        }
                    }
                    //创建信息赋值
                    if (object instanceof CreateInfoModel) {
                        CreateInfoModel model = (CreateInfoModel) object;
                        if (model.getCreateTime() == null) {
                            model.setCreateTime(new Date());
                            model.setCreateBy(currentUserId);
                        }
                        if (model.getUpdateTime() == null) {
                            model.setUpdateTime(new Date());
                            model.setUpdateBy(currentUserId);
                        }
                        if (param instanceof BaseModel) {
                            BaseModel baseModel = (BaseModel) param;
                            baseModel.setDelete(false);
                            baseModel.setVersion(0);
                        }
                    }

                    //如果是gofficeModel赋值createUser或者updateUser
                    if (object instanceof IGBaseModel) {
                        IGBaseModel model = (IGBaseModel) object;
                        String currentUserName = currentContext.getCurrentUserName();
                        model.setCreateUser(currentUserName);
                        model.setUpdateUser(currentUserName);
                    }
                }
            }

        }

        // 批量更新
        else if (StringUtils.endsWithAny(statement.getId(), ".updateBatch")) {
            if (param instanceof Collection) {
                Collection collection = (Collection) param;
                for (Object object : collection) {
                    if (object instanceof CreateInfoModel) {
                        CreateInfoModel model = (CreateInfoModel) object;
                        model.setUpdateTime(new Date());
                        model.setUpdateBy(currentUserId);
                        if (object instanceof BaseModel) {
                            BaseModel baseModel = (BaseModel) object;
                            if (null != baseModel.getVersion()) {
                                baseModel.setVersion(baseModel.getVersion() + 1);
                            } else {
                                baseModel.setVersion(1);
                            }
                        }
                    }

                    //如果是gofficeModel赋值createUser或者updateUser
                    if (object instanceof IGBaseModel) {
                        IGBaseModel model = (IGBaseModel) object;
                        model.setUpdateUser(currentContext.getCurrentUserName());
                    }
                }
            }

        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
