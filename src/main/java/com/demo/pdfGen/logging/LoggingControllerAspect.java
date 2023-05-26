package com.demo.pdfGen.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@Component
@Slf4j
public class LoggingControllerAspect {

// @Around("@annotation(TrackTime)")
// private static final Logger log = org.slf4j.LoggerFactory.getLogger(LoggingControllerAspect.class);

// @Around("@annotation(TrackTime)") // package info for TrackTime
// public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
// Long start = System.currentTimeMillis();
//
// Object[] args = joinPoint.getArgs(); // arguments passed in method
// String className = joinPoint.getTarget().getClass().toString();
// String methodName = joinPoint.getSignature().getName();
// //MDC.put(Constants.TRACE_ID,UUID.randomUUID().toString());
// log.info(" Request | {}.{}() : args {}", className, methodName, Arrays.asList(args));
// Object obj = joinPoint.proceed();
// log.info(" Response | {}.{}() : args {}", className, methodName, obj);
// long time = System.currentTimeMillis() - start;
// log.info("Total Time taken by method: {}.{} :: {} milliseconds", className, methodName, time);
// return obj;
// }
    
    @Around("@annotation(TrackTime)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        
        long start = System.currentTimeMillis();
        logEntry(joinPoint);
        var result = joinPoint.proceed();
        if (result instanceof Mono) {
            return logMonoResult(joinPoint, start, (Mono) result);
        } else if (result instanceof Flux) {
            return logFluxResult(joinPoint, start, (Flux) result);
        } else {
// body type is not Mono/Flux
            logResult(joinPoint, start, result);
            return result;
        }
        
    }
    
    private Mono logMonoResult(ProceedingJoinPoint joinPoint, long start, Mono result) {
        AtomicReference<String> traceId = new AtomicReference<>("");
        return result.doOnSuccess(o -> {
            setTraceIdInMDC(traceId);
            var response = Objects.nonNull(o) ? o.toString() : "";
            logSuccessExit(joinPoint, start, response);
        }).subscriberContext(context -> {
// the error happens in a different thread, so get the trace from context, set in MDC and downstream to doOnError
            setTraceIdFromContext(traceId, (Context) context);
            return context;
        }).doOnError(o -> {
            setTraceIdInMDC(traceId);
            logErrorExit(joinPoint, start, o.toString());
        });
    }
    
    private Flux logFluxResult(ProceedingJoinPoint joinPoint, long start, Flux result) {
        return result.doFinally(o -> {
            logSuccessExit(joinPoint, start, o.toString()); // NOTE: this is costly
        }).doOnError(o -> {
            logErrorExit(joinPoint, start, o.toString());
        });
    }
    
    private void logResult(ProceedingJoinPoint joinPoint, long start, Object result) {
        try {
            logSuccessExit(joinPoint, start, result.toString());
        } catch (Exception e) {
            logErrorExit(joinPoint, start, e.getMessage());
        }
    }
    
    
    private void logErrorExit(ProceedingJoinPoint joinPoint, long start, String error) {
        log.error("Exit: {}.{}() had arguments = {}, with result = {}, Execution time = {} ms", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), joinPoint.getArgs()[0], error, System.currentTimeMillis() - start);
    }
    
    private void logSuccessExit(ProceedingJoinPoint joinPoint, long start, String response) {
        log.info("Exit: {}.{}() had arguments = {}, with result = {}, Execution time = {} ms", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), joinPoint.getArgs()[0], response, System.currentTimeMillis() - start);
    }
    
    private void logEntry(ProceedingJoinPoint joinPoint) {
        log.info("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }
    
    private void setTraceIdFromContext(AtomicReference<String> traceId, Context context) {
        if (context.hasKey("x_trace_id")) {
            traceId.set(context.get("x_trace_id"));
            setTraceIdInMDC(traceId);
        }
    }
    
    private void setTraceIdInMDC(AtomicReference<String> traceId) {
        if (!traceId.get().isEmpty()) {
            MDC.put("x_trace_id", traceId.get());
        }
    }
    
    
}