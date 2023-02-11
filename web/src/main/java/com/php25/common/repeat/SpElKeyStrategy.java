package com.php25.common.repeat;

import com.php25.common.core.exception.Exceptions;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author penghuiping
 * @date 2023/2/11 22:53
 */
public class SpElKeyStrategy implements GetKeyStrategy {

    private final static SpelExpressionParser parser = new SpelExpressionParser();
    private final static DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();

    @Override
    public String getKey(Context context) {
        ProceedingJoinPoint pjp = context.getPjp();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Annotation[] annotations = method.getDeclaredAnnotations();
        AvoidRepeat annotation0 = (AvoidRepeat) Arrays.stream(annotations).filter(annotation -> annotation instanceof AvoidRepeat).findFirst().get();
        String spEl = annotation0.expression();
        Expression expression = parser.parseExpression(spEl);
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        Object[] args = pjp.getArgs();
        String[] parameterNames = discover.getParameterNames(method);
        if (null == parameterNames || parameterNames.length == 0) {
            throw Exceptions.throwIllegalStateException("此方法没有参数,不支持SpElKeyStrategy");
        }
        for (int i = 0; i < parameterNames.length; i++) {
            evaluationContext.setVariable(parameterNames[i], args[i]);
        }
        return expression.getValue(evaluationContext, String.class);
    }
}
