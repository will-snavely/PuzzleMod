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
import com.megacrit.cardcrawl.powers.FadingPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import org.barnhorse.puzzlemod.packs.model.PuzzleMonsterInfo;

public class PuzzleMonster extends AbstractMonster {
    private int fade;
    private String entranceDialog;
    private String deathDialog;

    public PuzzleMonster(
            String name, int maxHp,
            float hb_x, float hb_y,
            float hb_w, float hb_h,
            float offsetX, float offsetY,
            float dialogX, float dialogY,
            int fade, int damage,
            String entranceDialog, String deathDialog,
            String atlasUrl, String skeletonUrl, String animation, float scale) {
        super(
                name, "customMonster",
                maxHp,
                hb_x, hb_y, hb_w, hb_h,
                null,
                offsetX, offsetY);
        this.loadAnimation(atlasUrl, skeletonUrl, scale);
        AnimationState.TrackEntry e =
                this.state.setAnimation(0, animation, true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.damage.add(new DamageInfo(this, damage));
        this.fade = fade;
        this.entranceDialog = entranceDialog;
        this.deathDialog = deathDialog;
        this.dialogX = dialogX * Settings.scale;
        this.dialogY = dialogY * Settings.scale;
    }

    public PuzzleMonster(PuzzleMonsterInfo info) {
        this(
                info.name, info.maxHp,
                info.hb_x != null ? info.hb_x : 0,
                info.hb_y != null ? info.hb_y : 0,
                info.hb_w != null ? info.hb_w : 0,
                info.hb_h != null ? info.hb_h : 0,
                info.offsetX != null ? info.offsetX : 0,
                info.offsetY != null ? info.offsetY : 0,
                info.dialogX != null ? info.dialogX : 0,
                info.dialogY != null ? info.dialogY : 0,
                info.fade != null ? info.fade : 0,
                info.damage != null ? info.damage : 0,
                info.entranceDialog, info.deathDialog,
                info.atlasUrl, info.skeletonUrl, info.animation,
                info.scale != null ? info.scale : 0);
    }

    @Override
    public void usePreBattleAction() {
        if (this.fade > 0) {
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new FadingPower(this, this.fade)));
        }

        if (this.entranceDialog != null && !this.entranceDialog.isEmpty()) {
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
        if (this.deathDialog != null && !this.deathDialog.isEmpty()) {
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
        if (this.deathDialog != null && !this.deathDialog.isEmpty()) {
            AbstractDungeon.effectList.add(new SpeechBubble(
                    this.hb.cX + this.dialogX,
                    this.hb.cY + this.dialogY,
                    2.5F,
                    this.deathDialog,
                    false));
        }
        super.die(triggerRelics);
    }

    public void modify(PuzzleMonsterInfo info) {
        if (info.name != null && !info.name.isEmpty()) {
            this.name = info.name;
        }
        if (info.damage != null) {
            this.damage.clear();
            this.damage.add(new DamageInfo(this, info.damage));
        }
        if (info.fade != null) {
            this.fade = info.fade;
        }
        if (info.maxHp != null) {
            this.currentHealth = info.maxHp;
            this.maxHealth = info.maxHp;
        }
        if (info.hb_x != null) {
            this.hb_x = info.hb_x;
        }
        if (info.hb_y != null) {
            this.hb_y = info.hb_y;
        }
        if (info.hb_w != null) {
            this.hb_w = info.hb_w;
        }
        if (info.hb_h != null) {
            this.hb_h = info.hb_h;
        }
        if (info.dialogX != null) {
            this.dialogX = info.dialogX * Settings.scale;
        }
        if (info.dialogY != null) {
            this.dialogY = info.dialogY * Settings.scale;
        }
        if (info.offsetX != null) {
            this.drawX = (float) Settings.WIDTH * 0.75F + info.offsetX * Settings.scale;
        }
        if (info.offsetY != null) {
            this.drawY = AbstractDungeon.floorY + info.offsetY * Settings.scale;
        }
        if (info.entranceDialog != null && !info.entranceDialog.isEmpty()) {
            this.entranceDialog = info.entranceDialog;
        }
        if (info.deathDialog != null && !info.deathDialog.isEmpty()) {
            this.deathDialog = info.deathDialog;
        }
        if (info.atlasUrl != null && !info.atlasUrl.isEmpty()
                && info.skeletonUrl != null && !info.skeletonUrl.isEmpty()) {
            float scale = info.scale != null ? info.scale : 1.0f;
            this.loadAnimation(info.atlasUrl, info.skeletonUrl, scale);
        }
        if (info.animation != null) {
            AnimationState.TrackEntry e =
                    this.state.setAnimation(0, info.animation, true);
            e.setTime(e.getEndTime() * MathUtils.random());
        }
    }
}