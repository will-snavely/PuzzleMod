package org.barnhorse.puzzlemod.cards;

import basemod.abstracts.CustomCard;

public abstract class PuzzlerCard extends CustomCard {
    public PuzzlerCard( String id,
                       String name,
                       final String img,
                       final int cost,
                       String description,
                       final CardType type,
                       final CardColor color,
                       final CardRarity rarity,
                       final CardTarget target) {
        super(
                id,
                name,
                img,
                cost,
                description,
                type,
                color,
                rarity,
                target
        );

    }
}
