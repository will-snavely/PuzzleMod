package org.barnhorse.puzzlemod.characters;

import basemod.abstracts.CustomPlayer;
import basemod.animations.SpineAnimation;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.assets.StaticAssets;
import org.barnhorse.puzzlemod.packs.Puzzle;
import org.barnhorse.puzzlemod.packs.PuzzleCard;
import org.barnhorse.puzzlemod.rooms.PuzzleBossRoom;
import org.barnhorse.puzzlemod.rooms.PuzzleMonsterRoom;

import java.util.ArrayList;
import java.util.List;

public class ThePuzzler extends CustomPlayer {
    public static final Logger logger = LogManager.getLogger(ThePuzzler.class.getName());

    public static class Enums {
        @SpireEnum
        public static PlayerClass THE_PUZZLER;
        @SpireEnum(name = "DEFAULT_GRAY_COLOR")
        public static AbstractCard.CardColor COLOR_GRAY;
        @SpireEnum(name = "DEFAULT_GRAY_COLOR")

        @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 500;
    public static final int MAX_HP = 500;
    public static final int STARTING_GOLD = 0;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 0;

    private static final String ID = ModId.create("ThePuzzler");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;

    private static SpineAnimation armor;
    private static SpriterAnimation frog;

    static {
        armor = new SpineAnimation(
                "images/monsters/theCity/champ/skeleton.atlas",
                "images/monsters/theCity/champ/skeleton.json",
                1.0F);
        frog = new SpriterAnimation("puzzleModResources/images/char/frog/Spriter/frog.scml");
    }

    public ThePuzzler(String name, PlayerClass setClass) {
        super(
                name,
                setClass,
                null,
                null,
                null,
                armor);
        initializeClass(null,
                StaticAssets.FROG_SHOULDER_1, // campfire pose
                StaticAssets.FROG_SHOULDER_2, // another campfire pose
                StaticAssets.COFFIN, // dead corpse
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F,
                new EnergyManager(ENERGY_PER_TURN)); // energy manager
        dialogX = (drawX + 0.0F * Settings.scale); // set location for text bubbles
        dialogY = (drawY + 220.0F * Settings.scale); // you can just copy these values
        this.turnIntoChamp();
    }

    public void turnIntoChamp() {
        this.flipHorizontal = true;
        this.animation = armor;
        AnimationState.TrackEntry e = this.state.setAnimation(
                0, "Idle", true);
    }

    public void turnIntoFrog() {
        this.animation = frog;
        this.flipHorizontal = false;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(
                NAMES[0],
                TEXT[0],
                STARTING_HP,
                MAX_HP,
                ORB_SLOTS,
                STARTING_GOLD,
                CARD_DRAW,
                this,
                getStartingRelics(),
                getStartingDeck(),
                false);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> result = new ArrayList<>();
        result.add("Strike_R");
        result.add("Strike_R");
        result.add("Strike_R");
        result.add("Strike_R");
        result.add("Strike_R");
        result.add("Defend_R");
        result.add("Defend_R");
        result.add("Defend_R");
        result.add("Defend_R");
        result.add("Defend_R");
        result.add("Defend_R");
        return result;
    }

    public ArrayList<String> getStartingRelics() {
        return new ArrayList<>();
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_DAGGER_1", 1.25f); // Sound Effect
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT,
                false); // Screen Effect
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_DAGGER_1";
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 0;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return Enums.COLOR_GRAY;
    }

    @Override
    public Color getCardTrailColor() {
        return PuzzleMod.DEFAULT_GRAY;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Strike_Red();
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAMES[1];
    }

    // Should return a new instance of your character, sending name as its name parameter.
    @Override
    public AbstractPlayer newInstance() {
        return new ThePuzzler(name, chosenClass);
    }

    // Should return a Color object to be used to color the miniature card images in run history.
    @Override
    public Color getCardRenderColor() {
        return PuzzleMod.DEFAULT_GRAY;
    }

    // Should return a Color object to be used as screen tint effect when your
    // character attacks the heart.
    @Override
    public Color getSlashAttackColor() {
        return PuzzleMod.DEFAULT_GRAY;
    }

    // Should return an AttackEffect array of any size greater than 0. These effects
    // will be played in sequence as your character's finishing combo on the heart.
    // Attack effects are the same as used in DamageAction and the like.
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.BLUNT_HEAVY,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        };
    }

    // Should return a string containing what text is shown when your character is
    // about to attack the heart. For example, the defect is "NL You charge your
    // core to its maximum..."
    @Override
    public String getSpireHeartText() {
        return TEXT[1];
    }

    // The vampire events refer to the base game characters as "brother", "sister",
    // and "broken one" respectively.This method should return a String containing
    // the full text that will be displayed as the first screen of the vampires event.
    @Override
    public String getVampireText() {
        return TEXT[2];
    }

    private List<AbstractCard> createPuzzleCards(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> result = new ArrayList<>();

        for (PuzzleCard puzzleCard : puzzleCards) {
            AbstractCard card = CardLibrary.getCard(puzzleCard.key).makeCopy();
            if (puzzleCard.upgradeCount > 0) {
                for (int ii = 0; ii < puzzleCard.upgradeCount; ii++) {
                    card.upgrade();
                }
            }
            result.add(card);
        }
        return result;
    }

    private void initHand(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> startingHand = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : startingHand) {
            AbstractDungeon.actionManager.
                    addToBottom(new MakeTempCardInHandAction(card, true));
        }
    }

    private void initDrawPile(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> draw = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : draw) {
            this.drawPile.addToBottom(card);
        }
    }

    private void initDiscardPile(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> discard = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : discard) {
            this.discardPile.addToBottom(card);
        }
    }

    private void initExhaustPile(List<PuzzleCard> puzzleCards) {
        List<AbstractCard> exhaust = this.createPuzzleCards(puzzleCards);
        for (AbstractCard card : exhaust) {
            this.exhaustPile.addToBottom(card);
        }
    }

    @Override
    public void preBattlePrep() {
        AbstractRoom currentRoom = AbstractDungeon.getCurrRoom();
        Puzzle puzzle = null;
        if (currentRoom instanceof PuzzleMonsterRoom) {
            PuzzleMonsterRoom room = (PuzzleMonsterRoom) currentRoom;
            puzzle = room.getPuzzle();
        } else if (currentRoom instanceof PuzzleBossRoom) {
            PuzzleBossRoom room = (PuzzleBossRoom) currentRoom;
            puzzle = room.getPuzzle();
        }

        if (puzzle == null) {
            super.preBattlePrep();
        } else {
            super.preBattlePrep();
            if (puzzle.startingHand != null) {
                this.initHand(puzzle.startingHand);
            }

            if (puzzle.startingDrawPile != null) {
                this.initDrawPile(puzzle.startingDrawPile);
            }

            if (puzzle.startingDiscardPile != null) {
                this.initDiscardPile(puzzle.startingDiscardPile);
            }

            if (puzzle.startingExhaustPile != null) {
                this.initExhaustPile(puzzle.startingExhaustPile);
            }
        }
    }

    public static boolean isPuzzlerChosen() {
        return AbstractDungeon.player.chosenClass == Enums.THE_PUZZLER;
    }
}
