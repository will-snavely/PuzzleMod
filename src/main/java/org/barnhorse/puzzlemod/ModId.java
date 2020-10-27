package org.barnhorse.puzzlemod;

public class ModId {
    public static String create(String baseId) {
        return String.format("%s:%s", ModSettings.modId, baseId);
    }

    private ModId() {
    }
}
