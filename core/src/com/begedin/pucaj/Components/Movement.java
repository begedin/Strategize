package com.begedin.pucaj.Components;

import com.artemis.Component;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Pathfinding.Path;

/**
 * Stores a path for an entity as well as an elapsed time value
 *
 */
public class Movement extends Component {

    public Path path;
    public float elapsedTime;

    public Movement(Path path) {
        this.path = path;
        elapsedTime = 0;
    }

}
