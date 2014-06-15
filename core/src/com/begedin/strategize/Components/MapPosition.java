package com.begedin.strategize.Components;

import com.artemis.Component;

public class MapPosition extends Component {

    public MapPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MapPosition() {
        this(0,0);
    }

    public float x, y;
}
