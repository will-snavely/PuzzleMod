package org.barnhorse.puzzlemod;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.barnhorse.puzzlemod.assets.StaticAssets;
import org.barnhorse.puzzlemod.assets.StringsHelper;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.packs.DefaultPuzzleApplicator;
import org.barnhorse.puzzlemod.packs.PuzzleApplicator;
import org.barnhorse.puzzlemod.packs.model.PuzzlePack;
import org.barnhorse.puzzlemod.relics.BagOfPieces;
import theDefault.relics.CursedCornerPiece;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;
import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.THE_PUZZLER;

@SpireInitializer
public class PuzzleMod implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditCardsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber {
    public static final Logger logger = LogManager.getLogger(PuzzleMod.class.getName());
    public static final Color DEFAULT_GRAY = CardHelper.getColor(64.0f, 70.0f, 70.0f);

    private static final Path modWorkingPath = Paths
            .get("mods", "etc", "barnhorse", "puzzlemod")
            .toAbsolutePath();
    private static final Path puzzlesPath = modWorkingPath.resolve("puzzles");
    public static PuzzlePack currentPuzzlePack;
    public static int lastPuzzleRow;

    private static PuzzleApplicator puzzleApplicator;
    private static String currentPuzzleFile;
    private static String builtinPuzzlePrefix = "__builtin__";

    private static void createPuzzleDirectory() {
        File file = puzzlesPath.toFile();
        file.mkdirs();
        if (!file.exists()) {
            throw new RuntimeException("Failed to create directory at: " + puzzlesPath);
        }
    }

    public PuzzleMod() {
        logger.info("Creating the color " + COLOR_GRAY.toString());

        BaseMod.addColor(
                COLOR_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                DEFAULT_GRAY,
                StaticAssets.ATTACK_DEFAULT_GRAY,
                StaticAssets.SKILL_DEFAULT_GRAY,
                StaticAssets.POWER_DEFAULT_GRAY,
                StaticAssets.ENERGY_ORB_DEFAULT_GRAY,
                StaticAssets.ATTACK_DEFAULT_GRAY_PORTRAIT,
                StaticAssets.SKILL_DEFAULT_GRAY_PORTRAIT,
                StaticAssets.POWER_DEFAULT_GRAY_PORTRAIT,
                StaticAssets.ENERGY_ORB_DEFAULT_GRAY_PORTRAIT,
                StaticAssets.CARD_ENERGY_ORB);

        logger.info("Done creating the color");

    }

    public static String getCurrentPuzzleFile() {
        return currentPuzzleFile;
    }

    public static void setCurrentPuzzleFile(String puzzleFile) {
        currentPuzzleFile = puzzleFile;
    }

    private static boolean isBuiltin(String puzzle) {
        return puzzle != null && puzzle.startsWith(builtinPuzzlePrefix);
    }

    private static String getBaseResource(String puzzle) {
        if(puzzle == null) {
            return null;
        }
        return puzzle.replace(builtinPuzzlePrefix, "");
    }

    public static String builtinPuzzle(String name) {
        return String.format("%s%s", builtinPuzzlePrefix, name);
    }

    public static PuzzlePack loadCurrentPack() {
        Gson gson = new Gson();
        if(isBuiltin(currentPuzzleFile)) {
            String baseName = getBaseResource(currentPuzzleFile);
            String resource = "/puzzleModResources/packs/" + baseName;
            InputStream in = PuzzleMod.class.getResourceAsStream(resource);
            currentPuzzlePack = gson.fromJson(new InputStreamReader(in), PuzzlePack.class);
        } else {
            Path path = puzzlesPath.resolve(currentPuzzleFile);
            try {
                currentPuzzlePack = gson.fromJson(new FileReader(path.toFile()), PuzzlePack.class);
            } catch(IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return currentPuzzlePack;
    }

    public static void initialize() {
        createPuzzleDirectory();
        setCurrentPuzzleFile(builtinPuzzle("demo.json"));
        String saveKey = ModId.create("puzzleFile");
        BaseMod.addSaveField(saveKey, new PuzzleFileSave());

        PuzzleMod mod = new PuzzleMod();
        puzzleApplicator = new DefaultPuzzleApplicator();
        BaseMod.subscribe(mod);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(
                new ThePuzzler("the Default", THE_PUZZLER),
                StaticAssets.THE_DEFAULT_BUTTON,
                StaticAssets.THE_DEFAULT_PORTRAIT,
                THE_PUZZLER);
    }

    @Override
    public void receivePostInitialize() {
    }

    @Override
    public void receiveEditCards() {
        new AutoAdd("PuzzleMod")
                .packageFilter("org.barnhorse.puzzlemod.cards")
                .setDefaultSeen(true)
                .cards();
    }

    @Override
    public void receiveEditStrings() {
        String locale = "eng";
        BaseMod.loadCustomStringsFile(CardStrings.class,
                StringsHelper.getCardStringsPath(locale));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                StringsHelper.getRelicStringsPath(locale));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                StringsHelper.getCharacterStringsPath(locale));
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelicToCustomPool(new CursedCornerPiece(), COLOR_GRAY);
        BaseMod.addRelicToCustomPool(new BagOfPieces(), COLOR_GRAY);
    }

    public static PuzzleApplicator getPuzzleApplicator() {
        return puzzleApplicator;
    }
}