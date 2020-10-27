package org.barnhorse.puzzlemod.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import org.barnhorse.puzzlemod.events.PuzzlerVictoryEvent;


public class PuzzlerVictoryRoom extends VictoryRoom {
    public PuzzlerVictoryRoom() {
        super(EventType.NONE);
        this.phase = RoomPhase.EVENT;
    }

    @Override
    public void onPlayerEntry() {
        AbstractDungeon.overlayMenu.proceedButton.hide();
        this.event = new PuzzlerVictoryEvent();
        this.event.onEnterRoom();
    }

    @Override
    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp) {
            this.event.update();
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.event != null) {
            this.event.renderRoomEventPanel(sb);
            this.event.render(sb);
        }

        super.render(sb);
    }

    @Override
    public void renderAboveTopPanel(SpriteBatch sb) {
        super.renderAboveTopPanel(sb);
        if (this.event != null) {
            this.event.renderAboveTopPanel(sb);
        }
    }
}
