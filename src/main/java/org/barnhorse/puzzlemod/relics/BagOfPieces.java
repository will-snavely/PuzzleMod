package org.barnhorse.puzzlemod.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.assets.StaticAssets;


public class BagOfPieces extends CustomRelic {
    public static final String ID = ModId.create(BagOfPieces.class.getSimpleName());

    public BagOfPieces() {
        super(
                ID,
                ImageMaster.loadImage(StaticAssets.BAG_PIECES),
                ImageMaster.loadImage(StaticAssets.BAG_PIECES_OUTLINE),
                RelicTier.STARTER,
                LandingSound.SOLID
        );
        this.counter = 0;
    }

    @Override
    public void onVictory() {
        this.counter += 1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

}