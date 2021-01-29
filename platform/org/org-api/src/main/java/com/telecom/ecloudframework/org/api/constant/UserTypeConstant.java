package com.telecom.ecloudframework.org.api.constant;

/**
 * 系统支持的人员类型
 *
 * @author 谢石
 * @date 2020-10-15
 */
public enum UserTypeConstant {
    /**
     * 人员类型
     */
    NORMAL("1", "普通用户"),
    MANAGER("2", "管理员"),
    EXTERNAL("3","外部人员");
    private String key;
    private String label;

    UserTypeConstant(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public static UserTypeConstant fromStr(String key) {
        for (UserTypeConstant e : UserTypeConstant.values()) {
            if (key.equals(e.key)) {
                return e;
            }
        }
        return null;
    }
}
