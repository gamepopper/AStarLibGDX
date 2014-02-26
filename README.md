AStarLibGDX
============

A-Star Pathfinding Example done in LibGDX. Pathfinding stuff is in PathFinder and GridNode classes, rendering and input is in GameState class. Made on Sunday 23rd February 2014 as pretty much a challenge to see if I could get one made, not perfect it works.

How to use it
==============

This was developed in Eclipse (Kepler build), so it'll most likely work in there. It's built in LibGDX 0.9.9, therefore you need the gdx jar files (like gdx-backend-lwjgl-natives.jar and gdx-natives.jar).

How to play it
==============

If you build a runnable jar or run it in your favourite Java based IDE, you left click to place down unpassable tiles (black tiles that the path finder ignores), and clicking these tiles will remove them.

If you hold shift and left/right click, you'll create a start/end node respectively. Once a start and end node are created, you press space to generate a path.

If you want to change the size of the tiles, go to where the PathFinder class constructor is called and change the last number.
