package com.telecom.ecloudframework.sys.groovy;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.sys.api.groovy.IGroovyScriptEngine;
import com.telecom.ecloudframework.sys.api.groovy.IScript;
import com.telecom.ecloudframework.sys.api.groovy.ScriptLog;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 脚本引擎用于执行groovy脚本。<br/>
 * 实现了IScript接口的类。 可以在脚本中使用。
 */
@Component
public class GroovyScriptEngine implements IGroovyScriptEngine, ApplicationListener<ContextRefreshedEvent>{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private GroovyBinding groovyBinding = new GroovyBinding();
    @Resource
    private ScriptLog log  ;
    // 禁用的操作类
    @Value("${ecloud.groovy.blackKeywords:System,Runtime,BeanUtil,AppUtil,JdbcTemplate,FileUtil,InputStream,IoUtil,FileWriter,ReflectUtil,ClassUtil}")
    private String filters;

    private List<String> blackKeywords ;

    private List<String> getBackKeywordsList() {
        if(blackKeywords != null) return blackKeywords;

        blackKeywords = Arrays.asList(filters.split(","));
        return blackKeywords;
    }

    @Override
    public void execute(String script) {
        executeObject(script, null);
    }

    @Override
    public void execute(String script, Map<String, Object> vars) {
        executeObject(script, vars);
    }

    @Override
    public boolean executeBoolean(String script, Map<String, Object> vars) {
        return (Boolean) executeObject(script, vars);
    }

    @Override
    public String executeString(String script, Map<String, Object> vars) {
        return (String) executeObject(script, vars);
    }


    @Override
    public int executeInt(String script, Map<String, Object> vars) {
        return (Integer) executeObject(script, vars);
    }

    @Override
    public float executeFloat(String script, Map<String, Object> vars) {
        return (Float) executeObject(script, vars);
    }

    @Override
    public Object executeObject(String script, Map<String, Object> vars) {
        if(script == null) {
            return null;
        }

        for(String keyWords : getBackKeywordsList()) {
            if(StringUtils.isEmpty(keyWords)) continue;

            if(script.indexOf(keyWords)!=-1) {
                throw new BusinessException(String.format("GroovyScriptEngine 执行失败，使用了黑名单中的关键词：[%s]，请修改脚本：%s", keyWords,script));
            }
        }

        if (vars != null){
            vars.put("log", log);
        }
        groovyBinding.setThreadVariables(vars);

        if(logger.isDebugEnabled()) {
            logger.debug("执行:{}", script);
            logger.debug("variables:{}",vars+"");
        }

        GroovyShell shell = new GroovyShell(groovyBinding);

        script = script.replace("&apos;", "'").replace("&quot;", "\"")
                .replace("&gt;", ">").replace("&lt;", "<")
                .replace("&nuot;", "\n").replace("&amp;", "&");

        Object rtn = shell.evaluate(script);
        return rtn;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null || "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            Map<String, IScript> scirptImpls = AppUtil.getImplInstance(IScript.class);
            for (Entry<String, IScript> scriptMap : scirptImpls.entrySet()) {
                groovyBinding.setProperty(scriptMap.getKey(), scriptMap.getValue());
            }
        }
    }


    @Override
    public String executeFloat(String script, Map<String, Object> vars, String invalid) {
        groovyBinding.setThreadVariables(vars);
        GroovyShell shell = new GroovyShell(groovyBinding);

        script = script.replace("&apos;", "'").replace("&quot;", "\"")
                .replace("&gt;", ">").replace("&lt;", "<")
                .replace("&nuot;", "\n").replace("&amp;", "&");

        Object rtn = shell.evaluate(script);
        return (String)rtn;
    }
}
