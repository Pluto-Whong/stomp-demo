package person.pluto.lock.server;

import person.pluto.lock.model.LockInfo;

/**
 * 
 * <p>
 * 锁操作服务
 * </p>
 *
 * @author Pluto
 * @since 2020-04-01 14:20:42
 */
public interface ILockServer {

    boolean lock(LockInfo lockInfo);

    boolean unlock(LockInfo lockInfo);

}