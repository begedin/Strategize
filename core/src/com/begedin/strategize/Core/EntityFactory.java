package com.begedin.strategize.Core;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.begedin.strategize.Abilities.ActionFactory;
import com.begedin.strategize.Components.AI;
import com.begedin.strategize.Components.Abilities;
import com.begedin.strategize.Components.ColorAnimation;
import com.begedin.strategize.Components.Cursor;
import com.begedin.strategize.Components.Expiration;
import com.begedin.strategize.Components.FadingMessage;
import com.begedin.strategize.Components.MapPosition;
import com.begedin.strategize.Components.Movable;
import com.begedin.strategize.Components.ScaleAnimation;
import com.begedin.strategize.Components.Sprite;
import com.begedin.strategize.Components.SpriteAnimation;
import com.begedin.strategize.Components.Stats;
import com.begedin.strategize.Maps.GameMap;

/**
 * Created by Nikola Begedin on 30.12.13..
 */
public class EntityFactory {

    public static Entity createClick(World world, int x, int y, float startScale, float speed) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(x,y));

        Sprite sprite = new Sprite("click");
        sprite.r = 1f;
        sprite.g = 1f;
        sprite.b = 1f;
        sprite.a = 0.5f;
        sprite.rotation = 0f;
        sprite.scaleX = startScale;
        sprite.scaleY = startScale;
        e.addComponent(sprite);

        Expiration expires = new Expiration(1f);
        e.addComponent(expires);

        ScaleAnimation scaleAnimation = new ScaleAnimation(speed);
        e.addComponent(scaleAnimation);

        ColorAnimation colorAnimation = new ColorAnimation();
        colorAnimation.alphaAnimate = true;
        colorAnimation.alphaSpeed = -1f;
        e.addComponent(colorAnimation);


        return e;
    }

    public static Entity createBlue(World world, int x, int y, GameMap gameMap) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(x,y));
        gameMap.addEntity(e.getId(), x, y);

        Sprite sprite = new Sprite("cylinder");
        sprite.r = 0;
        sprite.g = 0;
        sprite.b = 0.4f;
        sprite.a = 1f;
        sprite.rotation = 0f;
        sprite.scaleX = 0.25f;
        sprite.scaleY = 0.25f;
        e.addComponent(sprite);

        e.addComponent(new Movable(10f, 0.14f));

        Abilities abilities = new Abilities();
        sprite.name = "tanks/tank1";
        abilities.actions.add(ActionFactory.offensive("Fire", 5, 85, 5));
        abilities.actions.add(ActionFactory.repair("Repair",2, 5, 1));

        e.addComponent(abilities);
        e.addComponent(new Stats());
        e.addComponent(new AI());
        world.getManager(CombatTeamManager.class).setPlayer(e, Players.Blue);

        return e;
    }

    public static Entity createRed(World world, int x, int y, GameMap gameMap) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(x,y));
        gameMap.addEntity(e.getId(), x, y);

        Sprite sprite = new Sprite("tanks/tank1");
        sprite.r = 0.4f;
        sprite.g = 0;
        sprite.b = 0f;
        sprite.a = 1f;
        sprite.rotation = 0f;
        sprite.scaleX = 0.25f;
        sprite.scaleY = 0.25f;
        e.addComponent(sprite);

        e.addComponent(new Movable(10f, 0.14f));

        Abilities abilities = new Abilities();
        sprite.name = "tanks/tank1";
        abilities.actions.add(ActionFactory.offensive("Shoot", 6, 65, 6));
        abilities.actions.add(ActionFactory.repair("Repair",4, 2, 1));

        e.addComponent(abilities);
        e.addComponent(new Stats());
        e.addComponent(new AI());
        world.getManager(CombatTeamManager.class).setPlayer(e, Players.Red);

        return e;
    }

    public static Entity createNPC(World world, int x, int y, GameMap gameMap) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(x,y));
        gameMap.addEntity(e.getId(), x, y);

        Sprite sprite = new Sprite("tanks/tank1");
        sprite.r = 1f;
        sprite.g = 1f;
        sprite.b = 1f;
        sprite.a = 1f;
        sprite.rotation = 0f;
        sprite.scaleX = 0.25f;
        sprite.scaleY = 0.25f;
        e.addComponent(sprite);

        Abilities abilities = new Abilities();
        abilities.actions.add(ActionFactory.offensive("Attack",4,90, 5));
        abilities.actions.add(ActionFactory.repair("Supply Drop",2, 7, 3));

        SpriteAnimation anim = new SpriteAnimation();
        anim.playMode = Animation.PlayMode.LOOP_PINGPONG;
        anim.frameDuration = 0.3f;
        anim.stateTime = MathUtils.random(10f);
        e.addComponent(anim);

        e.addComponent(new Movable(10f, 0.14f));

        e.addComponent(new Abilities());
        if (e.getId() == 64) e.addComponent(new Stats(true));
        else e.addComponent(new Stats());

        world.getManager(CombatTeamManager.class).setPlayer(e, Players.Human);

        return e;
    }

    public static Entity createCursor(World world) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(0,0));

        Sprite sprite = new Sprite("tanks/tank1");
        sprite.r = 1f;
        sprite.g = 1f;
        sprite.b = 1f;
        sprite.a = 1f;
        sprite.rotation = 0f;
        sprite.scaleX = 0.25f;
        sprite.scaleY = 0.25f;
        e.addComponent(sprite);

        e.addComponent(new Cursor());

        return e;
    }

    public static Entity createDamageLabel(World world, String label, float x, float y) {
        Entity e = world.createEntity();

        e.addComponent(new MapPosition(x,y));
        e.addComponent(new FadingMessage(label,1.2f,0f,1.3f));

        return e;
    }

    /**
     * Holds names of game teams
     */
    public static class Players {
        public static final String Human = "HUMAN_TEAM";
        public static final String Computer = "COMPUTER_TEAM";
        public static final String Blue = "BLUE_TEAM";
        public static final String Red = "RED_TEAM";
        public static final String Green = "GREEN_TEAM";
        public static final String Yellow = "YELLOW_TEAM";
        public static final String Purple = "PURPLE_TEAM";
        public static final String Teal = "TEAL_TEAM";
    }
}
