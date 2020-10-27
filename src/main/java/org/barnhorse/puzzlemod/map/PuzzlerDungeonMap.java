package org.barnhorse.puzzlemod.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import org.barnhorse.puzzlemod.characters.ThePuzzler;

import java.lang.reflect.Field;

public class PuzzlerDungeonMap extends DungeonMap {
    private static Texture top;
    private static Texture bot;
    private static Texture blend;

    private static final float H = 1020.0F * Settings.scale;
    private static final float BLEND_H = 512.0F * Settings.scale;

    public PuzzlerDungeonMap() {
        super();
        if (top == null) {
            top = ImageMaster.loadImage("images/ui/map/mapTop.png");
            bot = ImageMaster.loadImage("images/ui/map/mapBot.png");
            blend = ImageMaster.loadImage("images/ui/map/mapBlend.png");
        }
    }

    private float calculateMapSize() {
        return Settings.MAP_DST_Y * 10.0F - 1420.0F * Settings.scale;
    }

    public void show() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != ThePuzzler.Enums.THE_PUZZLER) {
            super.show();
        } else {
            this.targetAlpha = 1.0F;
            float mapSize = this.calculateMapSize();
            this.setMapMidDist(mapSize);
            this.setMapOffsetY(mapSize - 120.0F * Settings.scale);
        }
    }

    private Color getBaseMapColor() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("baseMapColor");
            field.setAccessible(true);
            return (Color) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access baseMapColor", e);
        }
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != ThePuzzler.Enums.THE_PUZZLER) {
            super.render(sb);
        } else {
            sb.setColor(this.getBaseMapColor());
            sb.draw(top, 0.0F,
                    H + DungeonMapScreen.offsetY + getMapOffsetY(),
                    (float) Settings.WIDTH,
                    1080.0F * Settings.scale);
            sb.draw(bot,
                    0.0F,
                    -this.getMapMidDist() + DungeonMapScreen.offsetY + getMapOffsetY() + 1.0F,
                    (float) Settings.WIDTH, 1080.0F * Settings.scale);
            this.renderMapBlender(sb);
        }
    }

    private void renderMapBlender(SpriteBatch sb) {
        sb.draw(blend, 0.0F, DungeonMapScreen.offsetY + getMapOffsetY() + 550.0F * Settings.scale, (float) Settings.WIDTH, BLEND_H);
        sb.draw(blend, 0.0F, DungeonMapScreen.offsetY + getMapOffsetY() + 650.0F * Settings.scale, (float) Settings.WIDTH, BLEND_H);
        sb.draw(blend, 0.0F, DungeonMapScreen.offsetY + getMapOffsetY() + 750.0F * Settings.scale, (float) Settings.WIDTH, BLEND_H);
    }

    private float getMapMidDist() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mapMidDist");
            field.setAccessible(true);
            return (Float) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access mapMidDist", e);
        }
    }

    private void setMapMidDist(float value) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mapMidDist");
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mapMidDist", e);
        }
    }

    private float getMapOffsetY() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mapOffsetY");
            field.setAccessible(true);
            return (Float) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access mapOffsetY", e);
        }
    }

    private void setMapOffsetY(float value) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mapOffsetY");
            field.setAccessible(true);
            field.set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mapOffsetY", e);
        }
    }
}