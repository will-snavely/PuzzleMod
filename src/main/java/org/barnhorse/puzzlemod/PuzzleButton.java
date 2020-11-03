package org.barnhorse.puzzlemod;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class PuzzleButton implements IUIElement {
    private Consumer<PuzzleButton> click;
    private Hitbox hb;
    private Texture texture;
    private float x;
    private float y;
    private float w;
    private float h;
    public ModPanel parent;
    private Color hoverColor;
    private Color bgColor;
    private BitmapFont font;
    private String text;

    public PuzzleButton(
            float xPos,
            float yPos,
            Texture texture,
            ModPanel p,
            Consumer<PuzzleButton> c,
            Color bgColor,
            Color hoverColor) {
        this(xPos, yPos, texture, p, c, bgColor, hoverColor, null, null);
    }

    public PuzzleButton(
            float xPos,
            float yPos,
            Texture tex,
            ModPanel p,
            Consumer<PuzzleButton> c,
            Color bgColor,
            Color hoverColor,
            String text,
            BitmapFont font) {
        this.texture = tex;
        this.x = xPos;
        this.y = yPos;
        this.w = (float) this.texture.getWidth();
        this.h = (float) this.texture.getHeight();
        this.hb = new Hitbox(
                this.x * Settings.scale,
                this.y * Settings.scale,
                this.w * Settings.scale,
                this.h * Settings.scale);
        this.parent = p;
        this.click = c;
        this.hoverColor = hoverColor;
        this.bgColor = bgColor;
        this.text = text;
        this.font = font;
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(this.hoverColor);
        } else {
            sb.setColor(this.bgColor);
        }

        sb.draw(
                this.texture,
                this.x * Settings.scale,
                this.y * Settings.scale,
                this.w * Settings.scale,
                this.h * Settings.scale);

        if (this.text != null && this.font != null) {
            FontHelper.renderFontCentered(
                    sb,
                    this.font,
                    this.text,
                    (this.x + this.w / 2) * Settings.scale,
                    (this.y + this.h / 2) * Settings.scale);
        }

        this.hb.render(sb);
    }

    public void update() {
        this.hb.update();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
            this.hb.clickStarted = true;
        }

        if (this.hb.clicked) {
            this.hb.clicked = false;
            this.onClick();
        }
    }

    private void onClick() {
        this.click.accept(this);
    }

    public int renderLayer() {
        return 1;
    }

    public int updateOrder() {
        return 1;
    }
}