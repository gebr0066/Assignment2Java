package sidescroller.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import sidescroller.entity.GenericEntity;
import sidescroller.entity.property.Entity;
import sidescroller.entity.property.HitBox;
import sidescroller.entity.property.Sprite;
import sidescroller.entity.sprite.*;
import sidescroller.entity.sprite.tile.BackgroundTile;
import sidescroller.entity.sprite.tile.FloraTile;
import sidescroller.entity.sprite.tile.PlatformTile;
import sidescroller.entity.sprite.tile.Tile;
import utility.Tuple;

public class MapBuilder implements MapBuilderInterface {
    private Tuple rowColCount;
    private Tuple dimension;
    private double scale;
    private Canvas canvas;
    private Entity background;
    private List<Entity> landMass;
    private List<Entity> other;

    protected MapBuilder() {
        landMass = new ArrayList<>();
        other = new ArrayList<>();
    }

    public static MapBuilder createBuilder() {
        return new MapBuilder();
    }

    public MapBuilder setGrid(Tuple rowColCount, Tuple dimension) {
        this.rowColCount = rowColCount;
        this.dimension = dimension;
        return this;
    }

    /**
     * <p>
     * build the background sprite.<br>
     * use {@link SpriteFactory#get(String)} and pass to it "Background". save in a {@link BackgroundSprite} variable.
     * <br>
     * on the return call {@link BackgroundSprite#init(double, Tuple, Tuple)} and pass scale, dimension, and Tuple.pair( 0, 0).
     * <br>
     * on the return call {@link BackgroundSprite#createSnapshot(Canvas, Tuple, BiFunction)} and pass canvas, rowColCount, and callback.
     * <br>
     * use {@link HitBox#build(double, double, double, double)} and pass 0, 0, scale * dimension.x() * rowColCount.y(), and scale * dimension.y() * rowColCount.x().
     * <br>
     * finally instantiate background using {@link GenericEntity#GenericEntity(Sprite, HitBox)}.
     * <br>
     * </p>
     *
     * @param callback - a lambda which takes two arguments (row and col) and returns a Tile enum of type {@link BackgroundTile}. will be used to generate background.
     * @return the current instance of this object.
     */
    public MapBuilder buildBackground(BiFunction<Integer, Integer, Tile> callback) {
        BackgroundSprite backgroundSprite = SpriteFactory.get("Background");

        backgroundSprite.init(scale, dimension, Tuple.pair(0, 0));
        backgroundSprite.createSnapshot(canvas, rowColCount, callback);
        HitBox hitBox = HitBox.build(0, 0, scale * dimension.x() * rowColCount.y(), scale * dimension.y() * rowColCount.x());
        background = new GenericEntity(backgroundSprite, hitBox);

        return this;
    }

    /**
     * <p>
     * build the background sprite.<br>
     * use {@link SpriteFactory#get(String)} and pass to it "Land". save in a {@link LandSprite} variable.
     * <br>
     * on the return call {@link LandSprite#init(double, Tuple, Tuple)} and pass scale, dimension, and Tuple.pair( colPos, rowPos).
     * <br>
     * on the return call {@link LandSprite#createSnapshot(Canvas, int, int)} and pass canvas, rowCount, and colCount.
     * <br>
     * use {@link HitBox#build(double, double, double, double)} and pass colPos * dimension.x() * scale, rowPos * dimension.y() * scale, scale * dimension.x() * colCount, and scale * dimension.y() * rowConut.
     * <br>
     * finally add the instance of {@link GenericEntity#GenericEntity(Sprite, HitBox)} to landMass list.
     * <br>
     * </p>
     *
     * @param rowPos   - first row from the top which the land will start.
     * @param colPos   - first column from the left which the land will start.
     * @param rowCount - number of rows which the land mass will cover.
     * @param colCount - number of columns which the land mass will cover.
     * @return the current instance of this object.
     */
    public MapBuilder buildLandMass(int rowPos, int colPos, int rowCount, int colCount) {
        LandSprite landSprite = SpriteFactory.get("Land");

        landSprite.init(scale, dimension, Tuple.pair(colPos, rowPos));
        landSprite.createSnapshot(canvas, rowCount, colCount);
        HitBox hitBox = HitBox.build(colPos * dimension.x() * scale, rowPos * dimension.y() * scale, scale * dimension.x() * colCount, scale * dimension.y() * rowCount);

        landMass.add(new GenericEntity(landSprite, hitBox));

        return this;
    }

