package org.barnhorse.puzzlemod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.map.DungeonMapHooks;

@SuppressWarnings("unused")
public class DungeonMapPatches {
    @SpirePatch(
            clz = DungeonMap.class,
            method = "render"
    )
    public static class PatchRender {
        public static SpireReturn Prefix(DungeonMap map, SpriteBatch sb) {
            if (AbstractDungeon.player != null && ThePuzzler.isPuzzlerChosen()) {
                DungeonMapHooks.render(map, sb);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMap.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static SpireReturn Prefix(DungeonMap map) {
            if (AbstractDungeon.player != null && ThePuzzler.isPuzzlerChosen()) {
                DungeonMapHooks.update(map);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMap.class,
            method = "show"
    )
    public static class PatchShow {
        public static SpireReturn Prefix(DungeonMap map) {
            if (AbstractDungeon.player != null && ThePuzzler.isPuzzlerChosen()) {
                DungeonMapHooks.show(map);
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}