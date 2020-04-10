package person.pluto.lock.model;

import java.util.UUID;

import person.pluto.lock.annotation.Lock;

import lombok.Data;

/**
 * 
 * <p>
 * 锁信息
 * </p>
 *
 * @author Pluto
 * @since 2020-04-01 14:22:03
 */
@Data
public class LockInfo {

    public static LockInfo of(Lock lock) {
        LockInfo info = new LockInfo();
        info.setLockName(lock.value());
        info.setLockValue(UUID.randomUUID().toString());
        info.setConcurrentType(lock.concurrentType());
        info.setExpire(lock.expire());
        info.setOvertime(lock.overtimeMillis());
        info.setWaittime(lock.waittimeMillis());
        return info;
    }

    public static LockInfo ofPreemptive(String key) {
        LockInfo lockInfo = new LockInfo();
        lockInfo.setLockName(key);
        lockInfo.setLockValue(UUID.randomUUID().toString());
        lockInfo.setConcurrentType(ConcurrentType.PREEMPTIVE);
        return lockInfo;
    }

    /**
     * 
     * <p>
     * 并发方式
     * </p>
     *
     */
    public static enum ConcurrentType {
        /**
         * 默认，抢占一次，失败直接退出
         */
        DEFAULT,
        /**
         * 抢占式，循环抢占，直至失败
         */
        PREEMPTIVE,
        /**
         * 队列式，直接进入队列（未实现）
         */
        // QUERY,

        /**
         * 抢占队列，抢占一次，失败进入队列（未实现）
         */
        // PREEMPTIVE_QUERY
    }

    public static final long expireDefault = 60000L;
    public static final String lockKeyPrefix = "common.lock.";

    public String getLockKey() {
        return String.format("%s%s", lockKeyPrefix, this.lockName);
    }

    /**
     * 锁名
     */
    private String lockName;

    /**
     * 锁定时赋值，在解锁时为防止解锁了其他的线程，则需要验证这个值，所以在重入锁时要保证lockValue相同
     */
    private String lockValue;

    /**
     * 并发方式
     */
    private ConcurrentType concurrentType = ConcurrentType.DEFAULT;

    /**
     * 锁持有时间（即redis中锁失效时间，ms）
     */
    private long expire = expireDefault;

    /**
     * 获取锁超时时间（ms）
     */
    private long overtime = 1000L;

    /**
     * 轮询抢占时间间隔（ms）
     */
    private long waittime = 1L;

    /**
     * 保持量，做重入锁时线程持有量判断
     */
    private int holdCount = 0;

    /**
     * 递增保持量
     * 
     * @return
     */
    public int incrHoldCount() {
        this.holdCount++;
        return this.holdCount;
    }

    /**
     * 递减保持量
     * 
     * @return
     */
    public int decrHoldCount() {
        this.holdCount--;
        return this.holdCount;
    }

    /**
     * 线程是否全部释放
     * 
     * @return
     */
    public boolean emptyHold() {
        return this.holdCount <= 0;
    }

}