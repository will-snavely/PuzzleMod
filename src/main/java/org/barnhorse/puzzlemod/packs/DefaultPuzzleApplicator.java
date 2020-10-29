package org.barnhorse.puzzlemod.packs;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.barnhorse.puzzlemod.monsters.PuzzleMonster;
import org.barnhorse.puzzlemod.packs.model.*;
import org.barnhorse.puzzlemod.relics.BagOfPieces;
import theDefault.relics.CursedCornerPiece;

import java.util.ArrayList;
import java.util.List;

public class DefaultPuzzleApplicator implements PuzzleApplicator {
    @Override
    public void onPlayerEnterRoom(Puzzle puzzle, AbstractRoom room, AbstractPlayer player) {
        player.maxHealth = puzzle.maxHp;

        if(puzzle.currentHp > 0) {
            player.currentHealth = puzzle.currentHp;
        }
        player.masterDeck.clear();
        player.masterHandSize = puzzle.masterHandSize;

        if (player.maxOrbs > 0) {
            player.masterMaxOrbs = 0;
        }
        if (puzzle.orbSlots > 0) {
            player.masterMaxOrbs = puzzle.orbSlots;
        }

        List<AbstractRelic> relics = new ArrayList<>(player.relics);
        for (AbstractRelic relic : relics) {
            if(relic.relicId.equals(CursedCornerPiece.ID)) {
                continue;
            }
            if(relic.relicId.equals(BagOfPieces.ID)) {
                continue;
            }
            player.loseRelic(relic.relicId);
        }

        List<AbstractPotion> potions = new ArrayList<>(player.potions);
        for (AbstractPotion potion : potions) {
            if (potion != null) {
                player.removePotion(potion);
            }
        }

        if(puzzle.relics != null) {
            for (PuzzleRelic puzzleRelic : puzzle.relics) {
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

        if(puzzle.potions != null) {
            for (PuzzlePotion puzzlePotion : puzzle.potions) {
                AbstractPotion potion = PotionHelper.getPotion(puzzlePotion.key);
                player.obtainPotion(potion);
            }
        }

        List<AbstractMonster> monsters = new ArrayList<>();
        for (PuzzleMonsterInfo monsterInfo : puzzle.monsters) {
            monsters.add(new PuzzleMonster(monsterInfo));
        }
        AbstractMonster[] monsterArray = new AbstractMonster[monsters.size()];
        monsters.toArray(monsterArray);
        room.monsters = new MonsterGroup(monsterArray);
        room.monsters.init();
    }

    @Override
    public void preBattlePrep(Puzzle puzzle, AbstractRoom room, AbstractPlayer player) {
        if (puzzle.startingHand != null) {
            this.initHand(player, puzzle.startingHand);
        }

        if (puzzle.startingDrawPile != null) {
            this.initDrawPile(player, puzzle.startingDrawPile);
        }

        if (puzzle.startingDiscardPile != null) {
            this.initDiscardPile(player, puzzle.startingDiscardPile);
        }

        if (puzzle.startingExhaustPile != null) {
            this.initExhaustPile(player, puzzle.startingExhaustPile);
        }
    }

    private void initHand(AbstractPlayer player, List<PuzzleCard> puzzleCards) {
        List<AbstractCard> startingHand = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : startingHand) {
            AbstractDungeon.actionManager.
                    addToBottom(new MakeTempCardInHandAction(card, true));
        }
    }

    private void initDrawPile(AbstractPlayer player, List<PuzzleCard> puzzleCards) {
        List<AbstractCard> draw = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : draw) {
            player.drawPile.addToBottom(card);
        }
    }

    private void initDiscardPile(AbstractPlayer player, List<PuzzleCard> puzzleCards) {
        List<AbstractCard> discard = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : discard) {
            player.discardPile.addToBottom(card);
        }
    }

    private void initExhaustPile(AbstractPlayer player, List<PuzzleCard> puzzleCards) {
        List<AbstractCard> exhaust = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : exhaust) {
            player.exhaustPile.addToBottom(card);
        }
    }

    private List<AbstractCard> createPuzzleCards(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> result = new ArrayList<>();
        for (PuzzleCard puzzleCard : puzzleCards) {
            AbstractCard card = CardLibrary.getCard(puzzleCard.key).makeCopy();
            if (puzzleCard.upgradeCount > 0) {
                for (int ii = 0; ii < puzzleCard.upgradeCount; ii++) {
                    card.upgrade();
                }
            }
            result.add(card);
        }
        return result;
    }
}
