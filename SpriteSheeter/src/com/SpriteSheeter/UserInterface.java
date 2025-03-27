package com.SpriteSheeter;

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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//        To explain layout for frame > https://stackoverflow.com/questions/24840860/boxlayout-for-a-jframe


class UserInterface implements KeyListener, MouseListener {

    //Boolean to be used to  toggle on/off the movement of the main map scroller by using directional keys.
    private boolean toggleMapMovement = false;
    private boolean toggleSpriteMovement = false;
    private boolean fillingBrush = false;

    //The scale ratio for the main map pic. This will modify the size for the main map to be displayed,
    //through multiplying map's values by its value.
    private int mapScale = 1;
    //The scale ratio for each individual sprite size. This will modify the size of the sprite side multiplying it
    //by its value.
    private int spriteListScale = 1;
    /*
    The current x and y coordinates of the actual position of the pointer in the map. The x and y coordinates of the
    top left point.
        0  1  2  3  4  5
    0   *---------------------
    1   |
    2   | x=2 v y=3
    3   |     *----
    4   |     |   |
    5   |     -----

    Top left corner of current position = * => x = 2 y = 3
     */
    private int x = 0;
    private int y = 0;
    private int id = 0;
    //The amount of pixels
    private int movementIncrement;
    private int direction = 0; //0 = no movement, 1 = left, 2 = right, 3 = up, 4 = down
    private final int MAX_LABEL_LENGHT = 17;
    private final int FRAME_GAP = 1;
    //Screen size
    private final int MAP_SCALE_RATIO = 1;
    private String actualCanvas = Strings.NO_LAYER;
    private final SpriteSheet SPRITESHEET = new SpriteSheet();
    private final Canvas CANVAS = new Canvas();
    private JFrame frame;
    private JScrollPane picScroller;
    private JLabel picLabel;
    private JPanel spritesPanel;
    private JLabel actualLayerLabel;
    private JPanel layerSelector;
    private JScrollPane spriteListScroller;
    private final JTextArea TA = new JTextArea();
    private final JViewport MAP_VIEW = new JViewport();
    private final JViewport SPRITE_VIEW = new JViewport();
    private JButton newLayerB;
    private JButton biggerMap;
    private JButton smallerMap;
    private JButton biggerSprite;
    private JButton smallerSprite;
    private JMenu layerManagement;
    private JMenuItem loadSpriteSheet;
    private JMenuItem exportCode;
    private JMenuItem exportCanvas;
    private BufferedImage previousSprite;


    private Map<String, int[]> getImportedData(String loadedData) {
        //Update the existing documentation
        boolean keep = true;
        Map<String, int[]> layers = new LinkedHashMap<>();

        Function<String, String> matcherFunc = string -> {
            String output = "";
            Matcher matcher = Pattern.compile(string).matcher(loadedData);
            while (matcher.find()) {
                output = matcher.group();
            }
            return output;
        };

        String matchedString = matcherFunc.apply(Strings.IMPORTED_FIRST_REGEX);
        int amountOfSprites = matchedString.matches("\\d+") ? (int) Math.pow(Integer.parseInt(matchedString), 2) : 0;

        if (amountOfSprites == 0) {
            keep = false;
        }

        if (keep) {
            if ((CANVAS.getCanvasSize() == 0) && (CANVAS.getSpriteSide() == 0) &&
                    (SPRITESHEET.getSpriteSide() == 0)) {
                matchedString = matcherFunc.apply(Strings.IMPORTED_SECOND_REGEX);
                int side = matchedString.matches("\\d+") ? Integer.parseInt(matchedString) : 0;

                matchedString = matcherFunc.apply(Strings.IMPORTED_THIRD_REGEX);
                int newCanvasSize = matchedString.matches("\\d+") ? Integer.parseInt(matchedString) : 0;

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
            Matcher matcher = Pattern.compile(Strings.IMPORTED_FOURTH_REGEX)
                    .matcher(loadedData);
            while (matcher.find()) {
                layers.put(matcher.group(), null);
            }
            for (Map.Entry<String, int[]> numbersMap : layers.entrySet()) {
                String numbersRegex = Strings.IMPORTED_FIFTH_REGEX_1 + numbersMap.getKey() +
                        Strings.IMPORTED_FIFTH_REGEX_2;
                String[] justNumbers = matcherFunc.apply(numbersRegex).split("\\s");
                if (amountOfSprites == justNumbers.length) {
                    int[] numbers = new int[justNumbers.length];
                    for (int i = 0; i < justNumbers.length; i++) {
                        numbers[i] = Integer.parseInt(justNumbers[i]);
                    }
                    numbersMap.setValue(numbers);
                } else {
                    System.out.println("Way to break the whole operation");
                    layers = null;
                    break;
                }
            }
        } else {
            return null;
        }
        return layers;
    }

    private JMenuBar getJMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu options = new JMenu("Options");

        //1
        JMenuItem newCanvas = new JMenuItem(Strings.NEW_CANVAS_MENU);
        newCanvas.addActionListener(e -> {
            runSubMenu("requestNewCanvasValues");
            enableUI();
        });
        //2
        loadSpriteSheet = handleLoadSpriteSheet();
        //3
        layerManagement = new JMenu(Strings.LAYER_MANAGEMENT_MENU);
        layerManagement.setEnabled(false);
        layerManagement.addMouseListener(this);
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
        help.addActionListener(e -> showInfoMessage(SubWindowOptions.HELP));

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
        options.setVisible(true);

        return jMenuBar;
    }


