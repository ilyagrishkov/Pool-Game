package nl.tudelft.cse.sem.server.test.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.google.common.testing.NullPointerTester;
import java.util.Optional;
import java.util.Properties;
import nl.tudelft.cse.sem.server.jwt.BasicJWTProvider;
import nl.tudelft.cse.sem.server.jwt.JWTProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public class BasicJWTProviderTest {
    private static final String testStringBecausePMDIsDumb = "test";
    public static final JWTProvider PROVIDER = BasicJWTProvider.builder()
        .algorithm(Algorithm.HMAC256(testStringBecausePMDIsDumb))
        .issuer(testStringBecausePMDIsDumb)
        .verifier(JWT.require(Algorithm.HMAC256(testStringBecausePMDIsDumb)).build())
        .properties(new Properties())
        .build();

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(BasicJWTProvider.class);
        tester.testAllPublicInstanceMethods(PROVIDER);
    }

    @Test
    public void shouldReturnSignedToken() {
        final String expectedToken = "token";
        JWTCreator.Builder builder = Mockito.mock(JWTCreator.Builder.class);

        // Setup builder
        when(builder.withIssuer(anyString())).thenReturn(builder);
        when(builder.withExpiresAt(any())).thenReturn(builder);
        when(builder.sign(any())).thenReturn(expectedToken);

        // Call method
        Optional<String> token = PROVIDER.signToken(builder);

        // Make sure the token was 'signed'
        assertTrue(token.isPresent());
        assertEquals(expectedToken, token.get());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenSigningFailed() {
        JWTCreator.Builder builder = Mockito.mock(JWTCreator.Builder.class);

        // Setup builder
        when(builder.withIssuer(anyString())).thenReturn(builder);
        when(builder.withExpiresAt(any())).thenReturn(builder);
        when(builder.sign(any())).thenThrow(JWTCreationException.class);

        // Call method
        Optional<String> token = PROVIDER.signToken(builder);

        // Make sure the token was not 'signed'
        assertTrue(token.isEmpty());
    }

    @Test
    public void shouldReturnDecodedToken() {
        // Create instance with mocked verifier
        JWTVerifier verifier = Mockito.mock(JWTVerifier.class);
        BasicJWTProvider provider = BasicJWTProvider.builder()
            .issuer(testStringBecausePMDIsDumb)
            .algorithm(Algorithm.HMAC256(testStringBecausePMDIsDumb))
            .verifier(verifier)
            .properties(new Properties())
            .build();

        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);

        // Setup dependencies
        when(claim.isNull()).thenReturn(false);
        when(decodedJWT.getClaim("claim")).thenReturn(claim);
        when(verifier.verify(anyString())).thenReturn(decodedJWT);

        // Call method
        Optional<DecodedJWT> decoded = provider.decodeToken("randomString");

        // Make sure the decoded jwt is correct
        assertTrue(decoded.isPresent());
        assertFalse(decoded.get().getClaim("claim").isNull());
    }

    @Test
    public void shouldReturnEmptyWhenInvalidToken() {
        // Create instance with mocked verifier
        JWTVerifier verifier = Mockito.mock(JWTVerifier.class);
        BasicJWTProvider provider = BasicJWTProvider.builder()
            .issuer(testStringBecausePMDIsDumb)
            .algorithm(Algorithm.HMAC256(testStringBecausePMDIsDumb))
            .verifier(verifier)
            .properties(new Properties())
            .build();

        // Setup dependencies
        when(verifier.verify(anyString())).thenThrow(JWTVerificationException.class);

        // Call method
        Optional<DecodedJWT> decoded = provider.decodeToken("randomString");

        // Make sure the optional is empty
        assertTrue(decoded.isEmpty());
    }

}
