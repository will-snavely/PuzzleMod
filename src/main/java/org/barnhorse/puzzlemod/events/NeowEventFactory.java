package org.barnhorse.puzzlemod.events;

import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.neow.NeowEvent;

public class NeowEventFactory {
    private NeowEventFactory() {
    }

    public static AbstractEvent create() {
        return new NeowEvent(false);
    }

    public static AbstractEvent create(boolean isDone) {
        return new PuzzlerNeowEvent(isDone);
    }
}
