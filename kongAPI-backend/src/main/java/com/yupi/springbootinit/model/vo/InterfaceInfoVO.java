package com.yupi.springbootinit.model.vo;

import com.common_kongapi.model.entity.InterfaceInfo;
import lombok.Data;

@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     *接口总调用次数
     */
    private Integer totalNum;
}
