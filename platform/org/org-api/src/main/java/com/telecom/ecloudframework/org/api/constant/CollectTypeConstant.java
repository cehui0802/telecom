package com.telecom.ecloudframework.org.api.constant;

/**
 * 系统支持的用户收藏类型
 *
 * @author 谢石
 * @date 2020-10-15
 */
public enum CollectTypeConstant {
    /**
     * 物品类型
     */
    COMMON_WORDS("commonWords", "常用语"),
    COLLECT("collect", "收藏菜单"),;
    private String key;
    private String label;

    CollectTypeConstant(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public static CollectTypeConstant fromStr(String key) {
        for (CollectTypeConstant e : CollectTypeConstant.values()) {
            if (key.equals(e.key)) {
                return e;
            }
        }
        return null;
    }
}
