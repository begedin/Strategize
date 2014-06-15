package com.begedin.strategize.Components;

import com.artemis.Component;

public class ColorAnimation extends Component {

    public ColorAnimation() {
        redMin = greenMin = blueMin = alphaMin = 0f;
        redMax = greenMax = blueMax = alphaMax = 1f;
        redAnimate = greenAnimate = blueAnimate = alphaAnimate = repeat = false;
    }

    public float redMin, redMax, redSpeed;
    public float greenMin, greenMax, greenSpeed;
    public float blueMin, blueMax, blueSpeed;
    public float alphaMin, alphaMax, alphaSpeed;

    public boolean redAnimate, greenAnimate, blueAnimate, alphaAnimate, repeat;
}
