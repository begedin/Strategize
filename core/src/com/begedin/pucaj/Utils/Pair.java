package com.begedin.pucaj.Utils;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class Pair {
    public int x,y;
    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Pair(){
        this(0,0);
    }

    public boolean equals(Object other){
        if (other instanceof  Pair){
            Pair o = (Pair)other;

            return (o.x == this.x) && (o.y == this.y);
        }

        return false;
    }
}
