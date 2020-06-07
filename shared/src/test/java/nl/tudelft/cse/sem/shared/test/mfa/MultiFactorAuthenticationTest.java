package nl.tudelft.cse.sem.shared.test.mfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import nl.tudelft.cse.sem.shared.mfa.MultiFactorAuthentication;
import org.jboss.aerogear.security.otp.Totp;
import org.junit.jupiter.api.Test;

public class MultiFactorAuthenticationTest {
    private static final String SECRET = "XDIYRHWCOMYWSHTX";

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // try with res is too difficult for PMD
    @Test
    public void shouldReturnCorrectQR() throws IOException, NotFoundException {
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder()
            .name("test1").secret(SECRET).build();
        try (InputStream in = mfa.createQRCode()) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(ImageIO.read(in))));
            String code = new MultiFormatReader().decode(bitmap).getText();

            assertEquals("otpauth://totp/test1?secret=XDIYRHWCOMYWSHTX&issuer=PoolGame",
                code);
        }
    }

    @Test
    public void shouldVerifyCorrectCode() {
        String code = new Totp(SECRET).now();
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder()
            .name("test2").secret(SECRET).build();

        assertTrue(mfa.verify(code));
    }

    @Test
    public void shouldRejectIncorrectCode() {
        String code = new Totp(SECRET).now();
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder()
            .name("test3").secret(SECRET).build();

        assertFalse(mfa.verify(code.equals("123456") ? "123455" : "123456"));
    }

    @Test
    public void shouldRejectInvalidCode() {
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder()
            .name("test4").secret(SECRET).build();

        assertFalse(mfa.verify("uhawd"));
    }
}
