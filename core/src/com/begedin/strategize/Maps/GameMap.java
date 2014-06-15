package com.begedin.strategize.Maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.begedin.strategize.Pathfinding.AStarPathFinder;
import com.begedin.strategize.Utils.Pair;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class GameMap {
    public int[][] map;
    private int[][] entityByCoord;
    private ObjectMap<Integer,Pair> coordByEntity;
    public int width, height;
    public Pixmap pixmap;
    public Texture texture;
    public AStarPathFinder pathFinder;

    public GameMap() {
        MapGenerator mg = new MapGenerator();
        map = mg.getDiamondSquare();
        width = map.length;
        height = map[0].length;

        entityByCoord = new int[width][height];
        coordByEntity = new ObjectMap<Integer,Pair>();

        int pixmapWidth = (int) Math.pow(2, (int)(Math.floor(Math.log10((double)width)/Math.log10(2))));
        int pixmapHeight = (int) Math.pow(2, (int)(Math.floor(Math.log10((double)height)/Math.log10(2))));

        pixmap = new Pixmap(pixmapWidth,pixmapHeight,Pixmap.Format.RGBA8888);

        for (int i=0; i<width;i++) {
            for (int j=0;j<height;j++) {
                int realj = pixmapHeight - (pixmapHeight - height) - j - 1;
                pixmap.setColor(getColor(map[i][realj]));
                pixmap.drawPixel(i, j);

                entityByCoord[i][j] = -1;

            }
        }

        texture = new Texture(pixmap);
        pixmap.dispose();

        pathFinder = new AStarPathFinder(this, 100);

    }

    private Color getColor(int color) {     //  r    g    b
        if (color == 0)      return myColor(34  ,53  ,230);
        else if (color == 1) return myColor(105 ,179 ,239);
        else if (color == 2) return myColor(216 ,209 ,129);
        else if (color == 3) return myColor(183 ,245 ,99);
        else if (color == 4) return myColor(109 ,194 ,46);
        else if (color == 5) return myColor(87  ,155 ,36);
        else if (color == 6) return myColor(156 ,114 ,35);
        else if (color == 7) return myColor(135 ,48  ,5);
        else return new Color(1,1,1,1);
    }

    private static Color myColor(int r, int g, int b) {
        return new Color(r/255f, g/255f, b/255f,1);
    }

    public int getEntityAt(int x, int y) {
        if (x < 0 || x > entityByCoord.length - 1 || y < 0 || y > entityByCoord[0].length - 1) return -1;
        return entityByCoord[x][y];
    }

    public Pair getCoordinatesFor(int entityId) {
        if (coordByEntity.containsKey(entityId)) return coordByEntity.get(entityId);
        return null;
    }

    public boolean cellOccupied(int x, int y) {
        return (entityByCoord[x][y] > -1);
    }

    public void addEntity(int id, int x, int y) {
        entityByCoord[x][y] = id;
        coordByEntity.put(id, new Pair(x,y));
    }

    public void moveEntity(int id, int x, int y) {
        Pair old = coordByEntity.put(id, new Pair(x,y));
        entityByCoord[old.x][old.y] = -1;
        entityByCoord[x][y] = id;
    }

    public void removeEntity(int id) {
        Pair old = coordByEntity.get(id);
        entityByCoord[old.x][old.y] = -1;
        coordByEntity.remove(id);
    }

    public boolean containsEntities(Array<Pair> cells) {
        for (Pair cell : cells) {
            if (cellOccupied(cell.x,cell.y)) return true;
        }
        return false;
    }

    public boolean containsEntitiesOtherThan(Array<Pair> cells, int entityId) {
        for (Pair cell : cells) {
            int e = getEntityAt(cell.x,cell.y);
            if (e > -1 && e != entityId) return true;
        }

        return false;
    }
}
