package com.SpriteSheeter.UI;

import com.SpriteSheeter.Enums.LayoutAxisEnum;
import com.SpriteSheeter.Enums.SubWindowOptionsEnum;
import com.SpriteSheeter.Sprites.SpriteSheet;
import com.SpriteSheeter.Strings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to build the whole window of the program. It deals with all the layout related matters, and it is used
 * as main frame for the whole back-end processes of all classes in used in this project.
 */

public class UserInterface implements KeyListener {

    private boolean fillingBrush = false;
    private boolean toggleMapMovement = false;
    private boolean toggleSpriteMovement = false; //UserInterface_notes
    private BufferedImage previousSprite;
    private final Canvas CANVAS = new Canvas();
    private int direction = 0; //0 = no movement, 1 = left, 2 = right, 3 = up, 4 = down
    private int id = 0;
    private int mapScale = 1;
    private int movementIncrement; //UserInterface_notes
    private int spriteListScale = 1; //UserInterface_notes
    private int y = 0; //UserInterface_notes
    private int x = 0; //UserInterface_notes
    private final int FRAME_GAP = 1; //UserInterface_notes
    private final int MAX_LABEL_LENGTH = 17;
    private final int MAP_SCALE_RATIO = 1;
    //User interface
    private JFrame frame;
    private JScrollPane picScroller;
    private JLabel spriteLabel;
    private JScrollPane spriteListScroller;
    private JPanel spritesPanel;
    private final JViewport SPRITE_VIEW = new JViewport();
    private JLabel actualLayerLabel;
    private JPanel layerSelector;
    private JButton newLayerB;
    private JButton biggerMap;
    private JButton smallerMap;
    private JButton biggerSprite;
    private JButton smallerSprite;
    private final JTextArea TA = new JTextArea();
    private JLabel picLabel;
    private final JViewport MAP_VIEW = new JViewport();
    private JMenu layerManagement;
    private JMenuItem loadSpriteSheet;
    private JMenuItem exportCode;
    private JMenuItem exportCanvas;
    //User subMenu
    private JDialog subFrame;
    private JTextField firstTF;
    private JTextField secondTF;
    //
    MouseAdapter mouseAdapter = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            TA.setText(Strings.NEW_LAYER_REQUIRED);
        }
    };
    private final SpriteSheet SPRITESHEET = new SpriteSheet();
    private String actualCanvas = Strings.NO_LAYER;

    /**
     * Looks for specific data in the provided string. Once extracted,
     * the data is assigned to different variables and used to build
     * a Map<String, int[]>. This map contains the mapping of all layer
     * names found in the input string to their corresponding ID arrays.
     * The data extracted is listed below:
     *
     * <ul>
     *    <li>The amount of sprites on each side of the canvas.</li>
     *    <li>The size of canvas' side and the side of sprite's side.</li>
     *    <li>The absolute path where the spritesheet is located.</li>
     *    <li>The name of each layer in the canvas and their corresponding ID arrays.</li>
     * </ul>
     *
     * @param loadedData the string read from the txt file during the importCode operation.
     * @return a {@link Map}, mapping the name of layers and their IDs arrays.
     */
    private Map<String, int[]> getImportedData(String loadedData) {
        //Update the existing documentation
        boolean keep = true;
        Map<String, int[]> layers = new LinkedHashMap<>();

        Function<String, String> matcherFunction = string -> {
            String output = "";
            Matcher matcher = Pattern.compile(string).matcher(loadedData);
            while (matcher.find()) {
                output = matcher.group();
            }
            return output;
        };

        String matchedString = matcherFunction.apply(Strings.IMPORTED_FIRST_REGEX);
        int amountOfSprites = matchedString.matches("\\d+") ? (int) Math.pow(Integer.parseInt(matchedString), 2) : 0;

        if (amountOfSprites == 0) {
            keep = false;
        }

        if (keep) {
            if ((CANVAS.getCanvasSize() == 0) && (CANVAS.getSpriteSide() == 0) && (SPRITESHEET.getSpriteSide() == 0)) {
                String matchedString2 = matcherFunction.apply(Strings.IMPORTED_SECOND_REGEX);
                int side = matchedString2.matches("\\d+") ? Integer.parseInt(matchedString2) : 0;
                int newCanvasSize = Integer.parseInt(matchedString) * side;
                if (side != 0 && newCanvasSize != 0) {
                    CANVAS.initializeCanvas(side, newCanvasSize);
                    SPRITESHEET.setSpriteSide(side);
                    movementIncrement = side;
                } else {
                    keep = false;
                }
            }
        } else {
            return null;
        }

        if (keep) {
            Matcher matcher = Pattern.compile(Strings.IMPORTED_FOURTH_REGEX).matcher(loadedData);
            while (matcher.find()) {
                layers.put(matcher.group(), null);
            }
            for (Map.Entry<String, int[]> numbersMap : layers.entrySet()) {
                String numbersRegex = Strings.IMPORTED_FIFTH_REGEX_1 + numbersMap.getKey() + Strings.IMPORTED_FIFTH_REGEX_2;
                String[] justNumbers = matcherFunction.apply(numbersRegex).split("\\s");
                if (amountOfSprites == justNumbers.length) {
                    int[] numbers = new int[justNumbers.length];
                    for (int i = 0; i < justNumbers.length; i++) {
                        numbers[i] = Integer.parseInt(justNumbers[i]);
                    }
                    numbersMap.setValue(numbers);
                } else {
                    SubWindow.runInfoWindow(SubWindowOptionsEnum.SQUARE_ERROR);
                    layers = null;
                    break;
                }
            }
        } else {
            return null;
        }
        return layers;
    }

    /**
     * <p>
     * Extracts a path string, given in a specific format, from a string. The string must be
     * wrapped by double # on each side, and it must start with a drive unit letter.
     * </p>
     * ## + X:path + ##<br>
     * Example: ##C:\Project\TestA##
     *
     * @param loadedData the string where the path will be extracted.
     * @return a {@link String} whose value is the extracted path.
     */
    private String getLoadedPath(String loadedData) {
        String path = "";
        Matcher matcher = Pattern.compile(Strings.LOADED_PATH_REGEX).matcher(loadedData);
        while (matcher.find()) {
            path = matcher.group();
        }
        matcher.reset();
        return path;
    }

    /**
     * Sets the value of {@code mapScale} by adding the specified value. The parameter of
     * this method can be either a positive or negative value.
     *
     * @param movementIncrement the value to be added to {@code mapScale}.
     */
    private void setMapScale(int movementIncrement) {
        this.mapScale += movementIncrement;
    }

    /**
     * Sets a new value for spriteListScale.
     *
     * @param spriteListScale the new value.
     */
    private void setSpriteListScale(int spriteListScale) {
        this.spriteListScale = spriteListScale;
    }

    /**
     * Builds up the whole layout to hold the layer selector button and the hide layer checkbox. This layout is
     * then added to the parent {@link JPanel}.
     *
     * @param layerName the name of the new layer.
     */
    private void addNewLayerButtons(String layerName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JButton layerButton = new JButton(layerName);
        layerButton.addKeyListener(this);
        layerButton.setFocusable(false);
        layerButton.addActionListener(e -> {
            actualCanvas = layerName;
            actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " + (actualCanvas.length() > MAX_LABEL_LENGTH ? actualCanvas.substring(0, 5) + "..." + actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
        });

        JRadioButton radioButton = new JRadioButton();
        radioButton.addKeyListener(this);
        radioButton.setFocusable(true);
        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                CANVAS.hideLayer(layerName);
            } else {
                CANVAS.showLayer(layerName);
            }
            updateMainCanvas(mapScale);
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(radioButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(layerButton);
        panel.add(Box.createHorizontalGlue());

        layerSelector.add(Box.createRigidArea(new Dimension(0, 3)));
        layerSelector.add(panel);
        layerSelector.updateUI();

        TA.setText("");
    }

    //UserInterface_notes

    /**
     * Turns into individual buttons each sprite in a spritesheet. Splits {@code SPRITESHEET} into smaller objects
     * {@code BufferedImage}, the sprites. These sprites are then assigned to a button, which is added to a
     * {@link JPanel}. This panel is added to {@code spritesPanel}. Each {@code JPanel} holds all the sprites in a row.
     *
     * @param spriteListScaleRatio each sprite dimension is multiplied by this value in order to display the sprite
     *                             at the desired size.
     */
    private void buildSpritesList(int spriteListScaleRatio) {
        int spriteSide = SPRITESHEET.getSpriteSide();
        int targetSide = spriteSide * spriteListScaleRatio;
        int j = 0;
        BufferedImage newImage;
        for (int i = -1; i < SPRITESHEET.getTilesInColumn(); i++) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
            spritesPanel.add(jPanel);
            for (; j < (SPRITESHEET.getTilesInColumn() * SPRITESHEET.getTilesInRow()) + 1; j++) {
                BufferedImage spriteToPrint;
                newImage = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_ARGB);
                Dimension dimension = new Dimension();
                JButton button = new JButton();
                button.addKeyListener(this);
                button.setFocusable(false);
                if (i == -1) {
                    button.setText("Empty sprite");
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    spriteToPrint = new BufferedImage(spriteSide, spriteSide, BufferedImage.TYPE_INT_ARGB);
                    dimension.width = targetSide * SPRITESHEET.getTilesInRow();
                    dimension.height = targetSide;
                } else {
                    spriteToPrint = SPRITESHEET.getSPRITES_HASHMAP().get(j).getSprite();
                    newImage.createGraphics().drawImage(spriteToPrint, 0, 0, targetSide, targetSide, null);
                    button.setIcon(new ImageIcon(newImage));
                    dimension.setSize(targetSide, targetSide);
                }
                button.setPreferredSize(dimension);
                button.setMaximumSize(dimension);
                int innerId = j;
                int finalI = i;
                button.addActionListener(e -> {
                    System.out.println("Sprite pressed = " + innerId);
                    if (!actualCanvas.equals(Strings.NO_LAYER)) {
                        Graphics2D pictureGraphics = CANVAS.getLayer(actualCanvas).createGraphics();
                        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
                        pictureGraphics.setComposite(ac);
                        pictureGraphics.drawImage(spriteToPrint, x, y, null);
                        pictureGraphics.dispose();
                        int arrayIndexY = y / spriteSide;
                        int arrayIndexX = x / spriteSide;
                        int[][] returnedArray = CANVAS.getID_ARRAY_MAP(actualCanvas);
                        returnedArray[arrayIndexY][arrayIndexX] = (finalI == -1) ? innerId : SPRITESHEET.getSPRITES_HASHMAP().get(innerId).getId();
                        id = innerId;
                        updateMainCanvas(mapScale);
                        previousSprite = spriteToPrint;
                        spriteLabel.setText(Strings.SPRITE_LABEL_NAME + " - Sprite selected: " + id);
                    } else {
                        TA.setText("No layer selected.");
                    }
                });
                jPanel.add(button);
                if (j % SPRITESHEET.getTilesInRow() == 0) {
                    break;
                }
            }
            j++;
        }
    }

    /**
     * Adds new options buttons to {@code layerSelector}, and new layers to canvas, for each layer name in the set
     * provided as parameter.
     *
     * @param newlayers the {@link Set} containing all the layer names.
     */
    private void buildNewLayers(Set<String> newlayers) {
        boolean firstLayerName = true;
        for (String newLayer : newlayers) {
            addNewLayerButtons(newLayer);
            CANVAS.addNewCanvas(newLayer);
            if (firstLayerName) {
                actualCanvas = newLayer;
                actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " + (actualCanvas.length() > MAX_LABEL_LENGTH ? actualCanvas.substring(0, 5) + "..." + actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
                firstLayerName = false;
            }
        }
    }

    /**
     * This method is called in order to delete all the layers. The actual layer label is reset. The maps
     * {@code LAYERS} and {@code ID_ARRAY_MAP} in the class {@link Canvas} are cleared. The buttons and
     * checkboxes to control the layers are removed. {@code actualCanvas} is set as NO_LAYER. Finally, the main
     * canvas is updated.
     */
    private void deleteAllLayer() {
        actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " ");
        CANVAS.deleteAllLayers();
        layerSelector.removeAll();
        actualCanvas = Strings.NO_LAYER;
        updateMainCanvas(mapScale);
    }

    /**
     * Enables all the inactive elements of the user interface. It removes the mouse listener associated to these
     * elements, and changes the {@link JTextArea} options.
     */
    private void enableUI() {
        loadSpriteSheet.setEnabled(true);
        layerManagement.setEnabled(true);
        exportCode.setEnabled(true);
        exportCanvas.setEnabled(true);
        biggerSprite.setEnabled(true);
        biggerMap.setEnabled(true);
        smallerSprite.setEnabled(true);
        smallerMap.setEnabled(true);
        newLayerB.setEnabled(true);
        TA.setEditable(true);
        loadSpriteSheet.removeMouseListener(mouseAdapter);
        layerManagement.removeMouseListener(mouseAdapter);
        exportCode.removeMouseListener(mouseAdapter);
        exportCanvas.removeMouseListener(mouseAdapter);
        biggerSprite.removeMouseListener(mouseAdapter);
        biggerMap.removeMouseListener(mouseAdapter);
        smallerSprite.removeMouseListener(mouseAdapter);
        smallerMap.removeMouseListener(mouseAdapter);
        newLayerB.removeMouseListener(mouseAdapter);
        TA.removeMouseListener(mouseAdapter);
        TA.setCaretColor(Color.BLACK);
        TA.setText("");
        picScroller.requestFocus();
    }

    /**
     * Returns a new {@link JMenuItem} to be used as "Delete actual layer" option in the dropdown options menu.
     * The action listener for this {@link JMenuItem} is configured before it is returned.
     *
     * @return a configured {@link JMenuItem} intended for use as {@code Delete actual layer}.
     */
    private JMenuItem handleDeleteLayer() {
        JMenuItem deleteLayer = new JMenuItem(Strings.DELETE_LAYER_ITEM);
        deleteLayer.addActionListener(e -> {
            CANVAS.deleteLayer(actualCanvas);
            layerSelector.removeAll();
            for (Map.Entry<String, BufferedImage> layers : CANVAS.getLAYERS().entrySet()) {
                addNewLayerButtons(layers.getKey());
            }
            actualLayerLabel.setText("Actual layer: " + Strings.NO_LAYER);
            actualCanvas = Strings.NO_LAYER;
            updateMainCanvas(mapScale);
        });
        return deleteLayer;
    }

    /**
     * Returns a new {@link JMenuItem} to be used as "Export canvas" option in the dropdown options menu.
     * The action listener for this {@link JMenuItem}, together with other attributes, are configured before
     * it is returned.
     *
     * @return a configured {@link JMenuItem} intended for use as {@code Export canvas}.
     */
    private JMenuItem handleExportCanvas() {
        JMenuItem exportCanvas = new JMenuItem(Strings.EXPORT_CANVAS_ITEM);
        exportCanvas.addActionListener(e -> subMenu("exportCanvas"));
        exportCanvas.setEnabled(false);
        exportCanvas.addMouseListener(mouseAdapter);
        return exportCanvas;
    }

    /**
     * Returns a new {@link JMenuItem} to be used as "Export" option in the dropdown options menu.
     * The action listener for this {@link JMenuItem}, together with other attributes, are configured before
     * it is returned.
     *
     * @return a configured {@link JMenuItem} intended for use as {@code Export}.
     */
    private JMenuItem handleExportCode() {
        JMenuItem exportCode = new JMenuItem("Export");
        exportCode.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("OK");
            try {
                int returnVal = fc.showOpenDialog(exportCode);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File notePad = new File(fc.getSelectedFile() + ".txt");
                    FileWriter writer = new FileWriter(notePad);
                    String arrayPrinted = CANVAS.getExportString(SPRITESHEET.getPicturePath());
                    writer.write(arrayPrinted);
                    writer.close();
                    TA.setText(Strings.EXPORT_SAVED_MESSAGE + fc.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        exportCode.setEnabled(false);
        exportCode.addMouseListener(mouseAdapter);
        return exportCode;
    }

    /**
     * Returns a new {@link JMenuItem} to be used as "Import" option in the dropdown options menu.
     * The action listener for this {@link JMenuItem} is configured before it is returned.
     *
     * @return a configured {@link JMenuItem} intended for use as {@code Import}.
     */
    private JMenuItem handleImportCode() {
        JMenuItem importCode = new JMenuItem("Import");
        importCode.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(importCode);
            if (!(returnVal == JFileChooser.CANCEL_OPTION)) {
                boolean validPictureFile;
                BufferedImage newSpriteSheet;
                try {
                    validPictureFile = Files.probeContentType(fc.getSelectedFile().toPath()).startsWith("text");
                    if (validPictureFile) {
                        File notePad = new File(fc.getSelectedFile().getAbsolutePath());
                        Scanner scanner = new Scanner(notePad);
                        StringBuilder stringBuilder = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            stringBuilder.append(scanner.nextLine());
                            stringBuilder.append("\n");
                        }
                        String newPicturePath = getLoadedPath(stringBuilder.toString());
                        newSpriteSheet = ImageIO.read(new File(newPicturePath));
                        validPictureFile = newSpriteSheet != null;
                        Map<String, int[]> loadedMap = getImportedData(stringBuilder.toString());
                        if (loadedMap != null && validPictureFile) {
                            enableUI();
                            initializePicLabel();
                            if(!CANVAS.getLAYERS().isEmpty()) {
                                deleteAllLayer();
                            }
                            buildNewLayers(loadedMap.keySet());
                            spritesPanel.removeAll();
                            SPRITESHEET.setPicturePath(newPicturePath);
                            SPRITESHEET.loadSpriteSheet(newSpriteSheet);
                            buildSpritesList(spriteListScale);
                            CANVAS.buildLayers(loadedMap, SPRITESHEET.getSPRITES_HASHMAP());
                            updateMainCanvas(mapScale);
                        } else {
                            if (!validPictureFile) {
                                showInfoMessage(SubWindowOptionsEnum.INVALID_IMAGE_PATH);
                            } else {
                                showInfoMessage(SubWindowOptionsEnum.CORRUPTED_FILE);
                            }
                            importCode.doClick();
                        }
                    } else {
                        showInfoMessage(SubWindowOptionsEnum.INVALID_TEXT);
                        importCode.doClick();
                    }
                } catch (IOException | NullPointerException exception) {
                    if (exception instanceof IOException) {
                        showInfoMessage(SubWindowOptionsEnum.CORRUPTED_FILE);
                    } else {
                        showInfoMessage(SubWindowOptionsEnum.INVALID_PATH);
                    }
                    importCode.doClick();
                }
            }
        });
        return importCode;
    }

    /**
     * Returns a preconfigured dropdown options menu.
     * This menu contains {@link JMenu} and {@link JMenuItem} items. The
     * options menu is structured as listed below.
     *
     * <ul>
     *    <li>Options</li>
     *    <ul>
     *        <li>Create a new canvas</li>
     *        <li>Layer management</li>
     *        <ul>
     *            <li>Clear layer</li>
     *            <ul>
     *                <li>Clear actual layer</li>
     *                <li>Clear all layers</li>
     *            </ul>
     *            <li>Delete layer</li>
     *            <ul>
     *                <li>Delete actual layer</li>
     *                <li>Delete all layers</li>
     *            </ul>
     *        </ul>
     *        <li>Import / export code</li>
     *        <ul>
     *            <li>Import</li>
     *            <li>Export</li>
     *        </ul>
     *        <li>Export canvas</li>
     *        <li>Help</li>
     *     </ul>
     * </ul>
     *
     * @return a preconfigured {@link JMenuBar}, intended for use as dropdown options menu.
     */
    private JMenuBar handleJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu options = new JMenu("Options");

        //1
        JMenuItem newCanvas = new JMenuItem(Strings.NEW_CANVAS_MENU);
        newCanvas.addActionListener(e -> {
            subMenu("requestNewCanvasValues");
        });
        //2
        loadSpriteSheet = handleLoadSpriteSheet();
        //3
        layerManagement = new JMenu(Strings.LAYER_MANAGEMENT_MENU);
        layerManagement.setEnabled(false);
        layerManagement.addMouseListener(mouseAdapter);
        //3.1
        JMenu clearLayerMenu = new JMenu(Strings.CLEAR_LAYER_MENU);
        //3.1.1
        JMenuItem clearLayer = new JMenuItem(Strings.CLEAR_LAYER_ITEM);
        clearLayer.addActionListener(e -> {
            CANVAS.clearLayer(actualCanvas);
            updateMainCanvas(mapScale);
        });
        //3.1.2
        JMenuItem clearAllLayer = new JMenuItem(Strings.CLEAR_ALL_LAYER_ITEM);
        clearAllLayer.addActionListener(e -> {
            CANVAS.clearAllLayers();
            updateMainCanvas(mapScale);
        });
        //3.2
        JMenu deleteLayerMenu = new JMenu(Strings.DELETE_LAYER_MENU);
        //3.2.1
        JMenuItem deleteLayer = handleDeleteLayer();
        //3.2.1
        JMenuItem deleteAllLayerMenu = new JMenuItem(Strings.DELETE_ALL_LAYER_ITEM);
        deleteAllLayerMenu.addActionListener(e -> {
            deleteAllLayer();
            actualLayerLabel.setText("Actual layer: " + Strings.NO_LAYER);
            actualCanvas = Strings.NO_LAYER;
        });

        //4
        JMenu importExport = new JMenu(Strings.IMPORT_EXPORT_MENU);
        //4.1
        JMenuItem importCode = handleImportCode();
        //4.2
        exportCode = handleExportCode();
        //5
        exportCanvas = handleExportCanvas();
        //6
        JMenuItem help = new JMenuItem(Strings.HELP_ITEM);
        help.addActionListener(e -> showInfoMessage(SubWindowOptionsEnum.HELP));

        jMenuBar.add(options);
        options.add(newCanvas);
        options.add(loadSpriteSheet);
        options.add(layerManagement);
        layerManagement.add(clearLayerMenu);
        clearLayerMenu.add(clearLayer);
        clearLayerMenu.add(clearAllLayer);
        layerManagement.add(deleteLayerMenu);
        deleteLayerMenu.add(deleteLayer);
        deleteLayerMenu.add(deleteAllLayerMenu);
        options.add(importExport);
        importExport.add(importCode);
        importExport.add(exportCode);
        options.add(exportCanvas);
        options.add(help);

        jMenuBar.setVisible(true);

        return jMenuBar;
    }

    /**
     * Returns a new {@link JMenuItem} to be used as "Load spritesheet" option in the dropdown options menu.
     * The action listener for this {@link JMenuItem}, together with other attributes, are configured before
     * it is returned.
     *
     * @return a configured {@link JMenuItem} intended for use as {@code Load spritesheet}.
     */
    private JMenuItem handleLoadSpriteSheet() {
        JMenuItem loadSpriteSheet = new JMenuItem(Strings.LOAD_SPRITESHEET_ITEM);
        loadSpriteSheet.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(loadSpriteSheet);
            if (!(returnVal == JFileChooser.CANCEL_OPTION)) {
                String newPicturePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean validPictureFile = false;
                BufferedImage newPicture = null;
                try {
                    newPicture = ImageIO.read(new File(newPicturePath));
                    validPictureFile = newPicture != null;
                } catch (IOException | NullPointerException | SecurityException ex) {
                    showInfoMessage(SubWindowOptionsEnum.INVALID_PATH);
                    loadSpriteSheet.doClick();
                }
                if (validPictureFile) {
                    if (spritesPanel.getComponentCount() > 0) {
                        spritesPanel.removeAll();
                    }
                    SPRITESHEET.setPicturePath(newPicturePath);
                    SPRITESHEET.loadSpriteSheet(newPicture);
                    buildSpritesList(spriteListScale);
                    spritesPanel.updateUI();
                } else {
                    showInfoMessage(SubWindowOptionsEnum.INVALID_IMAGE);
                    loadSpriteSheet.doClick();
                }
            }
        });
        loadSpriteSheet.setEnabled(false);
        loadSpriteSheet.addMouseListener(mouseAdapter);
        return loadSpriteSheet;
    }

    /**
     * Returns a new {@link MouseAdapter} to be used by {@code actualLayerLabel}.
     *
     * @return a preconfigured {@link MouseAdapter}.
     */
    private MouseAdapter handleMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                TA.setText(actualCanvas);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                TA.setText("");
            }
        };
    }

    /**
     * Returns a new {@link JButton} to be used as {@code newLayerB} inside {@code windowSetup()}. The action listener
     * for this {@link JButton} is configured before it is returned.
     *
     * @param width  the width size of the button.
     * @param height the height size of the button.
     * @return a configured {@link JButton} intended for use as {@code newLayerB}.
     */
    private JButton   handleNewLayerButton(int width, int height) {
        JButton button = newButton(Strings.NEW_LAYER_BUTTON, width, height);
        button.addActionListener(e -> {
            String newLayerName = TA.getText().trim();
            if (!newLayerName.isEmpty() && !CANVAS.hasLayer(newLayerName)) {
                newLayerName = newLayerName.replaceAll("\\s+", "_").toLowerCase();
                addNewLayerButtons(newLayerName);
                if ((CANVAS.getSpriteSide() == 0) && (CANVAS.getCanvasSize() == 0) && (SPRITESHEET.getSpriteSide() == 0)) {
                    subMenu("requestNewCanvasValues");
                }
                CANVAS.addNewCanvas(newLayerName);
                actualCanvas = newLayerName;
                actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " + (actualCanvas.length() > MAX_LABEL_LENGTH ? actualCanvas.substring(0, 5) + "..." + actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
            } else {
                showInfoMessage(SubWindowOptionsEnum.INVALID_LAYER_HELP);
            }
        });
        return button;
    }


    /**
     * Returns a new {@link JButton} to be used as option confirmation in the window {@code subMenu(String)}.
     * The action listener for this {@link JMenuItem}, together with other attributes, are configured before
     * it is returned.
     *
     * @param width  the width size of the button.
     * @param height the height size of the button.
     * @param menuName the kind of action to be performed once the returned button is pressed.
     * @return a configured {@link JMenuItem} intended for use as {@code Load spritesheet}.
     */
    private JButton handleOkButton(int width, int height, String menuName) {
        JButton ok = new JButton("OK");
        ok.setMaximumSize(new Dimension(width, height));
        ok.setMinimumSize(ok.getMaximumSize());
        ok.addActionListener(e -> {
            String firstTFS = firstTF.getText().isEmpty() ? "" : firstTF.getText();
            String secondTFS = secondTF.getText();
            if (menuName.equals("exportCanvas")) {
                if (!firstTFS.isEmpty() && !secondTFS.isEmpty()) {
                    boolean secondNumber = secondTFS.matches("\\d+");
                    if (secondNumber) {
                        try {
                            ImageIO.write(CANVAS.getScaledCanvas(CANVAS.getCanvas(), Integer.parseInt(secondTFS)), "png", new File(firstTFS + ".png"));
                            subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                        } catch (IOException ex) {
                            showInfoMessage(SubWindowOptionsEnum.INVALID_PATH);
                        }
                    } else {
                        showInfoMessage(SubWindowOptionsEnum.INVALID_SCALE);
                    }
                }
            } else if (menuName.equals("requestNewCanvasValues")) {
                boolean notEmpty = (!firstTFS.isEmpty()) || (!secondTFS.isEmpty());
                boolean condition = false;
                int side = 0;
                int newCanvasSize = 0;

                if (notEmpty) {
                    boolean firstNumber = firstTFS.matches("\\d+");
                    boolean secondNumber = secondTFS.matches("\\d+");
                    if (firstNumber && secondNumber) {
                        condition = true;
                        side = Integer.parseInt(firstTFS);
                        newCanvasSize = Integer.parseInt(secondTFS);
                    } else {
                        if (!firstNumber & !secondNumber) {
                            showInfoMessage(SubWindowOptionsEnum.SHEET_AND_SPRITE);
                        } else if (!firstNumber) {
                            showInfoMessage(SubWindowOptionsEnum.SPRITE_SIDE_FAIL);
                        } else {
                            showInfoMessage(SubWindowOptionsEnum.SPRITESHEET_FAIL);
                        }
                    }
                }
                if (condition) {
                    subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                    CANVAS.initializeCanvas(side, newCanvasSize);
                    SPRITESHEET.setSpriteSide(side);
                    movementIncrement = side;
                    initializePicLabel();
                    enableUI();
                }
            }
        });
        return ok;
    }

    /**
     * Initializes and configures the {@link JLabel} to be shown in the {@link JScrollPane} that holds the main canvas.
     */
    private void initializePicLabel() {
        picLabel = new JLabel(new ImageIcon(CANVAS.getFramedCanvas()));
        picLabel.addKeyListener(this);
        picLabel.setFocusable(true);
        picLabel.setVisible(true);
        MAP_VIEW.setView(picLabel);
        picScroller.setViewportView(MAP_VIEW);
    }

    /**
     * This method is called when there is a keyPressed event, specifically when a directional key or any of "A",
     * "D", "W", "S", are pressed. It first evaluates if any of the movement modifiers are true, so it calls the function
     * {@link #moveViewPort()}. If no movement modifier is true, then it will call {@link #movePointer()}.
     */
    private void movementSelector() {
        if (toggleMapMovement || toggleSpriteMovement) {
            moveViewPort();
        } else {
            int width = CANVAS.getPOINTER_LAYER().getWidth() - SPRITESHEET.getSpriteSide();
            switch (direction) {
                case 1:
                    if ((x - movementIncrement) >= 0) {
                        movePointer();
                    }
                    break;
                case 2:
                    if ((x + movementIncrement) <= width) {
                        movePointer();
                    }
                    break;
                case 3:
                    if ((y - movementIncrement) >= 0) {
                        movePointer();
                    }
                    break;
                case 4:
                    if ((y + movementIncrement) <= width) {
                        movePointer();
                    }
                    break;
            }
        }
    }

    /**
     * Method used to move the square used as pointer to a new location. A square {@link BufferedImage} is created.
     * The image uses the TYPE_INT_ARGB pixel format, which supports alpha transparency. Upon creation,
     * all pixels are initialized to fully transparent black (ARGB = 0x00000000), meaning the image starts
     * as entirely transparent. The current location of the pointer is painted with this new {@link BufferedImage},
     * in the {@code POINTER_LAYER}. After getting the new coordinates, the pointer will be printed in the
     * new location.
     * <br>If {@code fillingBrush} is true, the {@code previousSprite} will  be printed in the
     * {@code actualCanvas} layer.
     */
    private void movePointer() {
        int spriteSide = SPRITESHEET.getSpriteSide();
        Color pointer = Color.RED;
        Graphics2D pointerGraphics = CANVAS.getPOINTER_LAYER().createGraphics();
        pointerGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        pointerGraphics.drawImage(new BufferedImage(spriteSide, spriteSide, BufferedImage.TYPE_INT_ARGB), x + FRAME_GAP, y + FRAME_GAP, spriteSide, spriteSide, null);

        switch (direction) {
            case 1:
                x -= movementIncrement;
                break;
            case 2:
                x += movementIncrement;
                break;
            case 3:
                y -= movementIncrement;
                break;
            case 4:
                y += movementIncrement;
                break;
        }
        if (fillingBrush) {
            Graphics2D pictureGraphics = CANVAS.getLayer(actualCanvas).createGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
            pictureGraphics.setComposite(ac);
            pictureGraphics.drawImage(previousSprite, x, y, null);
            pictureGraphics.dispose();
            pointer = Color.GREEN;
            int arrayIndexY = y / spriteSide;
            int arrayIndexX = x / spriteSide;
            int[][] returnedArray = CANVAS.getID_ARRAY_MAP(actualCanvas);
            returnedArray[arrayIndexY][arrayIndexX] = id;
            System.out.println("returnedArray[arrayIndexY][arrayIndexX] = " + returnedArray[arrayIndexY][arrayIndexX]);
        }
        pointerGraphics.setColor(pointer);
        pointerGraphics.drawRect(x + FRAME_GAP, y + FRAME_GAP, spriteSide - FRAME_GAP, spriteSide - FRAME_GAP);
        pointerGraphics.dispose();

        updateMainCanvas(mapScale);
    }

    /**
     * Method used to move the position of {@link JViewport} for each {@link JScrollPane}, {@code picScroller} and
     * {@code spriteListScroller}.
     * <br>The method moves the specified {@link JViewport} according to the value "true" of
     * {@code toggleMapMovement} or {@code toggleSpriteMovement}.
     */
    private void moveViewPort() {
        Point newPicViewPosition = new Point();
        int viewMovement = 0;
        if (toggleMapMovement) {
            newPicViewPosition.x = MAP_VIEW.getViewPosition().x;
            newPicViewPosition.y = MAP_VIEW.getViewPosition().y;
            viewMovement = SPRITESHEET.getSpriteSide() * mapScale;
        } else if (toggleSpriteMovement) {
            newPicViewPosition.x = SPRITE_VIEW.getViewPosition().x;
            newPicViewPosition.y = SPRITE_VIEW.getViewPosition().y;
            viewMovement = SPRITESHEET.getSpriteSide() * spriteListScale;
        }
        switch (direction) {
            case 1:
                if ((newPicViewPosition.x - viewMovement) >= -viewMovement) {
                    newPicViewPosition.x -= viewMovement;
                }
                break;
            case 2:
                newPicViewPosition.x += viewMovement;
                break;
            case 3:
                if ((newPicViewPosition.y - viewMovement) >= -viewMovement) {
                    newPicViewPosition.y -= viewMovement;
                }
                break;
            default:
                newPicViewPosition.y += viewMovement;
                break;
        }
        if (toggleMapMovement) {
            MAP_VIEW.setViewPosition(newPicViewPosition);
            picScroller.setViewport(MAP_VIEW);
        } else if (toggleSpriteMovement) {
            SPRITE_VIEW.setViewPosition(newPicViewPosition);
            spriteListScroller.setViewport(SPRITE_VIEW);
        }
    }

    /**
     * Returns a new {@link JButton}. The {@link JButton} is returned only after it is fully configured.
     *
     * @return a configured {@link JButton}.
     */
    private JButton newButton(String name, int width, int height) {
        JButton button = new JButton(name);
        button.setFocusable(true);
        button.setEnabled(false);
        button.addKeyListener(this);
        button.addMouseListener(mouseAdapter);
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(button.getMaximumSize());
        return button;
    }

    /**
     * Returns a new {@link JPanel}. The size and the layout for this {@link JPanel} are configured before
     * it is returned.
     *
     * @return a configured {@link JPanel}.
     */
    private JPanel newPanel(int width, int height, LayoutAxisEnum boxLayoutAxis) {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(width, height));
        panel.setPreferredSize(panel.getMaximumSize());
        panel.setLayout(new BoxLayout(panel, boxLayoutAxis.getAxis()));
        return panel;
    }

    /**
     * Returns a new {@link JTextField}. The size and the layout for this {@link JTextField} are configured before
     * it is returned.
     *
     * @return a configured {@link JTextField}.
     */
    private JTextField newTextField(int width, int height, String tooltip) {
        JTextField textField = new JTextField();
        textField.setToolTipText(tooltip);
        textField.setMaximumSize(new Dimension(width, height));
        textField.setMinimumSize(textField.getMaximumSize());
        return textField;
    }

    /**
     * Sets up the whole layout for a new window. This new window contains two {@link JTextField} and two
     * {@link JButton} to handle the {@code Create a new canvas} and {@code Export canvas} menus.
     */
    private void subMenu(String menuName) {
        int fontSize = 15;
        Font font = new Font("", Font.PLAIN, fontSize);
        String frameName = "";
        String firstLabelS = "";
        String firstToolTip = "";
        String secondLabelS = "";
        String secondToolTip = "";
        final int frameHeight = 170;
        final int frameWidth = 400;
        final int WIDTH_GAP = 15;

        switch (menuName) {
            case "exportCanvas":
                frameName = "Export canvas";
                firstLabelS = "Path:  ";
                firstToolTip = "Paste your path here.";
                secondLabelS = "Scale:";
                secondToolTip = Strings.SUBMENU_EXPORTCANVAS_TOOLTIP + CANVAS.getCanvasSize() + ".";
                break;
            case "requestNewCanvasValues":
                frameName = "Canvas data";
                firstLabelS = "Sprite side:";
                firstToolTip = "The side size of each individual sprite in pixels.";
                secondLabelS = "Canvas side:";
                secondToolTip = "The new canvas side size in pixels.";
                break;
        }

        subFrame = new JDialog(frame, frameName, true);
        subFrame.setLayout(new BoxLayout(subFrame.getContentPane(), BoxLayout.Y_AXIS));
        subFrame.setLocationRelativeTo(null);
        subFrame.setIconImage(null);
        subFrame.setSize(new Dimension(frameWidth, frameHeight));
        subFrame.setResizable(false);

        int panelHeight = 70;
        int itemHeight = 30;
        int buttonPWidth = subFrame.getWidth(); //Button pane width
        int buttonPHeight = 30; //Button pane height

        JPanel mainPanel = newPanel(subFrame.getWidth(), panelHeight, LayoutAxisEnum.X_AXIS);

        int labelPWidth = (int) (subFrame.getWidth() * 0.3);

        //Labels management
        JPanel labelsP = newPanel(labelPWidth, panelHeight, LayoutAxisEnum.Y_AXIS);

        //Text fields Management
        int textPWidth = subFrame.getWidth() - labelPWidth - WIDTH_GAP;

        JPanel textP = newPanel(textPWidth, panelHeight, LayoutAxisEnum.Y_AXIS);

        //First panel
        JPanel firstP = newPanel(labelPWidth, itemHeight, LayoutAxisEnum.X_AXIS);

        //First Label
        JLabel firstLabel = new JLabel(firstLabelS);
        firstLabel.setFont(font);

        //First text
        firstTF = newTextField(textPWidth, itemHeight, firstToolTip);

        if (menuName.equals("exportCanvas")) {
            firstTF.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    final JFileChooser fc = new JFileChooser();
                    fc.setApproveButtonText("OK");
                    int returnVal = fc.showOpenDialog(firstTF);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        firstTF.setText(fc.getSelectedFile().getAbsolutePath());
                        TA.setText("Path located: " + fc.getSelectedFile().getAbsolutePath());
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            });
        }

        //Second panel
        JPanel secondP = newPanel(labelPWidth, itemHeight, LayoutAxisEnum.X_AXIS);

        //Second Label
        JLabel secondLabel = new JLabel(secondLabelS);
        secondLabel.setFont(font);

        //Second text
        secondTF = newTextField(textPWidth, itemHeight, secondToolTip);

        //Buttons
        //Buttons panel
        JPanel buttonsP = newPanel(buttonPWidth, buttonPHeight, LayoutAxisEnum.X_AXIS);

        int buttonWidth = (int) (buttonPWidth * 0.3);

        JButton ok = handleOkButton(buttonWidth, buttonPHeight, menuName);

        JButton cancel = new JButton("Cancel");
        cancel.setMaximumSize(new Dimension(buttonWidth, buttonPHeight));
        cancel.setMinimumSize(cancel.getMaximumSize());
        cancel.addActionListener(e -> subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING)));

        //Layout

        //Y_AXIS
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        subFrame.add(mainPanel);

        //>>> Inside subFrame > mainPanel
        //X_AXIS
        mainPanel.add(Box.createRigidArea(new Dimension(WIDTH_GAP, 0)));
        mainPanel.add(labelsP);
        //>>> Inside subFrame > mainPanel > labelsP
        //Y_AXIS
        labelsP.add(firstP);
        //>>> Inside subFrame > mainPanel > labelsP > firstP
        firstP.add(firstLabel);
        firstP.add(Box.createHorizontalGlue());
        //>>> Inside subFrame > mainPanel > labelsP
        labelsP.add(Box.createRigidArea(new Dimension(0, 5)));
        labelsP.add(secondP);
        //>>> Inside subFrame > mainPanel > labelsP > secondP
        secondP.add(secondLabel);
        secondP.add(Box.createHorizontalGlue());
        //>>> Inside subFrame > mainPanel > labelsP
        labelsP.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside subFrame > mainPanel
        //X_AXIS
        mainPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        mainPanel.add(textP);
        //>>> Inside subFrame > mainPanel > textP
        //Y_AXIS
        textP.add(firstTF);
        textP.add(Box.createRigidArea(new Dimension(0, 5)));
        textP.add(secondTF);
        textP.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside subFrame > mainPanel
        //X_AXIS
        mainPanel.add(Box.createRigidArea(new Dimension(WIDTH_GAP, 0)));

        //>>> Inside subFrame
        //Y_AXIS
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        subFrame.add(buttonsP);

        //>>> Inside subFrame > buttonsP
        //X_AXIS
        buttonsP.add(Box.createHorizontalGlue());
        buttonsP.add(ok);
        buttonsP.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsP.add(cancel);
        buttonsP.add(Box.createHorizontalGlue());

        //>>> Inside subFrame
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
        Uncomment the next lines while developing, in order to get the fields Sprite side and Canvas Side autofilled
        with default values.
        */
        //firstTF.setText("16");
        //secondTF.setText("80");

        //Component visibility
        subFrame.setVisible(true);
    }

    /**
     * Displays an information window by invoking {@link SubWindow#runInfoWindow(SubWindowOptionsEnum)} with the
     * specified {@link SubWindowOptionsEnum} value.
     *
     * @param value the option that determines the kind of message to display in the information window
     */
    private void showInfoMessage(SubWindowOptionsEnum value) {
        SubWindow.runInfoWindow(value);
    }

    /**
     * Sets up the whole layout for the main window.
     */
    public void windowSetup() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int SCREEN_HEIGHT = (int) screenSize.getHeight();
        final int SCREEN_WIDTH = (int) screenSize.getWidth();

        int FRAME_WIDTH = (int) (SCREEN_WIDTH * 0.6);
        int FRAME_HEIGHT = (int) (SCREEN_HEIGHT * 0.6);

        JMenuBar jMenuBar = handleJMenuBar();

        frame = new JFrame(Strings.FRAME_NAME_UI);
        frame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(null);
        frame.setJMenuBar(jMenuBar);

        //>>>Inside frame
        //Panel1
        int panel1Width = (int) (SCREEN_WIDTH * 0.40);
        JPanel panel1 = newPanel(panel1Width, SCREEN_HEIGHT, LayoutAxisEnum.Y_AXIS);
        panel1.setFocusable(true);
        panel1.addKeyListener(this);

        //Map Management
        JPanel picScrollerHolder = newPanel(SCREEN_WIDTH - panel1Width, SCREEN_HEIGHT, LayoutAxisEnum.Y_AXIS);
        picScrollerHolder.addKeyListener(this);
        picScrollerHolder.setFocusable(true);

        picScroller = new JScrollPane();
        picScroller.addKeyListener(this);
        picScroller.setFocusable(true);
        picScroller.setWheelScrollingEnabled(true);
        picScroller.setEnabled(false); //UserInterface_notes
        //>>> Inside panel1
        int panel2Width = (int) (SCREEN_WIDTH * 0.40);
        int panel2Height = (int) (SCREEN_HEIGHT * 0.90);
        JPanel panel2 = newPanel(panel2Width, panel2Height, LayoutAxisEnum.X_AXIS);
        panel2.addKeyListener(this);
        panel2.setFocusable(true);


        //>>> Inside panel1 > Inside panel2
        //Sprites management
        JPanel spritesFather = newPanel((int) (panel2.getMaximumSize().getWidth() * 0.75), panel2Height, LayoutAxisEnum.Y_AXIS);
        spritesFather.addKeyListener(this);


        //>>> Inside panel1 > Inside panel2 > Inside spritesFather
        JPanel spriteLabelPanel = new JPanel();
        spriteLabelPanel.setLayout(new BoxLayout(spriteLabelPanel, BoxLayout.X_AXIS));
        spriteLabelPanel.addKeyListener(this);
        //>>> Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteLabelPanel
        spriteLabel = new JLabel(Strings.SPRITE_LABEL_NAME);

        //>>> Inside panel1 > Inside spritesFather
        spriteListScroller = new JScrollPane();
        spriteListScroller.addKeyListener(this);
        spriteListScroller.setWheelScrollingEnabled(true);
        //>>> Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteListScroller
        spritesPanel = new JPanel();
        spritesPanel.setLayout(new BoxLayout(spritesPanel, BoxLayout.PAGE_AXIS));
        spritesPanel.addKeyListener(this);

        SPRITE_VIEW.setView(spritesPanel);

        //Layer Management
        //>>> Inside panel1 > Inside panel2
        JPanel layerPanel = newPanel((int) (panel2.getMaximumSize().getWidth() * 0.25), panel2Height, LayoutAxisEnum.Y_AXIS);
        //>>> Inside panel1 > Inside panel2 > Inside layerPanel
        JPanel layerLabelPanel = new JPanel();
        layerLabelPanel.setLayout(new BoxLayout(layerLabelPanel, BoxLayout.X_AXIS));
        layerLabelPanel.addKeyListener(this);
        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerLabelPanel
        actualLayerLabel = new JLabel(Strings.ACTUAL_LAYER_LABEL + " " + actualCanvas);
        actualLayerLabel.addMouseListener(handleMouseAdapter());

        //>>> Inside panel1 > Inside panel2 > Inside layerPanel
        JScrollPane layerScroller = new JScrollPane();
        layerScroller.setFocusable(true);
        layerScroller.addKeyListener(this);
        layerScroller.setWheelScrollingEnabled(true);
        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller
        layerSelector = new JPanel();
        layerSelector.setLayout(new BoxLayout(layerSelector, BoxLayout.Y_AXIS));
        layerSelector.setFocusable(false);

        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller > layerSelector
        JPanel newLayerBPanel = newPanel(layerPanel.getMaximumSize().width, (int) (layerPanel.getMaximumSize().getHeight() * 0.03), LayoutAxisEnum.X_AXIS);

        newLayerB = handleNewLayerButton(newLayerBPanel.getMaximumSize().width, newLayerBPanel.getMaximumSize().height);

        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller
        layerScroller.setViewportView(layerSelector);

        //>>> Inside panel1
        JPanel panel3 = newPanel(panel1Width, SCREEN_HEIGHT - panel2Height, LayoutAxisEnum.X_AXIS);
        panel3.addKeyListener(this);
        panel3.setFocusable(true);

        //>>> Inside panel1 > Inside panel3
        JPanel panel4 = newPanel((int) (SCREEN_WIDTH * 0.15), panel3.getMaximumSize().height, LayoutAxisEnum.Y_AXIS);
        panel4.addKeyListener(this);
        panel4.setFocusable(true);

        //>>> Inside panel1 > Inside panel3 > Inside panel4
        JPanel panel5 = newPanel(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2, LayoutAxisEnum.X_AXIS);
        panel5.addKeyListener(this);
        panel5.setFocusable(true);

        JPanel panel6 = newPanel(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2, LayoutAxisEnum.X_AXIS);
        panel6.addKeyListener(this);
        panel6.setFocusable(true);

        //>>> Inside panel1 > Inside panel3 > Inside panel5

        final int buttonsWidth = panel4.getMaximumSize().width / 2;
        final int buttonsHeight = panel4.getMaximumSize().height / 2;

        biggerSprite = newButton(Strings.BIGGER_SPRITE_BUTTON, buttonsWidth, buttonsHeight);
        biggerSprite.setActionCommand("+");
        biggerSprite.addActionListener(e -> {
            setSpriteListScale(++spriteListScale);
            int size = CANVAS.getCanvasSize() * spriteListScale;
            spritesPanel.setSize(new Dimension(size, size));
            spritesPanel.removeAll();
            buildSpritesList(spriteListScale);
            spritesPanel.updateUI();
        });

        smallerSprite = newButton(Strings.SMALLER_SPRITE_BUTTON, buttonsWidth, buttonsHeight);
        smallerSprite.setActionCommand("-");
        smallerSprite.addActionListener(e -> {
            if (spriteListScale > 1) {
                setSpriteListScale(--spriteListScale);
            }
            int size = CANVAS.getCanvasSize() * spriteListScale;
            spritesPanel.setSize(new Dimension(size, size));
            spritesPanel.removeAll();
            buildSpritesList(spriteListScale);
            spritesPanel.updateUI();
        });

