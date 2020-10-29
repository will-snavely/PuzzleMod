package org.barnhorse.puzzlemod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.ResourceHelper;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;

public class MediumSlap extends PuzzlerCard {
    public MediumSlap() {
        super(
                ModId.create(MediumSlap.class.getSimpleName()),
                "MediumSlap",
                ResourceHelper.getCardImagePath("horse.png"),
                1,
                "MediumSlap",
                CardType.ATTACK,
                COLOR_GRAY,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY);
        this.damage = 1;
        this.damageTypeForTurn = DamageInfo.DamageType.NORMAL;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        AbstractDungeon.actionManager.addToBottom(
                new DamageAction(abstractMonster,
                        new DamageInfo(abstractPlayer, damage, damageTypeForTurn),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }
}