package com.keviqn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;

public class Tower {
    private float x, y;
    private float width = 350, height = 350;
    private Texture texture;

    // Upgrade levels and prices.
    private int softwareVersionLevel = 1;
    private int processorSpeedLevel = 1;
    private int storageSpaceLevel = 1;
    private int internetSpeedLevel = 1;
    private int graphicsCardLevel = 1;

    private int softwareVersionPrice = 1;
    private int processorSpeedPrice = 1;
    private int storageSpacePrice = 1;
    private int internetSpeedPrice = 1;
    private int graphicsCardPrice = 1;

    // Derived stats:
    // Defense = softwareVersionLevel * 3.
    public float getDefense() {
        return softwareVersionLevel * 3;
    }

    // Processor Speed = processorSpeedLevel * 2.
    public float getProcessorSpeed() {
        return processorSpeedLevel * 2;
    }

    // Storage Available = 100 * storageSpaceLevel.
    public float getStorageAvailable() {
        return 100 * storageSpaceLevel;
    }

    // Internet Speed = internetSpeedLevel * 50.
    public float getInternetSpeed() {
        return internetSpeedLevel * 50;
    }

    // Graphics Card = graphicsCardLevel * 3.
    public float getGraphicsCard() {
        return graphicsCardLevel * 3;
    }

    // Storage total.
    private float storageTotal = 0;
    public float getStorageTotal() {
        return storageTotal;
    }
    public void addStorageDamage(float amount) {
        storageTotal += amount;
    }

    private float attackTimer = 0;
    private float currencyTimer = 0;

    private Texture cannonballTexture;

    public Tower(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        texture = new Texture(Gdx.files.internal("tower.png"));
        cannonballTexture = new Texture(Gdx.files.internal("zap.png"));
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public int getSoftwareVersionLevel() {
        return softwareVersionLevel;
    }
    public int getSoftwareVersionPrice() {
        return softwareVersionPrice;
    }
    public void upgradeSoftwareVersion() {
        softwareVersionLevel++;
        softwareVersionPrice = MathUtils.round(softwareVersionPrice * 1.3f);
    }

    public int getProcessorSpeedLevel() {
        return processorSpeedLevel;
    }
    public int getProcessorSpeedPrice() {
        return processorSpeedPrice;
    }
    public void upgradeProcessorSpeed() {
        processorSpeedLevel++;
        processorSpeedPrice = MathUtils.round(processorSpeedPrice * 1.3f);
    }

    public int getStorageSpaceLevel() {
        return storageSpaceLevel;
    }
    public int getStorageSpacePrice() {
        return storageSpacePrice;
    }
    public void upgradeStorageSpace() {
        storageSpaceLevel++;
        storageSpacePrice = MathUtils.round(storageSpacePrice * 1.3f);
    }

    public int getInternetSpeedLevel() {
        return internetSpeedLevel;
    }
    public int getInternetSpeedPrice() {
        return internetSpeedPrice;
    }
    public void upgradeInternetSpeed() {
        internetSpeedLevel++;
        internetSpeedPrice = MathUtils.round(internetSpeedPrice * 1.3f);
    }

    public int getGraphicsCardLevel() {
        return graphicsCardLevel;
    }
    public int getGraphicsCardPrice() {
        return graphicsCardPrice;
    }
    public void upgradeGraphicsCard() {
        graphicsCardLevel++;
        graphicsCardPrice = MathUtils.round(graphicsCardPrice * 1.3f);
    }

    /**
     * Tower attack: every 1/processorSpeed seconds, if a virus is within range (100+internetSpeed),
     * spawn a projectile that deals damage equal to graphicsCard.
     */
    public Projectile tryAttack(float delta, ArrayList<Virus> viruses) {
        attackTimer += delta;
        float cooldown = 1f / getProcessorSpeed();
        if (attackTimer < cooldown) return null;
        attackTimer = 0;
        for (Virus virus : viruses) {
            if (virus.isAlive()) {
                float distance = com.badlogic.gdx.math.Vector2.dst(getCenterX(), getCenterY(), virus.getCenterX(), virus.getCenterY());
                if (distance <= 100 + getInternetSpeed()) {
                    float effectiveDamage = getGraphicsCard();
                    return new Projectile(getCenterX(), getCenterY(), virus, effectiveDamage, cannonballTexture);
                }
            }
        }
        return null;
    }

    /**
     * Gather currency at a rate equal to processor speed per second.
     */
    public int gatherCurrency(float delta) {
        currencyTimer += delta;
        if (currencyTimer >= 1f) {
            currencyTimer = 0;
            return (int)getProcessorSpeed();
        }
        return 0;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
}