    private String getLoadedPath(String loadedData) {
        String path = "";
        Matcher matcher = Pattern.compile(Strings.LOADED_REGEX).matcher(loadedData);
        while (matcher.find()) {
            path = matcher.group();
        }
        matcher.reset();
        return path;
    }

    private void setMapScale(int movementIncrement) {
        this.mapScale += movementIncrement;
    }

    private void setSpriteListScale(int spriteListScale) {
        this.spriteListScale = spriteListScale;
    }

    private void addNewLayerButtons(String layerName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JButton layerButton = new JButton(layerName);
        layerButton.addKeyListener(this);
        layerButton.setFocusable(false);
        layerButton.addActionListener(e -> {
            actualCanvas = layerName;
            actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " +
                    (actualCanvas.length() > MAX_LABEL_LENGHT ? actualCanvas.substring(0, 5) + "..." +
                            actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
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

    private void buildJLabelList(JPanel spritesPanel, int spriteListScaleRatio) {
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
                //Se tiene que crear una variable local id, aunque ya exista una global, porque para poder
                //usar el valor de j dentro de la declaración del actionListener del botón, dicha variable debe
                //ser final o effectively final. No podemos hacer id = j; y asignarle id al método get
                //spriteSheet.getSPRITES_HASMAP().get(id - 1).getId()); porque para cuando se pulse el botón, y
                //se llame a la acción del botón, el valor de id será el último valor que se le asignase, es
                //decir, el valor del último botón que se crease, la última iteración de este bucle for, donde
                //se estableción id = j. Si el último botón que se creó fue el 540, el valor de id será id = 540
                //y será siempre así. No obstante, id ha de  ser actualizada al valor de innerId dentro de la acción
                //del botón para que cuando se active la función de dibujar de forma constante, el programa sepa
                //cuál es la id que ha de utilizar a la hora de pintar. La función de pintar usa la última id
                //conocida, y esa id se actualiza cada vez que se presiona un botón.
                int innerId = j;
                int finalI = i;
                button.addActionListener(e -> {
//                        https://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html
                    System.out.println("Sprite pressed = " + innerId);
                    //Used to set the kind of Composite to be used in the BufferedImage in use. Check the above
                    //link.
                    if (!actualCanvas.equals(Strings.NO_LAYER)) {
                        Graphics2D pictureGraphics = CANVAS.getLayer(actualCanvas).createGraphics();
                        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
                        pictureGraphics.setComposite(ac);
                        pictureGraphics.drawImage(spriteToPrint, x, y, null);
                        pictureGraphics.dispose();
                        int arrayIndexY = y / spriteSide;
                        int arrayIndexX = x / spriteSide;
                        int[][] returnedArray = CANVAS.getID_ARRAY_MAP(actualCanvas);
                        returnedArray[arrayIndexY][arrayIndexX] =
                                (finalI == -1) ? innerId : SPRITESHEET.getSPRITES_HASHMAP().get(innerId).getId();
                        id = innerId;
                        updateMainCanvas(mapScale);
                        previousSprite = spriteToPrint;
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

    CORRIGE LA EXCEPCIÓN CUANDO BORRAS UNA CAPA. AL QUEDARSE SIN ACTUAL LAYER, SI PRESIONAS UN SPRITE DA EXCEPCIÓN.

    private void deleteAllLayer() {
        actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " ");
        CANVAS.deleteAllLayers();
        layerSelector.removeAll();
        actualCanvas = Strings.NO_LAYER;
        updateMainCanvas(mapScale);
    }

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
        loadSpriteSheet.removeMouseListener(this);
        layerManagement.removeMouseListener(this);
        exportCode.removeMouseListener(this);
        exportCanvas.removeMouseListener(this);
        biggerSprite.removeMouseListener(this);
        biggerMap.removeMouseListener(this);
        smallerSprite.removeMouseListener(this);
        smallerMap.removeMouseListener(this);
        newLayerB.removeMouseListener(this);
        TA.removeMouseListener(this);
        TA.setCaretColor(Color.BLACK);
        TA.setText("");
        picScroller.requestFocus();
    }

    private JMenuItem handleDeleteLayer() {
        JMenuItem deleteLayer = new JMenuItem(Strings.DELETE_LAYER_ITEM);
        deleteLayer.addActionListener(e -> {
            CANVAS.deleteLayer(actualCanvas);
            layerSelector.removeAll();
            for (Map.Entry<String, BufferedImage> layers : CANVAS.getLAYERS().entrySet()) {
                addNewLayerButtons(layers.getKey());
            }
            actualLayerLabel.setText("Actual layer: " + Strings.NO_LAYER);
            updateMainCanvas(mapScale);
        });
        return deleteLayer;
    }

    private JMenuItem handleExportCanvas() {
        JMenuItem exportCanvas = new JMenuItem(Strings.EXPORT_CANVAS_ITEM);
        exportCanvas.addActionListener(e -> runSubMenu("exportCanvas"));
        exportCanvas.setEnabled(false);
        exportCanvas.addMouseListener(this);
        return exportCanvas;
    }

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
        exportCode.addMouseListener(this);
        return exportCode;
    }

    private JMenuItem handleImportCode() {
        JMenuItem importCode = new JMenuItem("Import");
        importCode.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(importCode);
            if (!(returnVal == JFileChooser.CANCEL_OPTION)) {
                boolean validPictureFile;
                BufferedImage newPicture;
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
                        newPicture = ImageIO.read(new File(newPicturePath));
                        validPictureFile = newPicture != null;
                        Map<String, int[]> loadedMap = getImportedData(stringBuilder.toString());
                        if (loadedMap != null && validPictureFile) {
                            enableUI();
                            initializePicLabel();
                            deleteAllLayer();//todo mira si puedes hacer que esto sea condicional en función de si hay capas o no
                            resetLayerSelector(loadedMap);
                            spritesPanel.removeAll();
                            SPRITESHEET.setPicturePath(newPicturePath);
                            SPRITESHEET.loadSpriteSheet(newPicture);
                            buildJLabelList(spritesPanel, spriteListScale);
                            CANVAS.buildLayers(loadedMap, SPRITESHEET.getSPRITES_HASHMAP());
                            updateMainCanvas(mapScale);
                        } else {
                            if (!validPictureFile) {
                                showInfoMessage(SubWindowOptions.INVALID_IMAGE_PATH);
                            } else {
                                showInfoMessage(SubWindowOptions.CORRUPTED_FILE);
                            }
                            importCode.doClick();
                        }
                    } else {
                        showInfoMessage(SubWindowOptions.INVALID_TEXT);
                        importCode.doClick();
                    }
                } catch (IOException | NullPointerException exception) {
                    if (exception instanceof IOException) {
                        showInfoMessage(SubWindowOptions.CORRUPTED_FILE);
                    } else {
                        showInfoMessage(SubWindowOptions.INVALID_PATH);
                    }
                    importCode.doClick();
                }
            }
        });
        return importCode;
    }

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
                    showInfoMessage(SubWindowOptions.INVALID_PATH);
                    loadSpriteSheet.doClick();
                }
                if (validPictureFile) {
                    if (spritesPanel.getComponentCount() > 0) {
                        spritesPanel.removeAll();
                    }
                    SPRITESHEET.setPicturePath(newPicturePath);
                    SPRITESHEET.loadSpriteSheet(newPicture);
                    buildJLabelList(spritesPanel, spriteListScale);
                    spritesPanel.updateUI();
                } else {
                    showInfoMessage(SubWindowOptions.INVALID_IMAGE);
                    loadSpriteSheet.doClick();
                }
            }
        });
        loadSpriteSheet.setEnabled(false);
        loadSpriteSheet.addMouseListener(this);

        return loadSpriteSheet;
    }

    private void initializePicLabel() {
        picLabel = new JLabel(new ImageIcon(CANVAS.getFramedCanvas()));
        picLabel.addKeyListener(this);
        picLabel.setFocusable(true);
        picLabel.setVisible(true);
        MAP_VIEW.setView(picLabel);
        picScroller.setViewportView(MAP_VIEW);
    }

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

    private void movePointer() {
        int spriteSide = SPRITESHEET.getSpriteSide();
        Color pointer = Color.RED;
        Graphics2D pointerGraphics = CANVAS.getPOINTER_LAYER().createGraphics();
        pointerGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        pointerGraphics.drawImage(new BufferedImage(spriteSide, spriteSide, BufferedImage.TYPE_INT_ARGB),
                x + FRAME_GAP, y + FRAME_GAP, spriteSide, spriteSide, null);

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
        pointerGraphics.drawRect(x + FRAME_GAP, y + FRAME_GAP, spriteSide - FRAME_GAP,
                spriteSide - FRAME_GAP);
        pointerGraphics.dispose();

        updateMainCanvas(mapScale);
    }

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

    private void resetLayerSelector(Map<String, int[]> newlayerSelector) {
        boolean firstLayerName = true;
        for (Map.Entry<String, int[]> newLayers : newlayerSelector.entrySet()) {
            addNewLayerButtons(newLayers.getKey());
            CANVAS.addNewCanvas(newLayers.getKey());
            if (firstLayerName) {
                actualCanvas = newLayers.getKey();
                actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " +
                        (actualCanvas.length() > MAX_LABEL_LENGHT ? actualCanvas.substring(0, 5) + "..." +
                                actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
                firstLayerName = false;
            }
        }
    }

    private void runSubMenu(String menuName) {
        int fontSize = 15;
        Font font = new Font("", Font.PLAIN, fontSize);
        String frameName = "";
        String firstLabelS = "";
        String firstToolTip = "";
        String secondLabelS = "";
        String secondToolTip = "";
        int frameHeight = 170;
        int amountOfLabels = 2;
        JTextField secondTF = new JTextField();

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
                secondLabelS = "New canvas side:";
                secondToolTip = "The new canvas side size in pixels.";
                break;
        }

        final JDialog subFrame = new JDialog(frame, frameName, true);
        subFrame.setLayout(new BoxLayout(subFrame.getContentPane(), BoxLayout.Y_AXIS));
        subFrame.setLocationRelativeTo(null);
        subFrame.setIconImage(null);
        subFrame.setSize(new Dimension(400, frameHeight));
        subFrame.setResizable(false);

        int panelHeight = 35 * amountOfLabels;
        int itemHeight = 30;
        int buttonPWidth = subFrame.getWidth();
        int buttonPHeight = 30;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setMaximumSize(new Dimension(subFrame.getWidth(), panelHeight));
        mainPanel.setMaximumSize(mainPanel.getMaximumSize());

        int labelPWidth = (int) (subFrame.getWidth() * 0.2);

        //Labels management
        JPanel labelsP = new JPanel();
        labelsP.setLayout(new BoxLayout(labelsP, BoxLayout.Y_AXIS));
        labelsP.setMaximumSize(new Dimension(labelPWidth, panelHeight));
        labelsP.setMinimumSize(labelsP.getMaximumSize());

        //Text fields Management
        int textPWidth = subFrame.getWidth() - labelPWidth;

        JPanel textP = new JPanel();
        textP.setLayout(new BoxLayout(textP, BoxLayout.Y_AXIS));
        textP.setMaximumSize(new Dimension(textPWidth, panelHeight));
        textP.setMinimumSize(labelsP.getMaximumSize());

        //First panel
        JPanel firstP = new JPanel();
        firstP.setMaximumSize(new Dimension(labelPWidth, itemHeight));
        firstP.setMinimumSize(firstP.getMaximumSize());
        firstP.setLayout(new BoxLayout(firstP, BoxLayout.X_AXIS));
        //First Label
        JLabel firstLabel = new JLabel(firstLabelS);
        firstLabel.setFont(font);
        firstP.add(firstLabel);
        firstP.add(Box.createHorizontalGlue());
        //First text
        JTextField firstTF = new JTextField();
        firstTF.setToolTipText(firstToolTip);
        firstTF.setMaximumSize(new Dimension(textPWidth, itemHeight));
        firstTF.setMinimumSize(firstTF.getMaximumSize());
        if (menuName.equals("exportCanvas")) {
            firstTF.addMouseListener(new MouseListener() {
                @Override
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

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }

        //Buttons
        //Buttons panel
        JPanel buttonsP = new JPanel();
        buttonsP.setLayout(new BoxLayout(buttonsP, BoxLayout.X_AXIS));
        buttonsP.setMaximumSize(new Dimension(buttonPWidth, buttonPHeight));
        buttonsP.setMinimumSize(buttonsP.getMaximumSize());

        int buttonWidth = (int) (buttonPWidth * 0.3);

        JButton ok = new JButton("OK");
        ok.setMaximumSize(new Dimension(buttonWidth, buttonPHeight));
        ok.setMinimumSize(ok.getMaximumSize());
        ok.addActionListener(e -> {
            String firstTFS = firstTF.getText().isEmpty() ? "" : firstTF.getText();
            String secondTFS = secondTF.getText();
            if (menuName.equals("exportCanvas")) {
                if (!firstTFS.isEmpty() && !secondTFS.isEmpty()) {
                    boolean secondNumber = secondTFS.matches("\\d+");
                    if (secondNumber) {
                        try {
                            ImageIO.write(CANVAS.getScaledCanvas(CANVAS.getCanvas(), Integer.parseInt(secondTFS)),
                                    "png", new File(firstTFS + ".png"));
                            subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                        } catch (IOException ex) {
                            showInfoMessage(SubWindowOptions.INVALID_PATH);
                        }
                    } else {
                        showInfoMessage(SubWindowOptions.INVALID_SCALE);
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
                        condition = true;//todo revisa si puedes bajar esta línea debajo de side y newCanvasSize
                        side = Integer.parseInt(firstTFS);
                        newCanvasSize = Integer.parseInt(secondTFS);
                    } else {
                        if (!firstNumber & !secondNumber) {
                            showInfoMessage(SubWindowOptions.SHEET_AND_SPRITE);
                        } else if (!firstNumber) {
                            showInfoMessage(SubWindowOptions.SPRITE_SIDE_FAIL);
                        } else {
                            showInfoMessage(SubWindowOptions.SPRITESHEET_FAIL);
                        }
                    }
                }
                if (condition) {
                    subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                    CANVAS.initializeCanvas(side, newCanvasSize);
                    SPRITESHEET.setSpriteSide(side);
                    movementIncrement = side;
                    initializePicLabel();
                }
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.setMaximumSize(new Dimension(buttonWidth, buttonPHeight));
        cancel.setMinimumSize(cancel.getMaximumSize());
        cancel.addActionListener(e -> subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING)));


        //Layout adding

        //X_AXIS
        buttonsP.add(Box.createHorizontalGlue());
        buttonsP.add(ok);
        buttonsP.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonsP.add(cancel);
        buttonsP.add(Box.createHorizontalGlue());

        //Y_AXIS
        labelsP.add(firstP);
        labelsP.add(Box.createRigidArea(new Dimension(0, 5)));

        //Y_AXIS
        textP.add(firstTF);
        textP.add(Box.createRigidArea(new Dimension(0, 5)));

        //X_AXIS
        mainPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        mainPanel.add(labelsP);
        mainPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        mainPanel.add(textP);
        mainPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        //Y_AXIS
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        subFrame.add(mainPanel);
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        subFrame.add(buttonsP);
        subFrame.add(Box.createRigidArea(new Dimension(0, 10)));


        firstTF.setText("16");
        secondTF.setText("80");

        //Export menu
        //Second panel
        JPanel secondP = new JPanel();
        secondP.setLayout(new BoxLayout(secondP, BoxLayout.X_AXIS));
        secondP.setMaximumSize(new Dimension(labelPWidth, itemHeight));
        secondP.setMinimumSize(secondP.getMaximumSize());
        JLabel secondLabel = new JLabel(secondLabelS);
        secondLabel.setFont(font);
        secondP.add(secondLabel);
        secondP.add(Box.createHorizontalGlue());

        secondTF.setToolTipText(secondToolTip);
        secondTF.setMaximumSize(new Dimension(textPWidth, itemHeight));
        secondTF.setMinimumSize(secondTF.getMaximumSize());

        labelsP.add(secondP);
        labelsP.add(Box.createRigidArea(new Dimension(0, 5)));

        textP.add(secondTF);
        textP.add(Box.createRigidArea(new Dimension(0, 5)));

        secondP.setVisible(true);
        secondLabel.setVisible(true);
        secondTF.setVisible(true);

        //Export menu

        //Component visibility
        subFrame.setVisible(true);
        mainPanel.setVisible(true);
        labelsP.setVisible(true);
        firstP.setVisible(true);
        firstLabel.setVisible(true);
        textP.setVisible(true);
        firstTF.setVisible(true);
        buttonsP.setVisible(true);
        ok.setVisible(true);
        cancel.setVisible(true);
    }

    private void showInfoMessage(SubWindowOptions value) {
        SubWindow.runInfoWindow(value);
    }

    public void windowSetup() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int SCREEN_HEIGHT = (int) screenSize.getHeight();
        final int SCREEN_WIDTH = (int) screenSize.getWidth();

        int FRAME_WIDTH = (int) (SCREEN_WIDTH * 0.6);
        int FRAME_HEIGHT = (int) (SCREEN_HEIGHT * 0.6);

        JMenuBar jMenuBar = getJMenuBar();

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
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setFocusable(true);
        panel1.addKeyListener(this);
        panel1.setMaximumSize(new Dimension(panel1Width, SCREEN_HEIGHT));
        panel1.setPreferredSize(panel1.getMaximumSize());

        //Map Management
        JPanel picScrollerHolder = new JPanel();
        picScrollerHolder.setLayout(new BoxLayout(picScrollerHolder, BoxLayout.Y_AXIS));
        picScrollerHolder.addKeyListener(this);
        picScrollerHolder.setFocusable(true);
        picScrollerHolder.setMaximumSize(new Dimension(SCREEN_WIDTH - panel1Width, SCREEN_HEIGHT));
        picScrollerHolder.setPreferredSize(picScrollerHolder.getMaximumSize());

        picScroller = new JScrollPane();
        picScroller.addKeyListener(this);
        picScroller.setFocusable(true);
        picScroller.setWheelScrollingEnabled(true);
        picScroller.setEnabled(false); //Enabled false allows the JScrollPane movement through arrow keys.

        //>>> Inside panel1
        int panel2Width = (int) (SCREEN_WIDTH * 0.40);
        int panel2Height = (int) (SCREEN_HEIGHT * 0.90);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.addKeyListener(this);
        panel2.setFocusable(true);
        panel2.setMaximumSize(new Dimension(panel2Width, panel2Height));
        panel2.setPreferredSize(panel2.getMaximumSize());

        //>>> Inside panel1 > Inside panel2
        //Sprites management
        JPanel spritesFather = new JPanel();
        spritesFather.setLayout(new BoxLayout(spritesFather, BoxLayout.Y_AXIS));
        spritesFather.addKeyListener(this);
        spritesFather.setMaximumSize(new Dimension((int) (panel2.getMaximumSize().getWidth() * 0.75), panel2Height));
        spritesFather.setPreferredSize(spritesFather.getMaximumSize());

        //>>> Inside panel1 > Inside panel2 > Inside spritesFather
        JPanel spriteLabelPanel = new JPanel();
        spriteLabelPanel.setLayout(new BoxLayout(spriteLabelPanel, BoxLayout.X_AXIS));
        spriteLabelPanel.addKeyListener(this);
        //>>> Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteLabelPanel
        JLabel spriteLabel = new JLabel(Strings.SPRITE_LABEL_NAME);

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
        JPanel layerPanel = new JPanel();
        layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
        layerPanel.setMaximumSize(new Dimension((int) (panel2.getMaximumSize().getWidth() * 0.25), panel2Height));
        layerPanel.setPreferredSize(layerPanel.getMaximumSize());
        //>>> Inside panel1 > Inside panel2 > Inside layerPanel
        JPanel layerLabelPanel = new JPanel();
        layerLabelPanel.setLayout(new BoxLayout(layerLabelPanel, BoxLayout.X_AXIS));
        layerLabelPanel.addKeyListener(this);
        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerLabelPanel
        actualLayerLabel = new JLabel(Strings.ACTUAL_LAYER_LABEL + " " + actualCanvas);
        actualLayerLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                actualLayerLabel.setText(actualCanvas);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                String layerLabel = Strings.ACTUAL_LAYER_LABEL + " " +
                        (actualCanvas.length() > MAX_LABEL_LENGHT ? actualCanvas.substring(0, 5) + "..." +
                                actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas);
                actualLayerLabel.setText(layerLabel);
            }
        });

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
        JPanel newLayerBPanel = new JPanel();
        newLayerBPanel.setLayout(new BoxLayout(newLayerBPanel, BoxLayout.X_AXIS));
        newLayerBPanel.setMaximumSize(new Dimension(layerPanel.getMaximumSize().width, (int) (layerPanel.getMaximumSize().getHeight() * 0.03)));
        newLayerBPanel.setPreferredSize(newLayerBPanel.getMaximumSize());

        newLayerB = new JButton(Strings.NEW_LAYER_BUTTON);
        newLayerB.setFocusable(true);
        newLayerB.setEnabled(false);
        newLayerB.addKeyListener(this);
        newLayerB.addMouseListener(this);
        newLayerB.addActionListener(e -> {
            String newLayerName = TA.getText().trim();
            if (!newLayerName.isEmpty() &&
                    newLayerName.matches("(\\w+(\\s+\\w+)*)") &&
                    !CANVAS.hasLayer(newLayerName)) {
                newLayerName = newLayerName.replaceAll("\\s+", "_").toLowerCase();
                addNewLayerButtons(newLayerName);
                if ((CANVAS.getSpriteSide() == 0) && (CANVAS.getCanvasSize() == 0)
                        && (SPRITESHEET.getSpriteSide() == 0)) {
                    runSubMenu("requestNewCanvasValues");
                }
                CANVAS.addNewCanvas(newLayerName);
                actualCanvas = newLayerName;
                actualLayerLabel.setText(Strings.ACTUAL_LAYER_LABEL + " " +
                        (actualCanvas.length() > MAX_LABEL_LENGHT ? actualCanvas.substring(0, 5) + "..." +
                                actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
            } else {
                showInfoMessage(SubWindowOptions.INVALID_LAYER_HELP);
            }
        });
        newLayerB.setMaximumSize(new Dimension(newLayerBPanel.getMaximumSize().width, (newLayerBPanel.getMaximumSize().height)));
        newLayerB.setPreferredSize(newLayerB.getMaximumSize());

        //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller
        layerScroller.setViewportView(layerSelector);

        //>>> Inside panel1
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
        panel3.addKeyListener(this);
        panel3.setFocusable(true);
        panel3.setMaximumSize(new Dimension(panel1Width, SCREEN_HEIGHT - panel2Height));
        panel3.setPreferredSize(panel3.getMaximumSize());


        //>>> Inside panel1 > Inside panel3
        JPanel panel4 = new JPanel();
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
        panel4.addKeyListener(this);
        panel4.setFocusable(true);
        panel4.setMaximumSize(new Dimension((int) (SCREEN_WIDTH * 0.15), panel3.getMaximumSize().height));

        //>>> Inside panel1 > Inside panel3 > Inside panel4
        JPanel panel5 = new JPanel();
        panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));
        panel5.addKeyListener(this);
        panel5.setFocusable(true);
        panel5.setMaximumSize(new Dimension(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2));

        JPanel panel6 = new JPanel();
        panel6.setLayout(new BoxLayout(panel6, BoxLayout.X_AXIS));
        panel6.addKeyListener(this);
        panel6.setFocusable(true);
        panel6.setMaximumSize(new Dimension(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2));

        //>>> Inside panel1 > Inside panel3 > Inside panel5
        Dimension buttonsDimension = new Dimension(panel4.getMaximumSize().width / 2, panel4.getMaximumSize().height / 2);

        biggerSprite = new JButton(Strings.BIGGER_SPRITE_BUTTON);
        biggerSprite.setFocusable(true);
        biggerSprite.setEnabled(false);
        biggerSprite.setActionCommand("+");
        biggerSprite.addKeyListener(this);
        biggerSprite.addMouseListener(this);
        biggerSprite.addActionListener(e -> {
            setSpriteListScale(++spriteListScale);
            int size = CANVAS.getCanvasSize() * spriteListScale;
            spritesPanel.setSize(new Dimension(size, size));
            spritesPanel.removeAll();
            buildJLabelList(spritesPanel, spriteListScale);
            spritesPanel.updateUI();
        });
        biggerSprite.setMaximumSize(buttonsDimension);

        smallerSprite = new JButton(Strings.SMALLER_SPRITE_BUTTON);
        smallerSprite.setFocusable(true);
        smallerSprite.setEnabled(false);
        smallerSprite.setActionCommand("-");
        smallerSprite.addKeyListener(this);
        smallerSprite.addMouseListener(this);
        smallerSprite.addActionListener(e -> {
            if (spriteListScale > 1) {
                setSpriteListScale(--spriteListScale);
            }
            int size = CANVAS.getCanvasSize() * spriteListScale;
            spritesPanel.setSize(new Dimension(size, size));
            spritesPanel.removeAll();
            buildJLabelList(spritesPanel, spriteListScale);
            spritesPanel.updateUI();
        });
        smallerSprite.setMaximumSize(buttonsDimension);

        //>>> Inside panel1 > Inside panel3 > Inside panel5 > Inside panel6
        //The string used for the name of map buttons must include extra empty spaces in order
        //to match the length of the strings used for the sprite buttons. both groups of strings
        //must be equally long in order to be resized in the same way when the screen resolution
        //changes.
        biggerMap = new JButton(Strings.BIGGER_MAP_BUTTON);
        biggerMap.setFocusable(true);
        biggerMap.setEnabled(false);
        biggerMap.addKeyListener(this);
        biggerMap.addMouseListener(this);
        biggerMap.addActionListener(e -> {
            setMapScale(MAP_SCALE_RATIO);
            updateMainCanvas(mapScale);
        });
        biggerMap.setMaximumSize(buttonsDimension);

        smallerMap = new JButton(Strings.SMALLER_MAP_BUTTTON);
        smallerMap.setFocusable(true);
        smallerMap.setEnabled(false);
        smallerMap.addKeyListener(this);
        smallerMap.addMouseListener(this);
        smallerMap.addActionListener(e -> {
            if ((mapScale - MAP_SCALE_RATIO) > 0) {
                setMapScale(-MAP_SCALE_RATIO);
                updateMainCanvas(mapScale);
            }
        });
        smallerMap.setMaximumSize(buttonsDimension);

        //>>> Inside panel1 > Inside panel3
        int panel7Width = panel3.getMaximumSize().width - panel4.getMaximumSize().width;
        int panel7Height = panel3.getMaximumSize().height;
        JPanel panel7 = new JPanel();
        panel7.setLayout(new BoxLayout(panel7, BoxLayout.Y_AXIS));
        panel7.addKeyListener(this);
        panel7.setFocusable(true);
        panel7.setMaximumSize(new Dimension(panel7Width, panel7Height));

        //>>> Inside panel1 > Inside panel3 > panel7
        JScrollPane taScroller = new JScrollPane();
        taScroller.addKeyListener(this);
        taScroller.setFocusable(true);
        taScroller.setWheelScrollingEnabled(true);

        //>>> Inside panel1 > Inside panel3 > panel7 > Inside taScroller
        TA.addKeyListener(this);
        TA.addMouseListener(this);
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
        panel1.setVisible(true);
        picScrollerHolder.setVisible(true);
        picScroller.setVisible(true);
        panel2.setVisible(true);
        spritesFather.setVisible(true);
        spriteLabel.setVisible(true);
        spriteListScroller.setVisible(true);
        spritesPanel.setVisible(true);
        layerPanel.setVisible(true);
        layerScroller.setVisible(true);
        layerSelector.setVisible(true);
        newLayerB.setVisible(true);
        panel3.setVisible(true);
        biggerSprite.setVisible(true);
        smallerSprite.setVisible(true);
        biggerMap.setVisible(true);
        smallerMap.setVisible(true);
        TA.setVisible(true);
    }


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
            //Next line is set in order to not allow X or Y to reach the end of the pointer BufferedImage.
            //If any coordinate reaches the end of the axis, it would generate the square to be drawn outside
            //of the buffered, since it is the top left coordinate the one which is taken9 in consideration.
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

    @Override
    public void mouseClicked(MouseEvent e) {
        TA.setText(Strings.NEW_LAYER_REQUIRED);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}