package com.SpriteSheeter;

import java.awt.image.BufferedImage;

/**
 * The {@code Sprite} class is meant to create a {@code BufferedImage}, out of a region
 * of another {@code BufferedImage}.
 */
public class Sprite {
    private final int id;
    private final BufferedImage sprite;

    /**
     * Constructs a {@code BufferedImage} as the result of calling the method getSubimage,
     * by the provided BufferedImage parameter of this class. The parameters provided to the
     * class must include the top X and Y coordinates of the region in the main BufferedImage
     * parameter, that will be used as subimage. The parameters will also include the side, in
     * pixels, of the square made by the region, and the ID of the sprite.
     *
     * @param spriteSheet the BufferedImage that will be used to create the subimage.
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
     * Returns the {@code sprite} item, the BufferedImage, of this Sprite.
     * */
    public BufferedImage getSprite() {
        return sprite;
    }
}