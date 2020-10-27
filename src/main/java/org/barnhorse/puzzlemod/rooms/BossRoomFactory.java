package org.barnhorse.puzzlemod.rooms;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.dungeons.PuzzlerExordium;
import org.barnhorse.puzzlemod.packs.PuzzlePack;

public class BossRoomFactory {
    private BossRoomFactory() {
    }

    public static MonsterRoomBoss create() {
        if (AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER) {
            PuzzlePack pack = PuzzlerExordium.currentPuzzlePack;
            return new PuzzleBossRoom(pack.boss);
        } else {
            return new MonsterRoomBoss();
        }
    }
}
