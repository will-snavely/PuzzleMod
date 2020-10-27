package org.barnhorse.puzzlemod.dungeons;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.TheBottomScene;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.Puzzle;
import org.barnhorse.puzzlemod.packs.PuzzlePack;
import org.barnhorse.puzzlemod.rooms.PuzzleBossRoom;
import org.barnhorse.puzzlemod.rooms.PuzzleMonsterRoom;
import org.barnhorse.puzzlemod.rooms.PuzzlerVictoryRoom;

import java.util.ArrayList;

public class PuzzlerExordium extends AbstractDungeon {
    public static final String ID = "Exordium";
    public static String puzzlerBossKey = "Puzzler Boss";
    public static int lastPuzzleRow;
    public static PuzzlePack currentPuzzlePack;

    private class SingletonMapRow {
        private ArrayList<MapRoomNode> row;
        private MapRoomNode node;

        public SingletonMapRow(AbstractRoom room, MapRoomNode prev, int y) {
            this.row = new ArrayList<>();
            row.add(new MapRoomNode(0, y));
            row.add(new MapRoomNode(1, y));
            row.add(new MapRoomNode(2, y));
            this.node = new MapRoomNode(3, y);
            this.node.room = room;
            if (prev != null) {
                connectNode(prev, this.node);
            }
            row.add(this.node);
            row.add(new MapRoomNode(4, y));
            row.add(new MapRoomNode(5, y));
            row.add(new MapRoomNode(6, y));
        }

        public ArrayList<MapRoomNode> getList() {
            return new ArrayList<>(this.row);
        }

        public MapRoomNode getNode() {
            return this.node;
        }
    }

    private ArrayList<ArrayList<MapRoomNode>> generateSpecialMap(PuzzlePack pack) {
        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<MapRoomNode>> map = new ArrayList<>();

        int y = 0;
        MapRoomNode prev = null;
        for (Puzzle puzzle : pack.puzzles) {
            SingletonMapRow row = new SingletonMapRow(new PuzzleMonsterRoom(puzzle), prev, y);
            map.add(row.getList());
            prev = row.getNode();
            y++;
        }
        lastPuzzleRow = y - 1;

        SingletonMapRow bossRow = new SingletonMapRow(new PuzzleBossRoom(pack.boss), prev, y);
        map.add(bossRow.getList());
        y++;

        SingletonMapRow victoryRoom = new SingletonMapRow(null, null, y);
        map.add(victoryRoom.getList());

        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, true));
        logger.info("Game Seed: " + Settings.seed);
        logger.info("Map generation time: " + (System.currentTimeMillis() - startTime) + "ms");
        return map;
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(
                new MapEdge(
                        src.x, src.y, src.offsetX, src.offsetY,
                        dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    public PuzzlerExordium(AbstractPlayer p, PuzzlePack pack) {
        super(pack.name, "Exordium", p, new ArrayList<>());
        currentPuzzlePack = pack;
        if (scene != null) {
            scene.dispose();
        }

        scene = new TheBottomScene();
        scene.randomizeScene();
        fadeColor = Color.valueOf("1e0f0aff");
        sourceFadeColor = Color.valueOf("1e0f0aff");
        mapRng = new Random(Settings.seed + (long) AbstractDungeon.actNum);
        map = this.generateSpecialMap(pack);
        firstRoomChosen = false;
        CardCrawlGame.music.changeBGM(id);
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = new NeowRoom(false);
        SaveHelper.saveIfAppropriate(SaveFile.SaveType.ENTER_ROOM);
        fadeIn();
    }

    public PuzzlerExordium(AbstractPlayer player, PuzzlePack pack, SaveFile saveFile) {
        super(pack.name, player, saveFile);
        CardCrawlGame.dungeon = this;
        if (scene != null) {
            scene.dispose();
        }

        scene = new TheBottomScene();
        fadeColor = Color.valueOf("1e0f0aff");
        sourceFadeColor = Color.valueOf("1e0f0aff");
        mapRng = new Random(Settings.seed + (long) saveFile.act_num);
        map = this.generateSpecialMap(pack);
        firstRoomChosen = true;
        this.populatePathTaken(saveFile, pack);
        if (this.isLoadingIntoNeow(saveFile)) {
            AbstractDungeon.firstRoomChosen = false;
        }

        ThePuzzler puzzler = (ThePuzzler) player;
        if (saveFile.chose_neow_reward || AbstractDungeon.floorNum >= 1) {
            puzzler.turnIntoFrog();
        } else {
            puzzler.turnIntoChamp();
        }
    }

    public void populatePathTaken(SaveFile saveFile, PuzzlePack pack) {
        MapRoomNode node = null;
        if (saveFile.current_room.equals(PuzzleBossRoom.class.getName())) {
            node = new MapRoomNode(-1, 15);
            node.room = new PuzzleBossRoom(pack.boss);
            nextRoom = node;
        } else if (saveFile.room_y == 15 && saveFile.room_x == -1) {
            node = new MapRoomNode(-1, 15);
            node.room = new PuzzlerVictoryRoom();
            nextRoom = node;
        } else if (saveFile.current_room.equals(NeowRoom.class.getName())) {
            nextRoom = null;
        } else {
            nextRoom = map.get(saveFile.room_y).get(saveFile.room_x);
        }

        for (int i = 0; i < pathX.size(); ++i) {
            int xPos = pathX.get(i);
            int yPos = pathY.get(i);
            MapRoomNode cur = map.get(yPos).get(xPos);

            if (yPos == PuzzlerExordium.lastPuzzleRow) {
                for(MapEdge e : cur.getEdges()) {
                    if(e != null) {
                        e.markAsTaken();
                    }
                }
            }

            if (yPos <= lastPuzzleRow) {
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

        if (this.isLoadingIntoNeow(saveFile)) {
            logger.info("Loading into Neow");
            currMapNode = new MapRoomNode(0, -1);
            currMapNode.room = new EmptyRoom();
            nextRoom = null;
        } else {
            logger.info("Loading into: " + saveFile.room_x + "," + saveFile.room_y);
            currMapNode = new MapRoomNode(0, -1);
            currMapNode.room = new EmptyRoom();
        }

        this.nextRoomTransition(saveFile);
        if (this.isLoadingIntoNeow(saveFile)) {
            if (saveFile.chose_neow_reward) {
                currMapNode.room = new NeowRoom(true);
            } else {
                currMapNode.room = new NeowRoom(false);
            }
        }

        if (currMapNode.room instanceof VictoryRoom) {
            CardCrawlGame.stopClock = true;
        }
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        cardUpgradedChance = 0.0F;
    }

    protected void generateMonsters() {
        this.generateWeakEnemies(3);
        this.generateStrongEnemies(12);
        this.generateElites(10);
    }

    protected void generateWeakEnemies(int count) {
    }

    protected void generateStrongEnemies(int count) {
    }

    protected void generateElites(int count) {
    }

    protected ArrayList<String> generateExclusions() {
        return new ArrayList<>();
    }

    protected void initializeBoss() {
        bossList.clear();
        bossList.add(puzzlerBossKey);
        bossList.add(puzzlerBossKey);
        bossList.add(puzzlerBossKey);
    }

    protected void initializeEventList() {
    }

    protected void initializeShrineList() {
    }

    protected void initializeEventImg() {
    }

    public static void postBossHandler(ProceedButton button) {
        MapRoomNode node = new MapRoomNode(-1, 15);
        node.room = new PuzzlerVictoryRoom();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
        button.hide();
    }
}
