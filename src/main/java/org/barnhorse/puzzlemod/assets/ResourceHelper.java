package org.barnhorse.puzzlemod.assets;

import org.barnhorse.puzzlemod.ModSettings;

import java.nio.file.Paths;

public class ResourceHelper {
    public static final String resourceRoot = String.format(
            "%sResources",
            ModSettings.modId);

    public static String getResourcePath(String... parts) {
        return Paths.get(resourceRoot, parts).toString();
    }

}
