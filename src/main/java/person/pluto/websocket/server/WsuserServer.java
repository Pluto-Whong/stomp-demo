package person.pluto.websocket.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import person.pluto.lock.actuator.LockActuator;
import person.pluto.system.exception.MyException;
import person.pluto.user.model.IMUserInfo;
import person.pluto.websocket.common.WebsocketFormat;
import person.pluto.websocket.model.WebsocketUserInfo;
import person.pluto.websocket.model.WebsocketUserInfoWrapper;

/**
 * <p>
 * wesocket用户服务
 * </p>
 * 
 * @author Pluto
 * @since 2020-03-23 14:50:55
 */
@Service
public class WsuserServer {

    private static final Map<String, WebsocketUserInfo> userInfoMap = new ConcurrentHashMap<>();

    /**
     * 检查token并返回用户ID
     *
     * @param token
     * @return
     * @author Pluto
     * @since 2020-03-24 17:22:52
     */
    public String inspectToken(String token) throws MyException {
        return token;
    }

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId
     * @return
     * @author Pluto
     * @since 2020-03-24 17:25:58
     */
    public IMUserInfo getIMUserInfoByUserId(String userId) {
        return IMUserInfo.of(userId);
    }

    /**
     * 获取用户
     *
     * @param wrapper
     * @return
     * @author Pluto
     * @since 2020-03-23 15:14:39
     */
    public WebsocketUserInfo getUserInfo(@NonNull WebsocketUserInfoWrapper wrapper) {
        return userInfoMap.get(WebsocketFormat.userUnoinAddress(wrapper));
    }

    /**
     * 保存用户
     *
     * @param userInfo
     * @author Pluto
     * @since 2020-03-23 15:14:44
     */
    public void setUserInfo(@NonNull WebsocketUserInfo userInfo) {
        // 锁定
        String userUnoinAddress = WebsocketFormat.userUnoinAddress(userInfo);
        Assert.isTrue(LockActuator.trylock(userUnoinAddress), String.format("锁定[%s]失败", userUnoinAddress));

        try {
            userInfoMap.put(userUnoinAddress, userInfo);
        } finally {
            LockActuator.releaseLock(userUnoinAddress);
        }
    }

    /**
     * 移除用户
     *
     * @param wrapper
     * @author Pluto
     * @since 2020-03-23 15:15:02
     */
    public void removeUserInfo(@NonNull WebsocketUserInfo wsUserInfo) {
        // 锁定
        String userUnoinAddress = WebsocketFormat.userUnoinAddress(wsUserInfo);
        Assert.isTrue(LockActuator.trylock(userUnoinAddress), String.format("锁定[%s]失败", userUnoinAddress));

        try {
            WebsocketUserInfoWrapper wrapper = WebsocketUserInfoWrapper.of(wsUserInfo);
            WebsocketUserInfo userInfo = this.getUserInfo(wrapper);
            if (userInfo == null) {
                return;
            }
            if (!userInfo.sameUser(wrapper)) {
                return;
            }
            userInfoMap.remove(userUnoinAddress);
        } finally {
            LockActuator.releaseLock(userUnoinAddress);
        }
    }

}
