package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ProceedButtonPatches {
    @SpirePatch(clz = ProceedButton.class, method = "update")
    public static class ProceedButton_update {
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    super.edit(m);
                    if (m.getClassName().equals("com.megacrit.cardcrawl.ui.buttons.ProceedButton")
                            && m.getMethodName().equals("goToTreasureRoom")) {
                        String puzzlerCheck =
                                "org.barnhorse.puzzlemod.characters.ThePuzzler.isPuzzlerChosen()";
                        String postBossHandler =
                                "org.barnhorse.puzzlemod.dungeons.PuzzlerExordium.postBossHandler(this)";

                        String code = String.format(
                                "(%s) ? %s : this.goToTreasureRoom();",
                                puzzlerCheck,
                                postBossHandler);
                        m.replace(code);
                    }
                }
            });
        }
    }
}

