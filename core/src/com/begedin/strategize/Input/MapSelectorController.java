package com.begedin.strategize.Input;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.begedin.strategize.Components.Movable;
import com.begedin.strategize.Core.EntityFactory;
import com.begedin.strategize.Maps.GameMap;
import com.begedin.strategize.Maps.MapTools;
import com.begedin.strategize.Screens.MapScreen;
import com.begedin.strategize.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 *
 * Input processor for handling selection of entities on the map screen
 */
public class MapSelectorController implements GestureListener {

    private OrthographicCamera camera;
    private World world;
    private GameMap gameMap;


    private InputManager inputManager;

    // We need a copy of the screen implementing this controller (which has a copy of
    // the Game delegating to it) so we can change screens based on users making selections
    private MapScreen screen;

    public MapSelectorController(OrthographicCamera camera, World world, GameMap gameMap, MapScreen screen, InputManager inputManager) {
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
        // Get the coordinates they clicked on
        Vector3 mousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);

        Pair coords = MapTools.window2world(mousePosition.x, mousePosition.y, camera);

        // Check the entityID of the cell they click on
        int entityId = gameMap.getEntityAt(coords.x, coords.y);

        if (entityId > -1) {
            // Now select the current entity
            inputManager.selectedEntity = entityId;
            EntityFactory.createClick(world, coords.x, coords.y, 0.1f, 4f).addToWorld();

            if (entityId == screen.activeEntity) {
                // Put up a menu for the selected entity
                inputManager.stage.clear();
                inputManager.menuBuilder.buildTurnMenu(world.getEntity(entityId));

                // Make sure we drop any of the highlighted cells
                screen.renderMovementRange = false;
                screen.renderAttackRange = false;
                screen.highlightedCells = null;

                return true;
            }
            else {
                Entity e = world.getEntity(entityId);
                inputManager.stage.clear();
                inputManager.menuBuilder.buildStatsMenu(e);

                Movable movable = e.getComponent(Movable.class);
                if (movable != null) {
                    screen.highlightedCells = gameMap.pathFinder.getReachableCells(coords.x, coords.y, movable);
                    screen.highlightMovementRange();
                }
            }
        }

        // If they didn't click on someone, we didn't process it
        return false;
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
