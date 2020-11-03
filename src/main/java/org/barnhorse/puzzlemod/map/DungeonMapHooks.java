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
import org.barnhorse.puzzlemod.packs.model.PuzzlePack;
import org.barnhorse.puzzlemod.rooms.PuzzleBossRoom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DungeonMapHooks {
    private static PieceNode pieceGraph;
    private static float BOSS_W = 512.0F * Settings.scale;

    private static class PieceNode {
        public Texture texture;
        public List<PieceNode> north = new ArrayList<>();
        public Color color;

        public PieceNode(Texture texture, Color color) {
            this.texture = texture;
            this.color = color;
        }
    }

    static {
        initPieceGraph();
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

    private static float calculateMapSize() {
        return Settings.MAP_DST_Y * 10.0F - 1420.0F * Settings.scale;
    }

    public static void show(DungeonMap map) {
        map.targetAlpha = 1.0F;
        float mapSize = calculateMapSize();
        setMapMidDist(map, mapSize);
        setMapOffsetY(map, mapSize - 120.0F * Settings.scale);
    }

    public static void update(DungeonMap map) {
        Color baseMapColor = getBaseMapColor(map);
        PuzzlePack currentPack = PuzzleMod.currentPuzzlePack;

        map.legend.update(
                baseMapColor.a,
                AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP);
        baseMapColor.a = MathHelper.fadeLerpSnap(baseMapColor.a, map.targetAlpha);

        map.bossHb.move(
                (float) Settings.WIDTH / 2.0F,
                DungeonMapScreen.offsetY + getMapOffsetY(map) + getBossOffset() + BOSS_W / 2.0F);
        map.bossHb.update();
        updateReticle(map);

        int curY = AbstractDungeon.getCurrMapNode().y;
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE &&
                AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP &&
                curY == PuzzleMod.lastPuzzleRow &&
                map.bossHb.hovered &&
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
            map.bossHb.hovered = false;
        }

        if (!map.bossHb.hovered && !map.atBoss) {
            getBossNodeColor(map).lerp(
                    getNotTakenColor(map),
                    Gdx.graphics.getDeltaTime() * 8.0F);
        } else {
            getBossNodeColor(map).set(MapRoomNode.AVAILABLE_COLOR.cpy());
        }
        getBossNodeColor(map).a = getBaseMapColor(map).a;
    }

    private static void updateReticle(DungeonMap map) {
        if (Settings.isControllerMode) {
            Color reticleColor = getReticleColor(map);
            if (map.bossHb.hovered) {
                reticleColor.a += Gdx.graphics.getDeltaTime() * 3.0F;
                if (reticleColor.a > 1.0F) {
                    reticleColor.a = 1.0F;
                }
            } else {
                reticleColor.a = 0.0F;
            }
        }
    }

    public static void render(DungeonMap map, SpriteBatch sb) {
        PieceNode cur = pieceGraph;
        int puzzleCount = PuzzleMod.currentPuzzlePack.puzzles.size();
        int bgPieceCount = (puzzleCount + 2) / 3;
        int yOffset = 0;
        float bottom = -getMapMidDist(map)
                + DungeonMapScreen.offsetY
                + getMapOffsetY(map) + 1.0F;
        while (bgPieceCount > 0) {
            Color pieceColor = cur.color;
            pieceColor.a = getBaseMapColor(map).a;
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

    private static float getBossOffset() {
        int puzzleCount = PuzzleMod.currentPuzzlePack.puzzles.size();
        return puzzleCount * 161.0f * Settings.scale;
    }

    public static void renderBossIcon(DungeonMap map, SpriteBatch sb) {
        if (DungeonMap.boss != null) {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, getBossNodeColor(map).a));

            sb.draw(DungeonMap.bossOutline,
                    (float) Settings.WIDTH / 2.0F - BOSS_W / 2.0F,
                    DungeonMapScreen.offsetY + getMapOffsetY(map) + getBossOffset(),
                    BOSS_W, BOSS_W);
            sb.setColor(getBossNodeColor(map));
            sb.draw(DungeonMap.boss,
                    (float) Settings.WIDTH / 2.0F - BOSS_W / 2.0F,
                    DungeonMapScreen.offsetY + getMapOffsetY(map) + getBossOffset(),
                    BOSS_W, BOSS_W);
        }

        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
            map.bossHb.render(sb);
            if (Settings.isControllerMode && AbstractDungeon.dungeonMapScreen.map.bossHb.hovered) {
                map.renderReticle(sb, AbstractDungeon.dungeonMapScreen.map.bossHb);
            }
        }
    }

    private static Color getReticleColor(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("reticleColor");
            field.setAccessible(true);
            return (Color) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access reticleColor", e);
        }
    }

    private static Color getNotTakenColor(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("NOT_TAKEN_COLOR");
            field.setAccessible(true);
            return (Color) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access NOT_TAKEN_COLOR", e);
        }
    }

    private static Color getBaseMapColor(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("baseMapColor");
            field.setAccessible(true);
            return (Color) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access baseMapColor", e);
        }
    }

    private static Color getBossNodeColor(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("bossNodeColor");
            field.setAccessible(true);
            return (Color) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access bossNodeColor", e);
        }
    }

    private static float getMapMidDist(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("mapMidDist");
            field.setAccessible(true);
            return (Float) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access mapMidDist", e);
        }
    }

    private static void setMapMidDist(DungeonMap map, float value) {
        try {
            Field field = DungeonMap.class.getDeclaredField("mapMidDist");
            field.setAccessible(true);
            field.set(map, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mapMidDist", e);
        }
    }

    private static float getMapOffsetY(DungeonMap map) {
        try {
            Field field = DungeonMap.class.getDeclaredField("mapOffsetY");
            field.setAccessible(true);
            return (Float) field.get(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access mapOffsetY", e);
        }
    }

    private static void setMapOffsetY(DungeonMap map, float value) {
        try {
            Field field = DungeonMap.class.getDeclaredField("mapOffsetY");
            field.setAccessible(true);
            field.set(map, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mapOffsetY", e);
        }
    }
}