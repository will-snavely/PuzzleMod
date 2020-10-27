package org.barnhorse.puzzlemod.rooms;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import org.barnhorse.puzzlemod.packs.Puzzle;

public class PuzzleBossRoom extends MonsterRoomBoss {
    private Puzzle puzzle;
    private PuzzleRoomAdapter adapter;

    public PuzzleBossRoom(Puzzle puzzle) {
        this.phase = RoomPhase.COMBAT;
        this.puzzle = puzzle;
        this.adapter = new PuzzleRoomAdapter(this, puzzle);
        this.mapSymbol = "B";
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
        CardCrawlGame.metricData.path_taken.add("BOSS");
        CardCrawlGame.music.silenceBGM();
        this.adapter.onPlayerEntry(AbstractDungeon.player);
        AbstractRoom.waitTimer = COMBAT_WAIT_TIME;
    }
}
