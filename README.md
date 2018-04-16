## 使用说明
1. 基于spring boot框架

## 配置maven依赖

```
<dependency>
    <artifactId>common-spring-boot-starter</artifactId>
    <groupId>com.php25</groupId>
    <version>1.1.1-RELEASE</version>
</dependency>
```

## model实体类

1. 实体类可以继承BaseModel

BaseModel中有一个id主键属性项，生成规则是uuid

2. 实体类可以继承BaseSoftDeleteModel

BaseSoftDeleteModel继承于BaseModel，加入了enable属性项，可用于软删除

## repository层

1. 在main方法的主类上加入 @EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class) 与 @EnableTransactionManagement两个注解

2. 所有的自定义的Repository接口，必须要继承BaseRepository接口，并且加上@Repository注解

泛型中有2个参数，从左至右，分别是实体类与此实体类的主键id
```
//所有的自定义的Repository接口，必须要继承BaseRepository接口
@Repository
public interface AdminUserRepository extends BaseRepository<AdminUser, String> {

    AdminUser findByLoginNameAndPassword(String username, String password);

}
```
实现类必须要以"接口名+Impl"结尾，如下

```
public class AdminUserRepositoryImpl {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public AdminUser findByLoginNameAndPassword(String loginName, String password) {
        Query query = entityManager.createQuery("select a from AdminUser a where a.username=?1 and a.password=?2 and a.enable=1");
        query.setParameter(1, loginName);
        query.setParameter(2, password);
        try {
            return (AdminUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
```

## service层

1. 所有的自定义Service接口，必须要继承BaseService接口

如果需要软删除的操作，可以继续继承SoftDeletable

BaseService泛型中有两个参数，从左至右分别是dto与model

SoftDeletable泛型中传入dto

```
public interface CustomerService extends BaseService<CustomerDto, Customer>, SoftDeletable<CustomerDto> {

}
```
### 关系型数据库

1. 所有的自定义Service实现类，可以继承BaseServiceImpl类，此类用spring jpa实现

```
@Transactional
@Service
public class CustomerServiceImpl extends BaseServiceImpl<CustomerDto, Customer> implements CustomerService {

}
```

2. 所有的自定义Service实现类，可以继承BaseNutzServiceImpl类，此类用Nutz实现

```
@Transactional
@Service
public class CustomerServiceImpl extends BaseNutzServiceImpl<CustomerDto, Customer> implements CustomerService {

}
```
### es库

1. 所有的自定义Service实现类，可以继承BaseEsServiceImpl类 ,此类可以用于操作es库

```
@Transactional
@Service
public class CustomerServiceImpl extends BaseEsServiceImpl<CustomerDto, Customer> implements CustomerService {

}
```

## controller层

1. 必须继承JsonController,并且使用@controller注解标识此controller类，自定义的controller类必须以Controller关键字结尾。如：

```
@Controller
@RequestMapping("/api")
public class ApiController extends JSONController {

}
```

2. 效验请求参数的合法性

基于spring validation，请统一 throws JsonException。如下:

注意: @NotEmpty注解

```
 @RequestMapping(value = "/insecure/common/SSOLogin.do", method = RequestMethod.GET)
 public
 @ResponseBody
 JSONResponse SSSLogin(@NotEmpty String mobile, @NotEmpty String password) throws JsonException {
 
 }
```

3. 接口返回值

api接口统一使用JSONResponse对象返回，JsonController这个父类中定义了succeed()与failed()方法用于生成这个返回对象。如下:

```
@RequestMapping(value = "/insecure/common/SSOLogin.do", method = RequestMethod.GET)
public
@ResponseBody
JSONResponse SSSLogin(@NotEmpty String mobile, @NotEmpty String password) throws JsonException {
    CustomerDto customer = customerRest.findOneByPhoneAndPassword(mobile, password);
    if (null != customer) {
        String jwtCustomerId = kongJwtRest.generateJwtCustomerId(customer);
        kongJwtRest.createJwtCustomer(jwtCustomerId);
        JwtCredentialDto jwtCredentialDto = kongJwtRest.generateJwtCredential(jwtCustomerId);
        String jwt = kongJwtRest.generateJwtToken(jwtCredentialDto);
        return succeed(jwt);
    } else {
        return failed("登入失败");
    }
}
```

JSONResponse类中有三个参数，分别如下:

参数名 | 说明
---|---
errorCode | 0表示没有错误一切正常，1001服务器错误， 1002业务逻辑错误
returnObject | 返回需要用到的对象数据都会在这里面。上面这个例子是一个用户对象
message | 如果发生错误如业务逻辑错误或者服务器错误，这里面就是错误的信息

4. 统一错误处理

api接口请统一throw一个JsonException

5. 

## 分层

### 使用三层设计，分别是repository、service、controller

调用顺序是controller==>service==>repository

controller里不要出现repository层的代码，只能出现service层。

controller与service层通过dto对象交互数据，不能出现model

service与repository层交互使用model。

controller暴露给外部系统的数据使用vo


### VO，DTO，MODEL概念

VO: 用于controller暴露给外部系统的数据

DTO: 本系统内部传输的数据

MODEL: 对应与数据库的实体类
 

### dto与model互转

可以使用Spring框架中的BeanUitls类的copyProperties方法。如果属性项名字一样的话，就会执行拷贝。否则属性项位null

```

import org.springframework.beans.BeanUtils;

BeanUtils.copyProperties(adminRole, adminRoleDto);
```