//        UserInterface_notes
        biggerMap = newButton(Strings.BIGGER_MAP_BUTTON, buttonsWidth, buttonsHeight);
        biggerMap.addActionListener(e -> {
            setMapScale(MAP_SCALE_RATIO);
            updateMainCanvas(mapScale);
        });

        smallerMap = newButton(Strings.SMALLER_MAP_BUTTON, buttonsWidth, buttonsHeight);
        smallerMap.addActionListener(e -> {
            if ((mapScale - MAP_SCALE_RATIO) > 0) {
                setMapScale(-MAP_SCALE_RATIO);
                updateMainCanvas(mapScale);
            }
        });

        //>>> Inside panel1 > Inside panel3
        int panel7Width = panel3.getMaximumSize().width - panel4.getMaximumSize().width;
        int panel7Height = panel3.getMaximumSize().height;
        JPanel panel7 = newPanel(panel7Width, panel7Height, LayoutAxisEnum.Y_AXIS);
        panel7.addKeyListener(this);
        panel7.setFocusable(true);

        //>>> Inside panel1 > Inside panel3 > panel7
        JScrollPane taScroller = new JScrollPane();
        taScroller.addKeyListener(this);
        taScroller.setFocusable(true);
        taScroller.setWheelScrollingEnabled(true);

        //>>> Inside panel1 > Inside panel3 > panel7 > Inside taScroller
        TA.addKeyListener(this);
        TA.addMouseListener(mouseAdapter);
        TA.setEditable(false);
        TA.setFont(new Font("", Font.BOLD, 15));
        TA.setLineWrap(true);
        TA.setWrapStyleWord(true);
        TA.setCaretColor(Color.WHITE);
        taScroller.setViewportView(TA);

        //Layout adding.

        //>>> Inside frame
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(panel1);

        //>>> Inside frame > Inside panel1
        panel1.add(Box.createRigidArea(new Dimension(5, 0)));
        panel1.add(panel2);

        //>>> Inside frame > Inside panel1 > Inside panel2
        panel2.add(Box.createRigidArea(new Dimension(5, 0)));
        panel2.add(spritesFather);
        //>>> Inside frame > Inside panel1 > Inside panel2 > Inside spritesFather
        spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));
        spritesFather.add(spriteLabelPanel);
        //>>> Inside frame > Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteLabelPanel
        spriteLabelPanel.add(spriteLabel);
        spriteLabelPanel.add(Box.createHorizontalGlue());
        //>>> Inside frame > Inside panel1 > Inside panel2 > Inside spritesFather
        spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));
        spritesFather.add(spriteListScroller);
        spriteListScroller.setViewportView(SPRITE_VIEW);
        spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside frame > Inside panel1 > Inside panel2
        panel2.add(Box.createRigidArea(new Dimension(5, 0)));
        panel2.add(layerPanel);
        //>>> Inside frame > Inside panel1 > Inside panel2

        //>>> Inside frame > Inside panel1 > Inside panel2 > Inside layerPanel
        layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        layerPanel.add(layerLabelPanel);
        //>>> Inside frame > Inside panel1 > Inside panel2 > layerPanel > layerLabelPanel
        layerLabelPanel.add(actualLayerLabel);
        layerLabelPanel.add(Box.createHorizontalGlue());
        //>>> Inside frame > Inside panel1 > Inside panel2 > layerPanel
        layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        layerPanel.add(layerScroller);
        layerScroller.setViewportView(layerSelector);
        layerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        layerPanel.add(newLayerBPanel);
        //>>> Inside frame > Inside panel1 > Inside panel2 > layerPanel > Inside newLayerBPanel
        newLayerBPanel.add(newLayerB);
        //>>> Inside frame > Inside panel1 > Inside panel2 > layerPanel
        layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside frame > Inside panel1 > Inside panel2
        panel2.add(Box.createRigidArea(new Dimension(5, 0)));

        //>>> Inside frame > Inside panel1
        panel1.add(Box.createRigidArea(new Dimension(5, 0)));
        panel1.add(panel3);
        panel1.add(Box.createRigidArea(new Dimension(5, 0)));

        //>>> Inside frame > Inside panel1 > Inside panel3
        panel3.add(Box.createRigidArea(new Dimension(5, 0)));
        panel3.add(panel4);
        panel3.add(Box.createRigidArea(new Dimension(5, 0)));
        panel3.add(panel7);
        panel3.add(Box.createRigidArea(new Dimension(5, 0)));

        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel4
        panel4.add(Box.createRigidArea(new Dimension(0, 5)));
        panel4.add(panel5);

        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel4 > Inside panel5
        panel5.add(biggerSprite);
        panel5.add(Box.createRigidArea(new Dimension(5, 0)));
        panel5.add(smallerSprite);

        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel4
        panel4.add(Box.createRigidArea(new Dimension(0, 5)));
        panel4.add(panel6);

        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel4 > Inside panel6
        panel6.add(biggerMap);
        panel6.add(Box.createRigidArea(new Dimension(5, 0)));
        panel6.add(smallerMap);

        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel4
        panel4.add(Box.createRigidArea(new Dimension(0, 5)));


        //>>> Inside frame > Inside panel1 > Inside panel3 > Inside panel7
        panel7.add(Box.createRigidArea(new Dimension(0, 5)));
        panel7.add(taScroller);
        panel7.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside frame
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(picScrollerHolder);

        //>>> Inside frame > picScrollerHolder
        picScrollerHolder.add(Box.createRigidArea(new Dimension(0, 5)));
        picScrollerHolder.add(picScroller);
        picScrollerHolder.add(Box.createRigidArea(new Dimension(0, 5)));

        //>>> Inside frame
        frame.add(Box.createRigidArea(new Dimension(5, 0)));

        //Setting everything visible
        frame.setVisible(true);
    }

    /**
     * Updates the main canvas display by scaling its current framed image according to the given scale factor.
     * The scaled image is then set as the icon of {@code picLabel}.
     *
     * @param scale the scale factor to apply to the framed canvas image.
     */
    private void updateMainCanvas(int scale) {
        picLabel.setIcon(new ImageIcon(CANVAS.getScaledCanvas(CANVAS.getFramedCanvas(), scale)));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (TA.isEditable()) {
            int key = e.getKeyCode();
            //UserInterface_notes To be checked.
            System.out.println("Key = " + key);
            if (TA.hasFocus()) {
                if (key == KeyEvent.VK_ESCAPE) {
                    frame.requestFocus();
                }
            } else {
                if (key == KeyEvent.VK_CONTROL) {
                    if (!toggleMapMovement) {
                        toggleMapMovement = true;
                    }
                } else if (key == KeyEvent.VK_SHIFT) {
                    if (!toggleSpriteMovement) {
                        toggleSpriteMovement = true;
                    }
                } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                    direction = 1;
                    movementSelector();
                } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                    direction = 2;
                    movementSelector();
                } else if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                    direction = 3;
                    movementSelector();
                } else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                    direction = 4;
                    movementSelector();
                } else if (key == KeyEvent.VK_PLUS || key == KeyEvent.VK_ADD) {
                    if (toggleMapMovement) {
                        biggerMap.doClick();
                    } else if (toggleSpriteMovement) {
                        biggerSprite.doClick();
                    }
                } else if (key == KeyEvent.VK_MINUS || key == KeyEvent.VK_SUBTRACT) {
                    if (toggleMapMovement) {
                        smallerMap.doClick();
                    } else if (toggleSpriteMovement) {
                        smallerSprite.doClick();
                    }
                } else if (key == KeyEvent.VK_ENTER) {
                    if (!actualCanvas.equals(Strings.NO_LAYER)) {
                        int spriteSide = SPRITESHEET.getSpriteSide() - FRAME_GAP;
                        fillingBrush = !fillingBrush;
                        Graphics2D pointerGraphics = CANVAS.getPOINTER_LAYER().createGraphics();
                        pointerGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                        if (fillingBrush) {
                            pointerGraphics.setColor(Color.GREEN);
                        } else {
                            pointerGraphics.setColor(Color.RED);
                        }
                        pointerGraphics.drawRect(x + FRAME_GAP, y + FRAME_GAP, spriteSide, spriteSide);
                        pointerGraphics.dispose();
                        direction = 0;
                        movePointer();
                    } else {
                        TA.setText("No layer selected.");
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == 17) {
            if (toggleMapMovement) {
                toggleMapMovement = false;
            }
        } else if (key == 16) {
            if (toggleSpriteMovement) {
                toggleSpriteMovement = false;
            }
        }
    }
}


