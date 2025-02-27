package com.keviqn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DamageIndicator {
    private float x, y;
    private String text;
    private float timer;
    private final float duration = 0.5f;
    private Color color;
    private BitmapFont font;

    public DamageIndicator(float x, float y, float damage) {
        this.x = x;
        this.y = y;
        if(damage > 0) {
            text = String.valueOf((int)damage);
            color = Color.RED;
        } else {
            text = "0";
            color = Color.BLUE;
        }
        timer = duration;
        font = new BitmapFont();  // Use default font (could be replaced by a high-res one)
        font.getData().setScale(5);
        font.setColor(color);
    }

    public void update(float delta) {
        timer -= delta;
    }

    public boolean isExpired() {
        return timer <= 0;
    }

    public void render(SpriteBatch batch) {
        font.setColor(color);
        font.draw(batch, text, x, y);
    }
}
