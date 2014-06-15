package com.begedin.strategize.Maps;

import com.badlogic.gdx.math.MathUtils;
import com.begedin.strategize.Utils.CustomMath;

// a modified diamond square algorithm used for generating a level map
// see http://en.wikipedia.org/wiki/Diamond-square_algorithm
// the idea is to create a grid of values, which then translate to specific terrain types based on tresholds
public class ModifiedDiamondSquare {
    public float deepWaterThreshold,
            shallowWaterThreshold,
            beachTreshold,
            grasslandThreshold,
            forestThreshold,
            hillsThreshold;

    public int n;
    public int wmult, hmult;

    public float smoothness;

    public ModifiedDiamondSquare() {

        // the thresholds which determine cutoffs for different terrain types
        deepWaterThreshold = 0.2f;
        shallowWaterThreshold = 0.4f;
        beachTreshold = 0.48f;
        grasslandThreshold = 0.62f;
        forestThreshold = 0.7f;
        hillsThreshold = 0.76f;

        // Smoothness controls how smooth the resultant terrain is.  Higher = more smooth
        smoothness = 2f;
    }

    /*
    * generates a map based on values of instanced propertie
    * @param n determines the basic size factor
    * @param wmult determines final width --> 2 to the power of n times wmult + 1
    * @param hmult determines final height --> 2 to the power of n times hmult + 1
    * @return generated 2d integer array of terrain types
    */
    public int[][] getMap(int n, int wmult, int hmult) {

        // get the dimensions of the map
        int power = CustomMath.pow(2, n);
        int width = wmult*power + 1;
        int height = hmult*power + 1;

        // initialize arrays to hold values
        float[][] map = new float[width][height];
        int[][] returnMap = new int[width][height];


        // initial step increment - this goes down in each iteration
        int step = power/2;
        float sum;
        int count;

        // h determines the fineness of the scale it is working on.  After every step, h
        // is decreased by a factor of "smoothness"
        float h = 1;

        // initialize points at random, based on the initial value of h
        for (int i=0; i<width; i+=2*step) {
            for (int j=0; j<height; j+=2*step) {
                map[i][j] = MathUtils.random(2*h);
            }
        }

        // Do the rest of the magic
        while (step > 0) {
            // Diamond step - mutate values for diagonally adjacent "tiles"
            // go through rows and columns in double-step
            for (int x = step; x < width; x+=2*step) {
                for (int y = step; y < height; y+=2*step) {
                    sum = map[x-step][y-step] + //down-left
                            map[x-step][y+step] + //up-left
                            map[x+step][y-step] + //down-right
                            map[x+step][y+step];  //up-right
                    map[x][y] = sum/4 + MathUtils.random(-h,h);
                }
            }

            // Square step - mutate values for immediately adjacent tiles (left,right, up, down)
            for (int x = 0; x < width; x+=step) {
                for (int y = step*(1-(x/step)%2); y<height; y+=2*step) {
                    sum = 0;
                    count = 0;
                    if (x-step >= 0) {
                        sum+=map[x-step][y];
                        count++;
                    }
                    if (x+step < width) {
                        sum+=map[x+step][y];
                        count++;
                    }
                    if (y-step >= 0) {
                        sum+=map[x][y-step];
                        count++;
                    }
                    if (y+step < height) {
                        sum+=map[x][y+step];
                        count++;
                    }
                    if (count > 0) map[x][y] = sum/count + MathUtils.random(-h,h);
                    else map[x][y] = 0;
                }

            }
            h /= smoothness;
            step /= 2;
        }

        // Normalize the map
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (float[] row : map) {
            for (float d : row) {
                if (d > max) max = d;
                if (d < min) min = d;
            }
        }

        // Use the thresholds to fill in the return map
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++){
                map[i][j] = (map[i][j]-min)/(max-min);
                if (map[i][j] < deepWaterThreshold) returnMap[i][j] = 0;
                else if (map[i][j] < shallowWaterThreshold) returnMap[i][j] = 1;
                else if (map[i][j] < beachTreshold) returnMap[i][j] = 2;
                else if (map[i][j] < grasslandThreshold) returnMap[i][j] = 3;
                else if (map[i][j] < forestThreshold) returnMap[i][j] = 4;
                else if (map[i][j] < hillsThreshold) returnMap[i][j] = 5;
                else returnMap[i][j] = 6;
            }
        }

        return returnMap;
    }
}
