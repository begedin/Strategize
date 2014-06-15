package com.begedin.strategize.Components;

import com.artemis.Component;
import com.begedin.strategize.AI.Plan;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class AI extends Component {

    public Plan plan;
    public float timer;
    public boolean active;

    public boolean planDone;

    public void begin() {
        timer = 0;
        active = true;
        planDone = false;
    }
}