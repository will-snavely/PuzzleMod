package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.neow.NeowRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class NeowRoomPatches {
    @SpirePatch(clz = NeowRoom.class, method = SpirePatch.CONSTRUCTOR)
    public static class NeowRoow_Ctor {
        public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e);
                    if (e.getClassName().equals("com.megacrit.cardcrawl.neow.NeowEvent")) {
                        e.replace("$_ = org.barnhorse.puzzlemod.events.NeowEventFactory.create($1);");
                    }
                }
            });
        }
    }
}

