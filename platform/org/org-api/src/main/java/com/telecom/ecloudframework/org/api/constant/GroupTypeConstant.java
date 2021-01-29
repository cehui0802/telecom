package com.telecom.ecloudframework.org.api.constant;
 import java.util.ArrayList;
import java.util.List;

/**
 * 系统支持的组类型
 *
 * @author 谢石
 * @date 2020-9-3
 */
public enum GroupTypeConstant {
    /**
     * 组织
     */
    ORG("org", "组织", new String[]{"bpm", "org"}),
    POST("post", "岗位", new String[]{"bpm"}),
    ROLE("role", "角色", new String[]{"bpm"});
    private String key;
    private String label;
    private String[] tags;

    GroupTypeConstant(String key, String label) {
        this.key = key;
        this.label = label;
    }

    GroupTypeConstant(String key, String label, String[] tags) {
        this.key = key;
        this.label = label;
        this.tags = tags;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }


    public static GroupTypeConstant fromStr(String key) {

        for (GroupTypeConstant e : GroupTypeConstant.values()) {
            if (key.equals(e.key)) {
                return e;
            }
        }

        return null;
    }

    public static GroupTypeConstant[] getValuesByTags(String tag) {
        List<GroupTypeConstant> groupTypeConstants = new ArrayList<>();
        for (GroupTypeConstant e : GroupTypeConstant.values()) {
            if (e.tags != null) {
                for (String enumTag : e.tags) {
                    if (enumTag.equals(tag)) {
                        groupTypeConstants.add(e);
                        break;
                    }
                }
            }
        }
        return groupTypeConstants.toArray(new GroupTypeConstant[]{});
    }

}
