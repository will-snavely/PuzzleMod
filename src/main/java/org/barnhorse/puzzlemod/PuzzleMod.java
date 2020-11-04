package org.barnhorse.puzzlemod;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
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
import org.barnhorse.puzzlemod.relics.CursedCornerPiece;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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

    // The color isn't really important, at the moment; it's a placeholder.
    public static final Color DEFAULT_GRAY =
            CardHelper.getColor(64.0f, 70.0f, 70.0f);

    // Custom puzzles are stored in mods/etc/barnhorse/puzzlemod/puzzles
    private static final Path modWorkingPath = Paths
            .get("mods", "etc", "barnhorse", "puzzlemod")
            .toAbsolutePath();
    private static final Path puzzlesPath = modWorkingPath.resolve("puzzles");

    // A reference to the currently active puzzle pack
    public static PuzzlePack currentPuzzlePack;

    // The y position of the last, non-boss puzzle node.
    public static int lastPuzzleRow;

    // A reference to a PuzzleApplicator, used to apply a given puzzle
    // configuration to a room.
    private static PuzzleApplicator puzzleApplicator;

    // If a puzzle has the __builtin__ prefix, it is loaded from a resource,
    // instead of from disk.
    private static String builtinPuzzlePrefix = "__builtin__";

    public static final String selectedPuzzleFileSetting = "selectedPuzzleFile";

    // Store the location of the puzzle file associated with the currently
    // active run. This is preserved when saving (see PuzzleFileSave)
    public static String currentRunPuzzleFile;

    private static void createPuzzleDirectory() {
        File file = puzzlesPath.toFile();
        file.mkdirs();
        if (!file.exists()) {
            throw new RuntimeException("Failed to create directory at: " + puzzlesPath);
        }
    }

    public PuzzleMod() {
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
    }

    public static String getCurrentRunPuzzleFile() {
        return currentRunPuzzleFile;
    }

    public static void setCurrentRunPuzzleFile(String puzzleFile) {
        currentRunPuzzleFile = puzzleFile;
    }

    public static boolean isBuiltin(String puzzle) {
        return puzzle != null && puzzle.startsWith(builtinPuzzlePrefix);
    }

    public static String getBaseResource(String puzzle) {
        if (puzzle == null) {
            return null;
        }
        return puzzle.replace(builtinPuzzlePrefix, "");
    }

    private static List<String> getBuiltinPuzzles() {
        return Arrays.asList(
                builtinPuzzle("ironclad_basic.json")
        );
    }

    public static String builtinPuzzle(String name) {
        return String.format("%s%s", builtinPuzzlePrefix, name);
    }

    public static PuzzlePack loadPackFromSettings() {
        String descriptor;
        String defaultPuzzle = builtinPuzzle("ironclad_basic.json");
        try {
            SpireConfig config = getSpireConfig();
            config.load();
            descriptor = config.getString(selectedPuzzleFileSetting);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            descriptor = defaultPuzzle;
        }
        PuzzlePack result = loadPack(descriptor);
        setCurrentRunPuzzleFile(descriptor);
        currentPuzzlePack = result;
        return result;
    }

    public static PuzzlePack loadPackFromSave() {
        assert (currentRunPuzzleFile != null && currentRunPuzzleFile != "");
        PuzzlePack result = loadPack(currentRunPuzzleFile);
        currentPuzzlePack = result;
        return result;
    }

    public static PuzzlePack loadPack(String descriptor) {
        Gson gson = new Gson();
        PuzzlePack result;
        if (isBuiltin(descriptor)) {
            String baseName = getBaseResource(descriptor);
            String resource = "/puzzleModResources/packs/" + baseName;
            InputStream in = PuzzleMod.class.getResourceAsStream(resource);
            result = gson.fromJson(new InputStreamReader(in), PuzzlePack.class);
        } else {
            Path path = puzzlesPath.resolve(descriptor);
            try {
                result = gson.fromJson(new FileReader(path.toFile()), PuzzlePack.class);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return result;
    }

    public static void initialize() {
        createPuzzleDirectory();
        String saveKey = ModId.create("puzzleFile");
        BaseMod.addSaveField(saveKey, new PuzzleFileSave());

        PuzzleMod mod = new PuzzleMod();
        puzzleApplicator = new DefaultPuzzleApplicator();
        BaseMod.subscribe(mod);
    }

    private static SpireConfig getSpireConfig() throws IOException {
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(
                selectedPuzzleFileSetting,
                builtinPuzzle("ironclad_basic.json"));
        return new SpireConfig(
                ModSettings.modId,
                "puzzleModConfig",
                defaultSettings);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(
                new ThePuzzler("the Default", THE_PUZZLER),
                StaticAssets.THE_DEFAULT_BUTTON,
                StaticAssets.THE_DEFAULT_PORTRAIT,
                THE_PUZZLER);
    }

    private static List<String> getCustomPuzzles() {
        try {
            return Files.list(puzzlesPath)
                    .map(p -> p.getFileName().toString())
                    .filter(p -> p.endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            return new ArrayList<>();
        }
    }

    private static List<String> getAllPuzzles() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(getBuiltinPuzzles());
        result.addAll(getCustomPuzzles());
        return result;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = ImageMaster.loadImage(StaticAssets.MOD_BADGE);
        ModPanel settingsPanel = new ModPanel();

        Color bgColor = Color.valueOf("#66bbf0");
        Color highlightColor = Color.valueOf("#bdc44d");
        Texture upArrow = ImageMaster.loadImage(StaticAssets.UP_ARROW);
        Texture downArrow = ImageMaster.loadImage(StaticAssets.DOWN_ARROW);
        Texture refresh = ImageMaster.loadImage(StaticAssets.REFRESH);
        Texture submit = ImageMaster.loadImage(StaticAssets.SUBMIT);

        ModLabel topLabel = new ModLabel(
                "Select a puzzle file",
                375,
                760,
                Color.WHITE,
                FontHelper.largeDialogOptionFont,
                settingsPanel,
                (panel) -> {
                });

        int listYPos = 740;
        int listXPos = 375;
        PuzzleList selector = new PuzzleList(
                listXPos,
                listYPos,
                settingsPanel,
                getAllPuzzles(),
                bgColor,
                highlightColor);

        int buttonYPos = listYPos - 60;
        PuzzleButton upButton = new PuzzleButton(
                listXPos + PuzzleList.listWidth + 10,
                buttonYPos,
                upArrow,
                settingsPanel,
                (b) -> selector.decreaseSelectedIndex(),
                bgColor,
                highlightColor);
        PuzzleButton downButton = new PuzzleButton(
                listXPos + PuzzleList.listWidth + 10,
                buttonYPos - 80,
                downArrow,
                settingsPanel,
                (b) -> selector.increaseSelectedIndex(),
                bgColor,
                highlightColor);
        PuzzleButton refreshButton = new PuzzleButton(
                listXPos + PuzzleList.listWidth + 10,
                buttonYPos - 160,
                refresh,
                settingsPanel,
                (b) -> selector.resetItems(getAllPuzzles()),
                bgColor,
                highlightColor);

        String currentlyLoaded = "NONE";
        try {
            SpireConfig config = getSpireConfig();
            config.load();
            currentlyLoaded = new PuzzleList.ListItem(
                    config.getString(selectedPuzzleFileSetting)).getDisplay();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        ModLabel bottomLabel = new ModLabel(
                "Currently selected puzzle: " + currentlyLoaded,
                375,
                listYPos - 500,
                Color.WHITE,
                FontHelper.largeDialogOptionFont,
                settingsPanel,
                (lbl) -> {
                });
        PuzzleButton selectButton = new PuzzleButton(
                375,
                listYPos - 460,
                submit,
                settingsPanel,
                (b) -> {
                    try {
                        PuzzleList.ListItem selected = selector.getSelected();
                        SpireConfig config = getSpireConfig();
                        config.setString(selectedPuzzleFileSetting, selected.getValue());
                        config.save();
                        bottomLabel.text = "Currently selected puzzle: " + selected.getDisplay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                bgColor,
                highlightColor,
                "Select",
                FontHelper.largeDialogOptionFont);

        settingsPanel.addUIElement(topLabel);
        settingsPanel.addUIElement(selector);
        settingsPanel.addUIElement(upButton);
        settingsPanel.addUIElement(downButton);
        settingsPanel.addUIElement(refreshButton);
        settingsPanel.addUIElement(selectButton);
        settingsPanel.addUIElement(bottomLabel);

        BaseMod.registerModBadge(
                badgeTexture,
                "PuzzleMod",
                "barnhorse",
                "Puzzle Mode for Spire",
                settingsPanel);
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