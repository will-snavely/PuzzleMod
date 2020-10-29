package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class BigSwole extends PuzzlerCard {
    public BigSwole() {
        super(
                ModId.create(BigSwole.class.getSimpleName()),
                "BigSwole",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "BigSwole",
                CardType.POWER,
                COLOR_GRAY,
                CardRarity.RARE,
                CardTarget.SELF);
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    }
}