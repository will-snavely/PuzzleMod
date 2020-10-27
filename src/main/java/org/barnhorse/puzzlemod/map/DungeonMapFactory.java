package org.barnhorse.puzzlemod.map;

import com.megacrit.cardcrawl.map.DungeonMap;

public class DungeonMapFactory {
    private DungeonMapFactory() {
    }

    public static DungeonMap create() {
        return new PuzzlerDungeonMap();
    }
}
