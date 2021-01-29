package com.telecom.ecloudframework.org.api.constant;

/**
 * 组织级别
 *
 * @author 谢石
 * @date 2020-9-3
 */
public enum RelationTypeConstant {
    /**
     * 用户与机构
     */
    GROUP_USER("groupUser", "机构用户"),
    USER_ROLE("userRole", "角色用户"),
    POST_USER("postUser", "岗位用户"),
    GROUP_MANAGER("groupManager", "机构管理员");

    private String key;
    private String label;


    RelationTypeConstant(String key, String label) {
        this.setKey(key);
        this.label = label;
    }


    public String label() {
        return label;
    }

    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    /**
     * 通过组类型转换成与用户的关系类型
     *
     * @param groupType
     * @return
     */
    public static RelationTypeConstant getUserRelationTypeByGroupType(String groupType) {
        GroupTypeConstant type = GroupTypeConstant.fromStr(groupType);
        if (null != type) {
            switch (type) {
                case ORG:
                    return RelationTypeConstant.GROUP_USER;
                case POST:
                    return RelationTypeConstant.POST_USER;
                case ROLE:
                    return RelationTypeConstant.USER_ROLE;
                default:
                    break;
            }
        }
        return null;
    }

}
