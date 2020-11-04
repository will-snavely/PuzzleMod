package org.barnhorse.puzzlemod.monsters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MonsterTemplates {
    public static Map<String, Supplier<PuzzleMonster>> monsters;

    static {
        monsters = new HashMap<>();
        monsters.put("Cultist", () -> new PuzzleMonster(
                "CultistTemplate", 50,
                -8.0f, 10.0f, 230.0f, 240.0f,
                0, 0,
                -50.0f, 50.0f,
                0, 10,
                null, null,
                "images/monsters/theBottom/cultist/skeleton.atlas",
                "images/monsters/theBottom/cultist/skeleton.json",
                "waving",
                1.0f));
    }

    public static PuzzleMonster getTemplate(String id) {
        return monsters.get(id).get();
    }
}
