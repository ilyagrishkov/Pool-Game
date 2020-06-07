package nl.tudelft.cse.sem.shared.mfa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.Builder;
import lombok.Getter;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;

/**
 * Helper class used for generating MFA secrets and QR codes.
 */
@Builder
@Getter
public class MultiFactorAuthentication {
    @Builder.Default
    private transient String secret = Base32.random();

    private transient String name;

    private static final String ISSUER = "PoolGame";

    /**
     * Returns a link for the QR code.
     *
     * @return String representing a link to the QR image
     */
    private String getLink() {
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s",
            URLEncoder.encode(this.name, StandardCharsets.UTF_8), this.secret, ISSUER);
    }

    /**
     * Verifies the given code.
     *
     * @param code The code to verify
     * @return A boolean representing the validity of the given code. True if valid, false if not
     */
    public boolean verify(String code) {
        try {
            // Make sure the secret is a proper secret by attempting to parse it
            Base32.decode(this.secret);

            return new Totp(this.secret).verify(code);
        } catch (NumberFormatException | Base32.DecodingException e) {
            return false;
        }
    }

    /**
     * Returns the QR code, in the form of an {@link InputStream}.
     *
     * @return An {@link InputStream} representing the QR code
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName") //it's nagging about QRC, isnt an issue
    public InputStream createQRCode() {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(getLink(), BarcodeFormat.QR_CODE,
                200, 200);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "png", out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
