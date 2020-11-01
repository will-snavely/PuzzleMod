package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class SmallSwole extends PuzzlerCard {
    public SmallSwole() {
        super(
                ModId.create(SmallSwole.class.getSimpleName()),
                "SmallSwole",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "SmallSwole",
                CardType.POWER,
                COLOR_GRAY,
                CardRarity.COMMON,
                CardTarget.SELF);
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    }
}