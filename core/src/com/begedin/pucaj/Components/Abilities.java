package com.begedin.pucaj.Components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Abilities.CombatAction;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class Abilities extends Component {

    public Array<CombatAction> actions;

    public Abilities() {
        actions = new Array<CombatAction>();
    }

    public Array<CombatAction> getActions() {
        return actions;
    }

}
