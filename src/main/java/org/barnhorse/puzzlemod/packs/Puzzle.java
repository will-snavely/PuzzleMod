package org.barnhorse.puzzlemod.packs;

import java.util.List;

public class Puzzle {
    public List<PuzzleCard> startingHand;
    public List<PuzzleCard> startingDiscardPile;
    public List<PuzzleCard> startingDrawPile;
    public List<PuzzleCard> startingExhaustPile;
    public List<PuzzleRelic> relics;
    public List<PuzzlePotion> potions;
    public List<PuzzleMonsterInfo> monsters;
    public int orbCount;
    public int maxHp;
    public int curHp;
    public int masterHandSize;
    public int maxEnergy;
}
