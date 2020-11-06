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

### Github Releases
Head to the [Releases page](https://github.com/will-snavely/PuzzleMod/releases)
and download the latest version of `PuzzleMod.jar`.
This should then be placed in the `mods` folder of
your Slay the Spire installation directory (if you 
are using Steam, you can find this by right-clicking
the game, and navigating to "Manage", then selecting
"Browse Local Files").

## Running

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

## Custom Puzzles 
Check out the wiki (https://github.com/will-snavely/PuzzleMod/wiki) for information about creating custom puzzles.