    /**
     * <p>
     * build the background sprite.<br>
     * use {@link SpriteFactory#get(String)} and pass to it "Tree". save in a {@link TreeSprite} variable.
     * <br>
     * on the return call {@link TreeSprite#init(double, Tuple, Tuple)} and pass scale, dimension, and Tuple.pair( colPos, rowPos).
     * <br>
     * on the return call {@link TreeSprite#createSnapshot(Canvas, Tile)} and pass canvas and tile.
     * <br>
     * by default there is no hitbox.
     * <br>
     * finally add the instance of {@link GenericEntity#GenericEntity(Sprite, HitBox)} to other list.
     * <br>
     * </p>
     *
     * @param rowPos - first row from the top.
     * @param colPos - first column from the left.
     * @param tile   - a tree type from enum {@link FloraTile}.
     * @return the current instance of this object.
     */
    public MapBuilder buildTree(int rowPos, int colPos, Tile tile) {
        TreeSprite treeSprite = SpriteFactory.get("Tree");

        treeSprite.init(scale, dimension, Tuple.pair(colPos, rowPos));
        treeSprite.createSnapshot(canvas, tile);

        other.add(new GenericEntity(treeSprite, null));

        return this;
    }

    /**
     * <p>
     * build the background sprite.<br>
     * use {@link SpriteFactory#get(String)} and pass to it "Platform". save in a {@link PlatformSprite} variable.
     * <br>
     * on the return call {@link PlatformSprite#init(double, Tuple, Tuple)} and pass scale, dimension, and Tuple.pair( colPos, rowPos).
     * <br>
     * on the return call {@link PlatformSprite#createSnapshot(Canvas, Tile, int)} and pass canvas, tile, and length.
     * <br>
     * use {@link HitBox#build(double, double, double, double)} and pass (colPos + .5) * dimension.x() * scale, rowPos * dimension.y() * scale, scale * dimension.x() * (length - 1), and scale * dimension.y() / 2.
     * <br>
     * finally add the instance of {@link GenericEntity#GenericEntity(Sprite, HitBox)} to other list.
     * <br>
     * </p>
     *
     * @param rowPos - first row from the top.
     * @param colPos - first column from the left.
     * @param length - number of columns which the platform will stretch.
     * @param tile   - a platform type from enum {@link PlatformTile}.
     * @return
     */
    public MapBuilder buildPlatform(int rowPos, int colPos, int length, Tile tile) {
        PlatformSprite platformSprite = SpriteFactory.get("Platform");

        platformSprite.init(scale, dimension, Tuple.pair(colPos, rowPos));
        platformSprite.createSnapshot(canvas, tile, length);

        HitBox hitBox = HitBox.build((colPos + .5) * dimension.x() * scale, rowPos * dimension.y() * scale, scale * dimension.x() * (length - 1), scale * dimension.y() / 2);

        other.add(new GenericEntity(platformSprite, hitBox));

        return this;
    }

    /**
     * pass a list which will be populated by landmass first then other objects.
     *
     * @param list - a list to be populated by the created entities.
     * @return the populated list.
     * @throws NullPointerException if list is null which means it was not initialized.
     */
    public List<Entity> getEntities(List<Entity> list) {
        list.addAll(landMass);
        list.addAll(other);

        return list;
    }

    public MapBuilder setGridScale(double scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public MapBuilder setCanvas(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    public Entity getBackground() {
        return background;
    }

}
