package org.barnhorse.puzzlemod.rooms;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.barnhorse.puzzlemod.monsters.PuzzleMonster;
import org.barnhorse.puzzlemod.packs.Puzzle;
import org.barnhorse.puzzlemod.packs.PuzzleMonsterInfo;
import org.barnhorse.puzzlemod.packs.PuzzlePotion;
import org.barnhorse.puzzlemod.packs.PuzzleRelic;
import org.barnhorse.puzzlemod.relics.BagOfPieces;
import theDefault.relics.CursedCornerPiece;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PuzzleRoomAdapter {
    private Puzzle puzzle;
    private AbstractRoom room;
    private static Set<String> reservedRelics;

    static {
        reservedRelics = new HashSet<>();
        reservedRelics.add(CursedCornerPiece.ID);
        reservedRelics.add(BagOfPieces.ID);
    }

    public PuzzleRoomAdapter(AbstractRoom room, Puzzle puzzle) {
        this.room = room;
        this.puzzle = puzzle;
    }

    public void onPlayerEntry(AbstractPlayer player) {
        player.maxHealth = this.puzzle.maxHp;
        player.currentHealth = this.puzzle.curHp;
        player.masterDeck.clear();
        player.masterHandSize = this.puzzle.masterHandSize;

        if (player.maxOrbs > 0) {
            player.masterMaxOrbs = 0;
        }
        if (puzzle.orbCount > 0) {
            player.masterMaxOrbs = puzzle.orbCount;
        }

        List<AbstractRelic> relics = new ArrayList<>(player.relics);
        for (AbstractRelic relic : relics) {
            if (!reservedRelics.contains(relic.relicId)) {
                player.loseRelic(relic.relicId);
            }
        }

        List<AbstractPotion> potions = new ArrayList<>(player.potions);
        for (AbstractPotion potion : potions) {
            if (potion != null) {
                player.removePotion(potion);
            }
        }

        if(this.puzzle.relics != null) {
            for (PuzzleRelic puzzleRelic : this.puzzle.relics) {
                AbstractRelic relic = RelicLibrary.getRelic(puzzleRelic.key).makeCopy();
                AbstractDungeon.getCurrRoom().
                        spawnRelicAndObtain(
                                Settings.WIDTH / 2.0f,
                                Settings.HEIGHT / 2.0f,
                                relic);
                relic.onEquip();
            }
        }
        player.reorganizeRelics();

        if(this.puzzle.potions != null) {
            for (PuzzlePotion puzzlePotion : this.puzzle.potions) {
                AbstractPotion potion = PotionHelper.getPotion(puzzlePotion.key);
                player.obtainPotion(potion);
            }
        }

        List<AbstractMonster> monsters = new ArrayList<>();
        for (PuzzleMonsterInfo monsterInfo : this.puzzle.monsters) {
            monsters.add(new PuzzleMonster(monsterInfo));
        }
        AbstractMonster[] monsterArray = new AbstractMonster[monsters.size()];
        monsters.toArray(monsterArray);
        this.room.monsters = new MonsterGroup(monsterArray);
        this.room.monsters.init();
    }
}
