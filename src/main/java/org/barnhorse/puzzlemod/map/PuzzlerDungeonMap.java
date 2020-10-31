package org.barnhorse.puzzlemod.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.assets.ResourceHelper;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.model.PuzzlePack;
import org.barnhorse.puzzlemod.rooms.PuzzleBossRoom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PuzzlerDungeonMap extends DungeonMap {
    private static PieceNode pieceGraph;
    private static float BOSS_W = 512.0F * Settings.scale;
    private static float BOSS_OFFSET_Y = 1416.0F * Settings.scale;

    private static class PieceNode {
        public Texture texture;
        public List<PieceNode> north = new ArrayList<>();
        public Color color;

        public PieceNode(Texture texture, Color color) {
            this.texture = texture;
            this.color = color;
        }
    }

    private static String pieceResource(String file) {
        return ResourceHelper.getResourcePath("images", "ui", file);
    }

    private static void initPieceGraph() {
        pieceGraph = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_bot.png")),
                Color.valueOf("#44c9f8"));
        PieceNode none = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_none.png")),
                Color.valueOf("#e65b5b"));
        PieceNode nsw = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_NSW.png")),
                Color.valueOf("#88dd6f"));
        PieceNode ew = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_EW.png")),
                Color.valueOf("#d5b350"));
        PieceNode nse = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_NSE.png")),
                Color.valueOf("#ce79ea"));
        PieceNode nwe = new PieceNode(
                ImageMaster.loadImage(pieceResource("map_piece_NWE.png")),
                Color.valueOf("#eae968"));

        pieceGraph.north.add(none);
        none.north.add(nsw);
        nsw.north.add(ew);
        ew.north.add(nse);
        nse.north.add(nwe);
        nwe.north.add(none);
    }

    public PuzzlerDungeonMap() {
        super();
        if (pieceGraph == null) {
            initPieceGraph();
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

    public void update() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != ThePuzzler.Enums.THE_PUZZLER) {
            super.update();
        } else {
            Color baseMapColor = this.getBaseMapColor();
            PuzzlePack currentPack = PuzzleMod.currentPuzzlePack;

            this.legend.update(
                    baseMapColor.a,
                    AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP);
            baseMapColor.a = MathHelper.fadeLerpSnap(baseMapColor.a, this.targetAlpha);
            this.setBaseMapColor(baseMapColor);

            this.bossHb.move(
                    (float) Settings.WIDTH / 2.0F,
                    DungeonMapScreen.offsetY + this.getMapOffsetY() + BOSS_OFFSET_Y + BOSS_W / 2.0F);

            this.bossHb.update();
            this.updateReticle();
            int curY = AbstractDungeon.getCurrMapNode().y;

            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE &&
                    AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP &&
                    curY == PuzzleMod.lastPuzzleRow &&
                    this.bossHb.hovered &&
                    (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
                AbstractDungeon.getCurrMapNode().taken = true;
                for (MapEdge edge : AbstractDungeon.getCurrMapNode().getEdges()) {
                    if (edge != null) {
                        edge.markAsTaken();
                    }
                }

                InputHelper.justClickedLeft = false;
                CardCrawlGame.music.fadeOutTempBGM();
                MapRoomNode node = new MapRoomNode(-1, 15);
                node.room = new PuzzleBossRoom(currentPack.boss);
                AbstractDungeon.nextRoom = node;
                if (AbstractDungeon.pathY.size() > 1) {
                    AbstractDungeon.pathX.add(AbstractDungeon.pathX.get(AbstractDungeon.pathX.size() - 1));
                    AbstractDungeon.pathY.add(AbstractDungeon.pathY.get(AbstractDungeon.pathY.size() - 1) + 1);
                } else {
                    AbstractDungeon.pathX.add(1);
                    AbstractDungeon.pathY.add(15);
                }

                AbstractDungeon.nextRoomTransitionStart();
                this.bossHb.hovered = false;
            }

            if (!this.bossHb.hovered && !this.atBoss) {
                this.getBossNodeColor().lerp(
                        this.getNotTakenColor(),
                        Gdx.graphics.getDeltaTime() * 8.0F);
            } else {
                setBossNodeColor(MapRoomNode.AVAILABLE_COLOR.cpy());
            }
            getBossNodeColor().a = getBaseMapColor().a;
        }
    }

    private void updateReticle() {
        if (Settings.isControllerMode) {
            Color reticleColor = this.getReticleColor();
            if (this.bossHb.hovered) {
                reticleColor.a += Gdx.graphics.getDeltaTime() * 3.0F;
                if (reticleColor.a > 1.0F) {
                    reticleColor.a = 1.0F;
                }
            } else {
                reticleColor.a = 0.0F;
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.chosenClass != ThePuzzler.Enums.THE_PUZZLER) {
            super.render(sb);
        } else {
            PieceNode cur = pieceGraph;
            int puzzleCount = PuzzleMod.currentPuzzlePack.puzzles.size();
            int bgPieceCount = (puzzleCount + 2) / 3;
            int yOffset = 0;
            float bottom = -this.getMapMidDist()
                    + DungeonMapScreen.offsetY
                    + this.getMapOffsetY() + 1.0F;
            while (bgPieceCount > 0) {
                Color pieceColor = cur.color;
                pieceColor.a = getBaseMapColor().a;
                sb.setColor(pieceColor);
                sb.draw(
                        cur.texture,
                        0.0F,
                        yOffset + bottom,
                        Settings.WIDTH,
                        cur.texture.getHeight() * Settings.scale);
                yOffset += (cur.texture.getHeight() / 2) * Settings.scale;
                yOffset += 138 * Settings.scale;
                cur = cur.north.get(0);
                bgPieceCount--;
            }
        }
    }

    private Color getReticleColor() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("reticleColor");
            field.setAccessible(true);
            return (Color) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access reticleColor", e);
        }
    }

    private Color getNotTakenColor() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("NOT_TAKEN_COLOR");
            field.setAccessible(true);
            return (Color) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access NOT_TAKEN_COLOR", e);
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

    private Color getBossNodeColor() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("bossNodeColor");
            field.setAccessible(true);
            return (Color) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access bossNodeColor", e);
        }
    }

    private void setBossNodeColor(Color color) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("bossNodeColor");
            field.setAccessible(true);
            field.set(this, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access bossNodeColor", e);
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