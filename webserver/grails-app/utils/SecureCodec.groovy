
import java.security.*
import javax.crypto.*
import javax.crypto.spec.*
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.codec.binary.Hex

class SecureCodec{

    static String password = "MB090901D4vidPaz"

    static encode = { str ->

        if(['null', 'Null', 'NULL', '', null].contains(str)) str = ''
        try {

            Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE, password)

            byte[] encodedBytes = cipher.doFinal(str.getBytes())

            String hex = new String(new Hex().encode(encodedBytes))?.toUpperCase()
            return hex;
        } catch(Exception e) {
            return str
        }
    }

    static decode = { hex ->
        try {

            byte[] bytes = new Hex().decodeHex((char[])hex)

            Cipher cipher = setupCipher(Cipher.DECRYPT_MODE, password)
            def decripted = new String(cipher.doFinal(bytes))

            return decripted
        } catch(Exception e) {

            return hex
        }
    }

    private static setupCipher(mode, password) {
        Cipher cipher = Cipher.getInstance("AES");

        byte[] keyBytes = new byte[16];
        byte[] b = password.getBytes();
        int len = b.length;
        if (len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        cipher.init(mode, keySpec);
        return cipher
    }


}