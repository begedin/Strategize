package com.begedin.strategize.Systems;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class HudRenderSystem extends VoidEntitySystem {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;

    public HudRenderSystem(OrthographicCamera camera, SpriteBatch batch) {
        this.camera = camera;
        this.batch = batch;
    }

    @Override
    protected void initialize() {

        Texture fontTexture = new Texture(Gdx.files.internal("fonts/digital7.png"));

        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.MipMapLinearLinear);
        TextureRegion fontRegion = new TextureRegion(fontTexture);
        font = new BitmapFont(Gdx.files.internal("fonts/digital7.fnt"), fontRegion, false);
        font.setUseIntegerPositions(false);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    @Override
    protected void processSystem() {
        batch.setColor(1, 1, 1, 1);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), camera.viewportWidth-100, camera.viewportHeight - 20);
        //font.draw(batch, "Active entities: " + world.getEntityManager().getActiveEntityCount(), 20, camera.viewportHeight - 40);
        //font.draw(batch, "Total created: " + world.getEntityManager().getTotalCreated(), 20, camera.viewportHeight - 60);
        //font.draw(batch, "Total deleted: " + world.getEntityManager().getTotalDeleted(), 20, camera.viewportHeight - 80);
    }

    @Override
    protected void end() {
        batch.end();
    }
}
