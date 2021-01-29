package com.telecom.ecloudframework.base.core.util;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.spring.CustPropertyPlaceholderConfigurer;
import com.telecom.ecloudframework.base.core.spring.IProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;



import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 属性工具类。
 */
public class PropertyUtil {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([^A-Z-])([A-Z])");

    private static final Pattern SEPARATED_TO_CAMEL_CASE_PATTERN = Pattern
            .compile("[_\\-.]");

    private static final String DONT = ".";

    //private static Environment env = null;
    private static IProperty propertyHolder = null;
    // spring boot 略 不同处理
    private static Environment environment = null;

    /**
     * 根据键值获取属性文件中配置的值。
     *
     * @param property     属性键值
     * @param defaultValue 默认值
     * @return String
     */
    public static String getProperty(String property, String defaultValue) {
        if (propertyHolder == null) {
            getEnvironment();
        }

        String v = propertyHolder.getValue(property);
        if (StringUtil.isEmpty(v)) {
            return defaultValue;
        }

        return v;
    }


    private static synchronized void getEnvironment() {
        propertyHolder = (CustPropertyPlaceholderConfigurer) AppUtil.getBean("custPlaceHolder");

        if (propertyHolder == null) {
            environment = AppUtil.getBean(Environment.class);
            if (environment == null) {
                throw new BusinessException("Environment cannot be found");
            }

            propertyHolder = new IProperty() {
                @Override
                public String getValue(String key) {
                    return environment.getProperty(key);
                }
            };
        }
    }


    /**
     * 获取系统属性值。
     *
     * @param property 属性键值。
     * @return String
     */
    public static String getProperty(String property) {
        return getProperty(property, "");
    }

    /**
     * 获取整形值。
     *
     * @param property
     * @return
     */
    public static Integer getIntProperty(String property) {
        String str = getProperty(property, "");
        if (StringUtil.isEmpty(str)) {
            return 0;
        }
        return Integer.valueOf(str);
    }

    /**
     * 获取整形值，可以指定默认值。
     *
     * @param property
     * @param defaultValue
     * @return
     */
    public static Integer getIntProperty(String property, Integer defaultValue) {
        String str = getProperty(property, "");
        if (StringUtil.isEmpty(str)) {
            return defaultValue;
        }
        return Integer.valueOf(str);
    }


    /**
     * 获取布尔值。如果配置值为 true，则返回true。这里不区分大小写，可以配置成True等。
     *
     * @param property
     * @return
     */
    public static boolean getBoolProperty(String property) {
        String str = getProperty(property, "");
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return str.equalsIgnoreCase("true");
    }

    /**
     * 获取当前数据库的类型。
     *
     * @return
     */
    public static String getJdbcType() {
        String str = getProperty("jdbc.dbType");
        if (StringUtil.isEmpty(str)) {
            str = getProperty("spring.datasource.dbType");
        }

        return str;
    }

    /**
     * <pre>
     * 获取表单备份地址
     * </pre>
     *
     * @return
     */
    public static String getFormDefBackupPath() {
        return getProperty("formDefBackupPath");
    }

    /**
     * 获取较松懈字符串值
     * <pre>
     *  PropertyUtil.getRelaxStringValue(environment, "spring.datasource.driverClassName");
     *  PropertyUtil.getRelaxStringValue(environment, "spring.datasource.driver_class_name");
     *  PropertyUtil.getRelaxStringValue(environment, "spring.datasource.driver-class-name");
     * </pre>
     *
     * @param environment 环境
     * @param key         键
     * @return 字符串值
     */
    public static String getRelaxStringValue(Environment environment, String key) {
        String value = environment.getProperty(key);
        if (StringUtils.isEmpty(value)) {
            int dontIndex = StringUtils.lastIndexOf(key, DONT);
            String prefix = StringUtils.EMPTY;
            String name = key;
            if (dontIndex != StringUtils.INDEX_NOT_FOUND) {
                prefix = StringUtils.substring(key, 0, dontIndex);
                name = StringUtils.substring(key, dontIndex + 1);
            }
            Set<String> names = new HashSet<>();
            addRelaxNames(name, names);
            for (String searchName : names) {
                value = environment.getProperty(StringUtils.join(prefix, DONT, searchName));
                if (StringUtils.isNotEmpty(value)) {
                    break;
                }
            }
        }
        return value;
    }

