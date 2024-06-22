# Changelog

## 0.0.12_1
*2024-06-22 22:20*
- Player now slowly falls through the world layers

## 0.0.12
*2024-06-22 22:11*
- Added layers of blocks to the world
  - World is made up of bedrock, stone and dirt, dirt, and grass layers
  - Go up a layer by jumping

## 0.0.11_2
*2024-06-22 20:31*
- Made void darker

## 0.0.11_1
*2024-06-21 02:24*
- Changed options file format
- Reset options version to 1.0
- Changed options incompatibility warning message

## 0.0.11
*2024-06-21 01:58*
- World no longer repeats infinitely
- Added the void
- Changed options file version to 6
- Changed default sprintSpeed to 0.8
- Renamed rotationSpeed option to sensitivity
- Removed skyHeight and groundHeight options
- Fixed world origin being off-center

## 0.0.10_8
*2024-06-20 20:16*
- Store blocks in world as 3D
- Fixed options file initialisation
- Changed options file version to 5

## 0.0.10_7
*2024-06-18 23:54*
- Properly fixed large mouse movement occurring on load
- Game window is now fully focused on load

## 0.0.10_6
*2024-06-18 19:20*
- Made walking smoother
- Changed default walkSpeed to 0.5 and sprintSpeed to 0.7
- Fixed large mouse movement occurring on load

## 0.0.10_5
*2024-06-18 19:04*
- Improved mouse look
- Changed default rotationSpeed to 0.005

## 0.0.10_4
*2024-06-17 01:17*
- Changed block placement key from X to left click

## 0.0.10_3
*2024-06-17 00:53*
- Fixed block placing not causing a rerender

## 0.0.10_2
*2024-06-16 11:21*
- Normalised all coordinate planes

## 0.0.10_1
*2024-06-14 16:15*
- Fixed world having two origins

## 0.0.10
*2024-06-14 15:49*
- Added mouse cursor
- Added block selecting using the number row
- Changed block placing to be at mouse cursor instead of at foot
- Removed block cycling with Z

## 0.0.9_2
*2024-06-14 15:09*
- Skip rerendering if player is idle

## 0.0.9_1
*2024-06-14 01:23*
- Allow placing blocks at world origin

## 0.0.9
*2024-06-13 13:22*
- Added block placing with X.
- Added block changing with Z.

## 0.0.8_1
*2024-06-13 12:59*
- Changed world generation to not be mirrored

## 0.0.8
*2024-06-13 12:42*
- World is now aligned in blocks
- Use random textures for each block
- Added a bedrock line at world origins
- Added worldRepeat option (defaults to 100)
- Added seed option (defaults to 100)

## 0.0.7_3
*2024-06-13 00:08*
- Use grid layout for world

## 0.0.7_2
*2024-06-12 23:52*
- Fixed crashing when options.txt is malformed

## 0.0.7_1
*2024-06-12 23:08*
- Fixed left and right movement

## 0.0.7
*2024-06-12 22:54*
- Added jumpStrength option (defaults to 0.2)
- Changed default walkSpeed to 0.5, sprintSpeed to 0.8, jumpHeight to 5.0, and gravity to 0.08
- Merged moveSpeed and walkSpeed options into walkSpeed
- Fixed gravity option
- Normalised coordinate plane

## 0.0.6
*2024-06-12 00:18*
- Added vertical mouse look
- Added gamma option (defaults to 4.0)
- Changed default rotation speed to 0.01
- Render horizon with sky texture

## 0.0.5_1
*2024-06-07 01:38*
- Added strafing

## 0.0.5
*2024-06-07 00:39*
- Fixed jumping
- Removed crouching
- Added gravity option (defaults to 0.4)
- Changed default jump height to 0.5
- Use defaults if options are not given in options.txt
- Coordinates are now rounded instead of truncated

## 0.0.4
*2019-06-13 18:29*
- Going to the 32-bit limits now displays "infinity"
- Game now gets screen size
- Game now exits on Escape
- Lowered options version to 4

## 0.0.3_2
*2019-06-11 19:34*
- Added data version (15)
- Added warning when options.txt is outdated

## 0.0.3_1
*2019-06-11 19:16*
- options.txt is now checked every 10 seconds

## 0.0.3
*2019-06-11 18:15*
- Game now gets reads and writes data from %appdata%\.mineo\options.txt

## 0.0.2
*2019-06-09 20:35*
- Added sky
- Added sprinting
- Added up and down movements
- Changed player position
- Doubled render distance
- Removed broken sideways movement
- Added coordinates to gui
- Shifted gui elements to the top left corner
- F3 now toggles gui

## 0.0.1_1
*2019-06-08 20:54*
- Removed ceiling
- Decreased render distance

## 0.0.1
*2019-06-08 20:48*
- Added textures
- Added FPS and version info
- Changed movement to use mouse

## in-060819
*2019-06-08 19:09*

## in-060818
*2019-06-08 18:26*

## in-060816
*2019-06-08 16:11*

## in-060810
*2019-06-08 10:01*

## in-060808
*2019-06-08 08:08*

## in-060620
*2019-06-06 20:30*
