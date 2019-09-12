package com.php25.common.validation.test;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.validation.CommonAutoConfigure;
import com.php25.common.validation.util.ValidationResult;
import com.php25.common.validation.util.ValidatorUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class ValidationTest {
    private static final Logger log = LoggerFactory.getLogger(ValidationTest.class);


    @Test
    public void validateMethod() throws Exception {
        MyObject myObject = new MyObject();

        //身份证 正例
        Method method0 = ReflectUtil.getMethod(myObject.getClass(), "printIdCard", String.class);
        ValidationResult validationResult0 = ValidatorUtil.validateParameters(myObject, method0, new Object[]{"110101199003072957"});
        Assert.assertTrue(!validationResult0.isHasErrors());

        //身份证 反例
        Method method1 = ReflectUtil.getMethod(myObject.getClass(), "printIdCard", String.class);
        ValidationResult validationResult1 = ValidatorUtil.validateParameters(myObject, method1, new Object[]{"11010119900307295711"});
        Assert.assertTrue(validationResult1.isHasErrors());

        //邮箱 正例
        Method method2 = ReflectUtil.getMethod(myObject.getClass(), "printEmail", String.class);
        ValidationResult validationResult2 = ValidatorUtil.validateParameters(myObject, method2, new Object[]{"123@qq.com"});
        Assert.assertTrue(!validationResult2.isHasErrors());

        //邮箱 反例
        Method method3 = ReflectUtil.getMethod(myObject.getClass(), "printEmail", String.class);
        ValidationResult validationResult3 = ValidatorUtil.validateParameters(myObject, method3, new Object[]{"123@.com"});
        Assert.assertTrue(validationResult3.isHasErrors());

        //ipv4 正例
        Method method4 = ReflectUtil.getMethod(myObject.getClass(), "printIpv4", String.class);
        ValidationResult validationResult4 = ValidatorUtil.validateParameters(myObject, method4, new Object[]{"192.168.1.1"});
        Assert.assertTrue(!validationResult4.isHasErrors());

        //ipv4 反例
        Method method5 = ReflectUtil.getMethod(myObject.getClass(), "printIpv4", String.class);
        ValidationResult validationResult5 = ValidatorUtil.validateParameters(myObject, method5, new Object[]{"192.168.1256.你好"});
        Assert.assertTrue(validationResult5.isHasErrors());

        //ipv6 正例
        Method method6 = ReflectUtil.getMethod(myObject.getClass(), "printIpv6", String.class);
        ValidationResult validationResult6 = ValidatorUtil.validateParameters(myObject, method6, new Object[]{"2001:3CA1:010F:001A:121B:0000:0000:0010"});
        Assert.assertTrue(!validationResult6.isHasErrors());

        //ipv6 反例
        Method method7 = ReflectUtil.getMethod(myObject.getClass(), "printIpv6", String.class);
        ValidationResult validationResult7 = ValidatorUtil.validateParameters(myObject, method7, new Object[]{"192.168.1256.你好"});
        Assert.assertTrue(validationResult7.isHasErrors());


        //手机 正例
        Method method8 = ReflectUtil.getMethod(myObject.getClass(), "printMobile", String.class);
        ValidationResult validationResult8 = ValidatorUtil.validateParameters(myObject, method8, new Object[]{"18812345678"});
        Assert.assertTrue(!validationResult8.isHasErrors());

        //手机 反例
        Method method9 = ReflectUtil.getMethod(myObject.getClass(), "printMobile", String.class);
        ValidationResult validationResult9 = ValidatorUtil.validateParameters(myObject, method9, new Object[]{"188123456781"});
        Assert.assertTrue(validationResult9.isHasErrors());

        //金额 正例
        Method method10 = ReflectUtil.getMethod(myObject.getClass(), "printMoney", String.class);
        ValidationResult validationResult10 = ValidatorUtil.validateParameters(myObject, method10, new Object[]{"100.10"});
        Assert.assertTrue(!validationResult10.isHasErrors());

        //金额 反例
        Method method11 = ReflectUtil.getMethod(myObject.getClass(), "printMoney", String.class);
        ValidationResult validationResult11 = ValidatorUtil.validateParameters(myObject, method11, new Object[]{"ab"});
        Assert.assertTrue(validationResult11.isHasErrors());

        //邮编 正例
        Method method12 = ReflectUtil.getMethod(myObject.getClass(), "printZipCode", String.class);
        ValidationResult validationResult12 = ValidatorUtil.validateParameters(myObject, method12, new Object[]{"201199"});
        Assert.assertTrue(!validationResult12.isHasErrors());

        //邮编 反例
        Method method13 = ReflectUtil.getMethod(myObject.getClass(), "printZipCode", String.class);
        ValidationResult validationResult13 = ValidatorUtil.validateParameters(myObject, method13, new Object[]{"ab123"});
        Assert.assertTrue(validationResult13.isHasErrors());

    }


}
