package com.begedin.strategize.Abilities;

import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.begedin.strategize.Components.Damage;
import com.begedin.strategize.Components.Stats;
import com.begedin.strategize.Maps.MapTools;
import com.begedin.strategize.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class CombatAction {
    public CombatAction(String name, int damage, int cost, int baseProbability, int range, int field,
                   ActionProcessor actionProcessor,
                   ScoreCalculator scoreCalculator,
                   FieldCalculator fieldCalculator,
                   RangeCalculator rangeCalculator) {
        this.name = name;
        this.damage = damage;
        this.baseProbability = baseProbability;
        this.cost = cost;
        this.range = range;
        this.field = field;
        this.actionProcessor = actionProcessor;
        this.scoreCalculator = scoreCalculator;
        this.fieldCalculator = fieldCalculator;
        this.rangeCalculator = rangeCalculator;
    }

    public CombatAction(String name, int damage, int cost, int baseProbability, int range, int field) {
        this.name = name;
        this.damage = damage;
        this.baseProbability = baseProbability;
        this.cost = cost;
        this.range = range;
        this.field = field;

        actionProcessor = new ActionProcessor() {
            @Override
            public void process(Entity sourceE, Array<Entity> targets, CombatAction action) {
                Stats source = sourceE.getComponent(Stats.class);
                source.energy -= action.cost;
                boolean hitOnce = false;

                for (Entity targetE : targets) {
                    Stats target = targetE.getComponent(Stats.class);
                    int damage = 0;
                    int probability = action.baseProbability; // +source.agility() - target.agility() +blah blah blah...
                    if (MathUtils.random(100) < probability) { //HIT
                        if (!hitOnce) {
                            source.xp += 10;
                            hitOnce = true;
                        }
                        damage = (int)(MathUtils.random(0.8f,1.2f)*(action.damage + source.getPower() - target.getDefense()));
                        if (damage < 1) damage = 1;
                    }

                    targetE.addComponent(new Damage(damage));
                    targetE.changedInWorld();
                }
            }
        };

        scoreCalculator = new ScoreCalculator() {
            @Override
            public ImmutableBag<Float> calculateScore(Stats source, ImmutableBag<Stats> targets, CombatAction action) {
                // If we can't even cast it, then don't bother
                int MP = source.energy;
                if (action.cost > MP) return null;

                Bag<Float> scoreBag = new Bag<Float>();

                // Get the cost to the source
                scoreBag.add(0.1f*(float)action.cost / (float)MP);

                // Get the scores for each target
                for (int i = 0; i < targets.size(); i++) {
                    Stats target = targets.get(i);
                    int HP = target.health;
                    int damage = (int)(MathUtils.random(0.8f,1.2f)*(action.damage + source.getPower() - target.getDefense()));
                    if (damage < 1) damage = 1;
                    scoreBag.add((float)action.baseProbability/100f * (float)Math.min(damage, HP) / (float)HP);
                }

                return scoreBag;
            }
        };

        fieldCalculator = new FieldCalculator() {
            @Override
            public Array<Pair> getField(Pair target, CombatAction action) {
                Array<Pair> field = MapTools.getNeighbors(target.x, target.y, action.field - 1);
                field.add(target);
                return field;
            }
        };

        rangeCalculator = new RangeCalculator() {
            @Override
            public Array<Pair> getRange(Pair source, CombatAction action) {
                return MapTools.getNeighbors(source.x, source.y, action.range);
            }
        };
    }

    public String name;
    public int cost, range, field, baseProbability, damage;

    public ActionProcessor actionProcessor;
    public ScoreCalculator scoreCalculator;
    public FieldCalculator fieldCalculator;
    public RangeCalculator rangeCalculator;

    public interface ActionProcessor {
        public void process(Entity source, Array<Entity> targets, CombatAction action);
    }

    public interface ScoreCalculator {
        public ImmutableBag<Float> calculateScore(Stats source, ImmutableBag<Stats> target, CombatAction action);
    }

    public interface FieldCalculator {
        public Array<Pair> getField(Pair target, CombatAction action);
    }

    public interface RangeCalculator {
        public Array<Pair> getRange(Pair source, CombatAction action);
    }
}
