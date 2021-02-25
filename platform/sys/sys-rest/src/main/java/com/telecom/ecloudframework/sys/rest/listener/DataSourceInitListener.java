package com.telecom.ecloudframework.sys.rest.listener;

import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.PropertyUtil;
import com.telecom.ecloudframework.base.api.constant.DbType;
import com.telecom.ecloudframework.base.db.datasource.DataSourceUtil;
import com.telecom.ecloudframework.sys.core.manager.SysDataSourceManager;
import com.telecom.ecloudframework.sys.core.model.SysDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <pre>
 * 在spring容器启动时加载数据源：
 * 从spring文件中做加载。 扫描系统spring 配置中数据源动态加入到系统的dataSourceMap数据源中，
 * </pre>
 *
 * <pre>
 * </pre>
 */
@Component
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SysDataSourceManager sysDataSourceManager;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DataSourceInitListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止重复执行。
        if (event.getApplicationContext().getParent() == null || "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            ApplicationContext context = event.getApplicationContext();
            // 加载配置文件中的数据源然后放到dynamicDataSource中，然后增加到系统数据中
            loadDataSourceFromFile(context);
            // 加载系统数据源到dynamicDataSource中
            loadDataSourceFromSysDataSource();
        }

    }

    /**
     * <pre>
     * 加载系统数据源到dynamicDataSource中
     * </pre>
     */
    private void loadDataSourceFromSysDataSource() {
        Iterator var1 = this.sysDataSourceManager.getAll().iterator();

        while(var1.hasNext()) {
            SysDataSource sysDataSource = (SysDataSource)var1.next();
            if (sysDataSource.getKey().equals("dataSourceDefault") && !PropertyUtil.getJdbcType().equals(sysDataSource.getDbType())) {
                sysDataSource.setDbType(PropertyUtil.getJdbcType());
                this.sysDataSourceManager.update(sysDataSource);
            }

            if (!DataSourceUtil.isDataSourceExist(sysDataSource.getKey()) && !sysDataSource.getKey().equals("dataSource") && !sysDataSource.getKey().equals("dataSourceDefault")) {
                try {
                    DataSource dataSource = this.sysDataSourceManager.tranform2DataSource(sysDataSource);
                    DataSourceUtil.addDataSource(sysDataSource.getKey(), dataSource, sysDataSource.getDbType(), false);
                    LOGGER.debug("add datasource " + sysDataSource.getKey());
                } catch (Exception var4) {
                    LOGGER.error("在系统配置的数据源[" + sysDataSource.getKey() + "]启动项目时无法正确加载进去，请正确配置该数据源", var4);
                }
            }
        }
    }

    /**
     * 加载配置文件中的数据源然后放到dynamicDataSource中，然后增加到系统数据中
     *
     * @param context void
     */
    void loadDataSourceFromFile(ApplicationContext context) {
        Map<String, DataSource> mapDataSource = context.getBeansOfType(DataSource.class);
        for (Entry<String, DataSource> entry : mapDataSource.entrySet()) {
            // 本地数据源不需要再次增加进去
            if (entry.getKey().equals(DataSourceUtil.GLOBAL_DATASOURCE) || entry.getKey().equals(DataSourceUtil.DEFAULT_DATASOURCE)) {
                continue;
            }
            String dbType = getDbType(entry.getValue());
            DataSourceUtil.addDataSource(entry.getKey(), entry.getValue(), dbType, false);
            LOGGER.debug("add datasource " + entry.getKey());
            // 将其新增到系统配置的数据源中，供客户使用
            if (sysDataSourceManager.getByKey(entry.getKey()) == null) {
                SysDataSource sysDataSource = new SysDataSource();
                sysDataSource.setKey(entry.getKey());
                sysDataSource.setName(entry.getKey() + "数据源");
                sysDataSource.setId(IdUtil.getSuid());
                sysDataSource.setDbType(dbType);
                sysDataSourceManager.create(sysDataSource);
            }
        }
    }

    /**
     * <pre>
     * 根据数据源获取数据库类型
     * </pre>
     *
     * @param dataSource
     * @return
     */
    private String getDbType(DataSource dataSource) {
        try {
            Class<?> cls = dataSource.getClass();
            Method getDriverClassNameMethod = cls.getDeclaredMethod("getDriverClassName");
            String driverClassName = (String) getDriverClassNameMethod.invoke(dataSource);
            for (DbType dbType : DbType.values()) {
                if (driverClassName.contains(dbType.getKey())) {
                    return dbType.getKey();
                }
            }
            return "mysql";
        } catch (Exception e) {
            return "mysql";
        }
    }

}
