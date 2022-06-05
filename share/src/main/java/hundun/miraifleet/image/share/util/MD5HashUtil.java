package hundun.miraifleet.image.share.util;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class MD5HashUtil {
    private final static String[] hexDigits = { "0", "1",
            "2","3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f" };

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


    private static byte[] readFileStr(BufferedImage image) throws IOException {

        ByteArrayOutputStream bs =new ByteArrayOutputStream();

        ImageOutputStream imOut =ImageIO.createImageOutputStream(bs);

        ImageIO.write(image,"png",imOut);

        byte[] re = bs.toByteArray();

        return re;
    }


    public static String imageMD5(BufferedImage image, String formatName) {
        String resultString = "UNKNOWN_MD5";
        if (image == null) {
            return resultString;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(readFileStr(image)));
        } catch (Exception ex) {
            
        }
        return resultString;
    }

    public static String stringMD5(String string) {
        String resultString = "UNKNOWN_MD5";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(string.getBytes()));
        } catch (Exception ex) {
            
        }
        return resultString;
    }
}
