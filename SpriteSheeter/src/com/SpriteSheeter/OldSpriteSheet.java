package com.SpriteSheeter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OldSpriteSheet {

    private final Map<String, int[][]> ID_ARRAY_MAP = new LinkedHashMap<>();
    private final Map<String, BufferedImage> LAYERS = new LinkedHashMap<>();
    private final Map<Integer, Sprite> SPRITES_HASMAP = new HashMap<>();
    private final List<String> HIDDEN_LAYERS = new ArrayList<>();
    private final BufferedImage POINTER_LAYER;
    private final int INITIAL_CANVAS_SIZE;
    private final int SPRITE_SIDE;
    private int tilesInColumn;
    private int tilesInRow;
    int arraySize;
    private String picturePath;

    public OldSpriteSheet(String picturePath, int spriteSide, int newCanvasSize) {
        int defaultSpriteSide = 16;
        int defaultNewCanvasSize = 80;

        this.picturePath = picturePath;
        SPRITE_SIDE = spriteSide <= 0 ? defaultSpriteSide : spriteSide;
        INITIAL_CANVAS_SIZE = newCanvasSize <= 0 ? defaultNewCanvasSize : newCanvasSize;
        arraySize = INITIAL_CANVAS_SIZE / SPRITE_SIDE;

        int frameThickness = 2;
        int pointerSize = INITIAL_CANVAS_SIZE + frameThickness;
        POINTER_LAYER = new BufferedImage(pointerSize, pointerSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D pictureGraphics = POINTER_LAYER.createGraphics();
        pictureGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        pictureGraphics.setPaint(Color.BLACK);
        pictureGraphics.drawRect(0, 0, INITIAL_CANVAS_SIZE + 1, INITIAL_CANVAS_SIZE + 1);
        pictureGraphics.setColor(Color.RED);
        pictureGraphics.drawRect(1, 1, 15, 15);
        pictureGraphics.dispose();

        addNewCanvas("default_layer");
        
        if (!picturePath.isEmpty()) {
            loadSpriteSheet();
        }
    }

    public int[][] getID_ARRAY_MAP(String idArrayName) {
        return ID_ARRAY_MAP.get(idArrayName);
    }

    public Map<String, BufferedImage> getLAYERS() {
        return LAYERS;
    }

    public Map<Integer, Sprite> getSPRITES_HASMAP() {
        return SPRITES_HASMAP;
    }

    public BufferedImage getPOINTER_LAYER() {
        return POINTER_LAYER;
    }

    public int getSPRITE_SIDE() {
        return SPRITE_SIDE;
    }

    public int getINITIAL_CANVAS_SIZE() {
        return INITIAL_CANVAS_SIZE;
    }

    public int getTilesInColumn() {
        return tilesInColumn;
    }

    public int getTilesInRow() {
        return tilesInRow;
    }

    /**
     * Builds a new BufferedImage out of the unhidden layers.
     * @return
     */
    public BufferedImage getCanvas() {
        //Mixing of all bufferedImages in layers in one single image.
        BufferedImage b = new BufferedImage(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = b.createGraphics();
        int i = 0;
        for (Map.Entry<String, BufferedImage> entry : LAYERS.entrySet()) {
            if (!HIDDEN_LAYERS.contains(entry.getKey())) {
                graphics2D.drawImage(LAYERS.get(entry.getKey()), null, 0, 0);
            }
        }
        return b;
    }

    /**
     * Scales the full canvas, all the layers and the frame layer, using the specified ration.
     * @param scaleRatio
     * @return
     */
    public BufferedImage getScaledCanvas(int scaleRatio) {
        int targetSide = (INITIAL_CANVAS_SIZE * scaleRatio) + 2;
        BufferedImage b = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_ARGB);
        b.createGraphics().drawImage(getFramedCanvas(), 0, 0, targetSide, targetSide, null);
        return b;
    }

    /**
     * Returns getCanvas without frame and pointer layer.
     * @param scaleRatio
     * @return
     */
    public BufferedImage getFramelessScaledCanvas(int scaleRatio) {
        int targetSide = (INITIAL_CANVAS_SIZE * scaleRatio) + 2; //<<<<<<<<<<<< Check this +2
        BufferedImage b = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_ARGB);
        b.createGraphics().drawImage(getCanvas(), 0, 0, targetSide, targetSide, null);
        return b;
    }

    /**
     * Returns getCanvas with frame and pointer layer.
     * @return
     */
    public BufferedImage getFramedCanvas() {
        /*
         * En b es + 2 porque se establece el ancho y el alto del canvas. Es decir, debe tener x initial pixels
         * más 2 porque en cada lado del canvas tiene que haber un pixel extra para el marco.
         * (Lado izquierdo = 1) + canvas = 80 + (lado derecho = 1) > todo suma 82
         * (Parte superior) = 1 + canvas = 80 + (parte inferior = 1) > todo suma 82
         * */

        BufferedImage b = new BufferedImage(INITIAL_CANVAS_SIZE + 2, INITIAL_CANVAS_SIZE + 2, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = b.createGraphics();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        graphics2D.drawImage(getCanvas(), 1, 1, INITIAL_CANVAS_SIZE,
                INITIAL_CANVAS_SIZE, null);
        graphics2D.drawImage(POINTER_LAYER, 0, 0, INITIAL_CANVAS_SIZE + 2,
                INITIAL_CANVAS_SIZE + 2, null);
        graphics2D.dispose();
        return b;
    }

    /**
     * Returns a string with all the data related to the current canvas. This data includes:
     * the amount of sprites on each side in the canvas, the path where the sprite sheet is
     * stored, the name of each layer in the canvas and the array of IDs for each layer.
     * @return
     */
    public String getIdArrayString() {
        int x = 0;
        int y = 0;
        StringBuilder variables = new StringBuilder();
        StringBuilder arrayNumbers = new StringBuilder();
        variables.append("//Sprites in side = ").append(arraySize).append("\n");
        variables.append("\n##").append(picturePath).append("##\n\n");
        for (Map.Entry<String, int[][]> array : ID_ARRAY_MAP.entrySet()) {
            variables.append("-\n//Layer: ").append(array.getKey()).append("\nint[][] ")
                    .append(array.getKey()).append(" = {\n{");
            int[][] layerArray = array.getValue();
            for (int i = 0; i < arraySize; i++) {
                for (int j = 0; j < arraySize; j++) {
                    arrayNumbers.append(layerArray[y][x]).append(" ");
                    variables.append(layerArray[y][x]);
                    x++;
                    if (x < arraySize) {
                        variables.append(",");
                    } else {
                        variables.append("}");
                    }
                }
                x = 0;
                y++;
                if (y < arraySize) {
                    variables.append(",\n{");
                } else {
                    variables.append("\n};\n");
                }
            }
            y = 0;
            variables.append("//").append(array.getKey()).append(":").append(arrayNumbers).append("\n-\n");
            arrayNumbers = new StringBuilder();
        }
        variables.append("-");
        return variables.toString();
    }

    /**
     * Returns the requested layer.
     * @param layerName
     * @return
     */
    public BufferedImage getLayer(String layerName) {
        return LAYERS.get(layerName);
    }

    /**
     * Sets the specified string as the path to the sprite sheet.
     * @param picturePath
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /**
     * Adds a new BufferedImage to the map LAYERS, and a new ID array to the MAP ID_ARRAY_MAP,
     * under the specified String.
     * @param layerName
     */
    public void addNewCanvas(String layerName) {
        BufferedImage canvas = new BufferedImage(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE, BufferedImage.TYPE_INT_ARGB);
        LAYERS.put(layerName, canvas);
        ID_ARRAY_MAP.put(layerName, new int[arraySize][arraySize]);
    }

    /**
     * Clear the bufferedImage of the specified layer.
     * @param layerToClear
     */
    public void clearLayer(String layerToClear) {
        LAYERS.put(layerToClear, new BufferedImage(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE,
                BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * Clear the bufferedImage of each layer.
     */
    public void clearAllLayers() {
        for (Map.Entry<String, BufferedImage> entry : LAYERS.entrySet()) {
            clearLayer(entry.getKey());
        }
    }

    /**
     * Removes the mapping for a key from LAYERS.
     * @param layerToDelete
     */
    public void deleteLayer(String layerToDelete) {
        LAYERS.remove(layerToDelete);
    }

    /**
     * Removes all the mappings from LAYERS.
     */
    public void deleteAllLayers() {
        LAYERS.clear();
    }

    /**
     * Adds the specified layer to HIDDEN_LAYERS.
     * @param layerName
     */
    public void hideLayer(String layerName) {
        HIDDEN_LAYERS.add(layerName);
    }

    /**
     * Removes the specified layer from HIDDEN_LAYERS.
     * @param layerName
     */
    public void showLayer(String layerName) {
        HIDDEN_LAYERS.remove(layerName);
    }

    /**
     * Builds layers out of imported ID arrays.
     * @param IDArray
     */
    public void buildLayers(Map<String, int[]> IDArray) {
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
        for (Map.Entry<String, int[]> newLayers : IDArray.entrySet()) {
            int sideSprites = (int) Math.sqrt(newLayers.getValue().length);
            int[] currentLayerIds = newLayers.getValue();
            int idCount = 0;
            int xSprite = 0;
            int ySprite = 0;
            int[][] loadedIdArray = new int[sideSprites][sideSprites];
            Graphics2D pictureGraphics = LAYERS.get(newLayers.getKey()).createGraphics();
            pictureGraphics.setComposite(ac);
            for (int y = 0; y < sideSprites; y++) {
                for (int x = 0; x < sideSprites; x++) {
                    pictureGraphics.drawImage(SPRITES_HASMAP.get(currentLayerIds[idCount]).getSprite(), xSprite, ySprite, null);
                    loadedIdArray[y][x] = currentLayerIds[idCount];
                    idCount++;
                    xSprite += SPRITE_SIDE;
                }
                xSprite = 0;
                ySprite += SPRITE_SIDE;
            }
            pictureGraphics.dispose();
            ID_ARRAY_MAP.put(newLayers.getKey(), loadedIdArray);
        }
    }

    /**
     * Builds the array of sprite buttons to be placed in the "Sprite list:" area,
     * out of the picture in the picturePath.
     */
    public void loadSpriteSheet() {
        try {
            //We use LinkedHashMap because we need to keep the order of the layers as they were
            // introduced in the map.
            BufferedImage picture = ImageIO.read(new File(picturePath));

            tilesInRow = picture.getWidth() / SPRITE_SIDE;
            tilesInColumn = picture.getHeight() / SPRITE_SIDE;

            int spriteHashMapKey = 0;
            int cornerX = 0;
            int cornerY = 0;
            for (int y = 0; y < tilesInColumn; y++) {
                for (int x = 0; x < tilesInRow; x++) {
                    SPRITES_HASMAP.put(spriteHashMapKey, new Sprite(picture, cornerX, cornerY, SPRITE_SIDE, spriteHashMapKey));
                    spriteHashMapKey++;
                    cornerX += SPRITE_SIDE;
                }
                cornerY += SPRITE_SIDE;
                cornerX = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}