    private static void addRelaxNames(String name, Set<String> values) {
        if (values.contains(name)) {
            return;
        }
        for (Variation variation : Variation.values()) {
            for (Manipulation manipulation : Manipulation.values()) {
                String result = name;
                result = variation.apply(result);
                result = manipulation.apply(result);
                values.add(result);
                addRelaxNames(result, values);
            }
        }
    }


    /**
     * Name variations.
     */
    enum Variation {

        NONE {
            @Override
            public String apply(String value) {
                return value;
            }

        },

        LOWERCASE {
            @Override
            public String apply(String value) {
                return (value.isEmpty() ? value : value.toLowerCase(Locale.ENGLISH));
            }

        },

        UPPERCASE {
            @Override
            public String apply(String value) {
                return (value.isEmpty() ? value : value.toUpperCase(Locale.ENGLISH));
            }

        };

        public abstract String apply(String value);

    }

    /**
     * Name manipulations.
     */
    enum Manipulation {

        NONE {
            @Override
            public String apply(String value) {
                return value;
            }

        },

        HYPHEN_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                return (value.indexOf('-') != -1 ? value.replace('-', '_') : value);
            }

        },

        UNDERSCORE_TO_PERIOD {
            @Override
            public String apply(String value) {
                return (value.indexOf('_') != -1 ? value.replace('_', '.') : value);
            }

        },

        PERIOD_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                return (value.indexOf('.') != -1 ? value.replace('.', '_') : value);
            }

        },

        CAMELCASE_TO_UNDERSCORE {
            @Override
            public String apply(String value) {
                if (value.isEmpty()) {
                    return value;
                }
                Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
                if (!matcher.find()) {
                    return value;
                }
                matcher = matcher.reset();
                StringBuffer result = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(result, matcher.group(1) + '_'
                            + org.springframework.util.StringUtils.uncapitalize(matcher.group(2)));
                }
                matcher.appendTail(result);
                return result.toString();
            }

        },

        CAMELCASE_TO_HYPHEN {
            @Override
            public String apply(String value) {
                if (value.isEmpty()) {
                    return value;
                }
                Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
                if (!matcher.find()) {
                    return value;
                }
                matcher = matcher.reset();
                StringBuffer result = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(result, matcher.group(1) + '-'
                            + org.springframework.util.StringUtils.uncapitalize(matcher.group(2)));
                }
                matcher.appendTail(result);
                return result.toString();
            }

        },

        SEPARATED_TO_CAMELCASE {
            @Override
            public String apply(String value) {
                return separatedToCamelCase(value, false);
            }

        },

        CASE_INSENSITIVE_SEPARATED_TO_CAMELCASE {
            @Override
            public String apply(String value) {
                return separatedToCamelCase(value, true);
            }

        },

        UNDERSCORE_TO_HYPHEN {
            @Override
            public String apply(String value) {
                return (value.indexOf('_') != -1 ? value.replace("_", "-") : value);
            }
        };

        private static final char[] SUFFIXES = new char[]{'_', '-', '.'};

        public abstract String apply(String value);

        private static String separatedToCamelCase(String value,
                                                   boolean caseInsensitive) {
            if (value.isEmpty()) {
                return value;
            }
            StringBuilder builder = new StringBuilder();
            for (String field : SEPARATED_TO_CAMEL_CASE_PATTERN.split(value)) {
                field = (caseInsensitive ? field.toLowerCase(Locale.ENGLISH) : field);
                builder.append(
                        builder.length() != 0 ? org.springframework.util.StringUtils.capitalize(field) : field);
            }
            char lastChar = value.charAt(value.length() - 1);
            for (char suffix : SUFFIXES) {
                if (lastChar == suffix) {
                    builder.append(suffix);
                    break;
                }
            }
            return builder.toString();
        }
    }
}
