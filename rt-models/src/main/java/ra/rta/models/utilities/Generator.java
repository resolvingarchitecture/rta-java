package ra.rta.models.utilities;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Helper methods
 */
public class Generator {

    private static String salt = "faoifnewofeinfklasfdaklfelfkaf";

    public static String adic(String partnerName, String ucic) {
        return DigestUtils.shaHex(partnerName + "|" + ucic + "|" + salt);
    }

}
