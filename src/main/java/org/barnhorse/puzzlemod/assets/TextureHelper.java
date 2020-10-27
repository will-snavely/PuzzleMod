package org.barnhorse.puzzlemod.assets;

public class TextureHelper {
    public static String getCardImagePath(String resourceName) {
        return ResourceHelper.getResourcePath("images", "cards", resourceName);
    }
}
