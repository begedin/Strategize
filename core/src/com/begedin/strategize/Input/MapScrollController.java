package com.begedin.strategize.Input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Nikola on 09.02.14..
 */
public class MapScrollController implements InputProcessor {

    private OrthographicCamera camera;

    public MapScrollController(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
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
    public boolean scrolled(int amount) {
        if ((camera.zoom > 0.2f || amount == 1) && (camera.zoom < 8 || amount == -1)) camera.zoom += amount*0.1;
        return true;
    }
}
