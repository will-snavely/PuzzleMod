package org.barnhorse.puzzlemod.packs.layout;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Layout a group of monsters in a straight line,
 * with some spacing between them.
 */
public class LinearLayout implements MonsterLayout {
    private float spacing;

    public LinearLayout(float spacing) {
        this.spacing = spacing * Settings.scale;
    }

    @Override
    public List<Vector2> layout(
            List<AbstractMonster> monsters,
            float xMin, float xMax, float yMin, float yMax) {
        int monsterCount = monsters.size();
        float midX = (xMax + xMin) / 2;

        List<Float> positions = new ArrayList<>();
        float curX = 0;
        for (AbstractMonster monster : monsters) {
            positions.add(curX);
            curX += monster.hb_w + spacing;
        }

        // Translate the positions so that they are centered
        // in the specified rectangle
        int midIndex = monsterCount / 2;
        final float delta;
        if (monsterCount % 2 == 0) {
            float left = positions.get(midIndex - 1);
            float right = positions.get(midIndex);
            float midPoint = (left + right) / 2;
            delta = midX - midPoint;
        } else {
            float midPoint = positions.get(midIndex);
            delta = midX - midPoint;
        }
        return positions.stream()
                .map(p -> new Vector2(p + delta, yMin))
                .collect(Collectors.toList());
    }
}
