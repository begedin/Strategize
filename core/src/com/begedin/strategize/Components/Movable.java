package com.begedin.strategize.Components;

import com.artemis.Component;

public class Movable extends Component {

    public float energy;            // Roughly how far can you go?
    public float slowness;          // How many seconds it takes to slide from 1 tile to an adjacent

    public boolean[] terrainBlocked;
    public float[] terrainCost;

    public Movable(float energy, float slowness) {
        this.energy = energy;
        this.slowness = slowness;

        terrainBlocked = new boolean[9];
        terrainCost = new float[9];

        for (int i = 0; i < terrainBlocked.length; i++) {
            terrainBlocked[i] = false;
            terrainCost[i] = 1.5f*Math.abs(i-4)+1;
        }
    }
}
