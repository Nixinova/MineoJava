# Changelog

## Next
- Made world rendering independent of world size
  - Increases performance significantly for larger world sizes
- Changed world saving to store only changed blocks
- Fixed crash caused by trees trying to generate outside the world.

## 0.2_1
*2025-07-31 21:13*
- Include mouse angle in save file
- Fixed falling into void when digging straight down
- Fixed world save and load
- Fixed block edge flickering

## 0.2
*2025-07-30 22:03*
- Added jagged terrain
- Added trees
- Added new blocks: log and leaf
- Added different textures per block face (used for log and grass)
- Fixed orientation of block side textures

## 0.1.4
*2025-07-28 23:36*
- New save file format (v2.0) using run-length encoding
  - Saves roughly 500x file size and makes saving and loading much faster
- Fixed input not working after losing focus

## 0.1.3_1
*2025-03-02 17:52*
- Include game version in save file
- Fix crashing on world size change

## 0.1.3
*2025-03-02 16:48*
- Added texture mipmapping

## 0.1.2_1
*2025-03-02 15:37*
- Fixed player movement breaking when at build height
- Fixed blocks being unselectable when at build height

## 0.1.2
*2025-03-02 14:21*
- Changed hotbar slots to also be selectable via mouse scrolling

## 0.1.1
*2025-03-02 14:02*
- Changed world saving to save all blocks in world

## 0.1_1
*2025-03-02 01:56*
- Changed default worldSize to 100
- Changed default groundLevel to 16
- Changed default buildHeight to 32
- Changed default jump height to 1.6

## 0.1
*2025-03-02 01:33*
- Added main menu
- Added level saving and loading
- Added save file format
- Changed sky to no longer render when player is underground
- Changed font glyphs
- Changed block rendering to render far away blocks monochromatically

## 0.0.21_1
*2024-08-17 22:13*
- Added HUD toggling with F2
- Added reach option (defaults to 5 blocks)
- Added block selection outline
- Limited block interactions to only what is within the player's reach
- Changed color of cursor and hotbar selection outline
- Changed fidelity of selected block raycaster
- Changed information line label "Hovered" to "Selected"

## 0.0.21
*2024-08-15 23:19*
- Added sky with gradient
- Added horizontal collision box to blocks
- Added collision boundary box to the player
- Changed renderer to only render blocks within view of the player
- Changed player spawn point to centre of world
- Changed player height to 1.6 blocks
- Changed default jumpHeight to 1.5
- Removed ability to place blocks inside the player's body
- Fixed jumping not pertaining to the specified jumpHeight

## 0.0.20_2
*2024-08-15 01:40*
- Added options groundLevel (defaults to 10) and buildHeight (defaults to 18)
- Changed distance fog effect to applied nonlinearly
- Changed default renderDistance to 20 blocks
- Fixed raycast culling

## 0.0.20_1
*2024-08-08 21:59*
- Changed display of game information lines
  - Changed information lines to be hidden by default
  - Merged "Block" and "Camera" lines into one "Position" line
  - Changed name of "Angle" line to "Rotation"
  - Changed "Hovered" line to only appear when looking at a block
- Fixed a rounding issue causing the world boundary to be offset by 1 block
- Fixed two sides of the world not rendering
- Fixed being able to place blocks outside of the world
- Fixed player not being able to fall into the void from inside the world

## 0.0.20
*2024-08-08 19:52*
- Added proper block texture rendering
- Changed top world layer to be flat
- Changed default renderDistance to 15 blocks for performance reasons

## 0.0.19
*2024-07-25 20:17*
- Blocks are now placed adjacent to the hovered block face
- Block textures are now applied to each face separately
- Block texels are now larger
- Fixed orientation of block textures

## 0.0.18_1
*2024-07-25 16:45*
- Collision is now based per-block instead of from the lowest ground in a column
- Changed player height to 1.8 blocks
- Removed "floor" debug text line

