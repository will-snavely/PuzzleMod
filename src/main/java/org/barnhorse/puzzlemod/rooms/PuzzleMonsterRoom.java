package org.barnhorse.puzzlemod.rooms;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import org.barnhorse.puzzlemod.PuzzleMod;
import org.barnhorse.puzzlemod.assets.StaticAssets;
import org.barnhorse.puzzlemod.packs.model.Puzzle;

public class PuzzleMonsterRoom extends MonsterRoom {
    private Puzzle puzzle;
    private PuzzleRoomAdapter adapter;

    public PuzzleMonsterRoom(Puzzle puzzle) {
        this.phase = RoomPhase.COMBAT;
        this.puzzle = puzzle;
        this.adapter = new PuzzleRoomAdapter(this, puzzle);

        this.mapSymbol = "P";
        int roll = MathUtils.random(4);
        this.mapImg = ImageMaster.loadImage(StaticAssets.PUZZLE_ICONS[roll]);
        this.mapImgOutline = ImageMaster.loadImage(StaticAssets.PUZZLE_OUTLINES[roll]);
    }

    public Puzzle getPuzzle() {
        return this.puzzle;
    }

    @Override
    public void addPotionToRewards() {
    }

    @Override
    public void addRelicToRewards(AbstractRelic.RelicTier tier) {
    }

    @Override
    public void addCardToRewards() {
    }

    @Override
    public void addCardReward(RewardItem rewardItem) {
    }

    @Override
    public void addGoldToRewards(int gold) {
    }

    @Override
    public void addStolenGoldToRewards(int gold) {
    }

    @Override
    public void addNoncampRelicToRewards(AbstractRelic.RelicTier tier) {
    }

    public void onPlayerEntry() {
        this.playBGM(null);
        PuzzleMod.getPuzzleApplicator().onPlayerEnterRoom(
                this.puzzle,
                this,
                AbstractDungeon.player
        );
        AbstractDungeon.lastCombatMetricKey = "Custom Monster";
        AbstractRoom.waitTimer = COMBAT_WAIT_TIME;
    }
}
