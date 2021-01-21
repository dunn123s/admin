package com.admin.project.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 会员基本信息表
 */
@Data
@TableName("gc_login_log")
public class GcLoginLog {
    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "login_log_id")
    private Integer loginLogId ;

    @ApiModelProperty("用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("用户名")
    @TableField("user_id")
    private int userId;

    @ApiModelProperty(value = "登录时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "登录状态")
    private boolean succeed;

    @ApiModelProperty(value = "登录Ip")
    @TableField("ip_address")
    private String ipAddress;


    @ApiModelProperty(value = "登录地址")
    @TableField("user_address")
    private String userAddress;
    @ApiModelProperty(value = "异常信息")
    private String mes;

}
