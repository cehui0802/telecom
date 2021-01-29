
package com.telecom.ecloudframework.security.core.model;

import com.telecom.ecloudframework.base.api.model.Tree;

import java.util.List;

/**
 * <pre>
 * 描述：菜单树实体
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-13 14:31
 */
public class MenuTree extends Menu implements Tree<MenuTree> {
    /**
     * title  默认为name 、如果name添加了 css 、则默认为 “”
     */
    protected String title = "";
    protected String breadcrumb = "";

    protected List<MenuTree> children;

    public MenuTree() {
    }

    public MenuTree(Menu menu) {
        this.id = menu.getId();
        this.systemId = menu.systemId;
        this.name = menu.name;
        this.code = menu.code;
        this.type = menu.type;
        this.icon = menu.icon;
        this.iconColor = menu.iconColor;
        this.parentId = menu.parentId;
        this.url = menu.url;
        this.sn = menu.sn;
        this.enable = menu.enable;
        if (!this.name.contains("style=")) {
            this.title = name;
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
        // 将title 设置成name
        if ("".equals(title) && !this.name.contains("style=")) {
            this.title = name;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<MenuTree> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<MenuTree> list) {
        this.children = list;
    }
}
