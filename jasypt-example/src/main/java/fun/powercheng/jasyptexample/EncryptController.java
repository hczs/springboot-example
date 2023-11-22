package fun.powercheng.jasyptexample;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hczs8
 */
@RestController
@RequiredArgsConstructor
public class EncryptController {

    private final StringEncryptor stringEncryptor;

    @Value("${test.password}")
    private String pwd;

    @GetMapping("/encrypt/{content}")
    public ResponseEntity<String> encrypt(@PathVariable String content) {
        return ResponseEntity.ok(stringEncryptor.encrypt(content));
    }

    @GetMapping("/decrypt/{encryptedMessage}")
    public ResponseEntity<String> decrypt(@PathVariable String encryptedMessage) {
        return ResponseEntity.ok(stringEncryptor.decrypt(encryptedMessage));
    }

    @GetMapping("/test/password")
    public ResponseEntity<String> getPwd() {
        return ResponseEntity.ok(pwd);
    }

}
