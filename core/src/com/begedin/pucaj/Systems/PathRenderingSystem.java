package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.begedin.pucaj.Components.Movement;
import com.begedin.pucaj.Maps.MapTools;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class PathRenderingSystem extends EntitySystem {

    @Mapper ComponentMapper<Movement> mm;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture highlight;

    @SuppressWarnings("unchecked")
    public PathRenderingSystem(OrthographicCamera camera) {
        super(Aspect.getAspectForAll(Movement.class));
        this.camera = camera;
    }

    @SuppressWarnings("unchecked")
    public PathRenderingSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Aspect.getAspectForAll(Movement.class));
        this.camera = camera;
        this.batch = batch;
    }

    @Override
    protected void initialize() {
        highlight = new Texture(Gdx.files.internal("graphics/path_highlight.png"));
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (int i=0; i < entities.size(); i++) {
            process(entities.get(i));
        }
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    private void process(Entity e) {
        Movement move = mm.get(e);
        if (move.path != null) {
            for (int i=0; i < move.path.getLength(); i++) {
                Vector2 coords = MapTools.world2window(move.path.getX(i), move.path.getY(i));
                batch.draw(highlight, coords.x-highlight.getWidth()/2, coords.y-highlight.getHeight()/2);
            }
        }
    }

    @Override
    protected void end() {
        batch.end();
    }
}
