package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import org.barnhorse.puzzlemod.dungeons.PuzzlerExordium;

public class AbstractDungeonPatches {
    @SpirePatch(clz = AbstractDungeon.class, method = "setBoss")
    public static class AbstractDungeon_setBoss {
        @SpirePostfixPatch
        public static void Insert(Object thisRef, String key) {
            if (key.equals(PuzzlerExordium.puzzlerBossKey)) {
                DungeonMap.boss = ImageMaster.loadImage(
                        "puzzleModResources/images/ui/puzzle_boss.png");
                DungeonMap.bossOutline = ImageMaster.loadImage(
                        "puzzleModResources/images/ui/puzzle_boss_outline.png");
            }
        }
    }
}

