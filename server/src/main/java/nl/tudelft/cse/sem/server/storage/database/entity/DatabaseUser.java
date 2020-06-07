package nl.tudelft.cse.sem.server.storage.database.entity;

import de.mkammerer.argon2.Argon2Factory;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import nl.tudelft.cse.sem.shared.mfa.MultiFactorAuthentication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

/**
 * Class to store user for database.
 */
@Entity
@Table(name = "`User`")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class DatabaseUser {
    private static final Logger logger = LogManager.getLogger(DatabaseUser.class);

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(name = "username", unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Column(name = "displayname")
    private String displayName;

    @Column(name = "password")
    private byte[] password;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @OrderBy("scoreAmount ASC")
    private List<DatabaseScore> scores;

    @Column(name = "mfaEnabled")
    private boolean multiFactorAuthenticationEnabled;

    @Column(name = "mfaSecret")
    private String multiFactorAuthenticationSecret;

    /**
     * Creates a user.
     *
     * @param username    The username
     * @param displayName The display name
     * @param password    The plain-text password
     * @param token       The MFA token
     */
    public DatabaseUser(String username, String displayName, String password, String token) {
        this.username = username;
        this.displayName = displayName;

        this.password = hashPassword(password);
        this.multiFactorAuthenticationSecret = token;
        this.multiFactorAuthenticationEnabled = token != null;
    }

    private byte[] hashPassword(String password) {
        return Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
            .hash(6, 256, 1, password.getBytes()).getBytes();
    }

    /**
     * Verifies a password against the actual password.
     *
     * @param password The password to check against the actual password.
     * @return True iff the password is correct.
     */
    public boolean verifyPassword(@NonNull String password) {
        return Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
            .verify(new String(this.password), password.getBytes());
    }

    /**
     * Verifies the given MFA token.
     *
     * @param token The token to verify
     * @return A boolean indicating if the mfa token was correct.
     */
    public boolean verifyMfaToken(String token) {
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder()
            .secret(this.multiFactorAuthenticationSecret).build();

        return !this.multiFactorAuthenticationEnabled || mfa.verify(token);
    }

    /**
     * Builds a {@link DatabaseUser} object for use by the rest of the server and client.
     *
     * @return A {@link DatabaseUser} object constructed using the values of this instance.
     */
    public nl.tudelft.cse.sem.server.entity.User toUser() {
        return nl.tudelft.cse.sem.server.entity.User.builder()
            .uuid(this.uuid)
            .displayName(this.displayName)
            .loginName(this.username)
            .build();
    }
}
