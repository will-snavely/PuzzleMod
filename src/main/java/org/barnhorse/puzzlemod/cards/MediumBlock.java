package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class MediumBlock extends PuzzlerCard {
    public MediumBlock() {
        super(
                ModId.create(MediumBlock.class.getSimpleName()),
                "MediumBlock",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "MediumBlock",
                CardType.SKILL,
                COLOR_GRAY,
                CardRarity.UNCOMMON,
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
