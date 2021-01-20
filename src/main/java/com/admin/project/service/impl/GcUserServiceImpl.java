package com.admin.project.service.impl;

import com.admin.common.base.BaseServiceImpl;
import com.admin.common.util.FileUploadUtils;
import com.admin.common.util.JwtTokenUtil;
import com.admin.common.util.RedisUtils;
import com.admin.project.entity.GcLoginLog;
import com.admin.project.entity.GcUser;
import com.admin.project.entity.vo.GcUserDetails;
import com.admin.project.mapper.GcLoginLogMapper;
import com.admin.project.mapper.GcUserMapper;
import com.admin.project.service.GcUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.chuang.urras.toolskit.third.javax.servlet.HttpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 会员基本信息 服务层实现类
 */
@Slf4j
@Service
public class GcUserServiceImpl extends BaseServiceImpl implements GcUserService {

    @Resource
    private GcUserMapper gcUserMapper;

    @Resource
    private GcLoginLogMapper gcLoginLogMapper;


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 根据用户名获取会员
     */
    @Override
    public GcUser getByUsername(String username) {
        GcUser exist_gcUser = gcUserMapper.selectOne(new QueryWrapper<GcUser>().eq("username", username));
        if (exist_gcUser == null) throw new ApiException("查询会员信息失败！");
        return exist_gcUser;
    }

