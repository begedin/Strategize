package com.begedin.pucaj.Maps;

/**
 * Created by Nikola Begedin on 30.12.13..
 *
 * Uses the modified diamond square alghorithm to generate a map
 */
public class MapGenerator {
    public static final int n = 4;
    public static final int wmult = 2;
    public static final int hmult = 2;

    public MapGenerator() {
    }

    public int[][] getDiamondSquare() {
        ModifiedDiamondSquare md = new ModifiedDiamondSquare();
        return md.getMap(n, wmult, hmult);
    }
}
