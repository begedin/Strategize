package com.begedin.strategize.UI;

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
import com.begedin.strategize.Abilities.CombatAction;
import com.begedin.strategize.Components.Stats;
import com.begedin.strategize.Input.InputManager;
import com.begedin.strategize.Input.MenuProcessor;

/**
 * Created by Nikola Begedin on 31.12.13..
 */
public class MenuBuilder {

    private InputManager inputManager;
    private MenuProcessor menuProcessor;
    private Stage stage;
    private Skin skin;

    private FramedMenu turnMenu;
    private FramedMenu statsMenu;

    public MenuBuilder(InputManager inputManager, MenuProcessor menuProcessor, Stage stage) {
        this.inputManager = inputManager;
        this.menuProcessor = menuProcessor;
        this.stage = stage;


        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        turnMenu = new FramedMenu(skin, 128, 300);
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

        // Stat based actions
        final Stats stats = e.getComponent(Stats.class);
        if (stats != null) {
            // If they click it, process that action
            ChangeListener offensiveListener = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    menuProcessor.action(stats.characterClass.offensiveAction);
                }
            };
            turnMenu.addButton(stats.characterClass.offensiveAction.name, offensiveListener, inputManager.canAct());

            // If they click it, process that action
            final ChangeListener defensiveListener = new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    menuProcessor.action(stats.characterClass.defensiveAction);
                }
            };
            turnMenu.addButton(stats.characterClass.defensiveAction.name, defensiveListener, inputManager.canAct());
        }

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
        statsMenu.setVisible(visible);
    }
}
