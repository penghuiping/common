package com.php25.common.timer.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author penghuiping
 * @date 2021/3/19 10:42
 */
@Setter
@Getter
@TableName("t_timer_inner_log")
public class TimerInnerLogPo {

    /**
     * job id
     */
    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 执行时间(单位毫秒)
     */
    @TableField("execution_time")
    private Long executionTime;

    /**
     * 0:未执行 1:已执行
     */
    @TableField("status")
    private Integer status;
}