## 0.0.18
*2024-07-25 00:39*
- Added block face culling
- World now only generates along the positive axes
- World now abides by worldSize option again
- World now generates in layers again
  - Top layer is now randomly offset by 1 or 2 blocks
  - Top layer of world is always grass
  - The three layers below the top layer are dirt
  - The rest of the layers down to Y=1 are stone
  - The bottom layer remains bedrock

## 0.0.17_3
*2024-07-24 23:03*
- Fixed movement smoothening
- Improved rendering performance

## 0.0.17_2
*2024-07-21 22:28*
- Changed rendering to make closer texels overwrite farther texels

## 0.0.17_1
*2024-07-21 22:11*
- Changed rendering culling
- Tweaked world generation

## 0.0.17
*2024-07-21 21:47*
- Re-added distance fog effect
- Blocks are now only rendered within the specified renderDistance
- Changed default gamma to 1.0
- Tweaked raycasting start position

## 0.0.16_1
*2024-07-19 13:29*
- Fixed incorrect hovered block calculation
- Changed "lowest ground" debug text line to "floor"
- Removed being able to place blocks by pressing Z

## 0.0.16
*2024-07-19 01:22*
- Blocks now render in 3D
- Added raycast rendering
- Added cooldown to mouse button actions
- Added "lowest ground", "camera", "angle", and "hovered" debug text lines
- Changed world generation to a test level
- Changed font size of game version to 1.25x
- Blocks can now also be placed by pressing Z

## 0.0.15
*2024-07-13 22:21*

### 0.0.14_1
*2024-07-10 20:30*
- Left click now breaks blocks
- Right click now places blocks on top of another
- Top layer of world is now jagged

## 0.0.14
*2024-07-10 20:03*
- World is now properly 3D
- Game now renders in 2.5D
- Player can now look fully up and down
- World size is now locked to 10 blocks in radius for performance reasons
- Removed distance fog effect

## 0.0.13_5
*2024-07-05 06:20*
- Reverted FPS measuring change
- Changed default sensitivity back to 0.005
- Fixed block duplication at origin
- Fixed textures being mirrored along world origin

## 0.0.13_4
*2024-07-04 05:44*
- Changed coordinate display to show current block to 1 decimal place
- Changed Y coordinate origin
- Fixed being able to Shift while in the void

## 0.0.13_3
*2024-07-04 04:59*
- Changed how FPS is measured
- Changed default sensitivity to 0.003

## 0.0.13_2
*2024-07-03 04:55*
- Made game start in windowed fullscreen
- Fixed position of hotbar

## 0.0.13_1
*2024-07-03 04:38*
- Changed world layer falling to use gravity value
- Fixed double-jumping by implementing a key cooldown
- Fixed Y-position not updating
- Fixed world layer falling effect

## 0.0.13
*2024-06-30 03:34*
- Added a hotbar, showing all available blocks, with the selected block outlined
- Added a preview of the selected block, displayed in top right
- Changed cursor to be white

## 0.0.12_6
*2024-06-30 02:20*
- Changed renderDistance and jumpHeight options to use blocks as a unit
- Changed gamma option scale
- Changed default renderDistance to 50, jumpHeight to 1.0, gamma to 0.8, and gravity to 0.2
- Removed jumpStrength and texturesFolder options
- Upgraded options version to 1.1

## 0.0.12_5
*2024-06-30 01:58*
- Changed default gamma to 6.0
- Changed default worldSize to 40
- Changed position of options warning message

## 0.0.12_4
*2024-06-28 15:35*
- Player is now put on the ground of the world layer when Shift is unpressed

## 0.0.12_3
*2024-06-23 16:02*
- Changed internal coordinate system
- Changed layer falling effect

## 0.0.12_2
*2024-06-22 22:43*
- Changed sprint key from Shift to Control
- Player now only falls through world layers when holding Shift
- Fixed Y-position not updating

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
