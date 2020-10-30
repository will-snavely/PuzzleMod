package org.barnhorse.puzzlemod.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import org.barnhorse.puzzlemod.assets.ResourceHelper;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.dungeons.PuzzlerExordium;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzlerDungeonMap extends DungeonMap {
    private static PieceNode pieceGraph;

    private class PieceNode {
        public Texture texture;
        public List<PieceNode> next = new ArrayList<>();
        public boolean outie;

        public PieceNode(Texture texture, boolean outie) {
            this.texture = texture;
            this.outie = outie;
        }

        public void shuffle() {
            Collections.shuffle(this.next);
        }
    }

    public PuzzlerDungeonMap() {
        super();
        this.setBaseMapColor(Color.CYAN);
        if (pieceGraph == null) {
            pieceGraph = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_bot.png")), true);
            PieceNode none = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_none.png")), false);
            PieceNode all = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_all.png")), true);
            PieceNode ew = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_EW.png")), false);
            PieceNode ews = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_EWS.png")), false);
            PieceNode nwe = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_NWE.png")), true);
            PieceNode nse = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_NSE.png")), true);
            PieceNode nsw = new PieceNode(
                    ImageMaster.loadImage(ResourceHelper.getResourcePath(
                            "images", "ui", "map_piece_NSW.png")), true);

            pieceGraph.next.add(ew);
            pieceGraph.next.add(nwe);
            pieceGraph.next.add(none);
            pieceGraph.shuffle();

            none.next.add(all);
            none.next.add(nse);
            none.next.add(nsw);
            none.next.add(ews);
            none.shuffle();

            all.next.add(ew);
            all.next.add(nwe);
            all.next.add(none);
            all.shuffle();

            ew.next.add(all);
            ew.next.add(nse);
            ew.next.add(nsw);
            ew.next.add(ews);
            ew.shuffle();

            ews.next.add(all);
            ews.next.add(nse);
            ews.next.add(nsw);
            ews.next.add(ews);
            ews.shuffle();

            nwe.next.add(ew);
            nwe.next.add(nwe);
            nwe.next.add(none);
            nwe.shuffle();

            nse.next.add(ew);
            nse.next.add(nwe);
            nse.next.add(none);
            nse.shuffle();

            nsw.next.add(ew);
            nsw.next.add(nwe);
            nsw.next.add(none);
            nsw.shuffle();
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

    private void setBaseMapColor(Color color) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("baseMapColor");
            field.setAccessible(true);
            field.set(this, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access baseMapColor", e);
        }
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != ThePuzzler.Enums.THE_PUZZLER) {
            super.render(sb);
        } else {
            sb.setColor(this.getBaseMapColor());
            PieceNode cur = pieceGraph;
            int puzzleCount = PuzzlerExordium.currentPuzzlePack.puzzles.size();
            int bgPieceCount = (puzzleCount + 2) / 3;
            int yOffset = 0;
            float bottom = -this.getMapMidDist()
                    + DungeonMapScreen.offsetY
                    + this.getMapOffsetY() + 1.0F;
            while (bgPieceCount > 0) {
                sb.draw(
                        cur.texture,
                        0.0F,
                        yOffset + bottom,
                        Settings.WIDTH,
                        cur.texture.getHeight() * Settings.scale);
                yOffset += (cur.texture.getHeight() / 2) * Settings.scale;
                yOffset += 140 * Settings.scale;
                cur = cur.next.get(0);
                bgPieceCount--;
            }
        }
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