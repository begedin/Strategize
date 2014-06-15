package com.begedin.strategize.Components;

import com.artemis.Component;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class FadingMessage extends Component {

    public String label;
    public float duration, currentTime;
    public float vx, vy;

    public FadingMessage(String label, float duration) {
        this(label,duration,0,0);
    }

    public FadingMessage(String label, float duration, float vx, float vy) {
        this.label = label;
        this.duration = duration;
        this.vx = vx;
        this.vy = vy;
        currentTime = 0f;
    }

}
