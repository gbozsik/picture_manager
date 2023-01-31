package hu.ponte.hr.services;

import hu.ponte.hr.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

import static hu.ponte.hr.utils.SignUtils.getPrivateKey;

@Service
public class SignService {

    private final static Logger log = LoggerFactory.getLogger(SignService.class);
    public static final String SIGNING_ALGORITHM = "SHA256withRSA";

    public String sign(String originalName, String uploadsDir) throws ServiceException {
        try {
            PrivateKey privateKey = getPrivateKey();
            Signature signature = Signature.getInstance(SIGNING_ALGORITHM);
            signature.initSign(privateKey);
            byte[] messageBytes = Files.readAllBytes(Paths.get(String.format("%s/%s", uploadsDir, originalName)));

            signature.update(messageBytes);
            byte[] digitalSignature = signature.sign();

            return Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            log.error("Could not sign picture", e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Somthing went wrong during signing picture!");
        }
    }
}
