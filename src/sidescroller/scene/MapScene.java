package sidescroller.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Background;
import sidescroller.animator.AnimatorInterface;
import sidescroller.entity.property.Entity;
import sidescroller.entity.property.HitBox;
import javafx.scene.canvas.Canvas;
import sidescroller.entity.sprite.tile.BackgroundTile;
import sidescroller.entity.sprite.tile.FloraTile;
import sidescroller.entity.sprite.tile.PlatformTile;
import utility.Tuple;

public class MapScene implements MapSceneInterface {
    private Tuple count;
    private Tuple size;
    private double scale;
    private AnimatorInterface animator;
    private List<Entity> staticShapes;
    private List<Entity> players;
    private BooleanProperty drawBounds;
    private BooleanProperty drawFPS;
    private BooleanProperty drawGrid;
    private Entity background;

    // Initialize the lists and BooleanProperties inside of the constructor.
    // to initialize a BooleanProperties use the class SimpleBooleanProperty.
    public MapScene() {
        staticShapes = new ArrayList<Entity>();
        players = new ArrayList<Entity>();
        drawBounds = new SimpleBooleanProperty();
        drawFPS = new SimpleBooleanProperty();
        drawGrid = new SimpleBooleanProperty();
    }

    @Override
    public BooleanProperty drawFPSProperty() {
        return drawFPS;
    }

    /**
     * @return the value of {@link BooleanProperty#get()}
     */
    @Override
    public boolean getDrawFPS() {
        return drawFPSProperty().get();
    }

    @Override
    public BooleanProperty drawBoundsProperty() {
        return drawBounds;
    }

    @Override
    public boolean getDrawBounds() {
        return drawBoundsProperty().get();
    }

    @Override
    public BooleanProperty drawGridProperty() {
        return drawGrid;
    }

    @Override
    public boolean getDrawGrid() {
        return drawGridProperty().get();
    }

    /**
     * save date in the correct variables.
     *
     * @param count - number of rows and columns in the grid.
     * @param size  - width and height of each cell in grid.
     * @param scale - a double multiplier for width and height of each grid cell.
     * @return current instance of this class.
     */
    @Override
    public MapSceneInterface setRowAndCol(Tuple count, Tuple size, double scale) {
        this.count = count;
        this.size = size;
        this.scale = scale;
        return this;

    }

    @Override
    public Tuple getGridCount() {
        return count;
    }

    @Override
    public Tuple getGridSize() {
        return size;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void start() {
        if (this.animator != null) {
            this.animator.start();
        }
    }

    @Override
    public void stop() {
        if (this.animator != null) {
            this.animator.stop();
        }
    }

    /**
     * @return static list. this list will hold all entities short of background, player, and any Entity that moves.
     */

    @Override
    public List<Entity> staticShapes() {
        return staticShapes;
    }

    @Override
    public List<Entity> players() {
        return players;
    }

    /**
     * <p>
     * this method creates the static entities in the game.
     * <br>
     * use {@link MapBuilder#createBuilder()} to get and instance of MapBuilder called mb.
     * <br>
     * on mb call methods {@link MapBuilder#setCanvas(Canvas)}, {@link MapBuilder#setGrid(Tuple, Tuple)}, and {@link MapBuilder#setGridScale(double)}.
     * <br>
     * call all or any combination of build methods in MapBuilder to create custom map, does not have to be complex. one landmass and a tree is good enough.
     * <br>
     * call {@link MapBuilder#getBackground()} and {@link MapBuilder#getEntities(List)} to retrieve the built entities.
     * </p>
     *
     * @param canvas
     * @return
     */
    @Override
    public MapSceneInterface createScene(Canvas canvas) {
        MapBuilder mb = MapBuilder.createBuilder();
        mb.setCanvas(canvas).setGrid(count, size).setGridScale(scale);
        mb.buildLandMass(9, 5, 5, 20);
        mb.buildPlatform(6, 15, 4, PlatformTile.STONE);
        mb.buildTree(2, 8, FloraTile.TREE);
        mb.buildBackground((row, col) -> {
        	if (row == 0) {
                return BackgroundTile.MORNING_TOP;
            } else if (row < 4) {
            	Random r = new Random();
                if(r.nextInt(10) > 7) {
                    return BackgroundTile.MORNING_CLOUD;
                }
            }

            return BackgroundTile.MORNING;
        });

        mb.getEntities(staticShapes);
        background = mb.getBackground();
        return this;
    }

    /**
     * @param hitbox - hitbox of an entity to check it is it still in background bounds.
     * @return true of hitbox of background containsBouns of argument.
     */
    @Override
    public boolean inMap(HitBox hitbox) {
        if (background != null) {
            return background.getHitBox().containsBounds(hitbox);
        }
        return false;
    }

    @Override
    public MapSceneInterface setAnimator(AnimatorInterface newAnimator) {
        this.animator = newAnimator;
        return this;
    }

    @Override
    public Entity getBackground() {
        return background;
    }
}
