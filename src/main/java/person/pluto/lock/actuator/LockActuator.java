package person.pluto.lock.actuator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import person.pluto.lock.model.LockInfo;
import person.pluto.lock.server.ILockServer;

/**
 * 
 * <p>
 * 锁执行器
 * </p>
 *
 * @author Pluto
 * @since 2020-04-01 15:47:49
 */
public class LockActuator {

    public static final LockActuator INSTANCE = new LockActuator();

    protected static ThreadLocal<Map<String, LockInfo>> lockThreadLocal = new ThreadLocal<Map<String, LockInfo>>();

    protected ILockServer lockServer;

    /**
     * 尝试锁起
     * 
     * @param lockInfo
     * @return
     */
    protected boolean trylock(LockInfo lockInfo) {
        // 此处为加锁，这是从我其中一个项目拷贝过来的，因这只是一个演示程序，为了减少包的下载、问题的出现这里全部删除了
        // 如果感兴趣可以到 https://github.com/Pluto-Whong/reentrant-lock.git 查看 可重入的分布式锁
        lockInfo.incrHoldCount();
        return true;
    }

    /**
     * 解锁
     * 
     * @param lockInfo
     * @return
     */
    protected boolean unlock(LockInfo lockInfo) {
        // 此处为解锁，这是从我其中一个项目拷贝过来的，因这只是一个演示程序，为了减少包的下载、问题的出现这里全部删除了
        // 如果感兴趣可以到 https://github.com/Pluto-Whong/reentrant-lock.git 查看 可重入的分布式锁
        lockInfo.decrHoldCount();
        return true;
    }

    /**
     * 静态方法，根据key获取锁
     * 
     * 可重入锁，该方法维护lockInfo
     * 
     * 注意锁定时放在finally外面，防止锁定失败，导致多进行一次解锁而导致提前释放锁
     * 
     * @param key
     * @return
     */
    public static boolean trylock(String key) {
        return lock(LockInfo.ofPreemptive(key));
    }

    /**
     * 根据lockInfo进行锁定
     * 
     * 注意，如果前面已经有了这个key的锁定并且还没有完全解除，那么再重入时，不会再根据新的lockInfo的配置进行抢占锁，即根据旧的也就是第一个
     * 
     * 注意锁定时放在finally外面，防止锁定失败，导致多进行一次解锁而导致提前释放锁
     * 
     * @param lockInfo
     * @return
     */
    public static boolean lock(LockInfo lockInfo) {
        Map<String, LockInfo> map = lockThreadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(lockInfo.getLockName(), lockInfo);
            lockThreadLocal.set(map);
        } else {
            // 线程重入，相当于单线程在操作这个map，所以不用担心并发问题
            if (map.containsKey(lockInfo.getLockName())) {
                lockInfo = map.get(lockInfo.getLockName());
            } else {
                map.put(lockInfo.getLockName(), lockInfo);
            }
        }

        boolean trylock = LockActuator.INSTANCE.trylock(lockInfo);
        if (!trylock) {
            // 如果锁定失败，则检查是否无持有（即第一次锁定），如果是则清除掉threadLocal中的数据
            if (lockInfo.emptyHold()) {
                map.remove(lockInfo.getLockName());
                if (map.isEmpty()) {
                    lockThreadLocal.remove();
                }
            }
        }
        return trylock;
    }

    /**
     * 解锁
     * 
     * 可重入锁，该方法维护lockInfo
     * 
     * @return
     */
    public static boolean releaseLock(String key) {
        Map<String, LockInfo> map = lockThreadLocal.get();
        LockInfo lockInfo = map.get(key);
        assert Objects.nonNull(lockInfo);

        boolean unlock = LockActuator.INSTANCE.unlock(lockInfo);
        if (lockInfo.emptyHold()) {
            map.remove(key);
            if (map.isEmpty()) {
                lockThreadLocal.remove();
            }
        }
        return unlock;
    }

    public void setLockServer(ILockServer lockServer) {
        this.lockServer = lockServer;
    }

}