package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class CoreCardCrawlGamePatches {
    private static String EXORDIUM_CLASS = "com.megacrit.cardcrawl.dungeons.Exordium";
    private static String FACTORY_CLASS = "org.barnhorse.puzzlemod.dungeons.ExordiumFactory";
    private static String FACTORY_METHOD = "create";

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class})
    public static class GetDungeonOverload1 {
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e);
                    if (e.getClassName().equals(EXORDIUM_CLASS)) {
                        String replacement = String.format(
                                "$_ = %s.%s($1, $2);",
                                FACTORY_CLASS,
                                FACTORY_METHOD
                        );
                        e.replace(replacement);
                    }
                }
            });
        }
    }

    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class GetDungeonOverload2 {
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e);
                    if (e.getClassName().equals(EXORDIUM_CLASS)) {
                        String replacement = String.format(
                                "$_ = %s.%s($1, $2);",
                                FACTORY_CLASS,
                                FACTORY_METHOD
                        );
                        e.replace(replacement);
                    }
                }
            });
        }
    }
}