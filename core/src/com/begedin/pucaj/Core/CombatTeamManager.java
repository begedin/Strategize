package com.begedin.pucaj.Core;

/**
 * Created by Nikola Begedin on 31.12.13..
 */

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

import java.util.HashMap;
import java.util.Map;

/**
 * Designates player teams (e.g. Human, AI_Enemy, AI_Ally, etc...)
 * Based off of Artemis: TeamManager.java by Arni Arent
 */
public class CombatTeamManager extends Manager {
    private Map<String, Bag<Entity>> entitiesByPlayer;
    private Map<Integer, String> playerByEntity;
    private Bag<String> players;

    public CombatTeamManager() {
        entitiesByPlayer = new HashMap<String, Bag<Entity>>();
        playerByEntity = new HashMap<Integer, String>();
        players = new Bag<String>();
    }

    public ImmutableBag<String> getPlayers() {
        return players;
    }

    @Override
    protected void initialize() {
    }

    public String getPlayer(int entityId) {
        return playerByEntity.get(entityId);
    }

    public void setPlayer(Entity e, String player) {
        removeFromPlayer(e);

        playerByEntity.put(e.getId(), player);

        Bag<Entity> entities = entitiesByPlayer.get(player);
        if(entities == null) {
            entities = new Bag<Entity>();
            entitiesByPlayer.put(player, entities);
            players.add(player);
        }
        entities.add(e);
    }

    public ImmutableBag<Entity> getEntities(String player) {
        return entitiesByPlayer.get(player);
    }

    public void removeFromPlayer(Entity e) {
        String player = playerByEntity.remove(e.getId());
        if(player != null) {
            Bag<Entity> entities = entitiesByPlayer.get(player);
            if(entities != null) {
                entities.remove(e);
            }
        }
    }

    @Override
    public void deleted(Entity e) {
        removeFromPlayer(e);
    }
}
