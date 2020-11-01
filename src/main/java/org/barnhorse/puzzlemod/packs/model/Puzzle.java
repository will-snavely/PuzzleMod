package org.barnhorse.puzzlemod.packs.model;

import java.util.List;

public class Puzzle {
    public int maxHp;
    public int curHp;
    public int masterHandSize;
    public int maxEnergy;

    public List<PuzzleCard> startingHand;
    public List<PuzzleCard> startingDiscardPile;
    public List<PuzzleCard> startingDrawPile;
    public List<PuzzleCard> startingExhaustPile;

    public List<PuzzleRelic> relics;
    public List<PuzzlePotion> potions;
    public List<PuzzleMonsterInfo> monsters;

    public int orbSlots;
    public List<PuzzleOrb> orbs;
}
