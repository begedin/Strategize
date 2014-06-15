package com.begedin.pucaj.Input;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Abilities.CombatAction;
import com.begedin.pucaj.Maps.GameMap;
import com.begedin.pucaj.Maps.MapTools;
import com.begedin.pucaj.Screens.MapScreen;
import com.begedin.pucaj.Utils.Pair;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class MapAttackController implements GestureListener{

    private OrthographicCamera camera;
    private World world;
    private GameMap gameMap;
    private MapScreen screen;
    private InputManager inputManager;

    public MapAttackController(OrthographicCamera camera, World world, GameMap gameMap, MapScreen screen, InputManager inputManager) {
        this.camera = camera;
        this.world = world;
        this.gameMap = gameMap;
        this.screen = screen;
        this.inputManager = inputManager;
    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean tap(float v, float v2, int i, int i2) {
        Pair coords = MapTools.window2world(Gdx.input.getX(), Gdx.input.getY(), camera);
        final CombatAction action = inputManager.selectedAction;

        // If they actually clicked in range
        if (screen.highlightedCells.contains(coords, false)) {

            // Get the source and targets of the action
            final Entity source = world.getEntity(screen.activeEntity);
            final Array<Entity> targets = new Array<Entity>();
            for (Pair cell : action.fieldCalculator.getField(coords, action)) {
                int id = gameMap.getEntityAt(cell.x, cell.y);
                if (id > -1) targets.add(world.getEntity(id));
            }

            // If the action actually has some targets
            if (targets.size > 0){

                // Highlight the target field
                screen.highlightedCells = action.fieldCalculator.getField(coords, action);

                // Build a confirm dialog box
                float w = 200f;
                float h = 100f;
                float x = (screen.stage.getWidth() - w)/2f;
                float y = (screen.stage.getHeight() - h)/2f;
                ChangeListener confirm = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        action.actionProcessor.process(source, targets, action);
                        screen.attacked = true;
                        inputManager.stage.clear();
                        inputManager.selectedEntity = -1;
                        screen.highlightedCells.clear();
                        screen.renderHighlighter = false;
                    }
                };
                ChangeListener decline = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        inputManager.menuBuilder.setMenusVisible(true);
                        screen.highlightedCells.clear();
                        screen.renderHighlighter = false;
                    }
                };
                inputManager.menuBuilder.buildDialog("","Confirm action?",x,y,w,h,
                        inputManager.menuBuilder.getTextButton("Yes", confirm),
                        inputManager.menuBuilder.getTextButton("No", decline));
            }

            // They clicked within range, but with no targets
            else {
                inputManager.menuBuilder.setMenusVisible(true);
                screen.highlightedCells.clear();
                screen.renderHighlighter = false;
            }

        }

        // If they clicked out of the range, bring the menu back up
        else {
            inputManager.menuBuilder.setMenusVisible(true);
            screen.highlightedCells.clear();
            screen.renderHighlighter = false;
        }

        // Wherever they clicked, they are now done with the "attacking" aspect of things
        inputManager.disableAttack();
        return true;
    }

    @Override
    public boolean longPress(float v, float v2) {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i) {
        return false;
    }

    @Override
    public boolean pan(float v, float v2, float v3, float v4) {
        return false;
    }

    @Override
    public boolean panStop(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean zoom(float v, float v2) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
        return false;
    }
}
