package mytrophy.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.el.util.ReflectionUtil.getMethod;

@Component
@Aspect
@Slf4j
public class LogAspect {
    @Pointcut("within(mytrophy.api.*.controller..*)")
    public void controller() {}

    // Pointcut 정의: ResponseEntity를 반환하는 메서드
    @Pointcut("execution(org.springframework.http.ResponseEntity *.*(..))")
    private void responseEntityMethods() {}

    @Before("controller()")
    public void beforeRequest(JoinPoint joinPoint) {

        // 요청한 경로 로깅
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestUrl = request.getRequestURL().toString();
        log.info("요청한 경로: " + requestUrl);

        // 컨트롤러 이름 로깅
        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        log.info("컨트롤러 이름: " + controllerName);

        // 메서드 정보 받아오기
        Method method = getMethod(joinPoint);
        log.info("메서드 이름: " + method.getName());

        /// 컨트롤러의 메서드 파라미터 로깅
        Object[] args = joinPoint.getArgs();
        if (args.length <= 0) {
            log.info("메서드에 전달된 인자가 없습니다.");
        } else {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = methodSignature.getParameterNames();

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null) {
                    // 파라미터의 타입과 값 로깅
                    log.info("파라미터의 타입: {}",  arg.getClass().getSimpleName());
                    log.info("파라미터의 값: {}",  arg);

                    // 파라미터의 선언된 이름 로깅
                    if (parameterNames != null && parameterNames.length > i) {
                        String parameterName = parameterNames[i];
                        log.info("파라미터의 선언된 이름: {}", parameterName);
                    }
                } else {
                    log.info("파라미터의 값: null");
                }
            }
        }

        // DTO의 이름과 변수값 로깅
        Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg != null && arg.getClass().getName().startsWith("your.package.name"))
                .forEach(arg -> {
                    log.info("DTO 이름: " + arg.getClass().getSimpleName());
                    log.info("DTO 변수값:");
                    Field[] fields = arg.getClass().getDeclaredFields();
                    Arrays.stream(fields)
                            .filter(field -> !Modifier.isStatic(field.getModifiers()))
                            .forEach(field -> {
                                field.setAccessible(true);
                                try {
                                    log.info("\t" + field.getName() + ": " + field.get(arg));
                                } catch (IllegalAccessException e) {
                                    log.info("DTO 필드 값을 가져오는 중 오류 발생: " + e.getMessage());
                                }
                            });
                });
    }

    // 컨트롤러 메서드 실행 후 반환값 로깅
    @AfterReturning(pointcut = "responseEntityMethods()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
        // 메서드 정보 받아오기
        Method method = getMethod(joinPoint);
        log.info("메서드 이름: " + method.getName());

        log.info("반환 타입: " + returnObj.getClass().getSimpleName());
        log.info("반환 값: " + returnObj);
    }

    // JoinPoint로 메서드 정보 가져오기
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}


