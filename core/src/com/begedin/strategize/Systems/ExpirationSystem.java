package com.begedin.strategize.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.begedin.strategize.Components.Expiration;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class ExpirationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Expiration> em;

    public ExpirationSystem() {
        super(Aspect.getAspectForAll(Expiration.class));
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void process(Entity e) {
        Expiration exp = em.get(e);
        exp.delay -= world.getDelta();
        if (exp.delay <= 0) {
            e.deleteFromWorld();
        }
    }
}
