package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Components.Stats;
import com.begedin.pucaj.Utils.CustomMath;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class TurnManagementSystem extends EntitySystem {
    @Mapper ComponentMapper<Stats> sm;

    private Array<Integer> unitOrder;
    private Array<Sorter> sorter;

    @SuppressWarnings("unchecked")
    public TurnManagementSystem(Array<Integer> unitOrder) {
        super(Aspect.getAspectForAll(Stats.class));

        this.unitOrder = unitOrder;
        sorter = new Array<Sorter>();
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {

        // Get the ID of the last entity to move - because they will get reset to 0
        // To be safe, first assume that this is the first turn, so that oldEntity = -1
        int oldEntity = -1;

        // We'll use this to store how many turns got skipped for the next "real" turn
        float turnsSkipped = 1f;

        // Then, if there is a list for unitOrder, the first entity was the one that
        // moved last turn
        if (unitOrder.size > 0) oldEntity = unitOrder.get(0);

        // Now we just clear the unit list because it needs to be recalculated
        unitOrder.clear();
        sorter.clear();

        // add the entity to the sorter array
        for (int i=0; i<entities.size(); i++) {
            Entity e = entities.get(i);
            Stats stats = sm.get(e);

            // Earlier we stored who moved last turn as oldEntity.  Evidently they
            // just moved, so we'll reset their actionPoints to 0.
            if (e.getId() == oldEntity) stats.actionPoints = 0;

            sorter.add(new Sorter(e.getId(), stats.actionPoints, stats.getSpeed()));
        }

        // Come up with a list of the next 30 entities to move
        for (int i = 0; i < 30; i++) {

            // Sort the list based on turnsSkipped
            sorter.sort();

            // The first unit in the sorted list is the next unit to get a turn
            unitOrder.add(sorter.get(0).id);

            // In case this is the 1st time we're going through the loop, that means
            // we're looking at the unit that actually gets to move this turn.  Note
            // how many turns it had to skip, because that's what we will ACTUALLY
            // use to increment unit's actionPoints.
            if (i == 0) turnsSkipped = sorter.get(0).turnsSkipped;

            // Update everyone's actionPoints
            for (int index = 1; index < sorter.size; index++) {
                Sorter s = sorter.get(index);
                s.actionPoints += (int)(sorter.get(0).turnsSkipped * s.speed);
                s.calculateTurnsSkipped();
            }

            // The first character in the array just had a turn (real or inferred),
            // so we'll set their actionPoints to 0.
            sorter.get(0).actionPoints = 0;
            sorter.get(0).calculateTurnsSkipped();
        }

        // Now we've made a list of the next 30 moves, but we didn't actually update
        // any of the real entity's action points (that was all projecting into the
        // future).  Now we'll increment them all based on the turnsSkipped of the
        // unit that actually gets to move (
        for (int i=0; i<entities.size(); i++) {
            Entity e = entities.get(i);
            Stats stats = sm.get(e);
            stats.actionPoints += stats.getSpeed() * turnsSkipped;
        }
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    private static class Sorter implements Comparable<Sorter> {

        public Sorter(int id, int actionPoints, int speed) {
            this.id = id;
            this.actionPoints = actionPoints;
            this.speed = speed;
            calculateTurnsSkipped();
        }

        int actionPoints;
        int speed;
        int id;
        float turnsSkipped;

        @Override
        public int compareTo(Sorter other) {

            // First try comparing how many "turns" each unit has to wait before its next turn
            if (turnsSkipped > other.turnsSkipped) return 1;
            if (turnsSkipped < other.turnsSkipped) return -1;

            // They are equal, try speed next
            if (speed < other.speed) return 1;
            if (speed > other.speed) return -1;

            // Barring all that, screw it
            return 0;
        }

        public void calculateTurnsSkipped() {
            turnsSkipped = (float) CustomMath.max(0, 100 - actionPoints)/(float)speed;
        }

    }
}
