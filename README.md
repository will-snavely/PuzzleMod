# PuzzleMod

## About
PuzzleMod is a mod for Slay the Spire that implements
a "puzzle" mode. In this mode, each floor of the 
Spire is an independent puzzle with its own cards,
relics, potion, etc., which must be solved in
order to progress.

## Installing
PuzzleMod is available on Github or Steam workshop.

### Steam Workshop
Subscribe to this Steam Workshop project:
- https://steamcommunity.com/sharedfiles/filedetails/?id=2269437478

### Github Releeases
Head to the [Releases page](https://github.com/will-snavely/PuzzleMod/releases)
and download the latest version of `PuzzleMod.jar`.
This should then be placed in the `mods` folder of
your Slay the Spire installation directory (if you 
are using Steam, you can find this by right-clicking
the game, and navigating to "Manage", then selecting
"Browse Local Files").

## Enabling

To use the mod, you must launch Slay the Spire with mods enabled, 
then ensure that "PuzzleMod" is selected in the mod configuation
dialog.

## Building

The mod can be built locally using Maven. There are some
steps you must following first, though, for this to work.

### Install Slay The Spire  on Steam 
The only supported build configuration requires you to 
have Slay the Spire installed through Steam. This should
work on both Windows and Linux, though this has not been
heavily tested.

### Install Steam Workshop Dependencies
You should install Mod the Spire, BaseMod, and StSLib through
Steam Workshop. This is done by "subscribing" to the
respective pages of these tools:

- https://steamcommunity.com/workshop/filedetails/?id=1605060445
- https://steamcommunity.com/workshop/filedetails/?id=1605833019
- https://steamcommunity.com/workshop/filedetails/?id=1609158507

### Setting up a `settings.xml` file for Maven
This tells Maven where to find all of the local Slay the Spire
Jar files. My `settings.xml` file [looks like this](./sample/settings_sample.xml).
This file should be placed in the appropriate location for
your system. Typically, this is inside a folder named `.m2`
in your home directory (`/home/username/.m2` on Linux, 
`C:\Users\UserName\.m2` on Windows). Read this link for
[more information on settings.xml](https://maven.apache.org/ref/3.6.3/maven-settings/settings.html).

You will likely need to update the sample XML file to suit your
local system. The critical thing is that the following variables
are set:
- `sts-home`: Should be set to the location of
your Slay the Spire installation (typically
a folder named `SlayTheSpire` somewhere in
 the local `Steam` folder.)
- `sts-jar`: Should be set to the path of your Slay the Spire 
jar distribution, a.k.a. `desktop-1.0.jar`. This should
be in `sts-home`.
- `basemod-jar`: Should be set to the path of your local 
BaseMod jar file (`BaseMod.jar`). This should be somewhere
within `Steam\steamapps\workshop\content`.
- `stslib-jar`: Should be set to the path of your local 
StSLib jar file (`StSLib.jar.jar`). This should be somewhere
within `Steam\steamapps\workshop\content`.
- `mts-jar`: Should be set to the path of your local 
Mod the Spire jar file (`ModTheSpire.jar`). This 
also should be somewhere under 
`Steam\steamapps\workshop\content`.

### Build the Mod
After downloading the source, the mod can be built using:
- `mvn clean install`

This should be run from the root of the repository.
Assuming your `settings.xml` file is configured properly,
this should compile the source and copy the mod jar file
to the appropriate folder in your Spire installation.

If you just wish to build the source, without
installing, you can run:
- `mvn clean package`

## The Puzzle File Format
Puzzles are organized into "packs", which are specified by a json file.
This file is organized as follows. You puzzles are loaded into a single
act (a modified Exordium). Multi-act packs are currently not supported, 
though are certainly possible.

Here is a [sample puzzle file](./src/main/resources/puzzleModResources/packs/starter.json).

### Top Level Organization
```
{
  # The Mod name is shown when the Act loads
  "name": "The name of your mod", 
  
  # The "puzzles" key stores the puzzles that show up in the
  # non-boss nodes
  "puzzles": [
    <puzzle object 1>,
    <puzzle object 2>,
    ...
  ],
  
  # One puzzle is designated as the boss puzzle, which means it
  # shows up in the boss room of the act
  "boss": {
       <puzzle object>
  }
}
```

### Puzzle Objects
Each puzzle object is specified in a json structure as follows:
```
{
  # The puzzle name currently isn't used
  "name": "Puzzle Name",

  # The size of the initial hand
  "masterHandSize": 5,

  # The initial draw pile, in order. This order will be maintained
  # when the puzzle is loaded. So the first 'masterHandSize' cards
  # you list here will be drawn at the start of combat
  "startingDrawPile": [
    <card object 1>,
    ...
  ],

  # The starting hand. Note that the player will still draw cards at the 
  # beginning of the first turn, on top of what's specified here (unless 
  # masterHandSize is set to 0)
  "startingHand": [
    <card object 1>,
    ...
  ],

  # The starting discard pile.
  "startingDiscardPile": [
    <card object 1>,
    ...
  ],

  # The starting discard pile. The order in which cards are listed is
  # maintained in game.
  "startingDrawPile": [
    <card object 1>,
    ...
  ],

  # The starting exhause pile.
  "startingExhaustPile": [
    <card object 1>,
    ...
  ],

  "relics": [
    <relic object 1>,
    ...
  ],

  "potions": [
    <potion object 1>,
    ...
  ],
  
  # The number of orb slots the player has
  "orbCount": 0,
  
  # The player's max health
  "maxHp": 20,

  # The player's current health
  "curHp": 20,

  # A list of monsters in the room
  "monsters": [
    <monster object 1>,
    ...    
  ]
}
```

### Card Objects
Cards are specified with a json object as follows.
```
{
  # The card key will need to be looked up from the game sources.
  # Generally it's the English name of the card, including spaces
  # e.g. "Perfected Strike", though there are some exceptions
  "key": "Card Key",

  # Number of times to upgrade the card.
  "upgradeCount": 0-N
}
```

### Monster Objects
Monsters are specified with a json object as follows.
```
{
  # The monster name
  "name": "Monster Name",

  # The monster's max HP
  "maxHp": 6,

  # How many "fade" counters should the monster have?
  # The monster will die after this many turns. Useful for
  # "survive" kind of puzzles
  "fade": 0,

  # How much damage does the monster do a turn?
  "damage": 20,

  # Hitbox x-position, y-position, width, and height
  # I stole these from the game source when possible, 
  # and in general these might take some tweaking to get
  # right
  "hb_x": -8.0,
  "hb_y": 10.0,
  "hb_w": 230.0,
  "hb_h": 240.0,

  # The relative location of the monster's dialog box
  "dialogX": -50.0,
  "dialogY": 50.0,

  # The X and Y offset of the monster in battle screen
  "offsetX": 0.0,
  "offsetY": -10.0,

  # Scaling factor for the monster's sprite; as far as I can tell,
  # < 1 produces a bigger sprite, while > 1 produces a smaller one.
  "scale": 1.0,

  # Animation settings for the monster; you can reuse existing game
  # assets here, as a starting place, or create your own. Currently
  # only Spine animations are supported. Here we show using the 
  # cultist animation.
  "atlasUrl": "images/monsters/theBottom/cultist/skeleton.atlas",
  "skeletonUrl": "images/monsters/theBottom/cultist/skeleton.json",
  "animation": "waving",

  # What does the monster say at the start of the fight? 
  "entranceDialog": "It will be years before you can strike me!",
  # What does the monster say at the end of the fight? 
  "deathDialog": "I can't believe you've done this."
}
```

### Relic Objects
Relics are specified with a json object as follows.
 ```
{
  # The relic unique key; needs to be looked up in the game sources.
  "key": "Relic Key",
}
```

### Potion Objects
Potion are specified with a json object as follows.
```
{
  # The unique potion key; needs to be looked up in the game sources.
  "key": "Potion Key",
}
```

### Current Shortcomings 
- Displayed strings in the puzzle file are not localized.
- Arbitrary powers (like Weakness, Vulnerability, etc) can't be applied
to players/monsters.
- Initial orb configurations can't be specified.
- Monster move sets can't be specified (they just attack every turn for
the same amount).
- Need better resources for looking up object keys.
- Multi-act packs are currently not supported.
- Probably the pack file should have a version number.