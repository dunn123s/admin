package com.admin.project.controller;

import com.admin.common.result.CommonResult;
import com.admin.project.service.GcUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 会员基本信息 控制层
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "GcUserController", description = "会员基本信息相关接口")
public class GcUserController {

    @Autowired
    private GcUserService gcUserService;

    /**
     * 生成手机登录验证码
     */
    @PostMapping("/generateCode")
    @ApiOperation("生成手机登录验证码接口")
    public CommonResult generateCode(@RequestParam(name = "phone", required = true) String phone) {
        return CommonResult.success(gcUserService.generateCode(phone));
    }

    /**
     * 会员注册（手机号+手机验证码）
     * （目前只根据用户名，密码，手机号和手机验证码注册，后续功能在迭代）
     */
    @PostMapping("/register")
    @ApiOperation("会员注册接口")
    public CommonResult register(@RequestParam(name = "username", required = true) String username,
                                 @RequestParam(name = "password", required = true) String password,
                                 @RequestParam(name = "phone", required = true) String phone,
                                 @RequestParam(name = "code", required = true) String code) {
        return CommonResult.success(gcUserService.register(username, password, phone, code));
    }

    /**
     * 会员用户名和密码登录（登录成功后产生token, app挂载全局token识别登录会员）
     * (目前只做用户名和密码登录，后续绑定微信和qq，继续迭代微信和qq一键登录功能)
     */
    @PostMapping("/login")
    @ApiOperation("会员用户名和密码登录接口")
    public CommonResult login(@RequestParam(name = "username", required = true) String username,
                              @RequestParam(name = "password", required = true) String password) {
        return CommonResult.success(gcUserService.login(username, password));
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    @ApiOperation("修改会员登录密码接口")
    public CommonResult updatePassword(@RequestParam(name = "phone", required = true) String phone,
                                       @RequestParam(name = "code", required = true) String code,
                                       @RequestParam(name = "password", required = true) String password) {
        return CommonResult.success(gcUserService.updatePassword(phone, code, password));
    }

    /**
     * 会员头像上传和修改
     */
    @PostMapping("/upload")
    @ApiOperation("会员头像上传和修改接口")
    public CommonResult upload(MultipartFile file) throws Exception {

        return CommonResult.success(gcUserService.upload(file));
    }
}
