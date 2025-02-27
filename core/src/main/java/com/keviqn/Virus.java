package com.keviqn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Virus {
    public float health;
    public float damage;  // fixed damage per attack
    public float x, y;
    public float size = 150;  // virus image size
    private boolean circling = false;
    private float circlingAngle = 0;
    private float attackTimer = 0;
    public boolean processedKill = false;
    public float speed;  // set externally
    private Texture texture;

    public Virus(float x, float y) {
        this.x = x;
        this.y = y;
        health = 5;
        damage = 2;
        texture = new Texture(Gdx.files.internal("virus.png"));
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isCircling() {
        return circling;
    }

    public void decreaseHealth(float amount) {
        health -= amount;
    }

    public float getCenterX() {
        return x + size / 2f;
    }

    public float getCenterY() {
        return y + size / 2f;
    }

    /**
     * Move toward the tower until within threshold, then circle.
     */
    public void update(float delta, float targetX, float targetY) {
        float centerX = getCenterX();
        float centerY = getCenterY();
        float dx = targetX - centerX;
        float dy = targetY - centerY;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        float threshold = 50;
        if (!circling) {
            if (distance > threshold) {
                float norm = distance;
                float vx = speed * dx / norm;
                float vy = speed * dy / norm;
                x += vx * delta;
                y += vy * delta;
            } else {
                circling = true;
                circlingAngle = (float)Math.atan2(dy, dx);
            }
        } else {
            float angularSpeed = 0.5f;
            circlingAngle += angularSpeed * delta;
            x = targetX - size / 2f + (float)Math.cos(circlingAngle) * threshold;
            y = targetY - size / 2f + (float)Math.sin(circlingAngle) * threshold;
        }
    }

    /**
     * When circling, attack the tower once per second, adding damage to tower's storage.
     */
    public void tryAttackTower(float delta, Tower tower) {
        if (!circling || !isAlive()) return;
        attackTimer += delta;
        if (attackTimer >= 1f) {
            attackTimer = 0;
            tower.addStorageDamage(damage);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, 250, 100);
    }
}
