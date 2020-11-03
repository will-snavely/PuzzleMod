package org.barnhorse.puzzlemod.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.StaticAssets;

public class CursedCornerPiece extends CustomRelic {
    public static final String ID = ModId.create(CursedCornerPiece.class.getSimpleName());

    public CursedCornerPiece() {
        super(
                ID,
                ImageMaster.loadImage(StaticAssets.CURSED_CORNER),
                ImageMaster.loadImage(StaticAssets.CURSED_CORNER_OUTLINE),
                RelicTier.STARTER,
                LandingSound.SOLID
        );
    }

    @Override
    public void onVictory() {
        flash();
    }

    @Override
    public int changeNumberOfCardsInReward(int numberOfCards) {
        return 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}