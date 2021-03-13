package com.php25.common.timer.repository;

import com.php25.common.timer.model.JobModel;

import java.util.List;

/**
 * @author penghuiping
 * @date 2021/3/13 12:02
 */
public interface JobModelRepository {


    void create(JobModel job);

    void update(JobModel job);

    void deleteAll(List<String> jobIds);

    List<JobModel> findAllEnabled();

    JobModel findById(String id);
}
