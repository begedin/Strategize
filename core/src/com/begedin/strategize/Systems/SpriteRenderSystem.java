package com.begedin.strategize.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.begedin.strategize.Components.MapPosition;
import com.begedin.strategize.Components.Sprite;
import com.begedin.strategize.Components.SpriteAnimation;
import com.begedin.strategize.Maps.MapTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class SpriteRenderSystem extends EntitySystem {
    @Mapper ComponentMapper<MapPosition> pm;
    @Mapper ComponentMapper<Sprite> sm;
    @Mapper ComponentMapper<SpriteAnimation> sam;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private TextureAtlas atlas;

    //private;
    private List<Entity> sortedEntities;

    @SuppressWarnings("unchecked")
    public SpriteRenderSystem(OrthographicCamera camera) {
        super(Aspect.getAspectForAll(MapPosition.class, Sprite.class));
        this.camera = camera;
    }

    @SuppressWarnings("unchecked")
    public SpriteRenderSystem(OrthographicCamera camera, SpriteBatch batch) {
        super(Aspect.getAspectForAll(MapPosition.class, Sprite.class));
        this.camera = camera;
        this.batch = batch;
    }

    @Override
    protected void initialize() {
        atlas = new TextureAtlas(Gdx.files.internal("graphics/strategize.pack"),Gdx.files.internal("graphics"));
        sortedEntities = new ArrayList<Entity>();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity e : sortedEntities) {
            process(e);
        }
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    protected void process(Entity e) {
        if (pm.has(e)) {
            MapPosition position = pm.getSafe(e);
            Sprite sprite = sm.get(e);

            TextureRegion spriteRegion = sprite.region;
            batch.setColor(sprite.r, sprite.g, sprite.b, sprite.a);

            int width = spriteRegion.getRegionWidth();
            int height = spriteRegion.getRegionHeight();

            sprite.region.setRegion(sprite.x, sprite.y, width, height);

            Vector2 pos = MapTools.world2window(position.x, position.y);

            float originX = spriteRegion.getRegionWidth() / 2f;
            float originY = spriteRegion.getRegionHeight() / 2f;

            batch.draw(spriteRegion, pos.x - originX, pos.y - originY, originX, originY, spriteRegion.getRegionWidth(), spriteRegion.getRegionHeight(), sprite.scaleX, sprite.scaleY, sprite.rotation);
        }
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void inserted(Entity e) {
        Sprite sprite = sm.get(e);
        sortedEntities.add(e);
        TextureRegion reg = atlas.findRegion(sprite.name);
        sprite.region = reg;
        sprite.x = reg.getRegionX();
        sprite.y = reg.getRegionY();
        sprite.width = reg.getRegionWidth();
        sprite.height = reg.getRegionHeight();
        if (sam.has(e)) {
            SpriteAnimation anim = sam.getSafe(e);
            anim.animation = new Animation( anim.frameDuration, atlas.findRegions(sprite.name), anim.playMode);
        }

        Collections.sort(sortedEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                Sprite s1 = sm.get(e1);
                Sprite s2 = sm.get(e2);
                return s1.layer.compareTo(s2.layer);
            }
        });
    }

    @Override
    protected void removed(Entity e) {
        sortedEntities.remove(e);
    }
}
