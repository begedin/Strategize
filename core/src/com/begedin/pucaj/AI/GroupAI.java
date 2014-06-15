package com.begedin.pucaj.AI;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.ObjectMap;
import com.begedin.pucaj.Components.Stats;
import com.begedin.pucaj.Core.CombatTeamManager;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class GroupAI {

    private CombatTeamManager playerManager;

    public ObjectMap<Integer,Float> entityScores;

    private float[][] invDistance;

    private GameMap gameMap;

    private Bag<Entity> allies;
    private Bag<Entity> enemies;
    private int[] allyLevels;
    private int[] enemyLevels;
    private int step;

    public GroupAI(World world, GameMap gameMap) {
        if (world == null) System.out.println("WTF");
        playerManager = world.getManager(CombatTeamManager.class);
        entityScores = new ObjectMap<Integer,Float>();
        this.gameMap = gameMap;
        step = 0;
        allies = new Bag<Entity>();
        enemies = new Bag<Entity>();
    }

    public boolean processGroup(int entityId) {
        String group = playerManager.getPlayer(entityId);
        ImmutableBag<String> players = playerManager.getPlayers();

        if (step == 0) {
            // Load enemies and allies + levels

            // Reset stuff from previous runs
            allies.clear();
            enemies.clear();
            entityScores.clear();

            // First load all the entities
            for (int i = 0; i < players.size(); i++) {
                // Load allies
                if (players.get(i).compareTo(group) == 0) {
                    allies.addAll(playerManager.getEntities(players.get(i)));
                }
                // Load enemies
                else {
                    enemies.addAll(playerManager.getEntities(players.get(i)));
                }
            }

            // If there are no more enemies, this whole thing becomes kind of silly
            if (enemies.size() == 0) {
                for (int i = 0; i < allies.size(); i++) {
                    entityScores.put(allies.get(i).getId(), -1f);
                }
                return true;
            }

            // Next store their levels
            allyLevels = new int[allies.size()];
            enemyLevels = new int[enemies.size()];
            for (int i = 0; i < allyLevels.length; i++) {
                allyLevels[i] = allies.get(i).getComponent(Stats.class).level;
            }
            for (int i = 0; i < enemyLevels.length; i++) {
                enemyLevels[i] = enemies.get(i).getComponent(Stats.class).level;
            }

            step++;
            return false;
        }

        else if (step == 1) {
            // Compute the distance from each enemy to each ally

            invDistance = new float[enemies.size()][allies.size()];
            Pair enemyPos, allyPos;

            // Loop over all enemies and allies, get distance between them
            for (int i = 0; i < enemies.size(); i++) {
                enemyPos = gameMap.getCoordinatesFor(enemies.get(i).getId());

                for (int j = 0; j < allies.size(); j++) {
                    allyPos = gameMap.getCoordinatesFor(allies.get(j).getId());

                    invDistance[i][j] = 1f / (1 + MapTools.distance(enemyPos.x, enemyPos.y, allyPos.x, allyPos.y) / 5);
                }
            }

            step++;
            return false;
        }

        else if (step == 2) {
            // Compute the weighted scores
            float sum;
            float weightSum;

   /*
    * Compute a weighted average for the "threat" posed by
    * each enemy (the average level your own allies, weighted
    * by their distance from each particular enemy)
    * For a given ally, the farther it is from the enemy, the less
    * it contributes to the enemy's score.
    *
    * For 2 allies the same distance away, the higher level one
    * is deemed to contribute more to the enemies threat.
    *
    * It may make sense to someday incorporate additional information,
    * like a more injured ally also makes the enemy seem more dangerous.
    *
    * Also, it's possible to do this iteratively, i.e., say that the first
    * estimate of everyone's importance is their level, then we run through
    * this once to get a refined estimate of their importance, then run it
    * again, etc... until it converges to something?  Maybe, I'll consider
    * this in more detail another time.
   */

            for (int i = 0; i < enemies.size(); i++) {
                sum = 0f;
                weightSum = 0f;

                for (int j = 0; j < allies.size(); j++) {
                    sum += invDistance[i][j] * allyLevels[j];
                    weightSum += allyLevels[j];
                }
                entityScores.put(enemies.get(i).getId(), enemyLevels[i] * sum / weightSum);
            }

            // Do the same for an allies.  This effectively measures the threat that
            // your enemy will detect from your allies.  The ones your enemies want dead
            // should be the ones you want to take care of!

            for (int i = 0; i < allies.size(); i++) {
                sum = 0f;
                weightSum = 0f;

                for (int j = 0; j < enemies.size(); j++) {
                    sum += invDistance[j][i] * enemyLevels[j];
                    weightSum += enemyLevels[j];
                }
                entityScores.put(allies.get(i).getId(), -1f*allyLevels[i] * sum / weightSum);
            }

            step++;
            return false;
        }
        else {
            // Normalize the scores - all enemies (and allies) on range from 0 to 1 (or -1)

            float bestEnemy=0;
            float bestAlly=0;
            float score;

            for (int x : entityScores.keys()) {
                score = entityScores.get(x);
                if (score > 0) { //Enemy
                    if (score > bestEnemy) bestEnemy = score;
                }
                else if (score < 0) { // Ally
                    if (score < bestAlly) bestAlly = score;
                }
            }

            for (int x : entityScores.keys()) {
                score = entityScores.get(x);
                if (score > 0) entityScores.put(x, score/bestEnemy);
                else entityScores.put(x, -1f*score/bestAlly);
            }

            step = 0;
            return true;
        }
    }

    public ImmutableBag<Integer> getEnemies(Entity e) {
        String player = playerManager.getPlayer(e.getId());
        ImmutableBag<String> players = playerManager.getPlayers();
        Bag<Integer> enemies = new Bag<Integer>();
        for (int i = 0; i < players.size(); i++) {
            if (player.compareTo(players.get(i)) == 0) continue;
            else {
                ImmutableBag<Entity> enemyPlayer = playerManager.getEntities(players.get(i));
                for (int j = 0; j < enemyPlayer.size(); j++) enemies.add(enemyPlayer.get(j).getId());
            }
        }
        return enemies;
    }
}