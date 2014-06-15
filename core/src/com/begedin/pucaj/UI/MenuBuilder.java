package com.begedin.pucaj.UI;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.begedin.pucaj.Abilities.CombatAction;
import com.begedin.pucaj.Components.Stats;
import com.begedin.pucaj.Input.InputManager;
import com.begedin.pucaj.Input.MenuProcessor;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class MenuBuilder {

    private InputManager inputManager;
    private MenuProcessor menuProcessor;
    private Stage stage;
    private Skin skin;

    private FramedMenu turnMenu;
    private FramedMenu actionMenu;
    private FramedMenu abilityMenu;
    private FramedMenu statsMenu;

    public MenuBuilder(InputManager inputManager, MenuProcessor menuProcessor, Stage stage) {
        this.inputManager = inputManager;
        this.menuProcessor = menuProcessor;
        this.stage = stage;


        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        turnMenu = new FramedMenu(skin, 128, 300);
        actionMenu = new FramedMenu(skin, 128, 400, turnMenu);
        abilityMenu = new FramedMenu(skin, 230, 400, actionMenu);
        statsMenu = new FramedMenu(skin, 128, 200);
    }

    public void buildTurnMenu(final Entity e) {

        turnMenu.clear();

        // Move button
        ChangeListener moveListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuProcessor.move();
            }
        };
        turnMenu.addButton("Move", moveListener, inputManager.canMove());

        // Action button
        ChangeListener actionListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buildActionMenu(e, 30, turnMenu.getY());
            }
        };
        turnMenu.addButton("Action", actionListener, inputManager.canAct());

        // Wait button
        ChangeListener waitListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuProcessor.selectWait();
            }
        };
        turnMenu.addButton("Wait", waitListener, true);

        // Stats button
        ChangeListener statListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float w = 200f;
                float h = 100f;
                float x = (stage.getWidth() - w)/2f;
                float y = (stage.getHeight() - h)/2f;
                buildDialog("", "Status is not yet implemented", x, y, w, h, new TextButton("OK",skin));
            }
        };
        turnMenu.addButton("Status", statListener, true);

        turnMenu.addToStage(stage, 30, stage.getHeight() - 30);
    }

    public void buildActionMenu(Entity e, float x, float y) {
        actionMenu.clear();
        actionMenu.getParent().disable();

        // Stat based actions
        final Stats stats = e.getComponent(Stats.class);
        if (stats != null) {

            // Attack
            if (stats.regularAttack != null) {
                ChangeListener attack = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        menuProcessor.action(stats.regularAttack);
                    }
                };
                actionMenu.addButton(stats.regularAttack.name, attack, inputManager.canAct());
            }

            // Primary class
            if (stats.primaryClass != null) {
                ChangeListener primary = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        buildAbilityMenu(stats.primaryClass.actions, stats.energy);
                    }
                };
                actionMenu.addButton(stats.primaryClass.name, primary, inputManager.canAct());
            }

            // Secondary class
            if (stats.secondaryClass != null) {
                ChangeListener secondary = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        buildAbilityMenu(stats.secondaryClass.actions, stats.energy);
                    }
                };
                actionMenu.addButton(stats.secondaryClass.name, secondary, inputManager.canAct());
            }
        }

        // Item
        ChangeListener item = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float w = 200f;
                float h = 100f;
                float x = (stage.getWidth() - w)/2f;
                float y = (stage.getHeight() - h)/2f;
                buildDialog("", "Items have not yet been implemented", x, y, w, h, new TextButton("OK",skin));
            }
        };
        actionMenu.addButton("Item", item, inputManager.canAct());

        actionMenu.addToStage(stage, 30, turnMenu.getY()-5);
    }

    private void buildAbilityMenu(Array<CombatAction> actions, int mp) {
        abilityMenu.clear();
        abilityMenu.getParent().disable();

        // Loop through all the actions
        for (final CombatAction action : actions) {
            // If they click it, process that action
            ChangeListener listener = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    menuProcessor.action(action);
                }
            };
            // If it has an MP cost, display it as a secondary label
            // The button is active if the character has not already attacked,
            // and if the MP cost is affordable
            if (action.cost > 0) abilityMenu.addButton(action.name, ""+action.cost, listener, inputManager.canAct() && (action.cost < mp));
            else abilityMenu.addButton(action.name, listener, inputManager.canAct() && (action.cost < mp));
        }

        abilityMenu.addToStage(stage, -1, -1);
    }

    public void buildStatsMenu(Entity e) {
        statsMenu.clear();
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float w = 200f;
                float h = 100f;
                float x = (stage.getWidth() - w)/2f;
                float y = (stage.getHeight() - h)/2f;
                buildDialog("", "Status is not yet implemented", x, y, w, h, new TextButton("OK",skin));
            }
        };
        statsMenu.addButton("Status", listener, true);
        statsMenu.addToStage(stage, 30, stage.getHeight()-30);
    }

    public void buildDialog(String title, String message, float x, float y, float width, float height, Button... buttons) {
        FramedDialog fd = new FramedDialog(skin, title, message, width, height);
        for (Button b : buttons) {
            b.align(Align.center);
            fd.addButton(b);
        }
        fd.addToStage(stage, x, y);
    }

    public TextButton getTextButton(String text, ChangeListener listener) {
        TextButton button = new TextButton(text, skin);
        if (listener != null) button.addListener(listener);
        return button;
    }

    public void setMenusVisible(boolean visible) {
        turnMenu.setVisible(visible);
        actionMenu.setVisible(visible);
        abilityMenu.setVisible(visible);
        statsMenu.setVisible(visible);
    }
}
