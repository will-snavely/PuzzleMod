package org.barnhorse.puzzlemod;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import org.barnhorse.puzzlemod.assets.StaticAssets;

import java.util.ArrayList;
import java.util.List;

public class PuzzleList implements IUIElement {
    private float x;
    private float y;
    private List<ListItem> items;
    private int windowStart = 0;
    private int windowSize = 6;
    private int selectedRow = 0;
    private Color bgColor;
    private Color highlightColor;
    private List<Hitbox> rowHitboxes;
    private static Texture listBackground;
    private static Texture rowHighlight;
    private static int rowHeight;
    public static int listWidth;

    static {
        listBackground = ImageMaster.loadImage(StaticAssets.LIST_BACKGROUND);
        rowHighlight = ImageMaster.loadImage(StaticAssets.ROW_HIGHLIGHT);
        rowHeight = 60;
        listWidth = 800;
    }

    public static class ListItem {
        public String value;

        public ListItem(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String getDisplay() {
            if (PuzzleMod.isBuiltin(this.value)) {
                return "(Builtin) " + PuzzleMod.getBaseResource(this.value);
            } else {
                return "(Custom) " + this.value;
            }
        }
    }

    public ModPanel parent;

    public void increaseSelectedIndex() {
        if (this.selectedRow == this.items.size() - 1) {
            return;
        }
        if (this.selectedRow == this.windowStart + this.windowSize - 1) {
            this.windowStart += 1;
        }
        this.selectedRow += 1;
    }

    public void decreaseSelectedIndex() {
        if (this.selectedRow == 0) {
            return;
        }
        if (this.selectedRow == this.windowStart) {
            this.windowStart -= 1;
        }
        this.selectedRow -= 1;
    }

    public void resetItems(List<String> options) {
        this.windowStart = 0;
        this.selectedRow = 0;
        this.items = new ArrayList<>();
        for (String option : options) {
            this.items.add(new ListItem(option));
        }
    }

    public ListItem getSelected() {
        return this.items.get(this.selectedRow);
    }

    public PuzzleList(
            float xPos,
            float yPos,
            ModPanel p,
            List<String> options,
            Color bgColor,
            Color highlightColor) {
        this.parent = p;
        this.x = xPos * Settings.scale;
        this.y = yPos * Settings.scale;
        this.resetItems(options);
        this.bgColor = bgColor;
        this.highlightColor = highlightColor;
        this.makeHitBoxes();
    }

    private void makeHitBoxes() {
        this.rowHitboxes = new ArrayList<>();
        for (int ii = 0; ii < windowSize; ii++) {
            this.rowHitboxes.add(new Hitbox(
                    this.x,
                    this.y - ((ii + 1) * rowHeight) * Settings.scale,
                    listWidth * Settings.scale,
                    rowHeight * Settings.scale
            ));
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(bgColor);
        float height = this.windowSize * rowHeight * Settings.scale;
        sb.draw(
                listBackground,
                this.x,
                this.y - height,
                listWidth * Settings.scale,
                height
        );

        sb.setColor(Color.WHITE);
        for (int ii = windowStart; ii < windowStart + windowSize; ii++) {
            if (ii >= this.items.size()) {
                continue;
            }
            int offset = ii - windowStart;
            if (selectedRow == ii) {
                sb.setColor(highlightColor);
                sb.draw(
                        rowHighlight,
                        this.x,
                        this.y - ((offset + 1) * rowHeight) * Settings.scale,
                        listWidth * Settings.scale,
                        rowHeight * Settings.scale
                );
            }
            sb.setColor(Color.WHITE);
            FontHelper.renderFontLeft(
                    sb,
                    FontHelper.largeDialogOptionFont,
                    this.items.get(ii).getDisplay(),
                    x + 20 * Settings.scale,
                    y - (offset * rowHeight + (rowHeight / 2.0f)) * Settings.scale,
                    Color.WHITE);
        }

        this.rowHitboxes.forEach(hb -> hb.render(sb));
    }

    @Override
    public void update() {
        this.rowHitboxes.forEach(Hitbox::update);
        for (int ii = 0; ii < this.windowSize; ii++) {
            Hitbox hb = this.rowHitboxes.get(ii);
            if (this.windowStart + ii < this.items.size()) {
                if (hb.hovered && InputHelper.justClickedLeft) {
                    CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
                    hb.clickStarted = true;
                }
                if (hb.clicked) {
                    hb.clicked = false;
                    this.selectedRow = this.windowStart + ii;
                }
            }
        }
    }

    @Override
    public int renderLayer() {
        return ModPanel.MIDDLE_LAYER;
    }

    @Override
    public int updateOrder() {
        return ModPanel.MIDDLE_LAYER;
    }
}
