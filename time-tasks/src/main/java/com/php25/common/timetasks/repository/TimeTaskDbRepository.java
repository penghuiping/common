package com.php25.common.timetasks.repository;

import com.php25.common.db.repository.BaseDbRepository;
import com.php25.common.timetasks.model.TimeTaskDb;
import com.php25.common.timetasks.timewheel.TimeTask;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author penghuiping
 * @date 2020/8/24 13:55
 */
public interface TimeTaskDbRepository extends BaseDbRepository<TimeTaskDb, String> {

    void save(TimeTask timeTask);

    void deleteAll(List<String> jobIds);

    void deleteAllInvalidJob();

    List<TimeTask> findByExecuteTimeScope(LocalDateTime start, LocalDateTime end);

    String generateJobId();
}
