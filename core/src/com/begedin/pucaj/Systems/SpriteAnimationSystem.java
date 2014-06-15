package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.begedin.pucaj.Components.Sprite;
import com.begedin.pucaj.Components.SpriteAnimation;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class SpriteAnimationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Sprite> sm;
    @Mapper ComponentMapper<SpriteAnimation> sam;

    @SuppressWarnings("unchecked")
    public SpriteAnimationSystem() {
        super(Aspect.getAspectForAll(Sprite.class, SpriteAnimation.class));
    }

    @Override
    protected void process(Entity e) {

        Sprite sprite = sm.get(e);
        SpriteAnimation anim = sam.get(e);

        anim.stateTime += world.getDelta();

        TextureRegion region = anim.getFrame();
        sprite.x = region.getRegionX();
        sprite.y = region.getRegionY();
        sprite.width = region.getRegionWidth();
        sprite.height = region.getRegionHeight();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
}
