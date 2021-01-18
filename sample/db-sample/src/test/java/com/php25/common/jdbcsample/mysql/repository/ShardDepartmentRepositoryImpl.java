//package com.php25.common.jdbcsample.mysql.repository;
//
//import com.php25.common.db.EntitiesScan;
//import com.php25.common.db.repository.shard.BaseShardDbRepositoryImpl;
//import com.php25.common.db.repository.shard.ShardRule;
//import com.php25.common.db.repository.shard.TwoPhaseCommitTransaction;
//import com.php25.common.jdbcsample.mysql.model.Department;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * @author penghuiping
// * @date 2020/9/14 14:50
// */
//@Profile(value = "many_db")
//@Repository
//public class ShardDepartmentRepositoryImpl extends BaseShardDbRepositoryImpl<Department, Long> implements ShardDepartmentRepository {
//
//    public ShardDepartmentRepositoryImpl(List<EntitiesScan> dbList, ShardRule shardRule, TwoPhaseCommitTransaction twoPhaseCommitTransaction) {
//        super(dbList, shardRule,twoPhaseCommitTransaction);
//    }
//}
