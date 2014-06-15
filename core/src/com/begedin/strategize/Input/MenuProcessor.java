package com.begedin.strategize.Input;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.begedin.strategize.Abilities.CombatAction;
import com.begedin.strategize.Components.Movable;
import com.begedin.strategize.Maps.GameMap;
import com.begedin.strategize.Screens.MapScreen;

/**
 * Created by Nikola Begedin on 01.01.14..
 */
public class MenuProcessor {

    private MapScreen screen;
    private InputManager inputManager;
    private World world;
    private GameMap gameMap;
    Stage stage;

    public MenuProcessor(MapScreen screen, InputManager inputManager, World world, GameMap gameMap, Stage stage) {
        this.screen = screen;
        this.inputManager = inputManager;
        this.world = world;
        this.gameMap = gameMap;
        this.stage = stage;
    }

    public void move() {
        if (screen.moved) return;

        Entity e = world.getEntity(screen.activeEntity);
        Movable movable = e.getComponent(Movable.class);
        if (movable == null) return;

        inputManager.enableMoving();

        screen.highlightedCells = gameMap.pathFinder.getReachableCells(screen.activeEntityCell.x, screen.activeEntityCell.y, movable);
        screen.renderHighlighter = true;
        screen.highlightMovementRange();
        inputManager.menuBuilder.setMenusVisible(false);
    }

    public void action(CombatAction action) {
        if (screen.attacked) return;
        inputManager.selectedAction = action;
        inputManager.enableAttack();

        screen.highlightedCells = action.rangeCalculator.getRange(screen.activeEntityCell, action);
        screen.highlightAttackRange();
        inputManager.menuBuilder.setMenusVisible(false);

        // DONE
        // AttackController - Add stage.clear() to yes button / add setMenusVisible(true) to "no" button
    }

    // public void item(Item item)
    public void item() {

    }

    public void selectWait() {
        inputManager.setDefault();
        screen.processTurn();
        stage.clear();
        inputManager.selectedEntity = -1;

        screen.moved = screen.attacked = false;
    }

}


