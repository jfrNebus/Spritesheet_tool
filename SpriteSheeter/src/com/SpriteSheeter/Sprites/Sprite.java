package com.SpriteSheeter.Sprites;

import java.awt.image.BufferedImage;

/**
 * The {@link Sprite} class is meant to create a {@link BufferedImage}, out of a region
 * of another {@link BufferedImage}.
 */
public class Sprite {
    private final int id;
    private final BufferedImage sprite;

    /**
     * Constructs a {@link BufferedImage} as the result of calling the method getSubimage(),
     * by the provided {@link BufferedImage} parameter of this class. The parameters provided
     * to the class must include the top X and Y coordinates of the region in the main
     * {@link BufferedImage} parameter, that will be used as subimage. The parameters will
     * also include the side, in pixels, of the square made by the region, and the ID of the
     * sprite.
     *
     * @param spriteSheet the {@link BufferedImage} that will be used to create the subimage.
     * @param topXCoordinate The top X coordinate of the subimage region in the spriteSheet.
     * @param topYCoordinate The top Y coordinate of the subimage region in the spriteSheet.
     * @param spriteSide The side, in pixels, of the subimage region.
     * @param id The Sprite id. The name set to the sprite.
     * */
    public Sprite(BufferedImage spriteSheet, int topXCoordinate, int topYCoordinate, int spriteSide, int id) {
        this.id = id;
        sprite = spriteSheet.getSubimage(topXCoordinate, topYCoordinate, spriteSide, spriteSide);
    }

    /**
     * Returns the {@code id} of this Sprite.
     * */
    public int getId() {
        return id;
    }

    /**
     * Returns the {@code sprite} item, the {@link BufferedImage}, of this Sprite.
     * */
    public BufferedImage getSprite() {
        return sprite;
    }
}