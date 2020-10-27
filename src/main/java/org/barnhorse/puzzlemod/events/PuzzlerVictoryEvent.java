package org.barnhorse.puzzlemod.events;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import org.barnhorse.puzzlemod.ModId;
import org.barnhorse.puzzlemod.characters.ThePuzzler;

public class PuzzlerVictoryEvent extends AbstractEvent {
    public static final String ID = ModId.create(PuzzlerVictoryEvent.class.getSimpleName());
    private static final CharacterStrings strings = CardCrawlGame.languagePack.getCharacterString(ID);
    public static final String[] TEXT = strings.TEXT;
    private static final String[] OPTIONS = strings.OPTIONS;

    private AnimatedNpc npc;
    private static final float DIALOG_X;
    private static final float DIALOG_Y;

    static {
        DIALOG_X = 1100.0F * Settings.scale;
        DIALOG_Y = AbstractDungeon.floorY + 60.0F * Settings.scale;
    }

    enum Screen {
        InitialDialogue,
        Death
    }

    private Screen screen;

    public PuzzlerVictoryEvent() {
        this.body = "";


        if (this.npc == null) {
            this.npc =
                    new AnimatedNpc(
                            1534.0F * Settings.scale,
                            AbstractDungeon.floorY - 60.0F * Settings.scale,
                            "images/npcs/neow/skeleton.atlas",
                            "images/npcs/neow/skeleton.json",
                            "idle");
        }

        this.screen = Screen.InitialDialogue;
        this.playSfx();
        this.roomEventText.clear();
        this.roomEventText.addDialogOption(this.getInitialProceedOption());
        this.hasDialog = true;
        this.hasFocus = true;
        DeathScreen.resetScoreChecks();
        DeathScreen.calcScore(true);
        this.talk(this.getCongratsText());
    }

    private String getInitialProceedOption() {
        return OPTIONS[0];
    }

    public String getFinalProceedOption() {
        return OPTIONS[1];
    }

    private String getCongratsText() {
        return TEXT[0];
    }

    private String getPostTransformText() {
        return TEXT[1];
    }

    private String getFinalText() {
        return TEXT[2];
    }

    public void update() {
        super.update();

        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }
    }

    private void talk(String msg) {
        AbstractDungeon.effectList.add(new InfiniteSpeechBubble(DIALOG_X, DIALOG_Y, msg));
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case InitialDialogue:
                this.dismissBubble();
                ThePuzzler puzzler = (ThePuzzler) AbstractDungeon.player;
                AbstractDungeon.effectList.add(new SmokeBombEffect(puzzler.hb.cX, puzzler.hb.cY));
                puzzler.turnIntoChamp();
                this.talk(this.getPostTransformText());
                this.roomEventText.clear();
                this.roomEventText.addDialogOption(this.getFinalProceedOption());
                this.screen = Screen.Death;
                break;
            case Death:
                AbstractDungeon.player.isDying = true;
                this.hasFocus = false;
                this.roomEventText.hide();
                AbstractDungeon.player.isDead = true;
                AbstractDungeon.deathScreen = new DeathScreen(null);
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
