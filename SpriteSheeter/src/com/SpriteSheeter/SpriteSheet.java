package com.SpriteSheeter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class used to handle each sprite in a spritesheet, as individual {@link Sprite} objects.
 */
public class SpriteSheet {

    private final HashMap<Integer, Sprite> SPRITES_HASHMAP = new HashMap<>();
    private int spriteSide;
    private int tilesInColumn;
    private int tilesInRow;
    private String picturePath;

    /**
     * Returns a string of the absolute path to the spritesheet picture.
     *
     * @return the absolute path to the spritesheet picture as {@link String}.
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * Returns the {@link HashMap} associated to the {@link SpriteSheet} object.
     *
     * @return the {@link HashMap} called {@code SPRITES_HASHMAP}
     */
    public HashMap<Integer, Sprite> getSPRITES_HASHMAP() {
        return SPRITES_HASHMAP;
    }

    /**
     * Returns an int value showing the size of the current sprite side.
     *
     * @return the current sprite side value, {@code spriteSide}.
     */
    public int getSpriteSide() {
        return spriteSide;
    }

    /**
     * Returns an int value showing the current amount of tiles in each column.
     *
     * @return the current amount of tiles in each column, {@code tilesInColumn}
     */
    public int getTilesInColumn() {
        return tilesInColumn;
    }

    /**
     * Returns an int value showing the current amount of tiles in each row.
     *
     * @return the current amount of tiles in each row, {@code tilesInRow}.
     */
    public int getTilesInRow() {
        return tilesInRow;
    }

    /**
     * Sets the specified string as the path to the spritesheet picture.
     *
     * @param picturePath the absolute path to the new spritesheet picture.
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**
     * Sets the sprite side size with the specified value.
     *
     * @param spriteSide the desired new sprite side size.
     */
    public void setSpriteSide(int spriteSide) {
        this.spriteSide = spriteSide;
    }

    /**
     * Splits the spriteSheet {@link BufferedImage} in objects {@link Sprite}. The sprites will
     * be stored in {@code SPRITES_HASHMAP}.
     *
     * @param picture the spritesheet image.
     */
    public void loadSpriteSheet(BufferedImage picture) {
        if(picture != null) {
            tilesInRow = picture.getWidth() / spriteSide;
            tilesInColumn = picture.getHeight() / spriteSide;

            int spriteHashMapKey = 1;
            int cornerX = 0;
            int cornerY = 0;
            for (int y = 0; y < tilesInColumn; y++) {
                for (int x = 0; x < tilesInRow; x++) {
                    SPRITES_HASHMAP.put(spriteHashMapKey, new Sprite(picture, cornerX, cornerY, spriteSide, spriteHashMapKey));
                    spriteHashMapKey++;
                    cornerX += spriteSide;
                }
                cornerY += spriteSide;
                cornerX = 0;
            }
        }
    }
}
