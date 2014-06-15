package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.begedin.pucaj.Components.MapPosition;
import com.begedin.pucaj.Components.Movable;
import com.begedin.pucaj.Components.Movement;
import com.begedin.pucaj.Components.Sprite;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Pathfinding.Path;
import com.begedin.pucaj.Screens.MapScreen;

/**
 * Created by Nikola Begedin on 31.12.13..
 *
 * Handles movement of entities on the map.
 */
public class MovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Movement> mm;
    @Mapper ComponentMapper<MapPosition> pm;
    @Mapper ComponentMapper<Movable> movem;
    @Mapper ComponentMapper<Sprite> sm;

    GameMap gameMap;
    MapScreen screen;

    @SuppressWarnings("unchecked")
    public MovementSystem(GameMap gameMap, MapScreen screen) {
        super(Aspect.getAspectForAll(Movement.class, MapPosition.class, Movable.class));
        this.gameMap = gameMap;
        this.screen = screen;
    }

    @Override
    protected void inserted(Entity e) {
        Path path = mm.get(e).path;

        // If the path was null (somehow) remove the movable component and get out of here!
        if (path == null) {
            e.removeComponent(mm.get(e));
            e.changedInWorld();
        }

        else {
            // As far as the gameMap is concerned, move the entity there right away
            // (The animation is just for show)
            gameMap.moveEntity(e.getId(), path.getX(0), path.getY(0));

            // Also, we should disable input during this
            screen.startMoving(path.getX(0), path.getY(0));
        }

        // TODO: Here we can also change the NPC's animation to a walking one
    }


    @Override
    protected void process(Entity e) {
        Movement movement = mm.get(e);
        MapPosition pos = pm.get(e);

        // Get the speed with which we move
        float slowness = movem.get(e).slowness;

        // Read the path and get it's length
        Path path = movement.path;
        int size = path.getLength();

        // Calculate what step we are on (e.g. cell_0 to cell_1, cell_1 to cell_2, etc...)
        int step = (int)(movement.elapsedTime/slowness);

        // Check to see if they've reached the end / gone beyond)
        if (size - 2 - step < 0) {
            // At the end of the day, no matter what, make sure the entity ended up where
            // it belonged.
            pos.x = path.getX(0);
            pos.y = path.getY(0);

            // Remove the movement component and let them be on their way
            e.removeComponent(movement);
            e.changedInWorld();
            return;
        }

        // Otherwise we must still be on the way
        // Get the coordinates of cell_i and cell_(i+1)
        int x0 = path.getX(size - 1 - step);
        int y0 = path.getY(size - 1 - step);

        int x1 = path.getX(size - 2 - step);
        int y1 = path.getY(size - 2 - step);

        changeStep(e, x0, y0, x1, y1);

        // Determine how close we are to reaching the next step
        float t = movement.elapsedTime/slowness - step;

        // Set position to be a linear interpolation between these too coordinates
        pos.x = x0 + t * (x1-x0);
        pos.y = y0 + t * (y1-y0);

        // Increase the time animation has been running
        movement.elapsedTime += Gdx.graphics.getDeltaTime();
    }

    @Override
    protected void removed(Entity e) {
        // TODO: Here we can reset the entity's animation to the default one
        // Also, we are done moving, so let the screen know to resume control
        screen.stopMoving();
    }

    private void changeStep(Entity e, int x0, int y0, int x1, int y1) {
        // TODO: Here we can maybe change the animation based on which direction the npc is moving.
        // Call MapTools.getDirectionVector(x0,y0,x1,y1) to see which direction entity is moving.
        Vector2 directionVector = MapTools.getDirectionVector(x0, y0, x1, y1);

        Sprite sprite = sm.get(e);
        sprite.rotation =  directionVector.angle() + 90f;
    }
}
