package com.keviqn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Main game; // Reference to your main game instance

    public MainMenuScreen(Main game) {
        this.game = game;

        // Create a Stage with a virtual resolution of 2400x1080.
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the UI skin (ensure uiskin.json, uiskin.atlas, and uiskin.png are in your android/assets folder)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create a root table that fills the screen.
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // Align the table at the top.
        rootTable.top();
        stage.addActor(rootTable);

        // Create the title label at the top.
        Label titleLabel = new Label("Another\nTower Defense\nGame?!", skin);
        titleLabel.setFontScale(5.5f); // Increase font scale as needed

        // Create a table to hold the buttons (centered in the middle of the screen)
        Table buttonTable = new Table();

        // Calculate button sizes based on 80% of width and 30% of height.
        float buttonWidth = 2400 * 0.8f;  // 1920 pixels
        float buttonHeight = 1080 * 0.3f; // 324 pixels

        // Create the Play button.
        TextButton playButton = new TextButton("Play", skin);
        playButton.getLabel().setFontScale(5); // Make the button text large
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // When Play is pressed, switch to the game screen.
                game.setScreen(new GameScreen(game));
            }
        });

        // Create the Stats button.
        TextButton statsButton = new TextButton("Stats", skin);
        statsButton.getLabel().setFontScale(5);

        // Add the buttons to the button table.
        buttonTable.add(playButton).width(buttonWidth).height(buttonHeight).padBottom(50);
        buttonTable.row();
        buttonTable.add(statsButton).width(buttonWidth).height(buttonHeight);

        // Layout the root table:
        // 1. Place the title at the top with minimal top padding so it's higher.
        // 2. Place the button table in the center.
        rootTable.add(titleLabel).expandX().padTop(1).padBottom(10);
        rootTable.row();
        rootTable.add(buttonTable).expand().center();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
