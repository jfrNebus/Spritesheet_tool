package com.SpriteSheeter.UI;

import com.SpriteSheeter.Sprites.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


 class Canvas {
    private BufferedImage POINTER_LAYER;
    private int arraySize;
    private int canvasSize = 0;
    private int spriteSide = 0;
    private final List<String> HIDDEN_LAYERS = new ArrayList<>();
    private final Map<String, int[][]> ID_ARRAY_MAP = new LinkedHashMap<>();
    private final Map<String, BufferedImage> LAYERS = new LinkedHashMap<>();

    /**
     * Builds a new {@link BufferedImage} out of the unhidden layers.
     *
     * @return a {@link BufferedImage} that is used as main canvas.
     */
    public BufferedImage getCanvas() {
        BufferedImage b = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = b.createGraphics();
        for (Map.Entry<String, BufferedImage> entry : LAYERS.entrySet()) {
            if (!HIDDEN_LAYERS.contains(entry.getKey())) {
                graphics2D.drawImage(LAYERS.get(entry.getKey()), null, 0, 0);
            }
        }
        return b;
    }

    /**
     * Returns a string with all the data related to the current canvas. This data includes:
     * <ul>
     *    <li>The amount of sprites on each side of the canvas.</li>
     *    <li>The size of canvas' side and the side of sprite side.</li>
     *    <li>The absolute path where the spritesheet is located.</li>
     *    <li>The name of each layer in the canvas and their corresponding ID arrays.</li>
     * </ul>
     *
     * @return a {@link String} to be written in a txt file during the export code operation.
     */
    public String getExportString(String picturePath) {
        int x = 0;
        int y = 0;
        StringBuilder data = new StringBuilder();
        StringBuilder arrayNumbers = new StringBuilder();
        data.append("//Sprites in side = ").append(arraySize).append("\n");
        data.append("\n//Sprite side = ").append(spriteSide).append("\n");
        data.append("\n##").append(picturePath).append("##\n\n");
        for (Map.Entry<String, int[][]> array : ID_ARRAY_MAP.entrySet()) {
            data.append("-\n//Layer: ").append(array.getKey()).append("\nint[][] ")
                    .append(array.getKey()).append(" = {\n{");
            int[][] layerArray = array.getValue();
            for (int i = 0; i < arraySize; i++) {
                for (int j = 0; j < arraySize; j++) {
                    int value = layerArray[y][x];
                    arrayNumbers.append(value).append(" ");
                    data.append(value).append(x < arraySize - 1 ? "," : "}");
                    x++;
                }
                x = 0;
                y++;
                data.append(y < arraySize ? ",\n{" : "\n};\n");
            }
            y = 0;
            data.append("//").append(array.getKey()).append(":").append(arrayNumbers).append("\n-\n");
            arrayNumbers = new StringBuilder();
        }
        data.append("-");
        return data.toString();
    }

    /**
     * Returns the {@link BufferedImage} resulted by mixing the value returned by {@code getCanvas()} with
     * {@code POINTER_LAYER}.
     *
     * @return a {@link BufferedImage} used as canvas with an extra layer used as frame.
     */
    public BufferedImage getFramedCanvas() {
        BufferedImage b = new BufferedImage(canvasSize + 2, canvasSize + 2, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = b.createGraphics();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        graphics2D.drawImage(getCanvas(), 1, 1, canvasSize,
                canvasSize, null);
        graphics2D.drawImage(POINTER_LAYER, 0, 0, canvasSize + 2,
                canvasSize + 2, null);
        graphics2D.dispose();
        return b;
    }

    /**
     * Returns the value to which the specified key is mapped in {@code ID_ARRAY_MAP}, or null if
     * this map contains no mapping for the key,
     *
     * @param idArrayName the key for the desired mapped value.
     * @return a two-dimensional array of integers or null.
     */
    public int[][] getID_ARRAY_MAP(String idArrayName) {
        return ID_ARRAY_MAP.get(idArrayName);
    }

    /**
     * Returns the size of canvas side.
     *
     * @return an integer which value is the size of canvas.
     */
    public int getCanvasSize() {
        return canvasSize;
    }

    /**
     * Returns the value to which the specified key is mapped in {@code LAYERS}, or null if
     * this map contains no mapping for the key,
     *
     * @param layerName The key for the desired mapped value.
     * @return a {@link BufferedImage} for the specified layer name.
     */
    public BufferedImage getLayer(String layerName) {
        return LAYERS.get(layerName);
    }

    /**
     * Returns the map {@code LAYERS}.
     *
     * @return the {@link Map} object {@code LAYERS}.
     */
    public Map<String, BufferedImage> getLAYERS() {
        return LAYERS;
    }

    /**
     * Returns the layer which displays the square pointer and the frame.
     *
     * @return the {@code BufferedImage} {@code POINTER_LAYER} which represents the frame layer.
     */
    public BufferedImage getPOINTER_LAYER() {
        return POINTER_LAYER;
    }

    /**
     * Returns a scaled copy of the provided {@link BufferedImage}, using the specified scale ratio.
     *
     * @param canvasToScale The {@link BufferedImage} to scale.
     * @param scaleRatio    The ratio to be used to scale {@code canvasToScale}.
     * @return a scaled {@link BufferedImage} version of the provided {@link BufferedImage}.
     */
    public BufferedImage getScaledCanvas(BufferedImage canvasToScale, int scaleRatio) {
        int targetSide = (canvasSize * scaleRatio) + 2;
        BufferedImage b = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_ARGB);
        b.createGraphics().drawImage(canvasToScale, 0, 0, targetSide, targetSide, null);
        return b;
    }

    /**
     * Returns an int value showing the current sprite side size.
     *
     * @return the int that represents {@code spriteSide}.
     */
    public int getSpriteSide() {
        return spriteSide;
    }

    /**
     * Adds a new {@link BufferedImage} to the map {@code LAYERS}, and a new ID array to the {@code MAP ID_ARRAY_MAP},
     * under the specified String.
     *
     * @param layerName the name key for the new layer.
     */
    public void addNewCanvas(String layerName) {
        BufferedImage canvas = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        LAYERS.put(layerName, canvas);
        ID_ARRAY_MAP.put(layerName, new int[arraySize][arraySize]);
    }

    /**
     * Builds {@code LAYERS} out of previously imported ID arrays.
     *
     * @param IDArray the map which stores each array of IDs for each layer.
     * @param spritesHashmap the map of sprites and their IDs.
     */
    public void buildLayers(Map<String, int[]> IDArray, HashMap<Integer, Sprite> spritesHashmap) {
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
                    int newId = currentLayerIds[idCount] <= spritesHashmap.size() ? currentLayerIds[idCount] : 0;
                    if (newId > 0) {
                        pictureGraphics.drawImage(spritesHashmap.get(newId).getSprite(), xSprite, ySprite, null);
                        loadedIdArray[y][x] = newId;
                    }
                    idCount++;
                    xSprite += spriteSide;
                }
                xSprite = 0;
                ySprite += spriteSide;
            }
            pictureGraphics.dispose();
            ID_ARRAY_MAP.put(newLayers.getKey(), loadedIdArray);
        }
    }

    /**
     * Clears the {@link BufferedImage} for the specified layer.
     *
     * @param layerToClear name of the layer to clear
     */
    public void clearLayer(String layerToClear) {
        LAYERS.put(layerToClear, new BufferedImage(canvasSize, canvasSize,
                BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * Clears the {@link BufferedImage} for each layer.
     */
    public void clearAllLayers() {
        for (Map.Entry<String, BufferedImage> entry : LAYERS.entrySet()) {
            clearLayer(entry.getKey());
        }
    }

    /**
     * Removes the mapping for the specified key in {@code LAYERS}.
     *
     * @param layerToDelete name of the layer to be deleted.
     */
    public void deleteLayer(String layerToDelete) {
        LAYERS.remove(layerToDelete);
        ID_ARRAY_MAP.remove(layerToDelete);
    }

    /**
     * Removes all the mappings from {@code LAYERS}.
     */
    public void deleteAllLayers() {
        LAYERS.clear();
        ID_ARRAY_MAP.clear();
    }

    /**
     * Adds the specified layer to the {@link List} {@code HIDDEN_LAYERS}.
     *
     * @param layerName name of the layer to be hidden.
     */
    public void hideLayer(String layerName) {
        HIDDEN_LAYERS.add(layerName);
    }

    /**
     * Checks if {@code LAYERS} contains any value mapped for the specified key.
     *
     * @param layerName name of the layer.
     * @return true if there is any mapping in {@code LAYERS} for the specified key, or null if there is not.
     */
    public boolean hasLayer(String layerName) {
        return LAYERS.containsKey(layerName);
    }

    /**
     * Initializes the values {@code spriteSide} and {@code canvasSize}, with the specified parameters. It
     * also creates the {@code POINTER_LAYER} layer.
     *
     * @param side the sprite side size.
     * @param newCanvasSize the canvas side size.
     */
    public void initializeCanvas(int side, int newCanvasSize) {
        spriteSide = side;
        canvasSize = newCanvasSize;
        arraySize = canvasSize / spriteSide;

        int frameThickness = 2;
        int pointerSize = canvasSize + frameThickness;
        POINTER_LAYER = new BufferedImage(pointerSize, pointerSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D pictureGraphics = POINTER_LAYER.createGraphics();
        pictureGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        pictureGraphics.setPaint(Color.BLACK);
        pictureGraphics.drawRect(0, 0, canvasSize + 1, canvasSize + 1);
        pictureGraphics.setColor(Color.RED);
        pictureGraphics.drawRect(1, 1, spriteSide - 1, spriteSide - 1);
        pictureGraphics.dispose();
    }

    /**
     * Removes the specified layer from {@link List} {@code HIDDEN_LAYERS}.
     *
     * @param layerName the name of the layer to be removed.
     */
    public void showLayer(String layerName) {
        HIDDEN_LAYERS.remove(layerName);
    }
}
