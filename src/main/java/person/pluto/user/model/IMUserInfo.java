package person.pluto.user.model;

import lombok.Data;

@Data
public class IMUserInfo {

    public static IMUserInfo of(String userId) {
        IMUserInfo userInfo = new IMUserInfo();
        userInfo.setUserId(userId);
        return userInfo;
    }

    private String userId;

}
