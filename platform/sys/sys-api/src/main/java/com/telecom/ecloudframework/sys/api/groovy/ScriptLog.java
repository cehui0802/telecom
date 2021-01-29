package com.telecom.ecloudframework.sys.api.groovy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 脚本接口输出日志。<br>
 */
@Component
public class ScriptLog {
    private static Logger logger = LoggerFactory.getLogger(ScriptLog.class);
    public void info(Object message){
        logger.info(String.valueOf(message));
    }
    public void debug(Object message){
        logger.debug(String.valueOf(message));
    }
    public void error(Object message){
        logger.error(String.valueOf(message));
    }
}
