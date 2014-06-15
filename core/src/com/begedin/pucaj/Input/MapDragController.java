package com.begedin.pucaj.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.begedin.pucaj.Screens.MapScreen;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class MapDragController implements GestureListener {
    float initialScale = 1;

    private OrthographicCamera camera;
    private MapScreen screen;

    public MapDragController(OrthographicCamera camera, MapScreen screen) {
        this.screen = screen;
        this.camera = camera;
    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2) {
        initialScale = camera.zoom;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int amount, int finger) {
        if (amount > 1) {
            screen.cameraMovementSystem.move(screen.activeEntityCell.x, screen.activeEntityCell.y);
            return true;
        }
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
    public boolean pan(float x1, float y1, float x2, float y2) {

        Vector2 delta = new Vector2(-camera.zoom * Gdx.input.getDeltaX(), camera.zoom * Gdx.input.getDeltaY());

        Gdx.app.log("MapDragController", "pan, delta x: " + Float.toString(delta.x) + ", y: " + Float.toString(delta.y) );

        camera.translate(delta);
        return true;
    }

    @Override
    public boolean panStop(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float ratio = initialDistance / distance;
        if ((initialScale * ratio > 0.1) && (initialScale * ratio < 8)){
            camera.zoom = initialScale * ratio;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
        return false;
    }
}
