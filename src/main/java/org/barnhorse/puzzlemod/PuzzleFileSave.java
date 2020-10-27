package org.barnhorse.puzzlemod;

import basemod.abstracts.CustomSavable;
import org.barnhorse.puzzlemod.PuzzleMod;

public class PuzzleFileSave implements CustomSavable<String> {
    @Override
    public String onSave() {
        return PuzzleMod.getCurrentPuzzleFile();
    }

    @Override
    public void onLoad(String value) {
        PuzzleMod.setCurrentPuzzleFile(value);
    }
}
