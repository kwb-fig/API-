package com.common_kongapi.service;


import com.common_kongapi.model.entity.User;

/**
 * 用户服务
 *
 * @author kong
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
