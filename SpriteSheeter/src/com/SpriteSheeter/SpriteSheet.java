package com.SpriteSheeter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class allows to handle each sprite in a spritesheet as individual {@code Sprite} objects.*/
public class SpriteSheet {

    private final HashMap<Integer, Sprite> SPRITES_HASMAP = new HashMap<>();
    private int spriteSide;
    private int tilesInColumn;
    private int tilesInRow;
    private String picturePath;

    public String getPicturePath() {
        return picturePath;
    }

    public HashMap<Integer, Sprite> getSPRITES_HASMAP() {
        return SPRITES_HASMAP;
    }

    public int getSpriteSide() {
        return spriteSide;
    }

    public int getTilesInColumn() {
        return tilesInColumn;
    }

    public int getTilesInRow() {
        return tilesInRow;
    }

    /**
     * Sets the specified string as the path to the sprite sheet.
     * @param picturePath
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**
     * */
    public void setSpriteSide(int spriteSide) {
        this.spriteSide = spriteSide;
    }

    /**
     * Builds the array of sprite buttons to be placed in the "Sprite list:" area,
     * out of the picture in the picturePath.
     */
    public void loadSpriteSheet() {
        try {
            BufferedImage picture = ImageIO.read(new File(picturePath));

            tilesInRow = picture.getWidth() / spriteSide;
            tilesInColumn = picture.getHeight() / spriteSide;

            int spriteHashMapKey = 0;
            int cornerX = 0;
            int cornerY = 0;
            for (int y = 0; y < tilesInColumn; y++) {
                for (int x = 0; x < tilesInRow; x++) {
                    SPRITES_HASMAP.put(spriteHashMapKey, new Sprite(picture, cornerX, cornerY, spriteSide, spriteHashMapKey));
                    spriteHashMapKey++;
                    cornerX += spriteSide;
                }
                cornerY += spriteSide;
                cornerX = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
