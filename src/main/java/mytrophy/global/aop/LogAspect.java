package mytrophy.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("within(mytrophy.api.game.controller..*)")
    public void controller() {}

    @Before("controller()")
    public void beforeRequest(JoinPoint joinPoint) {
        log.info("###Start request {}", joinPoint.getSignature().toShortString());

        // 메서드에 전달된 인자 로깅
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            log.info("\t{} = {}", parameterNames[i], args[i]);
        }

        // 메서드 내에서 선언된 변수명과 값 로깅
        Arrays.stream(joinPoint.getSignature().getDeclaringType().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(joinPoint.getTarget());
                        log.info("\t{} = {}", field.getName(), value);
                    } catch (IllegalAccessException e) {
                        log.error("Error logging field {} in {}: {}", field.getName(), joinPoint.getSignature().toShortString(), e.getMessage());
                    }
                });
    }

    @AfterReturning(pointcut = "controller()", returning = "returnValue")
    public void afterReturningLogging(JoinPoint joinPoint, Object returnValue) {
        log.info("###End request {}", joinPoint.getSignature().toShortString());

        if (returnValue == null) return;

        log.info("\t{}", returnValue.toString());
    }

}
