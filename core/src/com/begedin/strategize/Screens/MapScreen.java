package com.begedin.strategize.Screens;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.begedin.strategize.Components.AI;
import com.begedin.strategize.Components.Movable;
import com.begedin.strategize.Core.CombatTeamManager;
import com.begedin.strategize.Input.InputManager;
import com.begedin.strategize.Input.MapAttackController;
import com.begedin.strategize.Input.MapDragController;
import com.begedin.strategize.Input.MapMovingController;
import com.begedin.strategize.Input.MapSelectorController;
import com.begedin.strategize.Maps.GameMap;
import com.begedin.strategize.Core.EntityFactory;
import com.begedin.strategize.Core.StrategizeGame;
import com.begedin.strategize.Rendering.MapHighlighter;
import com.begedin.strategize.Rendering.MapRenderer;
import com.begedin.strategize.Systems.AISystem;
import com.begedin.strategize.Systems.CameraMovementSystem;
import com.begedin.strategize.Systems.DamageSystem;
import com.begedin.strategize.Systems.FadingMessageRenderSystem;
import com.begedin.strategize.Systems.HudRenderSystem;
import com.begedin.strategize.Systems.MovementSystem;
import com.begedin.strategize.Systems.SpriteRenderSystem;
import com.begedin.strategize.Maps.MapTools;
import com.begedin.strategize.Systems.TurnManagementSystem;
import com.begedin.strategize.Utils.Pair;

/**
 * Created by Nikola Begedin on 24.11.13..
 */
public class MapScreen extends AbstractScreen {

    public static GameMap gameMap;
    private OrthographicCamera hudCam;

    float timer;

    private SpriteRenderSystem spriteRenderSystem;
    private HudRenderSystem hudRenderSystem;
    private FadingMessageRenderSystem fadingMessageRenderSystem;
    private TurnManagementSystem turnManagementSystem;

    private MapRenderer mapRenderer;
    private MapHighlighter mapHighlighter;

    public int selectedEntity;
    public int activeEntity;
    public Pair activeEntityCell;
    public Array<Pair> highlightedCells;
    public boolean renderMap;
    public boolean renderMovementRange;
    public boolean renderAttackRange;
    public boolean renderHighlighter;

    public InputManager inputManager;
    public Stage stage;

    public int cursor;

    public MapDragController controllerDrag;
    public MapSelectorController controllerSelector;
    public MapMovingController controllerMoving;
    public MapAttackController controllerAttack;

    public Array<Integer> unitOrder;
    public boolean moved = false;
    public boolean attacked = false;

    public CameraMovementSystem cameraMovementSystem;
    private boolean firstShow = true;

