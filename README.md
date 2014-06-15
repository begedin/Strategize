Strategize
=====

Experimentations with Android Development Studio and libGDX

General
=======

This project serves for me to learn how to use Android Development Studio and libGDX by making a simple game. What this game will eventually become, I have no idea.


Basic idea
==========

A turn based strategy rpg with tanks. Grid based movements. Possible usage of procedural generation for some, if not most of the content.

Things we have right now
=========================

The game architecture is an ECS (entity-component-system)

Random map generation based on a modified diamond-square method. Custom map size and "aspect ratio". 

Pathfinding and basic AI.

Multiple teams do battle on a turn-based map using placeholder stats and abilities.

Support for touch screen gestures and mouse control.

Background asset loading.

Using a screen system to switch between major game states. For now, there's a loading screen and a basic map screen where the pregenerated battle takes place.


Short term goals
================

* Refactoring and restructuring. Some of the code is tightly coupled and needs to be simplified.
* Asset production, especially graphics.
* Sound

Long term goals
===============

* Figure out the actual game mechanics
* Lay out skills, abilities, plot, feature goals.
