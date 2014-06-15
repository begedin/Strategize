package com.begedin.strategize.Components;

import com.artemis.Component;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class Expiration extends Component {
    public float delay;

    public Expiration(float delay) {
        this.delay = delay;
    }

    public Expiration() {
        this(0);
    }
}
