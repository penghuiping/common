package com.baidu.fsg.uid.worker.dao;

import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import com.php25.common.db.Db;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author: penghuiping
 * @date: 2019/8/27 13:22
 * @description:
 */
@Repository
public class WorkerNodeDaoImpl implements WorkerNodeDAO {

    private Db db;

    public WorkerNodeDaoImpl(Db db) {
        this.db = db;
    }

    @Override
    public WorkerNodeEntity getWorkerNodeByHostPort(String host, String port) {
        return db.cndJdbc(WorkerNodeEntity.class).whereEq("hostName", host).andEq("port", port).single();
    }

    @Override
    public void addWorkerNode(WorkerNodeEntity workerNodeEntity) {
        workerNodeEntity.setModified(new Date());
        workerNodeEntity.setCreated(new Date());
        db.cndJdbc(WorkerNodeEntity.class).insert(workerNodeEntity);
    }
}
