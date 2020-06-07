package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN_LABEL;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.TRANSPARENT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.Optional;
import lombok.Getter;
import nl.tudelft.cse.sem.client.CurrentLogin;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.requests.Login;
import nl.tudelft.cse.sem.client.requests.Register;
import nl.tudelft.cse.sem.client.screens.mfa.MultiFactorRegisterScreen;
import org.jboss.aerogear.security.otp.Totp;

//Suppressing the pmd error for using the .json for the skin too many times.
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Getter
public class RegisterScreen implements Screen {

    private transient StartGame game;
    private transient Stage stage;
    private transient Texture texture;
    private transient MultiFactorRegisterScreen mfaScreen;

    private transient String name;
    private transient String password;
    private transient String secret;

    private transient Label error;
    private transient TextField usernameField;
    private transient TextField passwordField;
    private transient TextField confirmPasswordField;
    private transient TextButton mfaButton;
    private transient TextButton registerButton;
    private transient TextButton backButton;

    /**
     * Constructor for the LoginScreen.
     *
     * @param game - the game object
     */

    public RegisterScreen(StartGame game) {
        this.game = game;
        this.stage = new Stage();
        this.password = "";
        this.name = "";
        this.mfaScreen = new MultiFactorRegisterScreen(this);

        // Error display
        error = new Label("", BAHN_LABEL);
        error.setSize(220, 80);
        error.setPosition(130, 620);

        // Username field
        this.usernameField = new TextField(this.name, BAHN);

        // Password field
        this.passwordField = new TextField(this.password, BAHN);

        // Add change listeners
        this.usernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                name = usernameField.getText();
            }
        });

        this.passwordField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                password = passwordField.getText();
            }
        });

        //ConfirmPassword field
        this.confirmPasswordField = new TextField("", BAHN);

        // Register button
        this.registerButton = new TextButton("", TRANSPARENT);

        // Back button
        this.backButton = new TextButton("", TRANSPARENT);

        // MFA button
        this.mfaButton = new TextButton("", TRANSPARENT);

        // Add click listeners
        this.mfaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                mfaScreen.setName(name);
                game.setScreen(mfaScreen);
            }
        });

        this.registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                register();
            }
        });

        this.backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new LoginScreen(game));
            }
        });

        this.stage.addActor(error);
        this.stage.addActor(this.usernameField);
        this.stage.addActor(this.passwordField);
        this.stage.addActor(this.confirmPasswordField);
        this.stage.addActor(this.mfaButton);
        this.stage.addActor(this.registerButton);
        this.stage.addActor(this.backButton);
    }

    /**
     * Attempt to register and continue to the home menu screen.
     */
    private void register() {
        if (!this.password.equals(this.confirmPasswordField.getText())) {
            error.setText("Passwords don't match");

            return;
        }

        Optional<String> res = new Register(this.name, this.name, this.password,
            this.mfaScreen.isMfaVerified() ? this.secret : null).makeCall();

        if (res.isPresent()) {
            String token = new Login(this.name, this.password,
                this.mfaScreen.isMfaVerified() ? new Totp(this.secret).now() : null).makeCall()
                .orElse("").split("=")[1].split(",")[0];

            Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
            dispose();
            CurrentLogin.token = token;
            CurrentLogin.displayName = name;
            this.game.setScreen(new HomeMenuScreen(game));
        } else {
            error.setText("Name already taken");

        }
    }

    @Override
    public void show() {
        this.texture = new Texture("FinalRegisterScreen.png");
        this.secret = this.mfaScreen.getSecret();

        Gdx.input.setInputProcessor(stage);

        // Position fields
        this.usernameField.setPosition(300, 560);
        this.usernameField.setSize(220, 80);
        this.usernameField.setText(this.name);

        this.passwordField.setPosition(300, 465);
        this.passwordField.setSize(220, 80);
        this.passwordField.setPasswordCharacter('*');
        this.passwordField.setPasswordMode(true);
        this.passwordField.setText(this.password);

        this.confirmPasswordField.setPosition(300, 380);
        this.confirmPasswordField.setSize(220, 80);
        this.confirmPasswordField.setPasswordCharacter('*');
        this.confirmPasswordField.setPasswordMode(true);

        // Position buttons
        this.mfaButton.setPosition(75, 265);
        this.mfaButton.setSize(325, 80);

        this.registerButton.setPosition(75, 170);
        this.registerButton.setSize(260, 80);

        this.backButton.setPosition(75, 65);
        this.backButton.setSize(260, 80);
    }

    @Override
    public void render(float delta) {
        this.stage.act(delta);

        this.stage.getBatch().begin();
        this.stage.getBatch().draw(texture, 0, 0);
        this.stage.getBatch().end();

        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
