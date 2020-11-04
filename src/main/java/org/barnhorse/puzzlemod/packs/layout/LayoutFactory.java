package org.barnhorse.puzzlemod.packs.layout;

import org.barnhorse.puzzlemod.packs.model.PuzzleLayout;

public class LayoutFactory {
    public static MonsterLayout createLayout(PuzzleLayout layout) {
        switch (layout.type) {
            case "linear":
                float spacing = 60f;
                if (layout.settings != null) {
                    if (layout.settings.linear != null) {
                        if (layout.settings.linear.spacing != null) {
                            spacing = layout.settings.linear.spacing;
                        }
                    }
                }
                return new LinearLayout(spacing);
            case "manual":
                return null;
            default:
                throw new RuntimeException("Unsupported layout type: " + layout.type);
        }
    }
}
