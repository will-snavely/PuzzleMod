package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class MediumSwole extends PuzzlerCard {
    public MediumSwole() {
        super(
                ModId.create(MediumSwole.class.getSimpleName()),
                "MediumSwole",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "MediumSwole",
                CardType.POWER,
                COLOR_GRAY,
                CardRarity.UNCOMMON,
                CardTarget.SELF);
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    }
}