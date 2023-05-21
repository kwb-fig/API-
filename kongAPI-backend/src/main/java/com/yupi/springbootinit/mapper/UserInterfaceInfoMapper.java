package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.common_kongapi.model.entity.UserInterfaceInfo;

import java.util.List;


/**
* @author 22683
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-05-17 22:24:58
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    /**
     *统计接口调用次数前几名
     * @author kong
     * @date 2023/5/20 12:21
     */

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




