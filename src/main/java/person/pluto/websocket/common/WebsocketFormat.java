package person.pluto.websocket.common;

import person.pluto.websocket.model.WebsocketUserInfo;

/**
 * <p>
 * websocket格式化类
 * </p>
 *
 * @author Pluto
 * @since 2020-03-09 10:31:01
 */
public final class WebsocketFormat {

    public static final String CURRENT_USER_ID_ATTR_KEY = "currentUserId";
    public static final String CURRENT_USER_ATTR_KEY = "currentUser";

    public static final String TOKEN_HEADER_KEY = "token";
    public static final String TERMINAL_TYPE_KEY = "terminalType";

    public static final String USER_RECEIVER_PATH = "msg";

    /**
     * websocketUserInfo在redis和内存中的寻址key
     * 
     * @param userInfo
     * @return
     * @author Pluto
     * @since 2020-04-07 10:42:50
     */
    public static String userUnoinAddress(WebsocketUserInfo userInfo) {
        return String.format("%s.%s", userInfo.getUserId(), userInfo.getTerminalType());
    }

}
