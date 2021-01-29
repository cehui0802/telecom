package com.telecom.ecloudframework.base.rest.aop;

import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.handler.OperateLogHandler;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.RequestContext;
import com.telecom.ecloudframework.base.db.datasource.DbContextHolder;
import com.telecom.ecloudframework.base.operateLog.manager.LogOperateManager;
import com.telecom.ecloudframework.base.operateLog.model.LogOperate;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.sys.api.jms.model.DefaultJmsDTO;
import com.telecom.ecloudframework.sys.api.jms.producer.JmsProducer;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;


/**
 * 日志记录切面，记录操作日志（服务启动时需要注入带有日志相关表的数据源）
 *
 * @author guolihao
 * @date 2020/9/18 13:30
 */
@Aspect
@Component
public class OperateLogAspect {

    private Logger LOGGER = LoggerFactory.getLogger(OperateLogAspect.class);

    /**
     * 是否启用mq
     */
    @Value("${ecloud.log.mq.enabled:false}")
    private boolean mqEnabled;
    /**
     * 数据源别名
     */
    @Value("${ecloud.log.dataSource.dbAlias:}")
    private String dbAlias;
    /**
     * 数据源的类型：oracle,mysql,dmsql
     */
    @Value("${ecloud.log.dataSource.dbType:}")
    private String dbType;

    @Resource
    private LogOperateManager logOperateManager;

    @Autowired(required = false)
    private JmsProducer jmsProducer;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Pointcut("@annotation(com.telecom.ecloudframework.base.api.aop.annotion.OperateLog)")
    public void operateLogPoint() {
    }

    @AfterReturning(value = "@annotation(operateLog)", returning = "result")
    public void doRecordLogAfter(JoinPoint pjp, OperateLog operateLog, Object result) {
        try {
            LogOperate logOperate = new LogOperate();
            logOperate.setId(IdUtil.getSuid());
            //操作用户
            IUser currentUser = ContextUtil.getCurrentUser();
            if(Objects.nonNull(currentUser)){
                logOperate.setAccount(currentUser.getAccount());
                logOperate.setUserId(currentUser.getUserId());
            }
            //写入请求信息
            writeRequest(logOperate, pjp, operateLog);
            //操作时间
            logOperate.setOperateTime(new Date());
            //返回结果
            if (result instanceof ResultMsg) {
                ResultMsg resultMsg = (ResultMsg) result;
                if (Objects.nonNull(resultMsg)) {
                    //记录备份日志
                    String method = pjp.getSignature().getName();
                    if(method.startsWith("exportLog")){
                        String fileName = resultMsg.getData().toString();
                        logOperate.setBackupFileName(fileName.substring(0,fileName.indexOf(".")));
                        logOperate.setBackupFileType(fileName.substring(fileName.indexOf(".")+1));
                        logOperate.setLogType(2);
                    }
                    if (resultMsg.getIsOk()) {
                        logOperate.setResult(1);
                    } else {
                        logOperate.setResult(0);
                    }
                }
            }
            //写入返回信息
            if (operateLog.writeResponse()) {
                String responseResult = JSONObject.toJSONString(result);
                logOperate.setResponseResultData(responseResult);
            }
            logOperate.setCreateTime(new Date());
            //写入特殊处理接口参数
            writeHandler(logOperate,pjp);
            //记录日志
            threadPoolTaskExecutor.execute(() -> recordLopOperate(logOperate));
        }catch (Exception ex){
            LOGGER.error("日志记录切面，记录操作日志异常：",ex);
            //TODO
        }
    }

    /**
     * 特殊接口处理，通过handler返回值来保存配置项
     *
     * @param logOperate 日志记录信息
     * @param pjp 切面拦截方法上下文
     * @author guolihao
     * @date 2020/11/16 16:45
     */
    private void writeHandler(LogOperate logOperate, JoinPoint pjp) throws IOException {
        Object target = pjp.getTarget();
        if(target instanceof OperateLogHandler){
            OperateLogHandler handler = (OperateLogHandler) target;
            Object[] objects = pjp.getArgs();
            if(objects.length>0){
                Object config = handler.getLogConfig(objects);
                if(config instanceof Map){
                    Map map = (Map) config;
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                    String outJson = objectMapper.writeValueAsString(map);
                    ObjectReader objectReader = objectMapper.readerForUpdating(logOperate);
                    objectReader.readValue(outJson);
                }
            }
        }
    }

    /**
     * 记录操作日志
     *
     * @param logOperate 操作日志记录信息
     * @return
     * @author guolihao
     * @date 2020/9/28 17:19
     */
    private void recordLopOperate(LogOperate logOperate) {
        if (mqEnabled) {
            jmsProducer.sendToQueue(new DefaultJmsDTO("log", logOperate));
        } else {
            DbContextHolder.setDataSource(dbAlias, dbType);
            logOperateManager.create(logOperate);
        }
    }

    /**
     * 写入请求信息
     *
     * @param logOperate 操作日志记录实体类
     * @param pjp        切面拦截方法上下文
     * @param operateLog 日志记录切面注解
     * @return LogOperate
     * @author guolihao
     * @date 2020/9/21 11:57
     */
    private LogOperate writeRequest(LogOperate logOperate, JoinPoint pjp, OperateLog operateLog) {
        HttpServletRequest request = RequestContext.getHttpServletRequest();
        if (request != null) {
            //IP，访问路径
            String path = request.getServletPath();
            //流程访问路径处理
            if(path.contains("/model")){
                String model = path.substring(path.indexOf("/model"),("/model").length());
                String last = path.substring(path.lastIndexOf("/"));
                path = model+last;
            }
            String ip = RequestUtil.getIpAddr(request);
            logOperate.setIp(ip);
            logOperate.setPath(path);
            //请求参数
            String paramMap = getParams(pjp,request);
            //头信息
            if (operateLog.writeRequest()) {
                Enumeration headerNames = request.getHeaderNames();
                Map headerMap = Maps.newHashMap();
                while (headerNames.hasMoreElements()) {
                    String key = (String) headerNames.nextElement();
                    headerMap.put(key, request.getHeader(key));
                }
                logOperate.setRequestHead(JSONObject.toJSONString(headerMap));
                logOperate.setRequestParam(paramMap);
            }
        }
        return logOperate;
    }

    /**
     * 获取入参
     *
     * @param pjp 切面拦截方法上下文
     * @param request request
     * @return
     * @author guolihao
     * @date 2020/11/16 16:48
     */
    private String getParams(JoinPoint pjp, HttpServletRequest request) {
        String paramMap = JSONObject.toJSONString(request.getParameterMap());
        if (StringUtils.isEmpty(paramMap) || ("{}").equals(paramMap)) {
            paramMap = "";
            for (Object o : pjp.getArgs()) {
                if (o instanceof ServletRequest || o instanceof ServletResponse) {
                    continue;
                }
                //数据导入参数处理
                if(o instanceof MultipartFile){
                    paramMap = ((MultipartFile) o).getOriginalFilename();
                    break;
                }
                paramMap += JSONObject.toJSONString(o);
            }
        }
        return paramMap;
    }
}
