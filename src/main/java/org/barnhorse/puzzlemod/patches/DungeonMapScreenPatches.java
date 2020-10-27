package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class DungeonMapScreenPatches {
    @SpirePatch(clz = DungeonMapScreen.class, method = SpirePatch.CONSTRUCTOR)
    public static class DungeonMapScreen_Ctor {
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e);
                    if (e.getClassName().equals("com.megacrit.cardcrawl.map.DungeonMap")) {
                        e.replace("$_ = org.barnhorse.puzzlemod.map.DungeonMapFactory.create();");
                    }
                }
            });
        }
    }
}