    /**
     * 获取会员信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        GcUser exist_gcUser = getByUsername(username);
        if (exist_gcUser != null) return new GcUserDetails(exist_gcUser);
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    /**
     * 生成手机登录验证码
     */
    @Override
    public String generateCode(String phone) {
        // 手机正则校验
        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(phone);
        boolean b = matcher.find();
        if (!b) throw new ApiException("手机号码格式不正常！");

        String code_key = "code_key" + phone;
        String object = (String) redisUtils.get(code_key);
        System.out.println("缓存信息：" + object);
        if (object == null) {
            StringBuilder stringBuilder = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                stringBuilder.append(random.nextInt(10));
            }
            object = stringBuilder.toString();
            redisUtils.set(code_key, object, 5L, TimeUnit.MINUTES);
        }
        return object;
    }

    /**
     * 会员注册（手机号+手机验证码）
     * （目前只根据用户名，密码，手机号和手机验证码注册，后续功能在迭代）
     */
    @Override
    public boolean register(String username, String password, String phone, String code) {
        // 参数校验
        if (null == username) throw new ApiException("用户名不能为空！");
        if (null == password) throw new ApiException("密码不能为空！");
        if (null == phone) throw new ApiException("手机号不能为空！");
        if (null == code) throw new ApiException("验证码不能为空！");

        // 手机正则校验
        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(phone);
        boolean b = matcher.find();
        if (!b) throw new ApiException("手机号码格式不正常！");

        // 手机唯一性
        GcUser gcUser_phone = gcUserMapper.selectOne(new QueryWrapper<GcUser>().eq("phone", phone));
        if (gcUser_phone != null) throw new ApiException("手机号已存在，请重新输入手机号！");

        // 校验手机号验证码
        String phone_code = (String) redisUtils.get("code_key" + phone);
        if (!code.equals(phone_code)) throw new ApiException("验证码不正确，请重新输入！");

        // 用户名唯一性校验
        GcUser exist_gcUser = gcUserMapper.selectOne(new QueryWrapper<GcUser>().eq("username", username));
        if (null != exist_gcUser) throw new ApiException("用户名已存在，请重新输入！");

        // 入库会员（后续相关的字段通过会员账号的绑定，继续优化）
        GcUser create_gcUser = new GcUser();
        create_gcUser.setAppId(generateAppId());
        create_gcUser.setUsername(username);
        create_gcUser.setPhone(phone);
        create_gcUser.setPassword(passwordEncoder.encode(password));
        create_gcUser.setCreateTime(new Date());
        create_gcUser.setStatus(1);
        int count = gcUserMapper.insert(create_gcUser);
        if (count <= 0) return false;

        // 短信通知会员已注册成功 todo

        return true;
    }

    /**
     * 会员登录（登录成功后产生token, app挂载全局token识别登录会员）
     * (目前只做用户名和密码登录，后续绑定微信和qq，继续迭代微信和qq一键登录功能)
     */
    @Override
    public HashMap login(String username, String password) {
        HashMap map = new HashMap<>();
        String login_token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword()))
                throw new BadCredentialsException("密码不正确");
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            login_token = jwtTokenUtil.generateToken(userDetails);
            map.put("token", tokenHead + " " + login_token);

            // 入库登录记录表 todo

            GcLoginLog gcLoginLog = new GcLoginLog();
            gcLoginLog.setUserId(getUserId());
            gcLoginLog.setUserName(username);
            gcLoginLog.setCreateTime(new Date());
            String ip = HttpKit.getIpAddress().orElse("127.0.0.1");
            gcLoginLog.setIpAddress(ip);

            gcLoginLog.setSucceed(true);

            gcLoginLogMapper.insert(gcLoginLog);


            System.out.println("当前登录的会员id:" + getUserId());
            System.out.println("当前登录的会员名称：" + getUsername());
        } catch (Exception e) {

            // 入库登录记录表 todo

            GcLoginLog gcLoginLog = new GcLoginLog();

            gcLoginLog.setUserName(username);
            gcLoginLog.setCreateTime(new Date());
            String ip = HttpKit.getIpAddress().orElse("127.0.0.1");
            gcLoginLog.setIpAddress(ip);
            gcLoginLog.setSucceed(false);
            gcLoginLog.setMes(e.getMessage());
            gcLoginLogMapper.insert(gcLoginLog);
            log.warn("登录异常:{}", e.getMessage());
            throw new ApiException(e.getMessage());
        }
        return map;
    }

    /**
     * 修改密码
     */
    @Override
    public boolean updatePassword(String phone, String code, String password) {
        // 根据当前登录的用户名（唯一性）查询会员信息, 只能修改自己的密码。
        GcUser exist_gcUser = gcUserMapper.selectOne(new QueryWrapper<GcUser>().eq("username", getUsername()));
        if (null == exist_gcUser) throw new ApiException("查询失败，请检查登录的个人信息是否错误");
        if (!phone.equals(exist_gcUser.getPhone())) throw new ApiException("您的账号信息有误，您只能修改您自己的密码！");

        // 参数校验
        if (null == phone) throw new ApiException("手机号不能为空！");
        if (null == code) throw new ApiException("验证码不能为空！");
        if (null == password) throw new ApiException("密码不能为空！");

        // 校验手机号验证码
        String phone_code = (String) redisUtils.get("code_key" + phone);
        if (!code.equals(phone_code)) throw new ApiException("验证码不正确，请重新输入！");

        // 入库
        GcUser gcUser = new GcUser();
        gcUser.setId(exist_gcUser.getId());
        gcUser.setPassword(passwordEncoder.encode(password));
        int count = gcUserMapper.updateById(gcUser);
        if (count <= 0) return false;
        return true;
    }

    /**
     * 会员头像上传和修改
     */
    @Override
    public boolean upload(MultipartFile file) throws Exception {
        // 图片非空校验
        String originalFilename = file.getOriginalFilename();
        if (null == originalFilename || "".equals(originalFilename)) throw new ApiException("图片不能为空！");

        // 获取登录的会员id查询会员信息
        GcUser exist_gcUser = gcUserMapper.selectOne(new QueryWrapper<GcUser>().eq("id", getUserId()));
        if (null == exist_gcUser) throw new ApiException("查询会员信息有误");

        // 上传头像
        GcUser gcUser = new GcUser();
        gcUser.setId(getUserId());
        String icon = FileUploadUtils.upload(file);
        gcUser.setIcon(icon);
        int count = gcUserMapper.updateById(gcUser);
        if (count <= 0) return false;
        return true;
    }

    /**
     * 随机生成会员的唯一标识 appId
     */
    private String generateAppId() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dateStr = format.format(new Date());
        int random = 1000 + (int) (Math.random() * 9000);
        return "IDC" + dateStr + random;
    }
}
