package com.begedin.pucaj.Maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Utils.Pair;
import com.begedin.pucaj.Utils.CustomMath;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class MapTools {

    public static final int SCALE_FACTOR = 64;

    // returns an array of integer coordinates (custom Pair class) containing all neighbours for a specific coordinate
    public static Array<Pair> getNeighbors(int x0, int y0, int n) {
        Array<Pair> coordinates = new Array<Pair>();


        for (int x = x0 - n; x <= x0 + n; x++){
            for (int y = y0 - (n - Math.abs(x0 - x)); y <= y0 + (n - Math.abs(x0-x)); y++){
                if ((x > 0) && (y > 0) && (x < width() - 1) && (y < height() - 1)) coordinates.add(new Pair(x, y));
            }
        }

        return coordinates;
    }

    public static Array<Pair> getNeighbors(int x, int y) {
        return getNeighbors(x,y,1);
    }

    // a city-block distance between two integer points
    public static int distance(int x1, int y1, int x2, int y2){
        return Math.abs(x2-x1) + Math.abs(y2-y1);
    }

    // transforms window coordinates to map grid location
    public static Pair window2world(float x, float y, OrthographicCamera camera) {
        Vector3 pos = new Vector3(x,y,0);
        camera.unproject(pos);
        int posx = (int)(pos.x / (float)SCALE_FACTOR);
        int posy = (int)(pos.y / (float)SCALE_FACTOR);
        return new Pair(posx,posy);
    }

    public static Vector2 world2window(float x, float y) {
        int x0 = (int)x;
        float dx = x - x0;
        int y0 = (int)y;
        float dy = y - y0;

        float posx =  (x0 + 0.5f) * SCALE_FACTOR + dx * SCALE_FACTOR;
        float posy =  (y0 + 0.5f) * SCALE_FACTOR + dy * SCALE_FACTOR;

        return new Vector2(posx, posy);
    }

    public static Pair libgdx2world(float x, float y) {
        Vector3 pos = new Vector3(x,y,0);
        int posx = (int)(pos.x / (float)SCALE_FACTOR);
        int posy = (int)(pos.y / (float)SCALE_FACTOR);
        return new Pair(posx,posy);
    }

    public static int width() {
        int power = CustomMath.pow(2, MapGenerator.n);
        return MapGenerator.wmult*power + 1;
    }

    public static int height() {
        int power = CustomMath.pow(2,MapGenerator.n);
        return MapGenerator.hmult*power + 1;
    }


    public static Vector2 getDirectionVector(int x1, int y1, int x2, int y2) {
        Vector2 cell1 = world2window(x1, y1);
        Vector2 cell2 = world2window(x2, y2);
        return new Vector2(cell2.x-cell1.x, cell2.y-cell1.y);
    }

}
