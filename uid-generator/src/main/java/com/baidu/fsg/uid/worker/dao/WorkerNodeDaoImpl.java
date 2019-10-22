package com.baidu.fsg.uid.worker.dao;

import com.baidu.fsg.uid.worker.entity.WorkerNode1Entity;
import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author: penghuiping
 * @date: 2019/8/27 13:22
 * @description:
 */
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
        workerNodeEntity.setModified(LocalDateTime.now());
        workerNodeEntity.setCreated(LocalDateTime.now());
        DbType dbType = db.getDbType();
        switch (dbType) {
            case MYSQL: {
                db.cndJdbc(WorkerNodeEntity.class).insert(workerNodeEntity);
                break;
            }
            case POSTGRES: {
                WorkerNode1Entity workerNode1Entity = new WorkerNode1Entity();
                BeanUtils.copyProperties(workerNodeEntity, workerNode1Entity);
                db.cndJdbc(WorkerNode1Entity.class).insert(workerNode1Entity);
                workerNodeEntity.setId(workerNode1Entity.getId());
                break;
            }
            case ORACLE: {
                WorkerNode1Entity workerNode1Entity = new WorkerNode1Entity();
                BeanUtils.copyProperties(workerNodeEntity, workerNode1Entity);
                db.cndJdbc(WorkerNode1Entity.class).insert(workerNode1Entity);
                workerNodeEntity.setId(workerNode1Entity.getId());
                break;
            }
            default: {
                db.cndJdbc(WorkerNodeEntity.class).insert(workerNodeEntity);
                break;
            }
        }


    }
}
