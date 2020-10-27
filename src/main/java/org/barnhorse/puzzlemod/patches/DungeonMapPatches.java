package org.barnhorse.puzzlemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.map.DungeonMap;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.NewExpr;

public class DungeonMapPatches {
    @SpirePatch(clz = DungeonMap.class, method = "update")
    public static class DungeonMap_update {
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            ctMethodToPatch.instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    super.edit(f);
                    if(f.getClassName().equals("com.megacrit.cardcrawl.core.Settings")
                            && f.getFieldName().equals("isDebug")) {
                        f.replace("$_ = org.barnhorse.puzzlemod.map.BossChecker.beforeBossNode();");
                    }
                }

                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e);
                    if (e.getClassName().equals("com.megacrit.cardcrawl.rooms.MonsterRoomBoss")) {
                        e.replace("$_ = org.barnhorse.puzzlemod.rooms.BossRoomFactory.create();");
                    }
                }
            });
        }
    }
}

