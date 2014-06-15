package com.begedin.strategize.Core;


import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.begedin.strategize.Screens.MapScreen;
import com.begedin.strategize.Systems.ColorAnimationSystem;
import com.begedin.strategize.Systems.ExpirationSystem;
import com.begedin.strategize.Systems.ScaleAnimationSystem;
import com.begedin.strategize.Systems.SpriteAnimationSystem;

public class StrategizeGame extends Game {

    public int WINDOW_WIDTH;
    public int WINDOW_HEIGHT;

    public World world;
    private SpriteBatch batch;

    public StrategizeGame(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }

    public void create() {

        world = new World();
        batch = new SpriteBatch();

        world.setSystem(new SpriteAnimationSystem());
        world.setSystem(new ScaleAnimationSystem());
        world.setSystem(new ExpirationSystem());
        world.setSystem(new ColorAnimationSystem());
        world.initialize();

        setScreen(new MapScreen(this, batch, world));
    }
}
