package org.barnhorse.puzzlemod;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.barnhorse.puzzlemod.assets.StaticAssets;
import org.barnhorse.puzzlemod.assets.StringsHelper;
import org.barnhorse.puzzlemod.characters.ThePuzzler;
import org.barnhorse.puzzlemod.dungeons.ExordiumFactory;
import org.barnhorse.puzzlemod.packs.PuzzlePack;
import org.barnhorse.puzzlemod.relics.BagOfPieces;
import theDefault.relics.CursedCornerPiece;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.COLOR_GRAY;
import static org.barnhorse.puzzlemod.characters.ThePuzzler.Enums.THE_PUZZLER;

@SpireInitializer
public class PuzzleMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber {
    public static final Logger logger = LogManager.getLogger(PuzzleMod.class.getName());

    public static final Color DEFAULT_GRAY = CardHelper.getColor(64.0f, 70.0f, 70.0f);

    public static final String PUZZLE_FILE_SETTING = "puzzleFile";

    private static String currentPuzzleFile;

    public static Properties modSettings = new Properties();

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
        setCurrentPuzzleFile("starter.json");
        String saveKey = ModId.create("puzzleFile");
        BaseMod.addSaveField(saveKey, new PuzzleFileSave());
    }

    public static String getCurrentPuzzleFile() {
        return currentPuzzleFile;
    }

    public static void setCurrentPuzzleFile(String puzzleFile) {
        currentPuzzleFile = puzzleFile;
    }

    public static PuzzlePack loadCurrentPack() {
        Gson gson = new Gson();
        String puzzleFile = "/puzzleModResources/packs/" + currentPuzzleFile;
        InputStream in = ExordiumFactory.class.getResourceAsStream(puzzleFile);
        return gson.fromJson(new InputStreamReader(in), PuzzlePack.class);
    }

    public static void initialize() {
        PuzzleMod mod = new PuzzleMod();
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
        /*
        logger.info("Adding cards");
        new AutoAdd("PuzzleMod")
                .packageFilter("org.barnhorse.puzzlemod.cards")
                .setDefaultSeen(true)
                .cards();
        logger.info("Done adding cards!");
        */
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
    public void receiveEditKeywords() {
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelicToCustomPool(new CursedCornerPiece(), COLOR_GRAY);
        BaseMod.addRelicToCustomPool(new BagOfPieces(), COLOR_GRAY);
    }
}