package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.events.PuzzlerNeowEvent;

public class NeowRoomPatches {
    @SpirePatch(clz = NeowRoom.class, method = SpirePatch.CONSTRUCTOR)
    public static class NeowRoow_Ctor {
        public static SpireReturn Prefix(NeowRoom thisRef, boolean isDone) {
            if (ThePuzzler.isPuzzlerChosen()) {
                thisRef.phase = AbstractRoom.RoomPhase.EVENT;
                thisRef.event = new PuzzlerNeowEvent(isDone);
                thisRef.event.onEnterRoom();
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}



