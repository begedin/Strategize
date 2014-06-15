package com.begedin.pucaj.Systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.AI.ActionSequencer;
import com.begedin.pucaj.AI.GroupAI;
import com.begedin.pucaj.AI.Plan;
import com.begedin.pucaj.Abilities.CombatAction;
import com.begedin.pucaj.Components.AI;
import com.begedin.pucaj.Components.Abilities;
import com.begedin.pucaj.Components.Movable;
import com.begedin.pucaj.Components.Stats;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Pathfinding.Path;
import com.begedin.pucaj.Pathfinding.Step;
import com.begedin.pucaj.Screens.MapScreen;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class AISystem extends EntityProcessingSystem {

    @Mapper ComponentMapper<AI> AIm;
    @Mapper ComponentMapper<Stats> sm;
    @Mapper ComponentMapper<Movable> mm;
    @Mapper ComponentMapper<Abilities> am;

    private MapScreen screen;
    private GameMap gameMap;
    private ActionSequencer sequencer;
    private GroupAI groupAI;

    private boolean startMove, startAction, groupAiDone;

    @SuppressWarnings("unchecked")
    public AISystem(MapScreen screen, GameMap gameMap) {
        super(Aspect.getAspectForAll(AI.class, Stats.class, Movable.class, Abilities.class));

        this.screen = screen;
        this.gameMap = gameMap;

        startMove = true;
        startAction = true;

        groupAiDone= false;

    }

    @Override
    public void initialize() {
        groupAI = new GroupAI(world, gameMap);
    }

    @Override
    protected void process(Entity e) {

        AI ai = AIm.get(e);
        if (!ai.active) return;

        if (!groupAiDone) {
            groupAiDone = groupAI.processGroup(e.getId());
        }

        else if (!ai.planDone) {
            decidePlan(e, ai);
        }
        else {

            // If moveFirst
            if (ai.plan.moveFirst) {

                // moveFirst - Move
                if (!ai.plan.moveDone) {
                    if (startMove) {
                        ai.timer = 0;
                        startMove = false;
                    }
                    sequencer.move(gameMap, screen, ai.timer);
                }

                // moveFirst - Act
                else if (!ai.plan.actionDone) {
                    if (startAction) {
                        ai.timer = 0;
                        startAction = false;
                    }
                    sequencer.act(gameMap, screen, ai.timer);
                }
            }

            // Else, actFirst
            else {

                // actFirst - Act
                if (!ai.plan.actionDone) {
                    if (startAction) {
                        ai.timer = 0;
                        startAction = false;
                    }
                    sequencer.act(gameMap, screen, ai.timer);
                }

                // actFirst - Decide move location
                else if (!ai.plan.moveDecided) {
                    ai.plan.moveTarget = decideMovement(e);
                    ai.plan.moveDecided = true;
                }

                // actFirst - Begin moving to target location
                else if (!ai.plan.moveDone) {
                    if (startMove) {
                        ai.timer = 0;
                        startMove = false;
                    }
                    sequencer.move(gameMap, screen, ai.timer);
                }

            }
        }

        // Don't increment the timer while the camera is moving
        // This let's us focus on the character for a second (or so)
        // before they start acting.
        if (screen.cameraMoving()) return;
        ai.timer += world.getDelta();

        // If everything is done, process the turn
        if ((ai.plan != null) && ai.plan.actionDone && ai.plan.moveDone) {
            ai.active = false;
            startMove = startAction = true;
            groupAiDone= false;
            screen.processTurn();
        }
    }

    // If an entity dies in the middle of its own turn...
    @Override
    public void removed(Entity e) {
        AI ai = AIm.get(e);
        if (ai.active) {
            ai.active = false;
            startMove = startAction = true;
            groupAiDone= false;
            screen.processTurn();
            // Future note: This transition comes quite abruptly.
            // It would be nice to somehow delay it, but because
            // the entity has just been removed, it won't be
            // processed any more.  Perhaps give processTurn()
            // a boolean argument which will pause for a second
            // or so before selecting the new entity and moving
            // the camera on.

        }
    }


    private void decidePlan(Entity e, AI ai) {
        Pair pos = gameMap.getCoordinatesFor(e.getId());
        Movable movable = mm.get(e);
        Abilities abilities = am.get(e);

        Array<Pair> reachableCells = gameMap.pathFinder.getReachableCells(pos.x, pos.y, movable);
        Array<CombatAction> availableActions = abilities.getActions();

        Array<Plan> plans = new Array<Plan>();

        // Try plans that involve moving first, then acting
        boolean moveFirst = true;
        for (Pair moveTarget : reachableCells) {
            gameMap.moveEntity(e.getId(), moveTarget.x, moveTarget.y);
            for (CombatAction action : availableActions) {
                for (Pair actionTarget : action.rangeCalculator.getRange(moveTarget, action)) {
                    Plan tempPlan = new Plan(moveFirst, moveTarget, actionTarget, action, 0);
                    int score = scorePlan(e, tempPlan);
                    if (score > 0) plans.add(tempPlan);
                }
            }
        }
        // In the above section, we had to pretend that we kept moving
        // the active entity to new places - undo this now.
        gameMap.moveEntity(e.getId(), pos.x, pos.y);

        // Now look at plans that involve acting first, which means
        // that we can figure out where to move later
        moveFirst = false;
        for (CombatAction action : availableActions) {
            for (Pair actionTarget : action.rangeCalculator.getRange(pos, action)) {
                Plan tempPlan = new Plan(moveFirst, pos, actionTarget, action, 0);
                int score = scorePlan(e, tempPlan);
                if (score > 0) plans.add(tempPlan);
            }
        }

        // If you never saw anything worth doing, set moveFirst false and decide where to move
        // after acting (even though the action is null)
        if (plans.size == 0) plans.add(new Plan(false, null, null, null, 1));

        // Sort the plans, and take the first one (highest score) as the best
        plans.sort();
        ai.plan = plans.get(0);
        ai.planDone = true;
        sequencer = new ActionSequencer(ai.plan, e);
    }

    private int scorePlan(Entity e, Plan plan) {
        CombatAction action = plan.action;

        // Get the stats of the entity doing this action
        Stats source = sm.get(e);

        // If the action costs too much, we won't even consider this plan
        if (source.energy < action.cost) return 0;

        // get the target field of this action
        Array<Pair> field = action.fieldCalculator.getField(plan.actionTarget, action);

        // Start a bag of stats of all the entities who will be targeted in this attack, and their IDs
        Bag<Stats> targetBag = new Bag<Stats>();
        Bag<Integer> targetIds = new Bag<Integer>();
        int targetId;

        // Loop over all cells in the field
        for (Pair cell : field) {
            targetId = gameMap.getEntityAt(cell.x, cell.y);

            // If there is an entity at that cell, add it to the targetBag and targetIds bag
            if (targetId > -1) {
                Entity target = world.getEntity(targetId);
                targetBag.add(sm.get(target));
                targetIds.add(targetId);
                plan.targetEntities.add(target);
            }
        }

        // If there are no targets, return 0
        if (targetBag.size() == 0) return 0;

        // Calculate the score of this action for each target in the target bag, as well as the
        // cost to the caster (attacker)
        ImmutableBag<Float> scoreBag = action.scoreCalculator.calculateScore(source, targetBag, action);

        // Null means that the action exceeded the casters MP, so this action gets a 0 score
        if (scoreBag == null) return 0;

        // scoreBag(0) is the cost to the caster, then multiplied by casters importance
        float score = scoreBag.get(0) * groupAI.entityScores.get(e.getId());

        // Add to that the scores*importance for all targets of the spell
        for (int i = 1; i < scoreBag.size(); i++) {
            score += scoreBag.get(i) * groupAI.entityScores.get(targetIds.get(i-1));
        }

        // entityScores all range from -1 to +1, as should scores from the scoreCalculator
        // (though this is not strictly required).  Thus the product also ranges from
        // 0 to 1.  Adding them all up yields a potential score from -#-of-targets to +#-of-targets.
        // In the common case with a single target, this becomes -100 to +100.  With 5 targets
        // it goes up to -500 to +500, etc...
        plan.score = (int)(100*score);

        // A negative score means something bad happened, so we don't really want to bother
        // with this plan.  Otherwise, if the score is positive, and we are acting first
        // (which means we get to move later) we should get a bonus of 5 points (to indicate
        // that there is some benefit to moving after we act)
        if (!plan.moveFirst && plan.score > 0) plan.score+=5;
        return (int)plan.score;
    }

    /*
     * Call this when you have no good plans in your personal range.
     * Try to find an enemy to hunt down based on how far it is from you,
     * how powerful it is, and how many other enemies are near to it.
     */
    private Pair decideMovement(Entity e) {
        Pair pos = gameMap.getCoordinatesFor(e.getId());
        Movable movable = mm.get(e);
        Array<Pair> reachableCells = gameMap.pathFinder.getReachableCells(pos.x, pos.y, movable);
        ImmutableBag<Integer> enemies = groupAI.getEnemies(e);
        if (enemies.size() == 0) return reachableCells.get(MathUtils.random(reachableCells.size-1));

        // The best enemy you are considering chasing and its score
        int targetEnemy = -1;
        float bestScore = 0f;

        // The current enemy you are checking out and its score
        int id;
        float score;

        // How far away is the enemy?  How many enemies are within a small radius of it?
        int distance, count;

        for (int i = 0; i < enemies.size(); i++) {
            count = 1;
            Pair target = gameMap.getCoordinatesFor(enemies.get(i));
            distance = MapTools.distance(pos.x, pos.y, target.x, target.y);
            for (Pair cell : MapTools.getNeighbors(target.x, target.y, 6)) {
                id = gameMap.getEntityAt(cell.x, cell.y);
                if (!enemies.contains(id)) continue;
                count++;
            }

            score = groupAI.entityScores.get(enemies.get(i)) * count / (1 + distance / 5);
            if (score > bestScore) {
                bestScore = score;
                targetEnemy = enemies.get(i);
            }
        }

        if (targetEnemy > -1) {
            Pair target = gameMap.getCoordinatesFor(targetEnemy);
            Path path = gameMap.pathFinder.findPath(pos.x, pos.y, target.x, target.y, movable, true);
            for (int i = 0; i < path.getLength(); i++) {
                Step step = path.getStep(i);
                Pair p = new Pair(step.getX(),step.getY());
                if (reachableCells.contains(p, false)) return p;
            }
        }
        return reachableCells.get(MathUtils.random(reachableCells.size-1));
    }
}