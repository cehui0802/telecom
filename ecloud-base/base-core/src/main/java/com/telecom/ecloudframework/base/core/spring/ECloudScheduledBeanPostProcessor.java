package com.telecom.ecloudframework.base.core.spring;

import com.telecom.ecloudframework.base.api.aop.annotion.ECloudScheduled;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wacxhs
 */
public class ECloudScheduledBeanPostProcessor implements BeanPostProcessor, DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ECloudScheduledBeanPostProcessor.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<Object, Set<Method>> scheduledMethodMap = new HashMap<>();

    /**
     * 启动调度器
     */
    private final boolean enableScheduled;

    public ECloudScheduledBeanPostProcessor(boolean enableScheduled) {
        this.enableScheduled = enableScheduled;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (enableScheduled) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            Set<Method> methods = MethodIntrospector.selectMethods(targetClass, (ReflectionUtils.MethodFilter) method -> AnnotationUtils.getAnnotation(method, ECloudScheduled.class) != null);
            if (CollUtil.isNotEmpty(methods)) {
                scheduledMethodMap.put(bean, methods);
            }
        }
        return bean;
    }

    @Override
    public void destroy() {
        CronUtil.stop();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null || "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            finishRegistrar();
        }
    }

    private synchronized void finishRegistrar() {
        if (enableScheduled && scheduledMethodMap != null && !scheduledMethodMap.isEmpty()) {
            CronUtil.setMatchSecond(true);
            CronUtil.start();
            Map<String, ECloudScheduledExceptionHandler> eCloudScheduledExceptionHandlerMap = applicationContext.getBeansOfType(ECloudScheduledExceptionHandler.class);
            for (Map.Entry<Object, Set<Method>> entry : scheduledMethodMap.entrySet()) {
                for (Method method : entry.getValue()) {
                    ECloudScheduled scheduled = method.getAnnotation(ECloudScheduled.class);
                    CronUtil.schedule(scheduled.cron(), new CronTask(entry.getKey(), method, initialMethodParameterZero(method.getParameterTypes()), eCloudScheduledExceptionHandlerMap.values()));
                }
            }
            scheduledMethodMap.clear();
        }
    }

    /**
     * 初始化方法参数零值
     *
     * @param parameterTypeClass 参数类型
     * @return 初始零值
     */
    private Object[] initialMethodParameterZero(Class[] parameterTypeClass) {
        if (parameterTypeClass == null || parameterTypeClass.length == 0) {
            return null;
        }
        Object[] parameter = new Object[parameterTypeClass.length];
        for (int i = 0, l = parameterTypeClass.length; i < l; i++) {
            Class parameterType = parameterTypeClass[i];
            Object value = null;
            if (boolean.class.equals(parameterType)) {
                value = false;
            } else if (parameterType.isPrimitive()) {
                value = (byte) 0;
            }
            parameter[i] = value;
        }
        return parameter;
    }


    private static class CronTask implements Task {

        private Object object;

        private Method method;

        private Object[] methodParameter;

        private Collection<ECloudScheduledExceptionHandler> eCloudScheduledExceptionHandlers;

        CronTask(Object object, Method method, Object[] methodParameter, Collection<ECloudScheduledExceptionHandler> eCloudScheduledExceptionHandlers) {
            this.object = object;
            this.method = method;
            this.methodParameter = methodParameter;
            this.eCloudScheduledExceptionHandlers = eCloudScheduledExceptionHandlers;
        }

        @Override
        public void execute() {
            try {
                method.invoke(object, methodParameter);
            } catch (Throwable t) {
                notifyExceptionHandler(t);
            }
        }

        private void notifyExceptionHandler(Throwable throwable) {
            if (eCloudScheduledExceptionHandlers == null || eCloudScheduledExceptionHandlers.isEmpty()) {
                logger.error("schedule {} exception", method.toString(), throwable);
            } else {
                for (ECloudScheduledExceptionHandler ECloudScheduledExceptionHandler : eCloudScheduledExceptionHandlers) {
                    ECloudScheduledExceptionHandler.exception(object, method, throwable);
                }
            }
        }
    }

}
