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

    @TableField("step")
    private Integer step;

    @TableField("delta")
    private Integer delta;

    @TableField("remainder")
    private Integer remainder;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("version")
    private Long version;
}
