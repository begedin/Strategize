package com.begedin.pucaj.Rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nikola Begedin on 31.12.13..
 *
 * An abstract renderer class. Each render should own a camera and sprite batch field, both passed through the constructor,
 * as well as begin() and end() methods for rendering.
 */
public abstract class AbstractRenderer {

    protected OrthographicCamera camera;
    protected SpriteBatch batch;

    public AbstractRenderer(OrthographicCamera camera, SpriteBatch batch) {
        this.camera = camera;
        this.batch = batch;
    }

    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    protected void end() {
        batch.end();
        batch.setColor(1f,1f,1f,1f);
    }

}
