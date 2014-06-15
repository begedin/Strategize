package com.begedin.pucaj.Components;

import com.artemis.Component;

public class ScaleAnimation extends Component {
    public float min, max, speed;
    public boolean repeat, active;

    public ScaleAnimation(float speed) {
        this.speed = speed;
        this.min = 0f;
        this.max = 100f;
        this.repeat = false;
        this.active = true;
    }
}