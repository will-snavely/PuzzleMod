package org.barnhorse.puzzlemod.packs;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.barnhorse.puzzlemod.packs.model.Puzzle;

public interface PuzzleApplicator {
    void onPlayerEnterRoom(Puzzle puzzle, AbstractRoom room, AbstractPlayer player);
    void preBattlePrep(Puzzle puzzle, AbstractRoom room, AbstractPlayer player);
}
