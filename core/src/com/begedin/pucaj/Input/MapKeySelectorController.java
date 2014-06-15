package com.begedin.pucaj.Input;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.begedin.pucaj.Components.MapPosition;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Screens.MapScreen;

/**
 * Created by Nikola on 09.02.14..
 */
public class MapKeySelectorController implements InputProcessor {

    private OrthographicCamera camera;
    private World world;
    private GameMap gameMap;

    private InputManager inputManager;

    // We need a copy of the screen implementing this controller (which has a copy of
    // the Game delegating to it) so we can change screens based on users making selections
    private MapScreen screen;

    public MapKeySelectorController(OrthographicCamera camera, World world, GameMap gameMap, MapScreen screen, InputManager inputManager) {
        this.camera = camera;
        this.world = world;
        this.gameMap = gameMap;
        this.screen = screen;
        this.inputManager = inputManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        Entity e = world.getEntity(screen.cursor);
        MapPosition mp = e.getComponent(MapPosition.class);
        if (keycode == 19) mp.y+=1; // UP
        else if (keycode == 20) mp.y-=1; // DOWN
        else if (keycode == 21) mp.x-=1; // LEFT
        else if (keycode == 22) mp.x+=1; // RIGHT

        else if (keycode == 62) { // SPACE
            screen.cameraMovementSystem.move(screen.activeEntityCell.x, screen.activeEntityCell.y);
        }

        return true;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i2) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
