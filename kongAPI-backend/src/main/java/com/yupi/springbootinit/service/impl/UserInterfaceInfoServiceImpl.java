package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common_kongapi.model.entity.InterfaceInfo;
import com.common_kongapi.model.entity.User;
import com.common_kongapi.model.entity.UserInterfaceInfo;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.UserInterfaceInfoMapper;
import com.yupi.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import com.yupi.springbootinit.service.UserService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
* @author 22683
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-05-17 22:24:58
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Resource
    UserService userService;

    @Resource
    UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    InterfaceInfoService interfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add){
            if (userInterfaceInfo.getLeftNum() < 0  ){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余次数不能小于0!");
            }
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误!");
            }
        }
    }
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        User user = userService.getById(userId);
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        //剩余调用次数必须大于0
        updateWrapper.gt("leftNum", 0);
        synchronized (user.toString().intern()) {
            updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        }
        return this.update(updateWrapper);
    }

    @Override
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(int limit) {
        List<UserInterfaceInfo> userinterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(limit);
        //转换成视图对象返回
        Map<Long, List<UserInterfaceInfo>> userinterfaceinfomap = userinterfaceInfoList.stream().
                collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        QueryWrapper<InterfaceInfo> queryWrapper=new QueryWrapper();

        queryWrapper.in("id", userinterfaceinfomap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoVO> infoVOList = list.stream().map(interfaceInfo ->
                {
                    InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
                    BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
                    int totalNum = userinterfaceinfomap.get(interfaceInfo.getId()).get(0).getTotalNum();
                    interfaceInfoVO.setTotalNum(totalNum);
                    return interfaceInfoVO;
                }
        ).collect(Collectors.toList());
        return ResultUtils.success(infoVOList);
    }

    @Override
    public BaseResponse<Long> addUserInterfaceInfo(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        this.validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = this.save(userInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newinterfaceinfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newinterfaceinfoId);
    }

}




