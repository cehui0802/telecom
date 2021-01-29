package com.telecom.ecloudframework.base.rest.tree;

public interface DeleteAuth {


    /**
     * 删除分类时，判断是否存在具体业务节点，需要具体业务实现该方法，提供对分类下对象等是否为空
     * 根据分类ID判断分类下业务是否为空。若返回true,可以删除，具体业务节点为空。
     * Map的key为树的别名，value为
     * @param typeId 分类Id
     * @return Map key为不能删除描述，value为是否有权限删除
     */
    public Boolean deleteAuth(String typeId);

}
