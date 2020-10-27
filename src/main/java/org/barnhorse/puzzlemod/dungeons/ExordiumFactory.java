package org.barnhorse.puzzlemod.dungeons;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.PuzzlePack;

import java.util.ArrayList;

public class ExordiumFactory {
    public static AbstractDungeon create(AbstractPlayer player, ArrayList<String> specialEvents) {
        if (AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER) {
            PuzzlePack pack = PuzzleMod.loadCurrentPack();
            return new PuzzlerExordium(player, pack);
        } else {
            return new Exordium(player, specialEvents);
        }
    }

    public static AbstractDungeon create(AbstractPlayer player, SaveFile saveFile) {
        if (AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER) {
            PuzzlePack pack = PuzzleMod.loadCurrentPack();
            return new PuzzlerExordium(player, pack, saveFile);
        } else {
            return new Exordium(player, saveFile);
        }
    }
}
