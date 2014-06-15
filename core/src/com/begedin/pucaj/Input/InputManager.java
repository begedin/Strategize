package com.begedin.pucaj.Input;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.begedin.pucaj.Abilities.CombatAction;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Screens.MapScreen;
import com.begedin.pucaj.UI.MenuBuilder;

/**
 * Created by Nikola Begedin on 01.01.14..
 */
public class InputManager {

    private MapScreen screen;
    public Stage stage;

    public GestureDetector select;
    public MapKeySelectorController keySelect;
    public MapScrollController scroll;
    public GestureDetector drag;
    public GestureDetector move;
    public GestureDetector attack;

    public MenuBuilder menuBuilder;
    public MenuProcessor menuProcessor;

    private InputMultiplexer manager;

    public int selectedEntity;
    public CombatAction selectedAction;

    public InputManager(OrthographicCamera camera, World world, MapScreen screen, Stage stage, GameMap gameMap) {
        this.screen = screen;
        this.stage = stage;

        selectedEntity = -1;

        select = new GestureDetector(new MapSelectorController(camera, world, gameMap, screen, this));
        keySelect = new MapKeySelectorController(camera, world, gameMap, screen, this);
        move = new GestureDetector(new MapMovingController(camera, world, gameMap, screen, this));
        scroll = new MapScrollController(camera);
        drag = new GestureDetector(new MapDragController(camera, screen));
        attack = new GestureDetector(new MapAttackController(camera, world, gameMap, screen, this));

        manager = new InputMultiplexer(stage, scroll, drag, select, keySelect);

        menuProcessor = new MenuProcessor(screen, this, world, gameMap, stage);
        menuBuilder = new MenuBuilder(this, menuProcessor, stage);

        Gdx.input.setInputProcessor(manager);
    }


    public void setDefault() {
        manager = new InputMultiplexer(stage, scroll, drag, select, keySelect);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) Gdx.input.setInputProcessor(manager);
        else Gdx.input.setInputProcessor(null);
    }

    public boolean canMove() {
        return !screen.moved;
    }

    public boolean canAct() {
        return !screen.attacked;
    }

    public void enableAttack() {
        manager.removeProcessor(select);
        manager.removeProcessor(keySelect);
        manager.addProcessor(attack);
    }

    public void disableAttack() {
        manager.addProcessor(select);
        manager.addProcessor(keySelect);
        manager.removeProcessor(attack);
    }

    public void enableMoving() {
        manager.addProcessor(move);
    }

    public void disableMoving() {
        manager.removeProcessor(move);
    }

}