    public MapScreen(StrategizeGame game, SpriteBatch batch, World world) {
        super(game,world, batch);

        timer = 0f;

        cameraMovementSystem = new CameraMovementSystem(camera);
        activeEntityCell = new Pair(0,0);
        gameMap  = new GameMap();

        stage = new Stage();

        inputManager = new InputManager(camera, world, this, stage, gameMap);

        unitOrder = new Array<Integer>();
        setupWorld();
        fillWorldWithEntities();

        renderMap = true;
        renderMovementRange = false;
        renderAttackRange = false;
        renderHighlighter = false;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (firstShow) {
            cameraMovementSystem.move(activeEntityCell.x, activeEntityCell.y);
            firstShow = false;
        }

        if (renderMap) {
            mapRenderer.render();
            spriteRenderSystem.process();
        }

        if (renderHighlighter) {
            mapHighlighter.render(highlightedCells);
        }

        fadingMessageRenderSystem.process();

        if (renderHighlighter) {
            mapHighlighter.render(highlightedCells);
        }

        stage.act(delta);
        stage.draw();

        hudRenderSystem.process();

        cameraMovementSystem.process(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hudCam.setToOrtho(false, width, height);
        stage.setViewport(new ScalingViewport(Scaling.fit, width, height));
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        world.deleteSystem(hudRenderSystem);
        world.deleteSystem(spriteRenderSystem);
        world.deleteSystem(world.getSystem(MovementSystem.class));
    }

    private void setupWorld() {

        world.setManager(new CombatTeamManager());

        hudCam = new OrthographicCamera();

        mapRenderer = new MapRenderer(camera,batch,gameMap.map);
        mapHighlighter = new MapHighlighter(camera, batch);

        world.setSystem(new MovementSystem(gameMap,this));
        world.setSystem(new DamageSystem(gameMap));
        world.setSystem(new AISystem(this, gameMap));
        spriteRenderSystem = world.setSystem(new SpriteRenderSystem(camera,batch), true);
        hudRenderSystem = world.setSystem(new HudRenderSystem(hudCam, batch),true);
        fadingMessageRenderSystem = world.setSystem(new FadingMessageRenderSystem(camera,batch),true);
        turnManagementSystem = world.setSystem(new TurnManagementSystem(unitOrder), true);

        world.initialize();

    }

    private void fillWorldWithEntities() {
        int x, y;
        for (int i=0; i<5; i++) {
            do {
                x = MathUtils.random(MapTools.width()-1);
                y = MathUtils.random(MapTools.height()-1);
            } while (gameMap.cellOccupied(x, y) || (gameMap.map[x][y]==0) || (gameMap.map[x][y]==1));
            EntityFactory.createBlue(world,x,y,gameMap).addToWorld();
            do {
                x = MathUtils.random(MapTools.width()-1);
                y = MathUtils.random(MapTools.height()-1);
            } while (gameMap.cellOccupied(x, y) || (gameMap.map[x][y]==0) || (gameMap.map[x][y]==1));
            EntityFactory.createRed(world,x,y,gameMap).addToWorld();

            do {
                x = MathUtils.random(MapTools.width()-1);
                y = MathUtils.random(MapTools.height()-1);
            } while (gameMap.cellOccupied(x, y) || (gameMap.map[x][y]==0) || (gameMap.map[x][y]==1));
            EntityFactory.createNPC(world, x, y, gameMap).addToWorld();
        }

        Entity e = EntityFactory.createCursor(world);
        cursor = e.getId();
        e.addToWorld();

        // You have to process the world once to get all the entities loaded up with
        // the "Stats" component - I'm not sure why, but if you don't, the bag of entities
        // that turnManagementSystem gets is empty?
        world.process();
        // Running processTurn() once here initializes the unit order, and selects the first
        // entity to go
        processTurn();
    }

    public void processTurn() {
        turnManagementSystem.process();
        activeEntity = unitOrder.get(0);
        activeEntityCell = gameMap.getCoordinatesFor(activeEntity);

        Gdx.app.log("Active", Integer.toString(activeEntity));
        Gdx.app.log("Active cell", Integer.toString(activeEntityCell.x) + ", " + Integer.toString(activeEntityCell.y));

        // As long as that entity has a location, focus the camera on them
        if (activeEntityCell != null) {
            cameraMovementSystem.move(activeEntityCell.x, activeEntityCell.y);
        }

        // Try to get the next entity's AI
        AI ai = world.getEntity(activeEntity).getComponent(AI.class);

        // If they don't have AI, give control to the human player
        if (ai == null) inputManager.setEnabled(true);

            // Otherwise take control away, and begin that AI's processing.
        else {
            inputManager.setEnabled(false);
            ai.begin();
        }
    }

    public void startMoving(int x, int y) {
        cameraMovementSystem.move(x, y);
        //Gdx.input.setInputProcessor(null);
    }

    public void stopMoving() {
        //      Gdx.input.setInputProcessor(inputSystem);
    }

    public boolean cameraMoving() {
        return cameraMovementSystem.active;
    }

    public void setHighlightColor(float r, float g, float b, float a) {
        mapHighlighter.setColor(r,g,b,a);
    }

    public void highlightMovementRange() {
        renderHighlighter = true;
        setHighlightColor(0f, 0f, 0.2f, 0.3f);
    }

    public void highlightAttackRange() {
        renderHighlighter = true;
        setHighlightColor(0.5f, 0f, 0f, 0.3f);
    }

    public void addComponent(Component component, int entityId) {
        if (entityId < 0) return;
        Entity e = world.getEntity(entityId);
        e.addComponent(component);
        e.changedInWorld();
    }

}
