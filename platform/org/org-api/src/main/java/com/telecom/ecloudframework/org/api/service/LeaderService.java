package com.telecom.ecloudframework.org.api.service;

import com.telecom.ecloudframework.org.api.model.IUser;

public interface LeaderService {
    /**
     * 根据秘书用户ID获取领导用户的对象。
     *
     * @param secretaryId 用户ID
     * @return
     */
    IUser getUserBySecretaryId(String secretaryId);

    /**
     * 根据用户ID获取是否是领导用户。
     *
     * @param leaderId 用户ID
     * @return
     */
    boolean isLeaderByLeaderId(String leaderId);

    /**
     * 根据领导用户ID获取秘书用户的对象。
     *
     * @param leaderId 用户ID
     * @return
     */
    IUser getUserByLeaderId(String leaderId);
}
