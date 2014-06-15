package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.begedin.pucaj.Components.ColorAnimation;
import com.begedin.pucaj.Components.Sprite;


public class ColorAnimationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<ColorAnimation> cam;
    @Mapper ComponentMapper<Sprite> sm;

    @SuppressWarnings("unchecked")
    public ColorAnimationSystem() {
        super(Aspect.getAspectForAll(ColorAnimation.class, Sprite.class));
    }

    @Override
    protected void process(Entity e) {
        ColorAnimation c = cam.get(e);
        Sprite sprite = sm.get(e);

        if(c.alphaAnimate) {
            sprite.a += c.alphaSpeed * world.delta;

            if(sprite.a > c.alphaMax) {
                sprite.a = c.alphaMax;
                if(c.repeat) {
                    c.alphaSpeed = -c.alphaSpeed;
                } else {
                    c.alphaAnimate = false;
                }
            }

            else if(sprite.a < c.alphaMin) {
                sprite.a = c.alphaMin;
                if(c.repeat) {
                    c.alphaSpeed = -c.alphaSpeed;
                } else {
                    c.alphaAnimate = false;
                }
            }
        }

        if(c.redAnimate) {
            sprite.r += c.redSpeed * world.delta;

            if(sprite.r > c.redMax) {
                sprite.r = c.redMax;
                if(c.repeat) {
                    c.redSpeed = -c.redSpeed;
                } else {
                    c.redAnimate = false;
                }
            }

            else if(sprite.r < c.redMin) {
                sprite.r = c.redMin;
                if(c.repeat) {
                    c.redSpeed = -c.redSpeed;
                } else {
                    c.redAnimate = false;
                }
            }
        }

        if(c.greenAnimate) {
            sprite.g += c.greenSpeed * world.delta;

            if(sprite.g > c.greenMax) {
                sprite.g = c.greenMax;
                if(c.repeat) {
                    c.greenSpeed = -c.greenSpeed;
                } else {
                    c.greenAnimate = false;
                }
            }

            else if(sprite.g < c.greenMin) {
                sprite.g = c.greenMin;
                if(c.repeat) {
                    c.greenSpeed = -c.greenSpeed;
                } else {
                    c.greenAnimate = false;
                }
            }
        }

        if(c.blueAnimate) {
            sprite.b += c.blueSpeed * world.delta;

            if(sprite.b > c.blueMax) {
                sprite.b = c.blueMax;
                if(c.repeat) {
                    c.blueSpeed = -c.blueSpeed;
                } else {
                    c.blueAnimate = false;
                }
            }

            else if(sprite.b < c.blueMin) {
                sprite.b = c.blueMin;
                if(c.repeat) {
                    c.blueSpeed = -c.blueSpeed;
                } else {
                    c.blueAnimate = false;
                }
            }
        }


    }
}