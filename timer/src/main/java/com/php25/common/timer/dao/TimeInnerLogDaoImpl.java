package com.php25.common.timer.dao;

import com.php25.common.timer.po.TimerInnerLogPo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author penghuiping
 * @date 2022/1/15 13:41
 */
public class TimeInnerLogDaoImpl implements TimerInnerLogDao {

    private final JdbcTemplate jdbcTemplate;

    public TimeInnerLogDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TimerInnerLogPo findOneByIdAndExecutionTime(String id, Long executionTime) {
        return jdbcTemplate.queryForObject("select * from t_timer_inner_log where `id`=? and `execution_time`=?", new BeanPropertyRowMapper<>(), id, executionTime);
    }

    @Override
    public Boolean updateStatusByIdAndExecutionTime(Integer status, String id, Long executionTime) {
        int rows = jdbcTemplate.update("update t_timer_inner_log set `status`=? where `id`=? and `execution_time`=?", status, id, executionTime);
        return rows > 0;
    }

    @Override
    public Boolean insert(TimerInnerLogPo timerInnerLog) {
        int rows = jdbcTemplate.update("insert into t_timer_inner_log(`id`,`execution_time`,`status`)values(?,?,?)", timerInnerLog.getId(), timerInnerLog.getExecutionTime(), timerInnerLog.getStatus());
        return rows > 0;
    }
}
