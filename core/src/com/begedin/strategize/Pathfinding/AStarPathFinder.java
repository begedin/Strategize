package com.begedin.strategize.Pathfinding;

import com.badlogic.gdx.utils.Array;
import com.begedin.strategize.Components.Movable;
import com.begedin.strategize.Maps.GameMap;
import com.begedin.strategize.Utils.MyQueue;
import com.begedin.strategize.Utils.Pair;
import com.begedin.strategize.Maps.MapTools;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class AStarPathFinder {
    /** The set of nodes that have been searched through */
    private Array<Node> closed = new Array<Node>();
    /** The set of nodes that we do not yet consider fully searched */
    private SortedNodeList open = new SortedNodeList();

    /** The map being searched */
    private GameMap gameMap;
    private int[][] map;
    /** The maximum depth of search we're willing to accept before giving up */
    private int maxSearchDistance;

    /** The complete set of nodes across the map */
    private Node[][] nodes;

    /**
     * Create a path finder
     *
     * @param gameMap The map to be searched
     * @param maxSearchDistance The maximum depth we'll search before giving up
     */
    public AStarPathFinder(GameMap gameMap, int maxSearchDistance) {
        this.gameMap = gameMap;
        this.map = gameMap.map;
        this.maxSearchDistance = maxSearchDistance;

        nodes = new Node[map.length][map[0].length];
        for (int x=0;x<map.length;x++) {
            for (int y=0;y<map[0].length;y++) {
                nodes[x][y] = new Node(x,y);
            }
        }
    }


    /**
     *
     * @param sx x coordinate of the current location
     * @param sy y coordinate of the current location
     * @param tx x coordinate of the target location
     * @param ty y coordinate of the target location
     * @param mover mover component of the entity to find the path for
     * @return
     */
    public Path findPath(int sx, int sy, int tx, int ty, Movable mover, boolean ignoreRange) {
        // easy first check, if the destination is blocked, we can't get there
        if (isCellBlocked(tx,ty,mover) && !ignoreRange) {
            return null;
        }

        // initial state for A*. The closed group is empty. Only the starting
        // tile is in the open list and it's cost is zero, i.e. we're already there
        nodes[sx][sy].cost = 0;
        nodes[sx][sy].depth = 0;
        closed.clear();
        open.clear();
        open.add(nodes[sx][sy]);

        // we set the parent of the target node to null.
        // if we later find a path to the node, we will set it's parent to the previous node on the path
        nodes[tx][ty].parent = null;

        // While we still have more nodes to search and haven't exceeded our max search depth
        int maxDepth = 0;
        while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
            // pull out the first node in our open list, this is determined to
            // be the most likely to be the next step based on our heuristic
            Node current = open.first();
            if (current == nodes[tx][ty]) {
                break;
            }

            open.remove(current);
            closed.add(current);

            Array<Pair> neighbors = MapTools.getNeighbors(current.x, current.y);
            // search through all the neighbors of the current node evaluating
            // them as next steps
            for (Pair n : neighbors) {
                int xp = n.x;
                int yp = n.y;
                float nextStepCost = current.cost + getMovementCost(current.x,current.y,xp,yp, mover);
                Node neighbor = nodes[xp][yp];

                // If this step exceeds the movers energy, don't even bother with it
                if (nextStepCost > mover.energy && !ignoreRange) continue;

                // Check to see if we have found a new shortest route to this neighbor
                if (nextStepCost < neighbor.cost) {
                    if (open.contains(neighbor)) open.remove(neighbor);
                    if (closed.contains(neighbor, false)) closed.removeValue(neighbor, false);
                }

                // If it was a new shortest route
                if (!open.contains(neighbor) && !closed.contains(neighbor, false)) {
                    neighbor.cost = nextStepCost;
                    neighbor.heuristic = (float)MapTools.distance(xp, yp, tx, ty);
                    maxDepth = Math.max(maxDepth, neighbor.setParent(current));
                    open.add(neighbor);
                }
            }
        }

        // since we've got an empty open list or we've run out of search
        // there was no path. Just return null
        if (nodes[tx][ty].parent == null) {
            return null;
        }

        // At this point we've definitely found a path so we can uses the parent
        // references of the nodes to find out way from the target location back
        // to the start recording the nodes on the way.
        Path path = new Path();
        Node target = nodes[tx][ty];
        while (target != nodes[sx][sy]) {
            path.prependStep(target.x, target.y);
            target = target.parent;
        }
        path.prependStep(sx,sy);

        // thats it, we have our path
        return path;
    }


    /**
     *
     * @param x The x coordinate of the mover
     * @param y The y coordinate of the mover
     * @return An Array<Pair> containing the coordinates for all cells the mover can reach
     */
    public Array<Pair> getReachableCells(int x, int y, Movable mover) {
        Array<Pair> reachableCells = new Array<Pair>();
        MyQueue<Node> open = new MyQueue<Node>();
        closed.clear();
        Node start = nodes[x][y];
        start.depth = 0;
        start.cost = 0;
        open.push(start);
        while (open.size() > 0) {
            // poll() the open queue
            Node current = open.poll();

            for (Pair n : MapTools.getNeighbors(current.x,current.y)) {
                Node neighbor = nodes[n.x][n.y];
                float nextStepCost = current.cost + mover.terrainCost[map[n.x][n.y]];

                // If the cell is beyond our reach, or otherwise blocked, ignore it
                if (nextStepCost > mover.energy || isCellBlocked(n.x,n.y,mover)) continue;

                // Check to see if we have found a new shortest route to this neighbor, in
                // which case it must be totally reconsidered
                if (nextStepCost < neighbor.cost) {
                    if (closed.contains(neighbor,false)) closed.removeValue(neighbor,false);
                    if (open.contains(neighbor, false)) open.remove(neighbor,false);
                }

                if (!open.contains(neighbor, false) && !closed.contains(neighbor,false)) {
                    neighbor.cost = nextStepCost;
                    open.push(neighbor);
                }
            }
            closed.add(current);
        }

        for (Node n : closed) {
            if (n.x != x || n.y != y) reachableCells.add(new Pair(n.x,n.y));
        }

        return reachableCells;

    }

    public int getNumberOfTurns(int sx, int sy, int tx, int ty, Movable mover) {
        Path path = findPath(sx, sy, tx, ty, mover, true);
        if (path == null) return Integer.MAX_VALUE;

        int numberOfTurns = 1;
        int energySpent = 0;

        for (int i = path.getLength() - 1; i > 0; i--) {
            Step from = path.getStep(i);
            Step to = path.getStep(i-1);

            if (energySpent > mover.energy) {
                energySpent = 0;
                numberOfTurns++;
            }
            energySpent += getMovementCost(from.getX(),from.getY(),to.getX(),to.getY(), mover);
        }

        return numberOfTurns;
    }

    /**
     * Check if a given location is valid for the supplied mover
     *
     * @param sx The starting x coordinate
     * @param sy The starting y coordinate
     * @param x The x coordinate of the location to check
     * @param y The y coordinate of the location to check
     * @return True if the location is valid for the given mover
     */
    protected boolean isValidLocation(int sx, int sy, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= map.length) || (y >= map[0].length);

        if ((!invalid) && ((sx != x) || (sy != y))) {
            //invalid = map.blocked(mover, x, y);
        }

        return !invalid;
    }

    /**
     * Get the cost to move through a given location
     *
     * @param mover The entity that is being moved
     * @param sx The x coordinate of the tile whose cost is being determined
     * @param sy The y coordiante of the tile whose cost is being determined
     * @param tx The x coordinate of the target location
     * @param ty The y coordinate of the target location
     * @return The cost of movement through the given tile
     */
    public float getMovementCost(int sx, int sy, int tx, int ty, Movable mover) {
        return mover.terrainCost[map[tx][ty]];
    }

    private boolean isCellBlocked(int x, int y, Movable mover) {
        return ((mover.terrainBlocked[map[x][y]]) || gameMap.cellOccupied(x, y));
    }

    /**
     * Get the heuristic cost for the given location. This determines in which
     * order the locations are processed.
     *
     * @param x The x coordinate of the tile whose cost is being determined
     * @param y The y coordiante of the tile whose cost is being determined
     * @param tx The x coordinate of the target location
     * @param ty The y coordinate of the target location
     * @return The heuristic cost assigned to the tile
     */
    public float getHeuristicCost(int x, int y, int tx, int ty) {
        return MapTools.distance(x, y, tx, ty);
        //return heuristic.getCost(map, mover, x, y, tx, ty);
    }
}

class SortedNodeList{
    private ArrayList<Node> list = new ArrayList<Node>();

    public Node first(){
        return list.get(0);
    }

    public void clear(){
        list.clear();
    }

    public void add(Node n) {
        list.add(n);
        Collections.sort(list);
    }

    public void remove(Node n){
        list.remove(n);
    }

    public int size(){
        return list.size();
    }

    public boolean contains(Node n){
        return list.contains(n);
    }

}

class Node implements Comparable{
    public int x, y;

    public float cost;
    public float heuristic;

    public int depth;

    public Node parent;

    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int setParent(Node parent){
        this.depth = parent.depth + 1;
        this.parent = parent;

        return depth;
    }

    public int compareTo(Object other){
        Node o = (Node)other;

        float f = heuristic + cost;
        float of = o.heuristic + o.cost;

        if (f < of){
            return -1;
        } else if (f > of){
            return 1;
        } else {
            return 0;
        }
    }

    public boolean equals(Object other){
        if (other instanceof  Node){
            Node o = (Node)other;
            return (o.x == x) && (o.y == y);
        }
        return false;
    }
}
