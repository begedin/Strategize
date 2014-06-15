package com.begedin.strategize.Pathfinding;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class Path {
    public Array<Step> steps = new Array<Step>();

    public Path(){}

    public int getLength(){
        return steps.size;
    }

    public Step getStep(int index){
        return steps.get(index);
    }

    public int getX(int index){
        return steps.get(index).getX();
    }

    public int getY(int index){
        return steps.get(index).getY();
    }

    public void appendStep(int x, int y){
        steps.add(new Step(x,y));
    }

    public void prependStep(int x, int y) {
        steps.add(new Step(x, y));
    }


    public boolean contains(int x, int y){
        // we instantiate a new step and search with identify parameter off
        return steps.contains(new Step(x,y), false);
    }
}

