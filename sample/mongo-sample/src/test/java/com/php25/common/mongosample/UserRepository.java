package com.php25.common.mongosample;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author penghuiping
 * @date 2019/11/6 13:18
 */
public interface UserRepository extends MongoRepository<User, Long> {


}
