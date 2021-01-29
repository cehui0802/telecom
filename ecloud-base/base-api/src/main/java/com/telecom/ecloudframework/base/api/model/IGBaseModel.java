package com.telecom.ecloudframework.base.api.model;

/**
 * <pre>
 * GOfiiceModel接口类
 *
 * </pre>
 */
public interface IGBaseModel extends CreateInfoModel,IDModel {

    /**
     * <pre>
     * 设置创建人名称
     * </pre>
     *
     * @param createUser
     */
    public void setCreateUser(String createUser);

    /**
     * <pre>
     * 创建人名称
     * </pre>
     *
     * @return
     */
    public String getCreateUser();


    /**
     * <pre>
     * 设置更新人名称
     * </pre>
     *
     * @param updateUser
     */
    public void setUpdateUser(String updateUser);

    /**
     * <pre>
     * 创建人名称
     * </pre>
     *
     * @return
     */
    public String getUpdateUser();
}
