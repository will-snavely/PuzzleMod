package org.barnhorse.puzzlemod.dungeons;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.TheBottomScene;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.model.Puzzle;
import org.barnhorse.puzzlemod.packs.model.PuzzlePack;
import org.barnhorse.puzzlemod.rooms.PuzzleBossRoom;
import org.barnhorse.puzzlemod.rooms.PuzzleMonsterRoom;
import org.barnhorse.puzzlemod.rooms.PuzzlerVictoryRoom;

import java.util.ArrayList;

public class ExordiumHooks {
    public static String puzzlerBossKey = "Puzzler Boss";

    public static void init(Exordium exordium, AbstractPlayer player) {
        if (AbstractDungeon.scene != null) {
            AbstractDungeon.scene.dispose();
        }
        PuzzlePack pack = PuzzleMod.loadPackFromSettings();
        AbstractDungeon.scene = new TheBottomScene();
        AbstractDungeon.scene.randomizeScene();
        AbstractDungeon.fadeColor = Color.valueOf("1e0f0aff");
        AbstractDungeon.sourceFadeColor = Color.valueOf("1e0f0aff");
        AbstractDungeon.mapRng = new Random(Settings.seed + (long) AbstractDungeon.actNum);
        AbstractDungeon.map = generateSpecialMap(pack);
        AbstractDungeon.firstRoomChosen = false;
        CardCrawlGame.music.changeBGM(AbstractDungeon.id);
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = new NeowRoom(false);
        SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENTER_ROOM);
        AbstractDungeon.fadeIn();
    }

    public static void init(Exordium exordium, AbstractPlayer player, SaveFile saveFile) {
        AbstractDungeon.id = saveFile.level_name;
        CardCrawlGame.dungeon = exordium;
        if (AbstractDungeon.scene != null) {
            AbstractDungeon.scene.dispose();
        }
        PuzzlePack pack = PuzzleMod.loadPackFromSave();
        AbstractDungeon.scene = new TheBottomScene();
        AbstractDungeon.fadeColor = Color.valueOf("1e0f0aff");
        AbstractDungeon.sourceFadeColor = Color.valueOf("1e0f0aff");
        AbstractDungeon.miscRng = new Random(Settings.seed + (long) saveFile.floor_num);
        AbstractDungeon.mapRng = new Random(Settings.seed + (long) saveFile.act_num);
        CardCrawlGame.music.changeBGM(AbstractDungeon.id);
        AbstractDungeon.map = generateSpecialMap(pack);
        AbstractDungeon.firstRoomChosen = true;

        populatePathTaken(exordium, saveFile, pack);
        if (isLoadingIntoNeow(saveFile)) {
            AbstractDungeon.firstRoomChosen = false;
        }

        ThePuzzler puzzler = (ThePuzzler) player;
        if (saveFile.chose_neow_reward || AbstractDungeon.floorNum >= 1) {
            puzzler.turnIntoFrog();
        } else {
            puzzler.turnIntoChamp();
        }
    }

    private static ArrayList<ArrayList<MapRoomNode>> generateSpecialMap(PuzzlePack pack) {
        ArrayList<ArrayList<MapRoomNode>> map = new ArrayList<>();
        MapRoomNode prev = null;
        int y = 0;

        for (Puzzle puzzle : pack.puzzles) {
            SingletonMapRow row = new SingletonMapRow(new PuzzleMonsterRoom(puzzle), prev, y);
            map.add(row.getList());
            prev = row.getNode();
            y++;
        }
        PuzzleMod.lastPuzzleRow = y - 1;

        SingletonMapRow bossRow = new SingletonMapRow(new PuzzleBossRoom(pack.boss), prev, y);
        map.add(bossRow.getList());
        y++;

        SingletonMapRow victoryRoom = new SingletonMapRow(null, null, y);
        map.add(victoryRoom.getList());
        return map;
    }

    public static void populatePathTaken(Exordium dungeon, SaveFile saveFile, PuzzlePack pack) {
        MapRoomNode node = null;
        if (saveFile.current_room.equals(PuzzleBossRoom.class.getName())) {
            node = new MapRoomNode(-1, 15);
            node.room = new PuzzleBossRoom(pack.boss);
            AbstractDungeon.nextRoom = node;
        } else if (saveFile.room_y == 15 && saveFile.room_x == -1) {
            node = new MapRoomNode(-1, 15);
            node.room = new PuzzlerVictoryRoom();
            AbstractDungeon.nextRoom = node;
        } else if (saveFile.current_room.equals(NeowRoom.class.getName())) {
            AbstractDungeon.nextRoom = null;
        } else {
            AbstractDungeon.nextRoom =
                    AbstractDungeon.map.get(saveFile.room_y).get(saveFile.room_x);
        }

        for (int i = 0; i < AbstractDungeon.pathX.size(); ++i) {
            int xPos = AbstractDungeon.pathX.get(i);
            int yPos = AbstractDungeon.pathY.get(i);
            MapRoomNode cur = AbstractDungeon.map.get(yPos).get(xPos);

            if (yPos == PuzzleMod.lastPuzzleRow) {
                for (MapEdge e : cur.getEdges()) {
                    if (e != null) {
                        e.markAsTaken();
                    }
                }
            }

            if (yPos <= PuzzleMod.lastPuzzleRow) {
                cur.taken = true;
                if (node != null) {
                    MapEdge connectedEdge = node.getEdgeConnectedTo(cur);
                    if (connectedEdge != null) {
                        connectedEdge.markAsTaken();
                    }
                }
                node = cur;
            }
        }

        if (isLoadingIntoNeow(saveFile)) {
            AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
            AbstractDungeon.currMapNode.room = new EmptyRoom();
            AbstractDungeon.nextRoom = null;
        } else {
            AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
            AbstractDungeon.currMapNode.room = new EmptyRoom();
        }

        dungeon.nextRoomTransition(saveFile);
        if (isLoadingIntoNeow(saveFile)) {
            if (saveFile.chose_neow_reward) {
                AbstractDungeon.currMapNode.room = new NeowRoom(true);
            } else {
                AbstractDungeon.currMapNode.room = new NeowRoom(false);
            }
        }

        if (AbstractDungeon.currMapNode.room instanceof VictoryRoom) {
            CardCrawlGame.stopClock = true;
        }
    }

    private static boolean isLoadingIntoNeow(SaveFile saveFile) {
        return AbstractDungeon.floorNum == 0 || saveFile.current_room.equals(NeowRoom.class.getName());
    }

    public static void initializeBoss(Exordium exordium) {
        AbstractDungeon.bossList.clear();
        AbstractDungeon.bossList.add(puzzlerBossKey);
        AbstractDungeon.bossList.add(puzzlerBossKey);
        AbstractDungeon.bossList.add(puzzlerBossKey);
    }

    @SuppressWarnings("unused")
    public static void postBossHandler(ProceedButton button) {
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = new PuzzlerVictoryRoom();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
        button.hide();
    }
}

