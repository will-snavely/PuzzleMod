package org.barnhorse.puzzlemod.events;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile.SaveType;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import com.megacrit.cardcrawl.vfx.scene.LevelTransitionTextOverlayEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.relics.BagOfPieces;
import theDefault.relics.CursedCornerPiece;

public class PuzzlerNeowEvent extends AbstractEvent {
    private static final Logger logger = LogManager.getLogger(PuzzlerNeowEvent.class.getName());

    public static final String ID = ModId.create(PuzzlerNeowEvent.class.getSimpleName());
    private static final CharacterStrings strings = CardCrawlGame.languagePack.getCharacterString(ID);
    public static final String[] TEXT = strings.TEXT;
    private static final String[] OPTIONS = strings.OPTIONS;

    private AnimatedNpc npc;
    private boolean setPhaseToEvent;
    public static boolean waitingToSave;
    private static final float DIALOG_X;
    private static final float DIALOG_Y;

    static {
        waitingToSave = false;
        DIALOG_X = 1100.0F * Settings.scale;
        DIALOG_Y = AbstractDungeon.floorY + 60.0F * Settings.scale;
    }

    enum Screen {
        InitialDialogue,
        MakeYourChoice,
        Continue,
        Done
    }

    private Screen screen;

    public PuzzlerNeowEvent(boolean isDone) {
        this.body = "";
        this.setPhaseToEvent = false;
        waitingToSave = false;
        if (this.npc == null) {
            this.npc =
                    new AnimatedNpc(
                            1534.0F * Settings.scale,
                            AbstractDungeon.floorY - 60.0F * Settings.scale,
                            "images/npcs/neow/skeleton.atlas",
                            "images/npcs/neow/skeleton.json",
                            "idle");
        }

        this.roomEventText.clear();
        this.playSfx();

        if (!isDone) {
            this.screen = Screen.MakeYourChoice;
            this.talk(this.getOfferChoiceText());
            this.roomEventText.clear();
            this.roomEventText.addDialogOption(this.getAcceptOption());
            this.roomEventText.addDialogOption(this.getRejectOption());
        } else {
            this.screen = Screen.Continue;
            this.talk(this.getContinueText2());
            this.roomEventText.addDialogOption(this.getFinalProceedOption());
        }

        this.hasDialog = true;
        this.hasFocus = true;
    }

    public String getWelcomeText() {
        return TEXT[0];
    }

    public String getOfferChoiceText() {
        return TEXT[1];
    }

    public String getContinueText1() {
        return TEXT[2];
    }

    public String getBadChoiceText() {
        return TEXT[3];
    }

    public String getContinueText2() {
        return TEXT[4];
    }

    public String getAcceptOption() {
        return OPTIONS[1];
    }

    public String getRejectOption() {
        return OPTIONS[2];
    }

    public String getFinalProceedOption() {
        return OPTIONS[3];
    }

    public void update() {
        super.update();
        if (!this.setPhaseToEvent) {
            AbstractDungeon.getCurrRoom().phase = RoomPhase.EVENT;
            this.setPhaseToEvent = true;
        }

        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }

        if (waitingToSave &&
                !AbstractDungeon.isScreenUp &&
                AbstractDungeon.topLevelEffects.isEmpty() &&
                AbstractDungeon.player.relicsDoneAnimating()) {
            boolean doneAnims = true;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (!relic.isDone) {
                    doneAnims = false;
                    break;
                }
            }

            if (doneAnims) {
                waitingToSave = false;
                SaveHelper.saveIfAppropriate(SaveType.POST_NEOW);
            }
        }
    }

    private void talk(String msg) {
        AbstractDungeon.effectList.add(new InfiniteSpeechBubble(DIALOG_X, DIALOG_Y, msg));
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case MakeYourChoice:
                this.dismissBubble();
                switch (buttonPressed) {
                    case 0:
                        ThePuzzler puzzler = (ThePuzzler) AbstractDungeon.player;
                        AbstractDungeon.getCurrRoom().
                                spawnRelicAndObtain(
                                        Settings.WIDTH / 2.0f,
                                        Settings.HEIGHT / 2.0f,
                                        new CursedCornerPiece());
                        AbstractDungeon.getCurrRoom().
                                spawnRelicAndObtain(
                                        Settings.WIDTH / 2.0f,
                                        Settings.HEIGHT / 2.0f,
                                        new BagOfPieces());
                        AbstractDungeon.effectList.add(
                                new SmokeBombEffect(puzzler.hb.cX, puzzler.hb.cY));
                        puzzler.turnIntoFrog();
                        puzzler.maxHealth = 20;
                        puzzler.currentHealth = 20;
                        this.talk(this.getContinueText1());
                        this.roomEventText.clear();
                        this.roomEventText.addDialogOption(this.getFinalProceedOption());
                        waitingToSave = true;
                        this.screen = Screen.Continue;
                        break;
                    case 1:
                        this.talk(this.getBadChoiceText());
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, 999));
                        AbstractDungeon.effectList.add(
                                new FlashAtkImgEffect(AbstractDungeon.player.hb.cX,
                                        AbstractDungeon.player.hb.cY,
                                        AbstractGameAction.AttackEffect.FIRE));

                        break;
                }
                break;
            case Continue:
                this.screen = Screen.Done;
                this.openMap();
                break;
            default:
                this.openMap();
        }

    }

    private void dismissBubble() {
        for (AbstractGameEffect effect : AbstractDungeon.effectList) {
            if (effect instanceof InfiniteSpeechBubble) {
                ((InfiniteSpeechBubble) effect).dismiss();
            }
        }
    }

    private void playSfx() {
        int roll = MathUtils.random(3);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEOW_1A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_NEOW_1B");
        } else if (roll == 2) {
            CardCrawlGame.sound.play("VO_NEOW_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEOW_2B");
        }
    }

    public void render(SpriteBatch sb) {
        this.npc.render(sb);
    }

    public void dispose() {
        super.dispose();
        if (this.npc != null) {
            this.npc.dispose();
            this.npc = null;
        }
    }
}
