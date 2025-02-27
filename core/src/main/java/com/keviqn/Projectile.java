package com.keviqn;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private float x, y;
    private float width = 150, height = 150;
    private Virus target;
    private float damage;
    private Texture texture;
    private float speed = 600; // pixels per second
    private boolean collided = false;
    private Vector2 direction;

    public Projectile(float startX, float startY, Virus target, float damage, Texture texture) {
        this.x = startX - width / 2f;
        this.y = startY - height / 2f;
        this.target = target;
        this.damage = damage;
        this.texture = texture;
        direction = new Vector2(target.getCenterX() - startX, target.getCenterY() - startY).nor();
    }

    public void update(float delta) {
        x += direction.x * speed * delta;
        y += direction.y * speed * delta;
        float dist = Vector2.dst(x + width / 2f, y + height / 2f, target.getCenterX(), target.getCenterY());
        if (dist < 20) {
            collided = true;
        }
    }

    public boolean hasCollided() {
        return collided;
    }

    public float getDamage() {
        return damage;
    }

    public Virus getTarget() {
        return target;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
}
