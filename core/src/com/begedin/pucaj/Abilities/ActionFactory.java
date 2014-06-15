package com.begedin.pucaj.Abilities;

import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Components.Damage;
import com.begedin.pucaj.Components.Stats;
import com.begedin.pucaj.Abilities.CombatAction.ActionProcessor;
import com.begedin.pucaj.Abilities.CombatAction.FieldCalculator;
import com.begedin.pucaj.Abilities.CombatAction.RangeCalculator;
import com.begedin.pucaj.Abilities.CombatAction.ScoreCalculator;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Utils.CustomMath;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class ActionFactory {

    //public Action3(int strength, int mpCost, int baseProbability, int range, int field)

    public static CombatAction pointBlankNormal(String name, int damage, int baseProbability) {
        return new CombatAction(name, damage, 0, baseProbability, 1, 1);
    }

    public static CombatAction pointBlankSkill(String name, int damage, int cost, int baseProbability, int field) {
        return new CombatAction(name, damage, cost, baseProbability, 1, field,
                skillAttackActionProcessor(),
                skillAttackScoreCalculator(),
                skillFieldCalculator(),
                skillRangeCalculator());
    }

    public static CombatAction rangedNormal(String name, int damage, int baseProbability, int range) {
        return new CombatAction(name, damage, 0, baseProbability, range, 1);
    }

    public static CombatAction rangedSkill(String name, int damage, int cost, int baseProbability, int range, int field){
        return new CombatAction(name, damage, cost, baseProbability, range, field,
                skillAttackActionProcessor(),
                skillAttackScoreCalculator(),
                skillFieldCalculator(),
                skillRangeCalculator());
    }

    public static CombatAction repair(String name, int damage, int cost, int range, int field) {
        return new CombatAction(name, damage, cost, 100, range, field,
                repairActionProcessor(),
                repairScoreCalculator(),
                skillFieldCalculator(),
                skillRangeCalculator());
    }

    // Cure stuff:
    private static ActionProcessor repairActionProcessor() {
        ActionProcessor ap = new ActionProcessor() {

            @Override
            public void process(Entity sourceE, Array<Entity> targets, CombatAction action) {
                Stats source = sourceE.getComponent(Stats.class);
                source.energy -= action.cost;
                source.xp += 10;

                int cureAmt;
                for (Entity targetE : targets) {
                    cureAmt = (int)(MathUtils.random(0.8f, 1.2f)*(action.damage + source.getSupply()));
                    if (cureAmt < 1) cureAmt = 1;

                    targetE.addComponent(new Damage(-1*cureAmt));
                    targetE.changedInWorld();
                }
            }

        };
        return ap;
    }

    private static ScoreCalculator repairScoreCalculator() {
        ScoreCalculator sc = new ScoreCalculator() {

            @Override
            public ImmutableBag<Float> calculateScore(Stats source, ImmutableBag<Stats> targets, CombatAction action) {
                if (action.cost > source.energy) return null;

                Bag<Float> scoreBag = new Bag<Float>();
                // Get cost for source
                scoreBag.add(0.1f*(float)action.cost / (float)source.energy);

                // Get score for each target
                for (int i = 0; i < targets.size(); i++) {
                    Stats target = targets.get(i);
                    float cureAmt = (float) CustomMath.min(action.damage + source.getSupply(), target.maxHealth - target.health) / (float)target.maxHealth;
                    scoreBag.add(-1f*MathUtils.sinDeg(90*cureAmt));
                }

                return scoreBag;
            }
        };
        return sc;
    }

    private static ActionProcessor skillAttackActionProcessor() {
        ActionProcessor ap = new ActionProcessor() {

            @Override
            public void process(Entity sourceE, Array<Entity> targets, CombatAction action) {
                Stats source = sourceE.getComponent(Stats.class);
                source.energy -= action.cost;
                source.xp += 10;

                int dmgAmt;
                for (Entity targetE : targets) {
                    Stats target = targetE.getComponent(Stats.class);
                    dmgAmt = (int)(MathUtils.random(0.8f,1.2f)*(action.damage + source.getPower() - target.getDefense()));
                    if (dmgAmt < 1) dmgAmt = 1;

                    targetE.addComponent(new Damage(dmgAmt));
                    targetE.changedInWorld();
                }
            }

        };
        return ap;
    }

    private static ScoreCalculator skillAttackScoreCalculator() {
        ScoreCalculator sc = new ScoreCalculator() {

            @Override
            public ImmutableBag<Float> calculateScore(Stats source, ImmutableBag<Stats> targets, CombatAction action) {
                if (action.cost > source.energy) return null;

                Bag<Float> scoreBag = new Bag<Float>();
                // Get cost for source
                scoreBag.add(0.1f*(float)action.cost / (float)source.energy);

                // Get score for each target
                for (int i = 0; i < targets.size(); i++) {
                    Stats target = targets.get(i);
                    int dmgAmt = (int)(action.damage + source.getPower() - target.getDefense());
                    if (dmgAmt < 1) dmgAmt = 1;
                    float dmgPercent = (float)CustomMath.min(dmgAmt, target.maxHealth - target.health) / (float)target.health;
                    scoreBag.add(dmgPercent);
                }

                return scoreBag;
            }
        };
        return sc;
    }


    private static FieldCalculator skillFieldCalculator() {
        FieldCalculator fc = new FieldCalculator() {

            @Override
            public Array<Pair> getField(Pair target, CombatAction action) {
                Array<Pair> field = MapTools.getNeighbors(target.x, target.y, action.field - 1);
                field.add(target);
                return field;
            }
        };
        return fc;
    }

    private static RangeCalculator skillRangeCalculator() {
        RangeCalculator rc = new RangeCalculator() {

            @Override
            public Array<Pair> getRange(Pair source, CombatAction action) {
                Array<Pair> range = MapTools.getNeighbors(source.x, source.y, action.range);
                range.add(source);
                return range;
            }
        };
        return rc;
    }
}

