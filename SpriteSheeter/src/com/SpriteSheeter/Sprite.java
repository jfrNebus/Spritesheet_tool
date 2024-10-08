package com.SpriteSheeter;

import java.awt.image.BufferedImage;


public class Sprite {
    private final int id;
    private final BufferedImage sprite;

    public Sprite(BufferedImage spriteSheet, int topXCoordinate, int topYCoordinate, int spriteSide, int id){
        this.id = id;
        sprite = spriteSheet.getSubimage(topXCoordinate, topYCoordinate, spriteSide, spriteSide);
    }

    public int getId() {
        return id;
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}
