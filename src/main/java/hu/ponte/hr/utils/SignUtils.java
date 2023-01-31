package hu.ponte.hr.utils;

import org.springframework.util.ResourceUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignUtils {

    private static final String ALGORITHM = "RSA";

    public static PrivateKey getPrivateKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:config/keys/key.private");
        try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
            byte[] keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            return kf.generatePrivate(spec);
        }
    }

    public static PublicKey getPublicKey() throws Exception {
        File file = ResourceUtils.getFile("classpath:config/keys/key.pub");
        try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
            byte[] keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        }
    }
}
