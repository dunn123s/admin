package com.admin.project.service;

import com.admin.project.entity.GcUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

/**
 * 会员信息 服务层接口
 */
public interface GcUserService {

    /**
     * 根据用户名获取会员
     */
    GcUser getByUsername(String username);

    /**
     * 获取会员信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 生成手机登录验证码
     */
    String generateCode(String phone);

    /**
     * 会员注册（手机号+手机验证码）
     * （目前只根据用户名，密码，手机号和手机验证码注册，后续功能在迭代）
     */
    boolean register(String username, String password, String phone, String code);

    /**
     * 会员登录（登录成功后产生token, app挂载全局token识别登录会员）
     * (目前只做用户名和密码登录，后续绑定微信和qq，继续迭代微信和qq一键登录功能)
     */
    HashMap login(String username, String password);

    /**
     * 修改密码
     */
    boolean updatePassword(String phone, String code, String password);

    /**
     * 会员头像上传和修改
     */
    boolean upload(MultipartFile file) throws Exception;
}
