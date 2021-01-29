package com.telecom.ecloudframework.org.api.constant;

/**
 * 组织级别
 */
public enum GroupGradeConstant {
    /**
     * 集团
     */
    GROUP(0, "集团"),
    COMPANY(1, "公司"),
    DEPARTMENT(3, "部门");


    private int key;
    private String label;

    GroupGradeConstant(int key, String label) {
        this.key = key;
        this.label = label;
    }

    public int key() {
        return key;
    }

    public String label() {
        return label;
    }

    public static GroupGradeConstant getByLabel(String label) {
        for (GroupGradeConstant entity : values()) {
            if (entity.label.equals(label)) {
                return entity;
            }
        }
        return null;
    }

}
