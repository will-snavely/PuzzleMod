package org.barnhorse.puzzlemod.monsters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MonsterTemplates {
    public static Map<String, Supplier<PuzzleMonster>> monsters;

    static {
        monsters = new HashMap<>();
        monsters.put("Cultist", () -> new PuzzleMonster(
                "Cultist", 50,
                -8.0f, 10.0f, 230.0f, 240.0f,
                0, 0,
                -50.0f, 50.0f,
                0, 10,
                null, null,
                "images/monsters/theBottom/cultist/skeleton.atlas",
                "images/monsters/theBottom/cultist/skeleton.json",
                "waving",
                1.0f));

        monsters.put("Jaw Worm", () -> new PuzzleMonster(
                "Jaw Worm", 50,
                0.0F, -25.0F, 260.0F, 170.0F,
                0, 0,
                -50.0f, 15.0f,
                0, 10,
                null, null,
                "images/monsters/theBottom/jawWorm/skeleton.atlas",
                "images/monsters/theBottom/jawWorm/skeleton.json",
                "idle",
                1.0f));

        monsters.put("Red Slaver", () -> new PuzzleMonster(
                "Red Slaver", 50,
                0.0F, 0.0F, 170.0F, 230.0F,
                0, 0,
                -50.0f, 50.0f,
                0, 10,
                null, null,
                "images/monsters/theBottom/redSlaver/skeleton.atlas",
                "images/monsters/theBottom/redSlaver/skeleton.json",
                "idle",
                1.0f));

        monsters.put("Fungi Beast", () -> new PuzzleMonster(
                "Fungi Beast", 50,
                0.0F, -16.0F, 260.0F, 170.0F,
                0, 0,
                -50.0f, 50.0f,
                0, 10,
                null, null,
                "images/monsters/theBottom/fungi/skeleton.atlas",
                "images/monsters/theBottom/fungi/skeleton.json",
                "Idle",
                1.0f));
    }

    public static PuzzleMonster getTemplate(String id) {
        return monsters.get(id).get();
    }
}
