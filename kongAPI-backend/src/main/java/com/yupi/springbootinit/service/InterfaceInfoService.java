package com.yupi.springbootinit.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.common_kongapi.model.entity.InterfaceInfo;

/**
* @author 22683
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-05-14 23:21:22
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);
}
