package com.begedin.strategize.Components;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Nikola Begedin on 29.12.13..
 */
public class SpriteAnimation extends Component {

    public Animation animation;
    public float stateTime;
    public float frameDuration;
    public Animation.PlayMode playMode;

    public TextureRegion getFrame() {
        return animation.getKeyFrame(stateTime);
    }
}
