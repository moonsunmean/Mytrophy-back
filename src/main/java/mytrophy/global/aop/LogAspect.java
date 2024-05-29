package mytrophy.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("within(mytrophy.api.*.controller..*)")
    public void controller() {}

    @Before("controller()")
    public void beforeRequest(JoinPoint joinPoint) {
        log.info("###### 요청한 메서드 : {}", joinPoint.getSignature().toShortString());

        // 메서드에 전달된 인자 로깅
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                // DTO인 경우 필드 값을 출력
                if (args[i].getClass().getName().startsWith("your.package.name")) { // 여기에 DTO 패키지명을 입력하세요.
                    try {
                        Field[] fields = args[i].getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            Object value = field.get(args[i]);
                            log.info("\t{} = {}", field.getName(), value);
                        }
                    } catch (IllegalAccessException e) {
                        log.error("DTO 필드 로깅 중 오류 발생: {}", e.getMessage());
                    }
                } else {
                    log.info("\t{} = {}", parameterNames[i], args[i]);
                }
            } else {
                log.info("\t{} = null", parameterNames[i]);
            }
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
                        log.error("메서드 내 변수 로깅 중 오류 발생: {}", e.getMessage());
                    }
                });
    }

    @AfterReturning(pointcut = "controller()", returning = "returnValue")
    public void afterReturningLogging(JoinPoint joinPoint, Object returnValue) {
        log.info("###### 요청 처리 완료 : {}", joinPoint.getSignature().toShortString());

        if (returnValue == null) {
            log.info("\t반환 값: null");
            return;
        }

        // 반환 값이 DTO인 경우 필드 값을 출력
        if (returnValue.getClass().getName().startsWith("your.package.name")) { // 여기에 DTO 패키지명을 입력하세요.
            try {
                Field[] fields = returnValue.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(returnValue);
                    log.info("\t{} = {}", field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                log.error("DTO 반환 값 필드 로깅 중 오류 발생: {}", e.getMessage());
            }
        } else {
            log.info("\t반환 값: {}", returnValue.toString());
        }
    }


}
