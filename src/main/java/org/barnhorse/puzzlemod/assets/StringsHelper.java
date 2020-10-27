package org.barnhorse.puzzlemod.assets;

public class StringsHelper {
    public static String getCharacterStringsPath(String locale) {
        return ResourceHelper.getResourcePath("localization", locale, "CharacterStrings.json");
    }

    public static String getRelicStringsPath(String locale) {
        return ResourceHelper.getResourcePath("localization", locale, "RelicStrings.json");
    }

    public static String getCardStringsPath(String locale) {
        return ResourceHelper.getResourcePath("localization", locale, "CardStrings.json");
    }
}
