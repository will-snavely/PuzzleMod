package org.barnhorse.puzzlemod.rooms;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.model.PuzzlePack;

public class BossRoomFactory {
    private BossRoomFactory() {
    }

    public static MonsterRoomBoss create() {
        if (AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER) {
            PuzzlePack pack = PuzzleMod.currentPuzzlePack;
            return new PuzzleBossRoom(pack.boss);
        } else {
            return new MonsterRoomBoss();
        }
    }
}
