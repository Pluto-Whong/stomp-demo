package person.pluto.lock.annotation;

import java.lang.annotation.*;

import person.pluto.lock.model.LockInfo.ConcurrentType;

/**
 * 
 * <p>
 * 分布式锁
 * </p>
 *
 * @author Pluto
 * @since 2020-04-01 14:17:06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁名称
     */
    String value();

    /**
     * 并发方式
     */
    ConcurrentType concurrentType() default ConcurrentType.DEFAULT;

    /**
     * 锁最高持续时间（即redis中的超时时间）
     */
    long expire() default 60000L;

    /**
     * 获取锁超时时间
     */
    long overtimeMillis() default 1000L;

    /**
     * 抢占等待时间，只有concurrentType为PREEMPTIVE才有效
     */
    long waittimeMillis() default 1L;

}
