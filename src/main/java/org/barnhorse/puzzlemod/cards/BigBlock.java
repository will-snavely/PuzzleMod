package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class BigBlock extends PuzzlerCard {
    public BigBlock() {
        super(
                ModId.create(BigBlock.class.getSimpleName()),
                "BigBlock",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "BigBlock",
                CardType.SKILL,
                COLOR_GRAY,
                CardRarity.RARE,
                CardTarget.SELF);
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
