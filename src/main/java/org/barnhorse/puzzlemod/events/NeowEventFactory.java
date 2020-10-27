package org.barnhorse.puzzlemod.events;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowEvent;
import org.barnhorse.puzzlemod.characters.ThePuzzler;

public class NeowEventFactory {
    private NeowEventFactory() {
    }

    public static AbstractEvent create() {
        return new NeowEvent(false);
    }

    public static AbstractEvent create(boolean isDone) {
        if (AbstractDungeon.player.chosenClass == ThePuzzler.Enums.THE_PUZZLER) {
            return new PuzzlerNeowEvent(isDone);
        } else {
            return new NeowEvent(isDone);
        }
    }
}
