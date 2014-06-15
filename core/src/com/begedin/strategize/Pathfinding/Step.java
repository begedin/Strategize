package com.begedin.strategize.Pathfinding;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class Step {
    private int x, y;

    public Step(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int hashCode(){
        return x*y;
    }

    public boolean equals(Object other){
        if (other instanceof  Step){
            Step o = (Step)other;

            return (o.x == this.x) && (o.y == this.y);
        }

        return false;
    }
}
