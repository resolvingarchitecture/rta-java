package ra.rta.utilities;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Helper methods
 */
public class Generator {

    private static String salt = "jklfq89oj2r3kfma9f32fkshas89043";

    public static String adid(int groupId, long uId) {
        return DigestUtils.sha3_256Hex(groupId + "|" + uId + "|" + salt);
    }

}
