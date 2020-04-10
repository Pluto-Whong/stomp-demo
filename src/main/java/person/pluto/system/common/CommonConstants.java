package person.pluto.system.common;

import java.util.UUID;

/**
 * <p>
 * 通用参数
 * </p>
 *
 * @author Pluto
 * @since 2019-03-28 11:01:04
 */
public final class CommonConstants {

    public static final CommonConstants INSTANCE = new CommonConstants();

    // 实例ID
    private String exampleId = UUID.randomUUID().toString();

    /**
     * 实例ID
     *
     * @return
     * @author Pluto
     * @since 2020-03-24 17:12:22
     */
    public static String getExampleId() {
        return CommonConstants.INSTANCE.exampleId;
    }

}
