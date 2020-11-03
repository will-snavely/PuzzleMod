package org.barnhorse.puzzlemod;

import basemod.abstracts.CustomSavable;

public class PuzzleFileSave implements CustomSavable<String> {
    @Override
    public String onSave() {
        return PuzzleMod.getCurrentRunPuzzleFile();
    }

    @Override
    public void onLoad(String value) {
        PuzzleMod.setCurrentRunPuzzleFile(value);
    }
}
