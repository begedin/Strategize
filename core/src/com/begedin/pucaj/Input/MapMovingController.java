package com.begedin.pucaj.Input;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.begedin.pucaj.Components.MapPosition;
import com.begedin.pucaj.Components.Movable;
import com.begedin.pucaj.Components.Movement;
import com.begedin.pucaj.Components.Sprite;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Screens.MapScreen;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 *
 * Input processor for handling movement on the map screen
 */
public class MapMovingController implements GestureListener {
    private OrthographicCamera camera;
    private World world;
    private GameMap gameMap;
    private MapScreen screen;
    private InputManager inputManager;

    public MapMovingController(OrthographicCamera camera, World world, GameMap gameMap, MapScreen screen, InputManager inputManager) {
        this.camera = camera;
        this.world = world;
        this.gameMap = gameMap;
        this.screen = screen;
        this.inputManager = inputManager;

    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean tap(float v, float v2, int i, int i2) {
        final Pair coords = MapTools.window2world(Gdx.input.getX(), Gdx.input.getY(), camera);

        // Did they click within the movable range?
        if (screen.highlightedCells.contains(coords, false)) {
            Entity e = world.getEntity(screen.activeEntity);
            Sprite sprite = e.getComponent(Sprite.class);
            // Put a "ghost" entity there
            final Entity ghost = world.createEntity();
            Sprite sprite_ghost = new Sprite(sprite.name);
            sprite_ghost.a = 0.5f;
            sprite_ghost.r = 1;
            sprite_ghost.g = 1;
            sprite_ghost.b = 1;
            sprite_ghost.scaleX = sprite.scaleX;
            sprite_ghost.scaleY = sprite.scaleY;
            ghost.addComponent(sprite_ghost);
            MapPosition pos = new MapPosition(coords.x, coords.y);
            ghost.addComponent(pos);
            ghost.addToWorld();

            // Build a confirm dialog box
            float w = 200f;
            float h = 100f;
            float x = (inputManager.stage.getWidth() - w)/2f;
            float y = (inputManager.stage.getHeight() - h)/2f;

            // YES button
            ChangeListener confirm = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    // Add the Movement component to this entity
                    Entity e = world.getEntity(screen.activeEntity);
                    Movable movable = e.getComponent(Movable.class);
                    Pair p = screen.activeEntityCell;
                    e.addComponent(new Movement(gameMap.pathFinder.findPath(p.x, p.y, coords.x, coords.y, movable, false)));
                    e.changedInWorld();

                    // Update this entity's location for the screen
                    screen.activeEntityCell = coords;

                    // Tell the screen that the entity has already moved this turn
                    screen.moved = true;

                    // Clear the stage
                    inputManager.stage.clear();

                    // Clear the ghost
                    ghost.deleteFromWorld();
                }
            };

            // NO button
            ChangeListener decline = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Process moving
                    inputManager.menuProcessor.move();
                    // Clear the ghost
                    ghost.deleteFromWorld();
                }
            };

            // Build the box
            inputManager.menuBuilder.buildDialog("", "Confirm move?", x, y, w, h,
                    inputManager.menuBuilder.getTextButton("Yes", confirm),
                    inputManager.menuBuilder.getTextButton("No", decline));
        }

        // Wherever they clicked, they are now done with the "moving" aspect of things
        inputManager.menuBuilder.setMenusVisible(true);
        screen.highlightedCells.clear();
        screen.renderHighlighter = false;
        inputManager.selectedEntity = -1;
        inputManager.disableMoving();

        return true;
    }

    @Override
    public boolean longPress(float v, float v2) {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i) {
        return false;
    }

    @Override
    public boolean pan(float v, float v2, float v3, float v4) {
        return false;
    }

    @Override
    public boolean panStop(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean zoom(float v, float v2) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
        return false;
    }
}