package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common_kongapi.model.entity.InterfaceInfo;
import com.common_kongapi.model.entity.UserInterfaceInfo;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 22683
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-05-17 22:24:58
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    //修改调用次数
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     *统计接口调用次数
     * @param limit
     * @return {@link BaseResponse<List<InterfaceInfoVO>>}
     * @author kong
     * @date 2023/5/20 12:32
     */
     BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(int limit);

     /**
      * 新增用户调用接口次数
      * @param userInterfaceInfoAddRequest
      * @param request
      * @return {@link BaseResponse< Long>}
      * @author kong
      * @date 2023/5/20 23:20
      */

     BaseResponse<Long> addUserInterfaceInfo(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request);
}
