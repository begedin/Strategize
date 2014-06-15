package com.begedin.strategize.Rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.begedin.strategize.Maps.MapTools;

/**
 * Created by Nikola Begedin on 30.12.13..
 *
 * A renderer for the map. Renders all the map tiles.
 */


public class MapRenderer extends AbstractRenderer {

    private TextureAtlas atlas;
    private Array<AtlasRegion> textures;
    private int[][] map;

    public MapRenderer(OrthographicCamera camera, SpriteBatch batch, int[][] map) {
        super(camera, batch);
        this.map = map;

        atlas = new TextureAtlas(Gdx.files.internal("graphics/tiles.pack"),Gdx.files.internal("graphics"));
        textures = atlas.getRegions();
    }

    public void render() {
        begin();

        TextureRegion reg;

        // Get bottom left and top right coordinates of camera viewport and convert
        // into grid coordinates for the map
        int x0 = MathUtils.floor(camera.frustum.planePoints[0].x / (float)MapTools.SCALE_FACTOR) - 1;
        int y0 = MathUtils.floor(camera.frustum.planePoints[0].y / (float)MapTools.SCALE_FACTOR) - 1;
        int x1 = MathUtils.floor(camera.frustum.planePoints[2].x / (float)MapTools.SCALE_FACTOR) + 1;
        int y1 = MathUtils.floor(camera.frustum.planePoints[2].y / (float)MapTools.SCALE_FACTOR) + 1;

        // Restrict the grid coordinates to realistic values
        if (x0 < 0) x0 = 0;
        if (x1 > map.length) x1 = map.length;
        if (y0 < 0) y0 = 0;
        if (y1 > map[0].length) y1 = map[0].length;

        // Loop over everything in the window to draw
        for (int row = y0; row < y1; row++) {
            for (int col = x0; col < x1; col++) {
                reg = textures.get(map[col][row]);
                Vector2 position = MapTools.world2window(col,row);
                batch.draw(reg, position.x-reg.getRegionWidth()/2, position.y-reg.getRegionHeight()/2);
            }
        }

        end();
    }

}
