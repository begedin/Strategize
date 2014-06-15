package com.begedin.pucaj.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Created by Nikola Begedin on 01.01.14..
 *
 * A framed menu made in scene2d UI
 */
public class FramedMenu {

    private Image frame;
    private ScrollPane scrollPane;
    private Table table;
    private Skin skin;
    // max's represent the largest we want the menu getting
    private float maxHeight, maxWidth;

    // fontHeight and rows are used to estimate how tall our table is (frustratingly,
    // table.getHeight() always gives me 0.0)
    private float fontHeight;
    private int rows;

    // The parent menu is the one we will focus on if the user closes this one
    private FramedMenu parent;

    public FramedMenu(Skin skin, float preferredWidth, float preferredHeight) {
        this(skin, preferredWidth, preferredHeight, null);
    }

    public FramedMenu(Skin skin, float maxWidth, float maxHeight, FramedMenu parent) {
        this.skin = skin;

        table = new Table();

        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;

        fontHeight = skin.getFont("default-font").getCapHeight() + 14;
        rows = 0;
        this.parent = parent;
    }

    // Adds a button to the menu
    public void addButton(String label, ChangeListener listener, boolean active) {
        addButton(label, "", listener, active);
    }

    // Adds a button to the menu, with a secondary label (like MP cost) aligned to the right
    public void addButton(String label, String secondaryLabel, ChangeListener listener, boolean active) {
        Label.LabelStyle style;
        if (active) style = skin.get(Label.LabelStyle.class);
        else style = skin.get("inactive",Label.LabelStyle.class);

        Label l = new Label(label, style);

        Button b = new Button(skin.get(Button.ButtonStyle.class));
        b.addListener(listener);
        b.setDisabled(!active);

        b.add(l).left().expandX();
        b.add(new Label(secondaryLabel, style)).padRight(15f);

        table.add(b).left().padLeft(5f).expandX().fillX();
        table.row();

        rows++;
    }

    // Adds the frame and scrollpane to the specified stage at the specified location.
    // Sizes the scrollpane to the (estimated) table size, up to a maximum given
    // in the constructor.
    public void addToStage(final Stage stage, float x, float y) {
        scrollPane = new ScrollPane(table, skin);
        frame = new Image(skin.getPatch("frame"));

        // If the user presses "ESC", close this menu and focus on the "parent"
        stage.setKeyboardFocus(scrollPane);
        scrollPane.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == 131) { //escape
                    // If this menu is invisible, don't do anything
                    if (!frame.isVisible()) return false;

                    // If there is a parent, get rid of this
                    // menu and focus on it
                    if (parent != null) {
                        stage.setKeyboardFocus(parent.scrollPane);
                        parent.enable();
                        clear();
                    }
                    // Otherwise this must be the last one, so just clear it all
                    else {
                        stage.clear();
                    }
                }
                return true;
            }
        });

        // Go ahead and add them to the stage
        stage.addActor(scrollPane);
        stage.addActor(frame);

        // If the table does not fill our maximum size, resize it to our
        // estimated height, and disable scrolling both x and y
        if (rows*fontHeight < maxHeight) {
            scrollPane.setScrollingDisabled(true, true);
            scrollPane.setHeight(rows*fontHeight);
        }

        // Otherwise, it's bigger than our maximum size, so we need to
        // enable vertical scrolling, and set the height to our max.
        else {
            scrollPane.setScrollingDisabled(true, false);
            scrollPane.setHeight(maxHeight);
        }

        // For now, no matter what, the width is set to maxWidth
        scrollPane.setWidth(maxWidth);

        table.setBackground(skin.getDrawable("menuTexture"));

        // Move the table to the far left of the scrollPane
        table.left();

        // Prevent the scrollPane from scrolling (and snapping back) beyond the scroll limits
        scrollPane.setOverscroll(false, false);
        scrollPane.setFillParent(false);
        // If y is negative, center the scrollPane vertically on the stage
        if (y < 0) scrollPane.setY((stage.getHeight() - scrollPane.getHeight())/2f);
        else scrollPane.setY(y - scrollPane.getHeight());
        // If x is negative, do likewise
        if (x < 0) scrollPane.setX((stage.getWidth() - scrollPane.getWidth())/2f);
        else scrollPane.setX(x);

        // Make sure we can't touch the frame - that would make the scrollPane
        // inaccessible
        frame.setTouchable(Touchable.disabled);

        // Now set the Frame's position and size based on the scrollPane's stuff
        frame.setX(scrollPane.getX()-1);
        frame.setY(scrollPane.getY()-3);
        frame.setWidth(maxWidth + 4);
        frame.setHeight(scrollPane.getHeight() + 4);

        // In case they became invisible earlier, make them visible now
        scrollPane.setVisible(true);
        frame.setVisible(true);
    }

    // Wipe all the buttons off, and remove widgets from stage (and reset row count)
    public void clear() {
        table.clear();
        table.setColor(1f, 1f, 1f, 1);
        if (scrollPane != null) scrollPane.remove();
        if (frame != null) frame.remove();
        rows = 0;
    }

    public float getY() {
        return scrollPane.getY();
    }

    // Make it untouchable, and gray/transparent it out
    public void disable() {
        scrollPane.setTouchable(Touchable.disabled);
        table.setColor(0.7f, 0.7f, 0.7f, 0.7f);
    }

    // Re-enable
    public void enable() {
        scrollPane.setTouchable(Touchable.enabled);
        table.setColor(1,1,1,1);
    }

    // Make invisible or visible
    public void setVisible(boolean visible) {
        if (frame == null) return;
        frame.setVisible(visible);
        scrollPane.setVisible(visible);
    }

    // Let someone else know who your parent is - currently used in MenuBuilder
    public FramedMenu getParent () {
        return parent;
    }
}
