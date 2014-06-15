package com.begedin.pucaj.Rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 *
 * Renders highlights for possible fields a selected entity can move to.
 */
public class MapHighlighter extends AbstractRenderer {

    private Texture highlight;
    private float t;
    private float r, g, b, a;

    public MapHighlighter(OrthographicCamera camera, SpriteBatch batch) {
        super(camera,batch);
        highlight = new Texture(Gdx.files.internal("graphics/tile_blank.png"));
        t = 0;
    }

    public void render(Array<Pair> cells) {
        if (cells == null || cells.size < 1) return;

        // Get bottom left and top right coordinates of camera viewport and convert
        // into grid coordinates for the map
        int x0 = MathUtils.floor(camera.frustum.planePoints[0].x / (float)MapTools.SCALE_FACTOR) - 1;
        int y0 = MathUtils.floor(camera.frustum.planePoints[0].y / (float)MapTools.SCALE_FACTOR);
        int x1 = MathUtils.floor(camera.frustum.planePoints[2].x / (float)MapTools.SCALE_FACTOR);
        int y1 = MathUtils.floor(camera.frustum.planePoints[2].y / (float)MapTools.SCALE_FACTOR);


        begin();
        batch.setColor(r,g,b,a/8*(7+MathUtils.cos(8*t)));

        for (Pair cell : cells) {
            if (cell.x < x0 || cell.x > x1 || cell.y < y0 || cell.y > y1) continue;
            Vector2 coords = MapTools.world2window(cell.x, cell.y);
            batch.draw(highlight, coords.x-highlight.getWidth()/2, coords.y-highlight.getHeight()/2);
        }

        t+=Gdx.graphics.getDeltaTime();

        end();
    }

    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
