package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.begedin.pucaj.Components.FadingMessage;
import com.begedin.pucaj.Components.MapPosition;
import com.begedin.pucaj.Maps.MapTools;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class FadingMessageRenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<MapPosition> mpm;
    @Mapper ComponentMapper<FadingMessage> fmm;

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;


    @SuppressWarnings("unchecked")
    public FadingMessageRenderSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Aspect.getAspectForAll(MapPosition.class, FadingMessage.class));
        this.batch = batch;
        this.camera = camera;
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
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    protected void process(Entity e) {
        MapPosition position = mpm.get(e);
        FadingMessage message = fmm.get(e);

        Vector2 drawPosition = MapTools.world2window(position.x, position.y);
        float posX = drawPosition.x - message.label.length() * font.getSpaceWidth() / 2;
        float posY = drawPosition.y;

        font.setColor(1, 1, 1, 1 - message.currentTime / message.duration);
        font.draw(batch, message.label, posX, posY);

        position.x += message.vx * world.getDelta();
        position.y += message.vy * world.getDelta();
        message.currentTime += world.getDelta();

        if (message.currentTime >= message.duration) e.deleteFromWorld();
    }

    @Override
    protected void end() {
        batch.end();
    }
}
