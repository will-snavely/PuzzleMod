package org.barnhorse.puzzlemod.map;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.dungeons.PuzzlerExordium;

public class BossChecker {
    public static boolean beforeBossNode() {
        boolean isPuzzler = AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER;
        int curY = AbstractDungeon.getCurrMapNode().y;
        return Settings.isDebug || (isPuzzler && PuzzlerExordium.lastPuzzleRow == curY);
    }
}
