package nl.tudelft.cse.sem.client.screens.mfa;

import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN_LABEL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.screens.RegisterScreen;
import nl.tudelft.cse.sem.shared.mfa.MultiFactorAuthentication;


@SuppressWarnings({"checkstyle:VariableDeclarationUsageDistance", "PMD.AvoidDuplicateLiterals",
    "PMD.DataflowAnomalyAnalysis"})
@Getter
public class MultiFactorRegisterScreen implements Screen {
    private transient Stage stage;
    private transient Texture texture;
    private transient Texture qrTexture;
    private transient String secret;
    private transient RegisterScreen registerScreen;
    private transient boolean mfaVerified;
    //private transient Label error;

    @Setter
    private transient String name;

    /**
     * Constructor for the LoginScreen.
     */
    public MultiFactorRegisterScreen(RegisterScreen registerScreen) {
        this.registerScreen = registerScreen;
        this.mfaVerified = false;
    }

    @Override
    public void show() {
        MultiFactorAuthentication mfa = MultiFactorAuthentication.builder().name(name).build();
        Gdx2DPixmap pixmap = null;

        try {
            pixmap = new Gdx2DPixmap(mfa.createQRCode(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        qrTexture = new Texture(new Pixmap(pixmap), Pixmap.Format.LuminanceAlpha, true);
        texture = new Texture("MFARegisterScreen.png");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Error display
        Label error = new Label("", BAHN_LABEL);
        error.setSize(220, 80);
        error.setPosition(280, 230);

        // Add buttons
        TextButton returnButton = new TextButton("", TRANSPARENT);
        returnButton.setPosition(90, 80);
        returnButton.setSize(260, 90);

        TextButton completeButton = new TextButton("", TRANSPARENT);
        completeButton.setPosition(90, 185);
        completeButton.setSize(260, 90);

        // Username field
        TextField verificationField = new TextField("", BAHN);
        verificationField.setPosition(300, 300);
        verificationField.setSize(220, 80);
        verificationField.setMaxLength(6);

        this.secret = mfa.getSecret();

        // Set actions
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                dispose();
                registerScreen.getGame().setScreen(registerScreen);
            }
        });
        completeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (mfa.verify(verificationField.getText())) {
                    mfaVerified = true;

                    dispose();
                    registerScreen.getGame().setScreen(registerScreen);
                } else {
                    error.setText("Wrong code");
                }
            }
        });

        stage.addActor(error);
        stage.addActor(returnButton);
        stage.addActor(completeButton);
        stage.addActor(verificationField);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0);
        stage.getBatch().draw(qrTexture, 193, 382);
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
}
