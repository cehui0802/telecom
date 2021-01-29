package com.telecom.ecloudframework.component.j2cache;

import com.telecom.ecloudframework.base.core.util.PropertyUtil;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Properties;

/**
 * cacheChannel bean工厂
 *
 * @author wacxhs
 */
public class J2CacheChannelFactoryBean implements FactoryBean<CacheChannel> {

    @Autowired
    private StandardEnvironment environment;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * redis序列化模板
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public CacheChannel getObject() throws Exception {
        J2CacheConfig config = new J2CacheConfig();
        config.setSerialization(environment.getProperty("j2cache.serialization"));
        config.setBroadcast(PropertyUtil.getRelaxStringValue(environment, "j2cache.broadcast.provider_class"));
        config.setL1CacheName(PropertyUtil.getRelaxStringValue(environment, "j2cache.L1.provider_class"));
        config.setL2CacheName(PropertyUtil.getRelaxStringValue(environment, "j2cache.L2.provider_class"));
        config.setSyncTtlToRedis(BooleanUtils.toBoolean(PropertyUtil.getRelaxStringValue(environment, "j2cache.sync_ttl_to_redis")));
        config.setDefaultCacheNullObject(BooleanUtils.toBoolean(PropertyUtil.getRelaxStringValue(environment, "j2cache.default_cache_null_object")));
        putProperties(J2CacheConstant.SPRING_APPLICATION_CONTEXT, applicationContext, config.getL1CacheProperties(), config.getL2CacheProperties(), config.getBroadcastProperties());
        putProperties(J2CacheConstant.REDIS_TEMPLATE, redisTemplate, config.getL1CacheProperties(), config.getL2CacheProperties(), config.getBroadcastProperties());
        environment.getPropertySources().forEach(a -> {
            if (a instanceof MapPropertySource) {
                MapPropertySource c = (MapPropertySource) a;
                for (Map.Entry<String, Object> propertySourceEntry : c.getSource().entrySet()) {
                    if (propertySourceEntry.getKey().startsWith(J2CacheConstant.L1_PREFIX)) {
                        config.getL1CacheProperties().put(propertySourceEntry.getKey().substring(J2CacheConstant.L1_PREFIX.length() + 1).replace("-", "_"), String.valueOf(propertySourceEntry.getValue()));
                    } else if (propertySourceEntry.getKey().startsWith(J2CacheConstant.L2_PREFIX)) {
                        config.getL2CacheProperties().put(propertySourceEntry.getKey().substring(J2CacheConstant.L2_PREFIX.length() + 1).replace("-", "_"), String.valueOf(propertySourceEntry.getValue()));
                    } else if (propertySourceEntry.getKey().startsWith(J2CacheConstant.BROADCAST_PREFIX)) {
                        String key = StringUtils.substring(propertySourceEntry.getKey(), J2CacheConstant.BROADCAST_PREFIX.length() + 1).replace("-", "_");
                        if (StringUtils.isNotEmpty(key)) {
                            config.getBroadcastProperties().put(key, String.valueOf(propertySourceEntry.getValue()));
                        }

                    }
                }
            }
        });
        config.getBroadcastProperties().putAll(config.getL1CacheProperties());
        config.getBroadcastProperties().putAll(config.getL2CacheProperties());
        return J2CacheBuilder.init(config).getChannel();
    }

    @Override
    public Class<?> getObjectType() {
        return CacheChannel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * put value in properties
     *
     * @param key        键
     * @param value      值
     * @param properties 配置
     */
    private void putProperties(String key, Object value, Properties... properties) {
        for (Properties p : properties) {
            p.put(key, value);
        }
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
