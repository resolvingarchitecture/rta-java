package ra.rta.utilities;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Helper methods
 */
public class Hasher {

    private static String salt = "jklfq89oj2r3kfma9f32fkshas89043";

    public static String hash(long val) {
        return DigestUtils.sha3_256Hex(val + "|" + salt);
    }

}
