package person.pluto.websocket.model;

/**
 * <p>
 * ws用户搜索模型
 * </p>
 *
 * @author Pluto
 * @since 2020-03-23 14:52:06
 */
public class WebsocketUserInfoWrapper extends WebsocketUserInfo {

    public static WebsocketUserInfoWrapper of(WebsocketUserInfo userInfo) {
        WebsocketUserInfoWrapper wrapper = new WebsocketUserInfoWrapper();
        wrapper.setUserId(userInfo.getUserId());
        wrapper.setExampleId(userInfo.getExampleId());
        wrapper.setTerminalType(userInfo.getTerminalType());
        wrapper.setSessionId(userInfo.getSessionId());
        return wrapper;
    }

    public static WebsocketUserInfoWrapper of(String userId, String terminalType) {
        WebsocketUserInfoWrapper wrapper = new WebsocketUserInfoWrapper();
        wrapper.setUserId(userId);
        wrapper.setTerminalType(terminalType);
        return wrapper;
    }

}
