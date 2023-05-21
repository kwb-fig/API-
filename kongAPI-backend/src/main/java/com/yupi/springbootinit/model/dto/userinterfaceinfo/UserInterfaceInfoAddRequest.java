package com.yupi.springbootinit.model.dto.userinterfaceinfo;

import lombok.Data;

/**
 * 添加用户调用接口次数
 */

@Data
public class UserInterfaceInfoAddRequest {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
