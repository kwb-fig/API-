package com.yupi.springbootinit.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common_kongapi.model.entity.InterfaceInfo;
import com.common_kongapi.model.entity.User;
import com.common_kongapi.model.entity.UserInterfaceInfo;
import com.google.gson.Gson;
import com.kongapi.kongapiclientsdk.client.kongApiClient;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.*;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInvokeRequest;

import com.yupi.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.yupi.springbootinit.model.enums.InterfaceInfoEnum;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import com.yupi.springbootinit.service.UserService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 接口管理
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/interfaceinfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceinfoService;

    @Resource
    private UserService userService;

    @Resource
    private kongApiClient kongApiClient;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceinfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addinterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceinfoAddRequest, HttpServletRequest request) {
        if (interfaceinfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoAddRequest, interfaceinfo);
        interfaceinfoService.validInterfaceInfo(interfaceinfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceinfo.setUserId(loginUser.getId());
        boolean result = interfaceinfoService.save(interfaceinfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newinterfaceinfoId = interfaceinfo.getId();
        return ResultUtils.success(newinterfaceinfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteinterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldinterfaceinfo = interfaceinfoService.getById(id);
        ThrowUtils.throwIf(oldinterfaceinfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldinterfaceinfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceinfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceinfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateinterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceinfoUpdateRequest) {
        if (interfaceinfoUpdateRequest == null || interfaceinfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceinfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceinfoUpdateRequest, interfaceinfo);
//        List<String> tags = interfaceinfoUpdateRequest.getTags();
//        if (tags != null) {
//            interfaceinfo.setTags(GSON.toJson(tags));
//        }
        // 参数校验
        interfaceinfoService.validInterfaceInfo(interfaceinfo, false);
        long id = interfaceinfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldinterfaceinfo = interfaceinfoService.getById(id);
        ThrowUtils.throwIf(oldinterfaceinfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceinfoService.updateById(interfaceinfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceinfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listinterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                     HttpServletRequest request) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceinfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);

    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listMyinterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                       HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceinfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);

    }

    // endregion

    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        com.kongapi.kongapiclientsdk.model.User user = new com.kongapi.kongapiclientsdk.model.User();
        user.setName("test");
        String username = kongApiClient.getUsernameByPost(user);
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.ONLINE.getValue());
        boolean result = interfaceinfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateofflineinterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceinfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.OFFLINE.getValue());
        boolean result = interfaceinfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

   /**
    * @param interfaceInvokeRequest
    * @return {@link BaseResponse< Boolean>}
    * @author kong
    * @date 2023/5/17 18:04
    */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeinterfaceInfo(@RequestBody InterfaceInvokeRequest interfaceInvokeRequest,
                                                     HttpServletRequest request) {
        if (interfaceInvokeRequest == null || interfaceInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = interfaceInvokeRequest.getId();
        // 判断接口是否存在
        InterfaceInfo interfaceInfo = interfaceinfoService.getById(id);
        String interfaceInfoName = interfaceInfo.getName();
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口不存在");
        }
        //判断接口是否开启
        Integer status = interfaceInfo.getStatus();
        if(status != InterfaceInfoEnum.ONLINE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口未开启");
        }
        User loginUser = userService.getLoginUser(request);

        //每个用户每个接口默认分配5次调用次数
        //1. 先查询是否有记录
        QueryWrapper<UserInterfaceInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId()).eq("interfaceInfoId",interfaceInvokeRequest.getId());
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if(userInterfaceInfo==null){
            UserInterfaceInfoAddRequest userInterfaceInfoAddRequest = new UserInterfaceInfoAddRequest();
            userInterfaceInfoAddRequest.setInterfaceInfoId(interfaceInvokeRequest.getId());
            userInterfaceInfoAddRequest.setUserId(loginUser.getId());
            userInterfaceInfoAddRequest.setTotalNum(0);
            userInterfaceInfoAddRequest.setLeftNum(5);
            //2. 没有记录，表示用户还没有调用过这个接口，默认分配5次
            userInterfaceInfoService.addUserInterfaceInfo(userInterfaceInfoAddRequest,request);
        }

        //获取用户登录信息，获取ak和sk
        String accessKey=loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();


        String userRequestParams = interfaceInvokeRequest.getUserRequestParams();


        Object result = reflectionInterface(kongApiClient.class, interfaceInfoName, userRequestParams, accessKey, secretKey);
        return ResultUtils.success(result);
    }

    /**
     *通过反射动态获取接口方法信息
     * @param reflectionClass
     * @param methodName
     * @param parameter
     * @param accessKey
     * @param secretKey
     * @return {@link Object}
     * @author kong
     * @date 2023/5/21 10:21
     */
    public Object reflectionInterface(Class<?> reflectionClass,String methodName,String parameter,String accessKey,String secretKey){
        //构造反射类的实例
        Object result = null;
        try {
            Constructor<?> constructor = reflectionClass.getDeclaredConstructor(String.class, String.class);
            //获取SDK的实例，同时传入密钥
            kongApiClient ApiClient = (kongApiClient) constructor.newInstance(accessKey, secretKey);
            //获取SDK中所有的方法
            Method[] methods = ApiClient.getClass().getMethods();
            //筛选出调用方法
            for (Method method : methods) {
                if(method.getName().equals(methodName)){
                    //获取方法参数类型
                    Class<?>[] parameterTypes = method.getParameterTypes();
//                    Method method1;
                    if (parameterTypes.length == 0){
//                        method1=ApiClient.getClass().getMethod(methodName);
                        return method.invoke(ApiClient);
                    }else {
//                        method1 = ApiClient.getClass().getMethod(methodName, parameterTypes[0]);
                        //getMethod，多参会考虑重载情况获取方法,前端传来参数是JSON格式转换为String类型
                        //参数Josn化
                        Gson gson = new Gson();
                        Object args = gson.fromJson(parameter, parameterTypes[0]);
                        return result = method.invoke(ApiClient, args);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"反射调用参数错误");
        }
        return result;
    }

}

