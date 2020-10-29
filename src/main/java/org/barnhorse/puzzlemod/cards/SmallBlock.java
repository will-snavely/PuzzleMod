package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class SmallBlock extends PuzzlerCard {
    public SmallBlock() {
        super(
                ModId.create(SmallBlock.class.getSimpleName()),
                "SmallBlock",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "SmallBlock",
                CardType.SKILL,
                COLOR_GRAY,
                CardRarity.COMMON,
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
