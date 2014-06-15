package com.begedin.strategize.Components;

import com.artemis.Component;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class Velocity extends Component {
    public float vx, vy;

    public Velocity(float vx, float vy){
        this.vx = vx;
        this.vy = vy;
    }

    public Velocity(){
        this(0,0);
    }
}
