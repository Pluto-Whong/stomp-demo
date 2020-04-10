package person.pluto.websocket.model;

import java.security.Principal;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import person.pluto.system.common.CommonConstants;

/**
 * <p>
 * websocket用户信息
 * </p>
 *
 * @author Pluto
 * @since 2020-03-06 16:03:37
 */
@Data
public class WebsocketUserInfo implements Principal {

    /**
     * 填充全部信息
     * 
     * @param token
     * @param userId
     * @return
     * @author Pluto
     * @since 2020-04-03 15:31:41
     */
    public static WebsocketUserInfo ofFull(String userId, String terminalType, String sessionId) {
        WebsocketUserInfo wsUserInfo = new WebsocketUserInfo();
        wsUserInfo.setUserId(userId);
        wsUserInfo.setTerminalType(terminalType);
        wsUserInfo.setExampleId(CommonConstants.getExampleId());
        wsUserInfo.setSessionId(sessionId);
        return wsUserInfo;
    }

    private String userId;

    private String terminalType;

    private String exampleId;

    private String sessionId;

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("userId", this.userId);
        json.put("terminalType", this.terminalType);
        json.put("exampleId", this.exampleId);
        json.put("sessionId", this.sessionId);
        return json.toJSONString();
    }

    @Override
    public String getName() {
        return userId;
    }

    /**
     * 判断是否为同一个用户
     *
     * @param wsUserInfo
     * @return
     * @author Pluto
     * @since 2020-03-09 11:04:15
     */
    public boolean sameUser(WebsocketUserInfo wsUserInfo) {
        if (wsUserInfo == null) {
            return false;
        }
        return StringUtils.equals(userId, wsUserInfo.getUserId())
                && StringUtils.equals(terminalType, wsUserInfo.getTerminalType())
                && StringUtils.equals(exampleId, wsUserInfo.getExampleId())
                && StringUtils.equals(this.sessionId, wsUserInfo.getSessionId());
    }

}
