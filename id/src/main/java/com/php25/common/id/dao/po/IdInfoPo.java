package com.php25.common.id.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author penghuiping
 * @date 2022-01-05
 */
@Setter
@Getter
@TableName("t_id_info")
public class IdInfoPo {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 初始的id
     */
    @TableField("begin_id")
    private Long beginId;

    /**
     * 目前最大的id
     */
    @TableField("max_id")
    private Long maxId;

    /**
     * 步长,每次获取号段都会是maxId+step
     */
    @TableField("step")
    private Integer step;

    /**
     * 每次id增量
     */
    @TableField("delta")
    private Integer delta;

    /**
     * 余数
     */
    @TableField("remainder")
    private Integer remainder;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 版本号
     */
    @TableField("version")
    private Long version;
}
