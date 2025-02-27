package com.keviqn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen {

    private Main game;
    private Stage uiStage;
    private SpriteBatch batch;
    private Skin skin;

    // UI Labels and upgrade table.
    private Label waveLabel, gpLabel, towerStorageLabel, enemiesRemainingLabel;
    private Texture gpTexture;
    private Table tabContent;

    // Game objects.
    private Tower tower;
    private ArrayList<Virus> viruses;
    private ArrayList<Projectile> projectiles;
    private ArrayList<DamageIndicator> damageIndicators;

    // World camera.
    private OrthographicCamera worldCamera;
    private final float WORLD_WIDTH = 2400;
    private final float WORLD_HEIGHT = 1080;

    // Wave logic.
    private int currentWave = 1;
    private float waveCooldown = 2f;
    private float waveTimer = 0f;

    // Currency and stats.
    private int gpEarned = 0;
    private int enemiesKilled = 0;
    private float currencyTimer = 0f;

    // Game state.
    private boolean gamePaused = false;
    private boolean gameOver = false;

    public GameScreen(Main game) {
        this.game = game;

        // Set up world camera.
        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        // Set up batch.
        batch = new SpriteBatch();

        // Load skin.
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create the tower at the center.
        // Tower is rendered at 400x100.
        float towerWidth = 400;
        float towerHeight = 100;
        float towerX = WORLD_WIDTH / 2f - towerWidth / 2f;
        float towerY = WORLD_HEIGHT / 2f - towerHeight / 2f;
        tower = new Tower(towerX, towerY, towerWidth, towerHeight);

        // Initialize lists.
        viruses = new ArrayList<>();
        projectiles = new ArrayList<>();
        damageIndicators = new ArrayList<>();

        // Build UI overlays.
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        // -- Top-left info overlay.
        Table infoTable = new Table();
        infoTable.top().left();
        infoTable.setFillParent(true);
        infoTable.pad(20);

        waveLabel = new Label("Wave: " + currentWave, skin);
        waveLabel.setFontScale(5);

        towerStorageLabel = new Label("Storage: " + (int)tower.getStorageTotal() + " / " + (int)tower.getStorageAvailable(), skin);
        towerStorageLabel.setFontScale(5);

        gpTexture = new Texture(Gdx.files.internal("gp1.png"));
        Image gpImage = new Image(gpTexture);
        gpImage.setSize(65, 65);
        gpLabel = new Label(String.valueOf(gpEarned), skin);
        gpLabel.setFontScale(5);

        enemiesRemainingLabel = new Label("Viruses Remaining: 0", skin);
        enemiesRemainingLabel.setFontScale(5);

        infoTable.add(waveLabel).expandX().left();
        infoTable.row().padTop(10);
        infoTable.add(towerStorageLabel).expandX().left();
        infoTable.row().padTop(20);
        Table gpTable = new Table();
        gpTable.add(gpImage).size(65, 65).padRight(10);
        gpTable.add(gpLabel).left();
        infoTable.add(gpTable).expandX().left();
        infoTable.row().padTop(20);
        infoTable.add(enemiesRemainingLabel).expandX().left();
        uiStage.addActor(infoTable);

        // -- Top-right play/pause button.
        Table topRightTable = new Table();
        topRightTable.setFillParent(true);
        topRightTable.top().right();
        topRightTable.pad(20);
        final TextButton pauseButton = new TextButton("II", skin);
        pauseButton.getLabel().setFontScale(5);
        pauseButton.getLabel().setColor(Color.GREEN);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gamePaused = !gamePaused;
                if(gamePaused)
                    pauseButton.getLabel().setColor(Color.RED);
                else
                    pauseButton.getLabel().setColor(Color.GREEN);
            }
        });
        topRightTable.add(pauseButton).size(80,80);
        uiStage.addActor(topRightTable);

        // -- Bottom UI overlay for tabs.
        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom().padBottom(100);
        uiStage.addActor(bottomTable);

        float menuHeight = 500 / 2.5f;          // 200 px
        float tabButtonWidth = 800 / 2.5f;        // 320 px
        float tabButtonHeight = 200 / 2.5f;       // 80 px
        float tabContentHeight = menuHeight - tabButtonHeight;  // 120 px

        TextButton tab1Button = new TextButton("Tab 1", skin);
        tab1Button.getLabel().setFontScale(5);
        TextButton tab2Button = new TextButton("Tab 2", skin);
        tab2Button.getLabel().setFontScale(5);
        TextButton tab3Button = new TextButton("Tab 3", skin);
        tab3Button.getLabel().setFontScale(5);

        tabContent = new Table();
        populateTabContent("Tab 1");

        tab1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                populateTabContent("Tab 1");
            }
        });
        tab2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                populateTabContent("Tab 2");
            }
        });
        tab3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                populateTabContent("Tab 3");
            }
        });

        bottomTable.add(tab1Button).width(tabButtonWidth).height(tabButtonHeight).pad(5);
        bottomTable.add(tab2Button).width(tabButtonWidth).height(tabButtonHeight).pad(5);
        bottomTable.add(tab3Button).width(tabButtonWidth).height(tabButtonHeight).pad(5);
        bottomTable.row();
        bottomTable.add(tabContent).colspan(3)
            .width(uiStage.getViewport().getScreenWidth())
            .height(tabContentHeight).padTop(5);

        // Start with 0 GP.
        gpEarned = 0;
        gpLabel.setText(String.valueOf(gpEarned));

        // Initialize currency timer.
        currencyTimer = 0;

        // Initialize upgrade values in the tower (they start at level 1 and price 1).
        // (They are already initialized in Tower.)

        // Spawn first wave.
        spawnWave();
    }

    // Spawn a new wave of viruses.
    private void spawnWave() {
        viruses.clear();
        int enemyCount = currentWave * 10;
        for (int i = 0; i < enemyCount; i++) {
            float spawnX = 0;
            float spawnY = MathUtils.random(0, WORLD_HEIGHT - 60); // virus size assumed 60
            Virus virus = new Virus(spawnX, spawnY);
            // Effective speed = currentWave + 100.
            virus.speed = currentWave + 100;
            viruses.add(virus);
        }
        if (waveLabel != null)
            waveLabel.setText("Wave: " + currentWave);
    }

    // Update the viruses remaining counter.
    private void updateEnemiesRemaining() {
        int count = 0;
        for (Virus virus : viruses) {
            if (virus.isAlive()) count++;
        }
        enemiesRemainingLabel.setText("Viruses Remaining: " + count);
    }

    // Populate the bottom tab content.
    // For Tab 1, show upgrade options.
    private void populateTabContent(String tabName) {
        tabContent.clear();
        if (tabName.equals("Tab 1")) {
            Table upgradeTable = new Table();
            // For each upgrade, create a row.
            // Software Version Upgrade.
            upgradeTable.add(new Label("Software Version", skin)).padRight(10);
            final TextButton svButton = new TextButton(String.valueOf(tower.getSoftwareVersionLevel()), skin);
            svButton.getLabel().setFontScale(4);
            upgradeTable.add(svButton).padRight(10);
            upgradeTable.row();
            upgradeTable.add(new Label("Price: " + tower.getSoftwareVersionPrice(), skin)).colspan(2).padBottom(10);
            upgradeTable.row();
            // Processor Speed Upgrade.
            upgradeTable.add(new Label("Processor Speed", skin)).padRight(10);
            final TextButton psButton = new TextButton(String.valueOf(tower.getProcessorSpeedLevel()), skin);
            psButton.getLabel().setFontScale(4);
            upgradeTable.add(psButton).padRight(10);
            upgradeTable.row();
            upgradeTable.add(new Label("Price: " + tower.getProcessorSpeedPrice(), skin)).colspan(2).padBottom(10);
            upgradeTable.row();
            // Storage Space Upgrade.
            upgradeTable.add(new Label("Storage Space", skin)).padRight(10);
            final TextButton ssButton = new TextButton(String.valueOf(tower.getStorageSpaceLevel()), skin);
            ssButton.getLabel().setFontScale(4);
            upgradeTable.add(ssButton).padRight(10);
            upgradeTable.row();
            upgradeTable.add(new Label("Price: " + tower.getStorageSpacePrice(), skin)).colspan(2).padBottom(10);
            upgradeTable.row();
            // Internet Speed Upgrade.
            upgradeTable.add(new Label("Internet Speed", skin)).padRight(10);
            final TextButton isButton = new TextButton(String.valueOf(tower.getInternetSpeedLevel()), skin);
            isButton.getLabel().setFontScale(4);
            upgradeTable.add(isButton).padRight(10);
            upgradeTable.row();
            upgradeTable.add(new Label("Price: " + tower.getInternetSpeedPrice(), skin)).colspan(2).padBottom(10);
            upgradeTable.row();
            // Graphics Card Upgrade.
            upgradeTable.add(new Label("Graphics Card", skin)).padRight(10);
            final TextButton gcButton = new TextButton(String.valueOf(tower.getGraphicsCardLevel()), skin);
            gcButton.getLabel().setFontScale(4);
            upgradeTable.add(gcButton).padRight(10);
            upgradeTable.row();
            upgradeTable.add(new Label("Price: " + tower.getGraphicsCardPrice(), skin)).colspan(2).padBottom(10);
            upgradeTable.row();

            // Add listeners for each upgrade button.
            svButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(gpEarned >= tower.getSoftwareVersionPrice()){
                        gpEarned -= tower.getSoftwareVersionPrice();
                        tower.upgradeSoftwareVersion();
                        populateTabContent("Tab 1");
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            });
            psButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(gpEarned >= tower.getProcessorSpeedPrice()){
                        gpEarned -= tower.getProcessorSpeedPrice();
                        tower.upgradeProcessorSpeed();
                        populateTabContent("Tab 1");
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            });
            ssButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(gpEarned >= tower.getStorageSpacePrice()){
                        gpEarned -= tower.getStorageSpacePrice();
                        tower.upgradeStorageSpace();
                        populateTabContent("Tab 1");
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            });
            isButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(gpEarned >= tower.getInternetSpeedPrice()){
                        gpEarned -= tower.getInternetSpeedPrice();
                        tower.upgradeInternetSpeed();
                        populateTabContent("Tab 1");
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            });
            gcButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(gpEarned >= tower.getGraphicsCardPrice()){
                        gpEarned -= tower.getGraphicsCardPrice();
                        tower.upgradeGraphicsCard();
                        populateTabContent("Tab 1");
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            });

            tabContent.add(upgradeTable).expand().left();
        } else {
            Label contentLabel = new Label("Content for " + tabName, skin);
            contentLabel.setFontScale(5);
            tabContent.add(contentLabel).expand().center();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!gamePaused && !gameOver) {
            float towerCenterX = tower.getCenterX();
            float towerCenterY = tower.getCenterY();

            // Tower attacks: spawn a projectile if possible.
            Projectile proj = tower.tryAttack(delta, viruses);
            if(proj != null) {
                projectiles.add(proj);
            }

            // Accumulate currency based on processor speed.
            int gathered = tower.gatherCurrency(delta);
            if(gathered > 0){
                gpEarned += gathered;
                gpLabel.setText(String.valueOf(gpEarned));
            }

            // Update viruses.
            for(Virus virus : viruses) {
                virus.update(delta, towerCenterX, towerCenterY);
                if(virus.isAlive() && virus.isCircling()) {
                    virus.tryAttackTower(delta, tower);
                }
            }

            // Update projectiles.
            Iterator<Projectile> projIter = projectiles.iterator();
            while(projIter.hasNext()){
                Projectile p = projIter.next();
                p.update(delta);
                if(p.hasCollided()){
                    Virus target = p.getTarget();
                    target.decreaseHealth(p.getDamage());
                    damageIndicators.add(new DamageIndicator(target.getCenterX(), target.getCenterY(), p.getDamage()));
                    projIter.remove();
                }
            }

            // Update damage indicators.
            Iterator<DamageIndicator> diIter = damageIndicators.iterator();
            while(diIter.hasNext()){
                DamageIndicator di = diIter.next();
                di.update(delta);
                if(di.isExpired()){
                    diIter.remove();
                }
            }

            // Process virus kills and currency drops.
            for(Virus virus : viruses) {
                if(!virus.isAlive() && !virus.processedKill) {
                    virus.processedKill = true;
                    enemiesKilled++;
                    // 1 in 3 chance to drop 1-10 GP.
                    int dropChance = MathUtils.random(1,3);
                    if(dropChance == 1) {
                        int gpDrop = MathUtils.random(1,10);
                        gpEarned += gpDrop;
                        gpLabel.setText(String.valueOf(gpEarned));
                    }
                }
            }

            // Update tower storage (damage from viruses).
            towerStorageLabel.setText("Storage: " + (int)tower.getStorageTotal() + " / " + (int)tower.getStorageAvailable());
            if(tower.getStorageTotal() >= tower.getStorageAvailable()){
                gameOver = true;
                showGameOverDialog();
            }

            // Wave spawn logic.
            boolean allDead = true;
            for(Virus virus : viruses) {
                if(virus.isAlive()){
                    allDead = false;
                    break;
                }
            }
            if(allDead && !viruses.isEmpty()){
                waveTimer += delta;
                if(waveTimer >= waveCooldown){
                    currentWave++;
                    spawnWave();
                    waveTimer = 0f;
                }
            }
        }

        updateEnemiesRemaining();

        // Draw game objects.
        batch.begin();
        tower.render(batch);
        for(Virus virus : viruses) {
            virus.render(batch);
        }
        for(Projectile p : projectiles) {
            p.render(batch);
        }
        batch.end();

        // Draw damage indicators.
        batch.begin();
        for(DamageIndicator di : damageIndicators) {
            di.render(batch);
        }
        batch.end();

        // Render UI.
        uiStage.act(delta);
        uiStage.draw();
    }


    private void showGameOverDialog() {
        com.badlogic.gdx.scenes.scene2d.ui.Dialog dialog = new com.badlogic.gdx.scenes.scene2d.ui.Dialog("Game Over", skin) {
            protected void result(Object obj) {
                game.setScreen(new GameScreen(game));
            }
        };
        dialog.text("Game Over\nEnemies Killed: " + enemiesKilled + "\nGP Earned: " + gpEarned);
        dialog.button("Restart");
        dialog.show(uiStage);
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        worldCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
    }

    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }

    @Override
    public void dispose() {
        uiStage.dispose();
        batch.dispose();
        skin.dispose();
        gpTexture.dispose();
    }
}
