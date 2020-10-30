package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class PuzzlerDefend extends PuzzlerCard {
    public PuzzlerDefend() {
        super(
                ModId.create(PuzzlerDefend.class.getSimpleName()),
                "PuzzlerDefend",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "PuzzlerDefend",
                CardType.SKILL,
                COLOR_GRAY,
                CardRarity.BASIC,
                CardTarget.SELF);
        this.tags.add(CardTags.STARTER_DEFEND);
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(
                new GainBlockAction(abstractPlayer, abstractPlayer, 1));
    }
}