package com.begedin.pucaj.Core;


import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.begedin.pucaj.Screens.MapScreen;
import com.begedin.pucaj.Systems.ColorAnimationSystem;
import com.begedin.pucaj.Systems.ExpirationSystem;
import com.begedin.pucaj.Systems.ScaleAnimationSystem;
import com.begedin.pucaj.Systems.SpriteAnimationSystem;

public class PucajGame extends Game {

    public int WINDOW_WIDTH;
    public int WINDOW_HEIGHT;

    public World world;
    private SpriteBatch batch;

    public PucajGame(int width, int height) {
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
