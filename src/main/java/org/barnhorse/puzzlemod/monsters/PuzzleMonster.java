package org.barnhorse.puzzlemod.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.powers.FadingPower;
import com.megacrit.cardcrawl.relics.PreservedInsect;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.barnhorse.puzzlemod.packs.PuzzleMonsterInfo;

public class PuzzleMonster extends AbstractMonster {
    private int fade;
    private String entranceDialog;
    private String deathDialog;

    public PuzzleMonster(PuzzleMonsterInfo info) {
        super(
                info.name, "customMonster",
                info.maxHp,
                info.hb_x, info.hb_y, info.hb_w, info.hb_h,
                null,
                info.offsetX, info.offsetY);
        this.loadAnimation(info.atlasUrl, info.skeletonUrl, info.scale);
        AnimationState.TrackEntry e =
                this.state.setAnimation(0, info.animation, true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.damage.add(new DamageInfo(this, info.damage));
        this.fade = info.fade;
        this.entranceDialog = info.entranceDialog;
        this.deathDialog = info.deathDialog;
        this.dialogX = info.dialogX * Settings.scale;
        this.dialogY = info.dialogY * Settings.scale;
    }

    @Override
    public void usePreBattleAction() {
        if(this.fade > 0) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FadingPower(this, this.fade)));
        }

        if(this.entranceDialog != null && !this.entranceDialog.isEmpty()) {
            AbstractDungeon.effectList.add(new SpeechBubble(
                    this.hb.cX + this.dialogX,
                    this.hb.cY + this.dialogY,
                    2.5F,
                    this.entranceDialog,
                    false));
        }
    }

    @Override
    public void takeTurn() {
        AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
        AbstractDungeon.actionManager.addToBottom(
                new DamageAction(AbstractDungeon.player,
                        this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override
    protected void getMove(int i) {
        this.setMove(
                "Attack",
                (byte) 0,
                Intent.ATTACK,
                this.damage.get(0).base);
    }

    @Override
    public void die() {
        if(this.deathDialog != null && !this.deathDialog.isEmpty()) {
            AbstractDungeon.effectList.add(new SpeechBubble(
                    this.hb.cX + this.dialogX,
                    this.hb.cY + this.dialogY,
                    2.5F,
                    this.deathDialog,
                    false));
        }
        super.die();
    }

    @Override
    public void die(boolean triggerRelics) {
        if(this.deathDialog != null && !this.deathDialog.isEmpty()) {
            AbstractDungeon.effectList.add(new SpeechBubble(
                    this.hb.cX + this.dialogX,
                    this.hb.cY + this.dialogY,
                    2.5F,
                    this.deathDialog,
                    false));
        }
        super.die(triggerRelics);
    }
}
