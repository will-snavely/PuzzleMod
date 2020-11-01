package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.dungeons.ExordiumHooks;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ExordiumPatches {
    @SpirePatch(
            clz = Exordium.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractPlayer.class, ArrayList.class}
    )
    public static class PatchCtor1 {
        public static SpireReturn Prefix(
                Exordium exordium,
                AbstractPlayer player,
                ArrayList events) {
            if (player != null && ThePuzzler.isPuzzlerChosen()) {
                ExordiumHooks.init(exordium, player);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Exordium.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractPlayer.class, SaveFile.class}
    )
    public static class PatchCtor2 {
        public static SpireReturn Prefix(
                Exordium exordium,
                AbstractPlayer player,
                SaveFile saveFile) {
            if (player != null && ThePuzzler.isPuzzlerChosen()) {
                ExordiumHooks.init(exordium, player, saveFile);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = Exordium.class,
            method = "initializeBoss"
    )
    public static class PatchInitializeBoss {
        public static SpireReturn Prefix(Exordium exordium) {
            if (AbstractDungeon.player != null && ThePuzzler.isPuzzlerChosen()) {
                ExordiumHooks.initializeBoss(exordium);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}