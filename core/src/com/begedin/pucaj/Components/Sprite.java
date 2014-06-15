package com.begedin.pucaj.Components;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
    A single sprite for a drawable component
 */
public class Sprite extends Component {

    public enum Layer {
        DEFAULT,
        BACKGROUND,
        ACTORS_1,
        ACTORS_2,
        ACTORS_3,
        PARTICLES;

        public int getLayerId() {
            return ordinal();
        }
    }

    public Sprite(String name, Layer layer) {
        this.name = name;
        this.layer = layer;
    }

    public Sprite(String name) {
        this(name, Layer.DEFAULT);
    }

    public Sprite() {
        this("default",Layer.DEFAULT);
    }

    public String name;
    public TextureRegion region;

    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;
    public float scaleX = 1;
    public float scaleY = 1;
    public float rotation;

    public Layer layer = Layer.DEFAULT;

    public int x, y, width, height;
}
