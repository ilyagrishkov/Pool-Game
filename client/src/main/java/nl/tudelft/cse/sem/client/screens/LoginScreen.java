package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Optional;
import lombok.Getter;
import nl.tudelft.cse.sem.client.CurrentLogin;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.requests.Login;
import nl.tudelft.cse.sem.client.screens.mfa.MultiFactorLoginScreen;


@SuppressWarnings({"checkstyle:VariableDeclarationUsageDistance",
        "PMD.AvoidDuplicateLiterals", "PMD.AssignmentToNonFinalStatic"})

@Getter
public class LoginScreen implements Screen {

    public static int SMALL_WINDOW_POS = 660;

    private transient StartGame game;

    private transient Stage stage;

    private transient Texture texture;

    private transient String name;
    private transient String password;

    private transient TextField usernameField;
    private transient TextField passwordField;
    private transient TextButton loginButton;
    private transient TextButton registerButton;
    private transient Label error;

    /**
     * Constructor for the LoginScreen.
     *
     * @param game - the game object
     */
    public LoginScreen(StartGame game) {
        Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
        SMALL_WINDOW_POS = window.getPositionX();

        texture = new Texture("FinalLoginScreen.png");

        this.game = game;
        this.stage = new Stage();
        this.name = "";
        this.password = "";

        // Error display
        error = new Label("", BAHN_LABEL);
        error.setSize(220, 80);
        error.setPosition(90, 620);

        usernameField = new TextField(this.name, BAHN);
        passwordField = new TextField(this.password, BAHN);
        loginButton = new TextButton("", TRANSPARENT);
        registerButton = new TextButton("", TRANSPARENT);

        usernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                name = usernameField.getText();
            }
        });

        passwordField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                password = passwordField.getText();
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                login(null);
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });

        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    login(null);
                    return false;
                }
                return super.keyUp(event, keycode);
            }
        });

        stage.addActor(error);
        stage.addActor(usernameField);
        stage.addActor(passwordField);
        stage.addActor(loginButton);
        stage.addActor(registerButton);
    }

    /**
     * Attempt to login and continue to the next screen.
     */
    public void login(String mfaToken) {
        Optional<String> res = new Login(this.name, this.password, mfaToken).makeCall();

        if (res.isPresent()) {
            // Check if we need mfa
            if (res.get().equals("mfa")) {
                if (mfaToken == null) {
                    game.setScreen(new MultiFactorLoginScreen(this, ""));
                } else {
                    String errorMessage = "Wrong code, please try again";
                    game.setScreen(new MultiFactorLoginScreen(this, errorMessage));
                }

            } else {
                CurrentLogin.token = res.orElse("").split("=")[1].split(",")[0];
                CurrentLogin.displayName = name;

                Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
                dispose();
                game.setScreen(new HomeMenuScreen(game));
            }
        } else {
            error.setText("Wrong username/password");
        }
    }

    @Override
    public void show() {
        texture = new Texture("FinalLoginScreen.png");
        Gdx.input.setInputProcessor(stage);

        // Username field
        usernameField.setPosition(300, 560);
        usernameField.setSize(220, 80);
        usernameField.setText(this.name);
        usernameField.setCursorPosition(this.name.length());

        // Password field
        passwordField.setPosition(300, 465);
        passwordField.setSize(220, 80);
        passwordField.setText(this.password);
        passwordField.setCursorPosition(this.password.length());

        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);

        // Login button
        loginButton.setPosition(75, 275);
        loginButton.setSize(220, 80);

        // Register button
        registerButton.setPosition(75, 170);
        registerButton.setSize(260, 80);
        //
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0);
        stage.getBatch().end();

        stage.draw();
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
        stage.dispose();
    }

    public static final Skin TRANSPARENT = new Skin(Gdx.files.internal("transparent.json"));
    public static final Skin BAHN = new Skin(Gdx.files.internal("bahnfinal.json")) {
        //Override json loader to process FreeType fonts from skin JSON
        @Override
        protected Json getJsonLoader(final FileHandle skinFile) {
            Json json = super.getJsonLoader(skinFile);
            final Skin skin = this;

            json.setSerializer(FreeTypeFontGenerator.class,
                new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
                    @Override
                    public FreeTypeFontGenerator read(Json json,
                                                      JsonValue jsonData, Class type) {
                        String path = json.readValue("font", String.class, jsonData);
                        jsonData.remove("font");

                        FreeTypeFontGenerator.Hinting hinting =
                            FreeTypeFontGenerator.Hinting.valueOf(json.readValue("hinting",
                                String.class, "AutoMedium", jsonData));
                        jsonData.remove("hinting");

                        Texture.TextureFilter minFilter = Texture.TextureFilter.valueOf(
                            json.readValue("minFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("minFilter");

                        Texture.TextureFilter magFilter = Texture.TextureFilter.valueOf(
                            json.readValue("magFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("magFilter");

                        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                            json.readValue(FreeTypeFontGenerator
                                .FreeTypeFontParameter.class, jsonData);
                        parameter.hinting = hinting;
                        parameter.minFilter = minFilter;
                        parameter.magFilter = magFilter;
                        FreeTypeFontGenerator generator =
                            new FreeTypeFontGenerator(skinFile.parent().child(path));
                        BitmapFont font = generator.generateFont(parameter);
                        skin.add(jsonData.name, font);
                        if (parameter.incremental) {
                            generator.dispose();
                            return null;
                        } else {
                            return generator;
                        }
                    }
                });

            return json;
        }
    };

    public static final Skin BAHN_LABEL = new Skin(Gdx.files.internal("bahnlabel.json")) {
        //Override json loader to process FreeType fonts from skin JSON
        @Override
        protected Json getJsonLoader(final FileHandle skinFile) {
            Json json = super.getJsonLoader(skinFile);
            final Skin skin = this;

            json.setSerializer(FreeTypeFontGenerator.class,
                new Json.ReadOnlySerializer<>() {
                    @Override
                    public FreeTypeFontGenerator read(Json json,
                                                      JsonValue jsonData, Class type) {
                        String path = json.readValue("font", String.class, jsonData);
                        jsonData.remove("font");

                        FreeTypeFontGenerator.Hinting hinting =
                            FreeTypeFontGenerator.Hinting.valueOf(json.readValue("hinting",
                                String.class, "AutoMedium", jsonData));
                        jsonData.remove("hinting");

                        Texture.TextureFilter minFilter = Texture.TextureFilter.valueOf(
                            json.readValue("minFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("minFilter");

                        Texture.TextureFilter magFilter = Texture.TextureFilter.valueOf(
                            json.readValue("magFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("magFilter");

                        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                            json.readValue(FreeTypeFontGenerator
                                .FreeTypeFontParameter.class, jsonData);
                        parameter.hinting = hinting;
                        parameter.minFilter = minFilter;
                        parameter.magFilter = magFilter;
                        FreeTypeFontGenerator generator =
                            new FreeTypeFontGenerator(skinFile.parent().child(path));
                        BitmapFont font = generator.generateFont(parameter);
                        skin.add(jsonData.name, font);
                        if (parameter.incremental) {
                            generator.dispose();
                            return null;
                        } else {
                            return generator;
                        }
                    }
                });

            return json;
        }
    };
}
