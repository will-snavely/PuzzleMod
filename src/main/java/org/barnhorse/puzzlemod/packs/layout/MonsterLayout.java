package org.barnhorse.puzzlemod.packs.layout;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.List;

public interface MonsterLayout {
    /**
     * Layout a collection of monsters in a 2d rectangle.
     *
     * @param monsters A list of monsters
     * @param xMin     The minimum x-coordinate of the space
     * @param xMax     The maximum x-coordinate of the space
     * @param yMin     The minimum y-coordinate of the space
     * @param yMax     The maximum y-coordinate of the space
     * @return The position for each monster, as a Vector2
     */
    List<Vector2> layout(
            List<AbstractMonster> monsters,
            float xMin, float xMax, float yMin, float yMax);
}
