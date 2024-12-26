package com.SpriteSheeter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UserInterface implements KeyListener {

    //Boolean to be used to toggle on/off the movement of the main map scroller throught directional keys.
    boolean toggleMapMovement = false;
    boolean toggleSpriteMovement = false;

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
    int maxActualCanvasLength = 17;
    //The amount of pixels
    private int movementIncrement;
    //Screen size
    private final int MAP_SCALE_RATIO = 1;
    private String actualCanvas = "default_layer";
    private SpriteSheet spriteSheet;
    private JFrame frame;
    private JScrollPane picScroller;
    private JLabel picLabel;
    private JPanel spritesPanel;
    private JLabel actualLayerLabel;
    private JPanel layerSelector;
    private JScrollPane spriteListScroller;
    private JTextArea ta = new JTextArea();
    private final JViewport mapView = new JViewport();
    private final JViewport spriteView = new JViewport();
    private JButton biggerMap;
    private JButton smallerMap;
    private JButton biggerSprite;
    private JButton smallerSprite;
    private BufferedImage previousSprite;

    public void getStartValues() {
        runSubMenu("generateNewCanvas");
    }

    public void setUpEverything() {
        if (spriteSheet != null) {
            int SCREEN_HEIGHT = 1080;
            int SCREEN_WIDTH = 1920;
            //Creo una ventana con todas las configuraciones relativas a la misma
            int FRAME_WIDTH = 1280;
            int FRAME_HEIGHT = 720;

            frame = new JFrame("Spriter");
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        To explain layout for frame > https://stackoverflow.com/questions/24840860/boxlayout-for-a-jframe
            frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
            frame.addKeyListener(this);
            frame.setFocusable(true);
            frame.setFocusTraversalKeysEnabled(false);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(null);

            JMenuBar jMenuBar = getjMenuBar();

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
            picScrollerHolder.addKeyListener(this);
            picScrollerHolder.setFocusable(true);
            picScrollerHolder.setLayout(new BoxLayout(picScrollerHolder, BoxLayout.Y_AXIS));
            picScrollerHolder.setMaximumSize(new Dimension(SCREEN_WIDTH - panel1Width, SCREEN_HEIGHT));
            picScrollerHolder.setPreferredSize(picScrollerHolder.getMaximumSize());

            picScroller = new JScrollPane();
            picScroller.addKeyListener(this);
            picScroller.setFocusable(true);
            picScroller.setWheelScrollingEnabled(true);

//            Graphics2D pictureGraphics = spriteSheet.getPOINTER_LAYER().createGraphics();
//            pictureGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
//            pictureGraphics.setColor(Color.RED);
//            pictureGraphics.drawRect(x + 1, y + 1, 15, 15);
//            pictureGraphics.dispose();

            picLabel = new JLabel(new ImageIcon(spriteSheet.getFullCanvas()));
            picLabel.addKeyListener(this);
            picLabel.setFocusable(true);

            mapView.setView(picLabel);

            //>>> Inside panel1
            int panel2Width = (int) (SCREEN_WIDTH * 0.40);
            int panel2Height = (int) (SCREEN_HEIGHT * 0.90);
            JPanel panel2 = new JPanel();
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
            panel2.setFocusable(true);
            panel2.addKeyListener(this);
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
            JLabel spriteLabel = new JLabel("Sprites list:");
            //>>> Inside panel1 > Inside spritesFather
            spriteListScroller = new JScrollPane();
            spriteListScroller.addKeyListener(this);
            spriteListScroller.setWheelScrollingEnabled(true);
            //>>> Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteListScroller
            spritesPanel = new JPanel();
            spritesPanel.setLayout(new BoxLayout(spritesPanel, BoxLayout.PAGE_AXIS));
            spritesPanel.addKeyListener(this);

            buildJLabelList(spritesPanel, spriteSheet, spriteListScale);
            spriteView.setView(spritesPanel);


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
            actualLayerLabel = new JLabel("Actual layer: " + actualCanvas);
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
                    actualLayerLabel.setText("Actual layer: " +
                            (actualCanvas.length() > maxActualCanvasLength ? actualCanvas.substring(0, 5) + "..." +
                                    actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
                }
            });

            //>>> Inside panel1 > Inside panel2 > Inside layerPanel
            JScrollPane layerScroller = new JScrollPane();
            layerScroller.addKeyListener(this);
            layerScroller.setFocusable(true);
            layerScroller.setWheelScrollingEnabled(true);
            //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller
            layerSelector = new JPanel();
            layerSelector.setLayout(new BoxLayout(layerSelector, BoxLayout.Y_AXIS));
            layerSelector.setFocusable(false);


            //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller > layerSelector
            addNewLayerButtons(actualCanvas);

            JPanel newLayerBPanel = new JPanel();
            newLayerBPanel.setLayout(new BoxLayout(newLayerBPanel, BoxLayout.X_AXIS));
            newLayerBPanel.setMaximumSize(new Dimension(layerPanel.getMaximumSize().width, (int) (layerPanel.getMaximumSize().getHeight() * 0.03)));
            newLayerBPanel.setPreferredSize(newLayerBPanel.getMaximumSize());

            JButton newLayerB = new JButton("New layer");
            newLayerB.addKeyListener(this);
            newLayerB.setFocusable(true);
            newLayerB.setMaximumSize(new Dimension(newLayerBPanel.getMaximumSize().width, (int) (newLayerBPanel.getMaximumSize().height)));
            newLayerB.setPreferredSize(newLayerB.getMaximumSize());

            newLayerB.addActionListener(e -> {
                String newLayerName = ta.getText().trim();
                if (!newLayerName.isEmpty() && newLayerName.matches("(\\w+(\\s+\\w+)*)")) {
                    newLayerName = newLayerName.replaceAll("\\s+", "_").toLowerCase();
                    System.out.println("Inside");
                    addNewLayerButtons(newLayerName);
                    spriteSheet.addNewCanvas(newLayerName);
                    actualCanvas = newLayerName;
                    actualLayerLabel.setText("Actual layer:\n" +
                            (actualCanvas.length() > maxActualCanvasLength ? actualCanvas.substring(0, 5) + "..." +
                                    actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
                } else {
                    runInfoWindo("invalidLayerHelp");
                }
            });


            //>>> Inside panel1 > Inside panel2 > Inside layerPanel > layerScroller
            layerScroller.setViewportView(layerSelector);

            //>>> Inside panel1
            JPanel panel3 = new JPanel();
            panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
            panel3.setFocusable(true);
            panel3.addKeyListener(this);
            panel3.setMaximumSize(new Dimension(panel1Width, SCREEN_HEIGHT - panel2Height));
            panel3.setPreferredSize(panel3.getMaximumSize());


            //>>> Inside panel1 > Inside panel3
            JPanel panel4 = new JPanel();
            panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
            panel4.setFocusable(true);
            panel4.addKeyListener(this);
            panel4.setMaximumSize(new Dimension((int) (SCREEN_WIDTH * 0.15), panel3.getMaximumSize().height));

            //>>> Inside panel1 > Inside panel3 > Inside panel4
            JPanel panel5 = new JPanel();
            panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));
            panel5.setFocusable(true);
            panel5.addKeyListener(this);
            panel5.setMaximumSize(new Dimension(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2));

            JPanel panel6 = new JPanel();
            panel6.setLayout(new BoxLayout(panel6, BoxLayout.X_AXIS));
            panel6.setFocusable(true);
            panel6.addKeyListener(this);

            panel6.setMaximumSize(new Dimension(panel4.getMaximumSize().width, panel4.getMaximumSize().height / 2));

            //>>> Inside panel1 > Inside panel3 > Inside panel5
            Dimension buttonsDimension = new Dimension(panel4.getMaximumSize().width / 2, panel4.getMaximumSize().width / 2);

            biggerSprite = new JButton("Sprite zoom +");
            biggerSprite.setMaximumSize(buttonsDimension);
            biggerSprite.setActionCommand("+");
            biggerSprite.addKeyListener(this);
            biggerSprite.setFocusable(true);
            biggerSprite.addActionListener(e -> {
                setSpriteListScale(++spriteListScale);
                int size = spriteSheet.getINITIAL_CANVAS_SIZE() * spriteListScale;
                spritesPanel.setSize(new Dimension(size, size));
                spritesPanel.removeAll();
                buildJLabelList(spritesPanel, spriteSheet, spriteListScale);
                spritesPanel.updateUI();
            });

            smallerSprite = new JButton("Sprite zoom -");
            smallerSprite.setMaximumSize(buttonsDimension);
            smallerSprite.setActionCommand("-");
            smallerSprite.addKeyListener(this);
            smallerSprite.setFocusable(true);
            smallerSprite.addActionListener(e -> {
                if (spriteListScale > 1) {
                    setSpriteListScale(--spriteListScale);
                }
                int size = spriteSheet.getINITIAL_CANVAS_SIZE() * spriteListScale;
                spritesPanel.setSize(new Dimension(size, size));
                spritesPanel.removeAll();
                buildJLabelList(spritesPanel, spriteSheet, spriteListScale);
                spritesPanel.updateUI();
            });

            //>>> Inside panel1 > Inside panel3 > Inside panel5 > Inside panel6
            biggerMap = new JButton("Map zoom +");
            biggerMap.setMaximumSize(buttonsDimension);
            biggerMap.addKeyListener(this);
            biggerMap.setFocusable(true);
            biggerMap.addActionListener(e -> {
                setMapScale(MAP_SCALE_RATIO);
//                setLabelSize(picLabel, SPRITE_SHEET, getMapScale());
                updateMainCanvas(getMapScale());
            });

            smallerMap = new JButton("Map zoom -");
            smallerMap.setMaximumSize(buttonsDimension);
            smallerMap.addKeyListener(this);
            smallerMap.setFocusable(true);
            smallerMap.addActionListener(e -> {
                if ((mapScale - MAP_SCALE_RATIO) > 0) {
                    setMapScale(-MAP_SCALE_RATIO);
//                    setLabelSize(picLabel, SPRITE_SHEET, getMapScale());
                    updateMainCanvas(getMapScale());
                }
            });

            //>>> Inside panel1 > Inside panel3
            int panel7Width = panel3.getMaximumSize().width - panel4.getMaximumSize().width;
            int panel7Height = panel3.getMaximumSize().height;
            JPanel panel7 = new JPanel();
            panel7.setLayout(new BoxLayout(panel7, BoxLayout.Y_AXIS));
            panel7.setFocusable(true);

            panel7.addKeyListener(this);
            panel7.setMaximumSize(new Dimension(panel7Width, panel7Height));

            //>>> Inside panel1 > Inside panel3 > panel7
            JScrollPane taScroller = new JScrollPane();
            taScroller.addKeyListener(this);
            taScroller.setFocusable(true);
            taScroller.setWheelScrollingEnabled(true);

            //>>> Inside panel1 > Inside panel3 > panel7 > Inside taScroller
            ta.addKeyListener(this);
            taScroller.setViewportView(ta);

            //Layout adding.
            frame.add(Box.createRigidArea(new Dimension(5, 0)));
            frame.add(panel1);
            frame.add(Box.createRigidArea(new Dimension(5, 0)));
            frame.add(picScrollerHolder);
            frame.add(Box.createRigidArea(new Dimension(5, 0)));


            //>>> Inside panel1
            panel1.add(Box.createRigidArea(new Dimension(5, 0)));
            panel1.add(panel2);
            panel1.add(Box.createRigidArea(new Dimension(5, 0)));
            panel1.add(panel3);
            panel1.add(Box.createRigidArea(new Dimension(5, 0)));


            //>>> Inside panel1 > Inside panel2
            panel2.add(Box.createRigidArea(new Dimension(5, 0)));
            panel2.add(spritesFather);

            //>>> Inside panel1 > Inside panel2 > Inside spritesFather
            spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));
            spritesFather.add(spriteLabelPanel);
            //>>> Inside panel1 > Inside panel2 > Inside spritesFather > Inside spriteLabelPanel
            spriteLabelPanel.add(spriteLabel);
            spriteLabelPanel.add(Box.createHorizontalGlue());
            //>>> Inside panel1 > Inside panel2 > Inside spritesFather
            spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));
            spritesFather.add(spriteListScroller);
            spriteListScroller.setViewportView(spriteView);
            spritesFather.add(Box.createRigidArea(new Dimension(0, 5)));

            //>>> Inside panel1 > Inside panel2
            panel2.add(Box.createRigidArea(new Dimension(5, 0)));
            panel2.add(layerPanel);
            //>>> Inside panel1 > Inside panel2

            //>>> Inside panel1 > Inside panel2 > Inside layerPanel
            layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            layerPanel.add(layerLabelPanel);
            //>>> Inside panel1 > Inside panel2 > layerPanel > layerLabelPanel
            layerLabelPanel.add(actualLayerLabel);
            layerLabelPanel.add(Box.createHorizontalGlue());
            //>>> Inside panel1 > Inside panel2 > layerPanel
            layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            layerPanel.add(layerScroller);
            layerScroller.setViewportView(layerSelector);
            layerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
            layerPanel.add(newLayerBPanel);
            //>>> Inside panel1 > Inside panel2 > layerPanel > Inside newLayerBPanel
            newLayerBPanel.add(newLayerB);
            //>>> Inside panel1 > Inside panel2 > layerPanel
            layerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            //>>> Inside panel1 > Inside panel2
            panel2.add(Box.createRigidArea(new Dimension(5, 0)));


            //>>> Inside panel1 > Inside panel3
            panel3.add(Box.createRigidArea(new Dimension(5, 0)));
            panel3.add(panel4);
            panel3.add(Box.createRigidArea(new Dimension(5, 0)));
            panel3.add(panel7);
            panel3.add(Box.createRigidArea(new Dimension(5, 0)));

            //>>> Inside panel1 > Inside panel3 > Inside panel4
            panel4.add(Box.createRigidArea(new Dimension(0, 5)));
            panel4.add(panel5);
            panel4.add(Box.createRigidArea(new Dimension(0, 5)));
            panel4.add(panel6);
            panel4.add(Box.createRigidArea(new Dimension(0, 5)));

            //>>> Inside panel1 > Inside panel3 > Inside panel4 > Inside panel5
            panel5.add(biggerSprite);
            panel5.add(Box.createRigidArea(new Dimension(5, 0)));
            panel5.add(smallerSprite);

            //>>> Inside panel1 > Inside panel3 > Inside panel4 > Inside panel5
            panel6.add(biggerMap);
            panel6.add(Box.createRigidArea(new Dimension(5, 0)));
            panel6.add(smallerMap);

            //>>> Inside panel1 > Inside panel3 > Inside panel7
            panel7.add(Box.createRigidArea(new Dimension(0, 5)));
            panel7.add(taScroller);
            panel7.add(Box.createRigidArea(new Dimension(0, 5)));


            //>>>frane > picScrollerHolder
            picScrollerHolder.add(Box.createRigidArea(new Dimension(0, 5)));
            picScrollerHolder.add(picScroller);
            picScroller.setViewportView(mapView);
            picScrollerHolder.add(Box.createRigidArea(new Dimension(0, 5)));


            //Setting everything visible
            frame.setVisible(true);
            panel1.setVisible(true);
            panel1.setVisible(true);
            panel3.setVisible(true);
            spritesFather.setVisible(true);
            spriteLabel.setVisible(true);
            spriteListScroller.setVisible(true);
            spritesPanel.setVisible(true);
            layerPanel.setVisible(true);
            layerScroller.setVisible(true);
            layerSelector.setVisible(true);
            newLayerB.setVisible(true);
            picScrollerHolder.setVisible(true);
            picScroller.setVisible(true);
            picLabel.setVisible(true);
            biggerSprite.setVisible(true);
            smallerSprite.setVisible(true);
            ta.setVisible(true);
            biggerMap.setVisible(true);
            smallerMap.setVisible(true);
        } else {
            System.exit(0);
        }
    }

    private JMenuBar getjMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenu subOptions = new JMenu("Import / export code");
        JMenu clearLayerMenu = new JMenu("Clear layer           ");
        JMenu deleteLayerMenu = new JMenu("Delete layer           ");
        JMenu layerMenu = new JMenu("Layer management           ");


        //JMenuItem clearlayer
        JMenuItem clearLayer = new JMenuItem("Clear actual layer           ");
        clearLayer.addActionListener(e -> {
            spriteSheet.clearLayer(actualCanvas);
            updateMainCanvas(getMapScale());
        });

        //        JMenuItem clearlayer
        JMenuItem clearAllLayer = new JMenuItem("Clear all layers           ");
        clearAllLayer.addActionListener(e -> {
            spriteSheet.clearAllLayers();
            updateMainCanvas(getMapScale());
        });

        //JMenuItem clearlayer
        JMenuItem deleteLayer = new JMenuItem("Delete actual layer           ");
        deleteLayer.addActionListener(e -> {
            System.out.println("Inside");
            spriteSheet.deleteLayer(actualCanvas);
            updateLayerButtons();
            updateMainCanvas(getMapScale());
        });

        //        JMenuItem clearlayer
        JMenuItem deleteAllLayer = new JMenuItem("Delete all layers           ");
        deleteAllLayer.addActionListener(e -> {
            spriteSheet.deleteAllLayers();
            removeAllLayer();
            actualCanvas = "noLayer";
            updateMainCanvas(getMapScale());
        });

        //JMenuItem  generateCode
        JMenuItem importCode = new JMenuItem("Import");
        importCode.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(importCode);
            if (!(returnVal == JFileChooser.CANCEL_OPTION)) {
                try {
                    File notePad = new File(fc.getSelectedFile().getAbsolutePath());
                    Scanner scanner = new Scanner(notePad);
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine());
                        sb.append("\n");
                    }
                    ta.setText(sb.toString());
                    Map<String, int[]> loadedData = getLoadedLayers(sb.toString());
                    if (loadedData != null) {
                        resetLayerSelector(loadedData);
                        spritesPanel.removeAll();
                        spriteSheet.setPicturePath(getLoadedPath(sb.toString()));
                        spriteSheet.loadSpriteSheet();
                        buildJLabelList(spritesPanel, spriteSheet, spriteListScale);
                        spriteSheet.loadImportedData(loadedData);
                        updateMainCanvas(getMapScale());
                    } else {
                        runInfoWindo("invalidFile");
                        System.out.println("Operation Failed.");
                    }
                } catch (IOException ex) {
                    runInfoWindo("invalidPath");
                    returnVal = fc.showOpenDialog(importCode);
                }
            }
        });

        JMenuItem exportCode = new JMenuItem("Export");
        exportCode.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            String arrayPrinted = spriteSheet.getIdArrayPrinted();
            fc.setApproveButtonText("OK");
            try {
                int returnVal = fc.showOpenDialog(exportCode);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File notePad = new File(fc.getSelectedFile() + ".txt");
                    FileWriter writer = new FileWriter(fc.getSelectedFile() + ".txt");
                    writer.write(arrayPrinted);
                    writer.close();
                    System.out.println("Successfully wrote to the file.");
                    System.out.println(fc.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ta.setText(arrayPrinted);
        });

        //JMenuItem  exportCanvas
        JMenuItem exportCanvas = new JMenuItem("Export canvas           ");
        exportCanvas.addActionListener(e -> {
            runSubMenu("export");
        });

        //JMenuItem help
        JMenuItem help = new JMenuItem("Help           ");
        help.addActionListener(e -> {
            runInfoWindo("help");
        });


        clearLayer.setVisible(true);
        importCode.setVisible(true);
        exportCode.setVisible(true);
        exportCanvas.setVisible(true);
        help.setVisible(true);

        clearLayerMenu.add(clearLayer);
        clearLayerMenu.add(clearAllLayer);

        deleteLayerMenu.add(deleteLayer);
        deleteLayerMenu.add(deleteAllLayer);

        layerMenu.add(clearLayerMenu);
        layerMenu.add(deleteLayerMenu);

        subOptions.add(importCode);
        subOptions.add(exportCode);

        options.add(layerMenu);
        options.add(subOptions);
        options.add(exportCanvas);
        options.add(help);


        options.setVisible(true);

        jMenuBar.setVisible(true);

        jMenuBar.add(options);
        return jMenuBar;
    }

    private int getMapScale() {
        return mapScale;
    }

    private void setMapScale(int movementIncrement) {
        this.mapScale += movementIncrement;
    }

    private int getSpriteListScale() {
        return spriteListScale;
    }

    private void setSpriteListScale(int spriteListScale) {
        this.spriteListScale = spriteListScale;
    }

    private Map<String, int[]> getLoadedLayers(String loadedData) {
        //There's documentation about  this method already done.
        Matcher matcher = Pattern.compile("(?<=//Sprites\\sin\\sside\\s=\\s)\\d+(?=\\n)").matcher(loadedData);
        int amountOfSprites = 0;
        while (matcher.find()) {
            amountOfSprites = (int) Math.pow(Integer.parseInt(matcher.group()), 2);
        }
        matcher.reset();

        String firstRegex = "(?<=//Layer:\\s)(\\w+(_+\\w+)*)(?=\\nint\\[\\]\\[\\])";

        matcher.usePattern(Pattern.compile(firstRegex, Pattern.MULTILINE));
        Map<String, int[]> layers = new LinkedHashMap<>();
        while (matcher.find()) {
            layers.put(matcher.group(), null);
        }
        matcher.reset();

        for (Map.Entry<String, int[]> numbersMap : layers.entrySet()) {
            String secondRegex = "(?<=};\\n//" + numbersMap.getKey() + ":)((\\d)+\\s)+(?=\\n)";
            matcher.usePattern(Pattern.compile(secondRegex, Pattern.MULTILINE));
            String numbersResult = "";
            while (matcher.find()) {
                numbersResult = matcher.group();
            }
            String[] justNumbers = numbersResult.split("\\s");
            if (amountOfSprites == justNumbers.length) {
                int[] numbers = new int[justNumbers.length];
                for (int i = 0; i < justNumbers.length; i++) {
                    numbers[i] = Integer.parseInt(justNumbers[i]);
                }
                numbersMap.setValue(numbers);
                matcher.reset();
            } else {
                System.out.println("Way to break the whole operation");
                layers = null;
                break;
            }
        }

        return layers;
    }

    private String getLoadedPath(String loadedData) {
        String path = "";
        Matcher matcher = Pattern.compile("(?<=\\n##)(\\w:(.*))(?=##\\n\\n)").matcher(loadedData);
        while (matcher.find()) {
            path = matcher.group();
        }
        matcher.reset();
        return path;
    }


    private void updateLayerButtons() {
        removeAllLayer();
        for (Map.Entry<String, BufferedImage> layers : spriteSheet.getLAYERS().entrySet()) {
            addNewLayerButtons(layers.getKey());
        }
    }

    private void removeAllLayer() {
        layerSelector.removeAll();
    }

    private void resetLayerSelector(Map<String, int[]> newlayerSelector) {
        removeAllLayer();
        for (Map.Entry<String, int[]> newLayers : newlayerSelector.entrySet()) {
            addNewLayerButtons(newLayers.getKey());
            spriteSheet.addNewCanvas(newLayers.getKey());
            actualCanvas = newLayers.getKey();
            actualLayerLabel.setText("Actual layer:\n" +
                    (actualCanvas.length() > maxActualCanvasLength ? actualCanvas.substring(0, 5) + "..." +
                            actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
        }
    }

    private void updateMainCanvas(int scale) {
        picLabel.setIcon(new ImageIcon(spriteSheet.getScaledCanvas(scale)));
    }

    private void buildJLabelList(JPanel spritesPanel, SpriteSheet spriteSheet, int spriteListScaleRatio) {
        int targetSide = spriteSheet.getSPRITE_SIDE() * spriteListScaleRatio;
        int j = 0;
        BufferedImage newImage;
        for (int i = 0; i < spriteSheet.getTilesInColumn(); i++) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
            spritesPanel.add(jPanel);
            j++;
            for (; j < (spriteSheet.getTilesInColumn() * spriteSheet.getTilesInRow()) + 1; j++) {
                newImage = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_ARGB);
                BufferedImage b = spriteSheet.getSPRITES_HASMAP().get(j - 1).getSprite();
                newImage.createGraphics().drawImage(b, 0, 0, targetSide, targetSide, null);
                JButton button = new JButton();
                button.setIcon(new ImageIcon(newImage));
                button.setPreferredSize(new Dimension(targetSide, targetSide));
                button.setMaximumSize(new Dimension(targetSide, targetSide));
                button.addKeyListener(this);
                button.setFocusable(false);
                //Se tiene que crear una variable local id, aunque ya exista una global, porque para poder
                //usar el valor de j dentro de la declaración del actionListener del botón, dicha variable debe
                //ser final o effectively final. Y no podemos hacer id = j; y asignarle id al método get
                //spriteSheet.getSPRITES_HASMAP().get(id - 1).getId()); porque para cuando se pulse el botón, y
                //se llame a la acción del botón, el valor de id será el último valor que se le asignase, es
                //decir, el valor del último botón que se crease, la última iteración de este bucle for, donde
                //se estableción id = j; Si el último botón que se creo fue el 540, el valor de id será id = 540
                //y será siempre así. No obstante, id se ha de actualizar al valor de innerId dentro de la acción
                //del botón para que cuando se active la función de dibujar de forma constante, el programa sepa
                //cual es la id que ha de utilizar a la hora de pintar. La función de pintar usa la última id
                //conocida, y esa id se actualiza cada vez que se presiona un botón.
                int innerId = j;
                button.addActionListener(e -> {
//                        https://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html

                    System.out.println("Pressed sprite idd: " + spriteSheet.getSPRITES_HASMAP().get(innerId - 1).getId());
                    //Used to set the kind of Composite to be used in the BufferedImage in use. Check the above
                    //link.
                    if (!actualCanvas.equals("noLayer")) {
                        Graphics2D pictureGraphics = spriteSheet.getLayer(actualCanvas).createGraphics();
                        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
                        pictureGraphics.setComposite(ac);
                        pictureGraphics.drawImage(b, x, y, null);
                        pictureGraphics.dispose();
                        previousSprite = b;

                        int arrayIndexY = y / 16;
                        int arrayIndexX = x / 16;
                        int[][] returnedArray = spriteSheet.getID_ARRAY_MAP(actualCanvas);
                        returnedArray[arrayIndexY][arrayIndexX] = spriteSheet.getSPRITES_HASMAP().get(innerId - 1).getId();
                        id = innerId;
                        updateMainCanvas(getMapScale());
                    }
                });
                jPanel.add(button);
                if (j % spriteSheet.getTilesInRow() == 0) {
                    break;
                }
            }
        }
    }

    private void addNewLayerButtons(String layerName) {
//        layerName = layerName.replaceAll("\\s+", "_").toLowerCase();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JButton layerButton = new JButton(layerName);
        layerButton.addKeyListener(this);
        layerButton.setFocusable(false);

        String finalLayerName = layerName;

        layerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualCanvas = finalLayerName;
                actualLayerLabel.setText("Actual layer: " +
                        (actualCanvas.length() > maxActualCanvasLength ? actualCanvas.substring(0, 5) + "..." +
                                actualCanvas.substring(actualCanvas.length() - 5) : actualCanvas));
            }
        });

        JRadioButton radioButton = new JRadioButton();
        radioButton.addKeyListener(this);
        radioButton.setFocusable(true);
        radioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioButton.isSelected()) {
                    spriteSheet.hideLayer(finalLayerName);
                } else {
                    spriteSheet.showLayer(finalLayerName);
                }
                updateMainCanvas(getMapScale());
            }
        });

        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(radioButton);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(layerButton);
        panel.add(Box.createHorizontalGlue());

        layerSelector.add(Box.createRigidArea(new Dimension(0, 3)));
        layerSelector.add(panel);
        layerSelector.updateUI();

        ta.setText("");
    }

//    private void checkCommand(String command) {
//        System.out.println("Command: " + command);
//        if (command.startsWith("/new ")) {
//            command = command.replace("/new ", "");
//            spriteSheet.addNewCanvas(command);
//            actualCanvas = command;
////            setLabelSize(picLabel, SPRITE_SHEET, getMapScale());
//            updateMainCanvas(getMapScale());
//            System.out.println("New canvas: -" + actualCanvas + "-");
//        } else if (command.startsWith("/layer ")) {
//            command = command.replace("/layer ", "");
//            actualCanvas = command;
//            System.out.println("Actual canvas: -" + actualCanvas + "-");
//        } else if (command.startsWith("/get")) {
//            for (Map.Entry<Integer, Sprite> entry : spriteSheet.getSPRITES_HASMAP().entrySet()) {
//                System.out.println("entry.getKey() = " + entry.getKey());
//
//            }
//        }
//    }

    private void pointerMovement(String direction) {
        int spriteSide = spriteSheet.getSPRITE_SIDE();
        Color pointer = Color.RED;
        Graphics2D pointerGraphics = spriteSheet.getPOINTER_LAYER().createGraphics();
        pointerGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        pointerGraphics.drawImage(new BufferedImage(spriteSide, spriteSide, BufferedImage.TYPE_INT_ARGB),
                x + 1, y + 1, 16, 16, null);

        switch (direction) {
            case "up":
                y -= movementIncrement;
                break;
            case "down":
                y += movementIncrement;
                break;
            case "left":
                x -= movementIncrement;
                break;
            case "right":
                x += movementIncrement;
                break;
        }
        if (fillingBrush) {
            Graphics2D pictureGraphics = spriteSheet.getLayer(actualCanvas).createGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
            pictureGraphics.setComposite(ac);
            pictureGraphics.drawImage(previousSprite, x, y, null);
            pictureGraphics.dispose();
            pointer = Color.GREEN;
            int arrayIndexY = y / 16;
            int arrayIndexX = x / 16;
            int[][] returnedArray = spriteSheet.getID_ARRAY_MAP(actualCanvas);
            returnedArray[arrayIndexY][arrayIndexX] = spriteSheet.getSPRITES_HASMAP().get(id - 1).getId();
            System.out.println("returnedArray[arrayIndexY][arrayIndexX] = " + returnedArray[arrayIndexY][arrayIndexX]);
        }

        pointerGraphics.setColor(pointer);
        pointerGraphics.drawRect(x + 1, y + 1, 15, 15);
        pointerGraphics.dispose();

        updateMainCanvas(getMapScale());
    }

    private void moveMapViewPort(String direction) {
        Point newPicViewPosition = mapView.getViewPosition();
        int viewMovement = spriteSheet.getSPRITE_SIDE() * getMapScale();
        switch (direction) {
            case "left":
                if ((newPicViewPosition.x - viewMovement) >= -viewMovement) {
                    newPicViewPosition.x -= viewMovement;
                }
                break;
            case "right":
                newPicViewPosition.x += viewMovement;
                break;
            case "up":
                if ((newPicViewPosition.y - viewMovement) >= -viewMovement) {
                    newPicViewPosition.y -= viewMovement;
                }
                break;
            case "down":
                newPicViewPosition.y += viewMovement;
                break;
        }
        mapView.setViewPosition(newPicViewPosition);
        picScroller.setViewport(mapView);
    }

    private void moveSpriteViewPort(String direction) {
        Point newPicViewPosition = spriteView.getViewPosition();
        int viewMovement = spriteSheet.getSPRITE_SIDE() * getSpriteListScale();
        switch (direction) {
            case "left":
                if ((newPicViewPosition.x - viewMovement) >= -viewMovement) {
                    newPicViewPosition.x -= viewMovement;
                }
                break;
            case "right":
                newPicViewPosition.x += viewMovement;
                break;
            case "up":
                if ((newPicViewPosition.y - viewMovement) >= -viewMovement) {
                    newPicViewPosition.y -= viewMovement;
                }
                break;
            case "down":
                newPicViewPosition.y += viewMovement;
                break;
        }
        spriteView.setViewPosition(newPicViewPosition);
        spriteListScroller.setViewport(spriteView);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //Next line is set in order to not allow X or Y to reach the end of the pointer BufferedImage.
        //If any coordinate reaches the end of the axis, it would generate the square to be drawn outside
        //of the buffered, since it is the top left coordinate the one which is taken in consideration.
        int width = spriteSheet.getPOINTER_LAYER().getWidth() - spriteSheet.getSPRITE_SIDE();
        System.out.println("Key = " + key);
        if (ta.hasFocus()) {
            if (key == 27) {
                //Key 27 = Esc
                frame.requestFocus();
            }
        } else {
            if (key == 17) {
                //Key 17 = Ctrl
                if (!toggleMapMovement) {
                    toggleMapMovement = true;
                }
            } else if (key == 16) {
                //Key 17 = Shift
                if (!toggleSpriteMovement) {
                    toggleSpriteMovement = true;
                }
            } else if (key == 65 || key == 37) {
                //Left > Key 65 = 'A', key 37 = left arrow
                if (toggleMapMovement) {
                    moveMapViewPort("left");
                } else if (toggleSpriteMovement) {
                    moveSpriteViewPort("left");
                } else {
                    if ((x - movementIncrement) >= 0) {
                        pointerMovement("left");
                    }
                }
            } else if (key == 68 || key == 39) {
                //Right > Key 68 = 'D', key 37 = right arrow
                if (toggleMapMovement) {
                    moveMapViewPort("right");
                } else if (toggleSpriteMovement) {
                    moveSpriteViewPort("right");
                } else {
                    if ((x + movementIncrement) <= width) {
                        pointerMovement("right");
                    }
                }
            } else if (key == 87 || key == 38) {
                //Up > Key 65 = 'W', key 37 = up arrow
                if (toggleMapMovement) {
                    moveMapViewPort("up");
                } else if (toggleSpriteMovement) {
                    moveSpriteViewPort("up");
                } else {
                    if ((y - movementIncrement) >= 0) {
                        pointerMovement("up");
                    }
                }
            } else if (key == 83 || key == 40) {
                //Down > Key 65 = 'S', key 37 = down arrow
                if (toggleMapMovement) {
                    moveMapViewPort("down");
                } else if (toggleSpriteMovement) {
                    moveSpriteViewPort("down");
                } else {
                    if ((y + movementIncrement) <= width) {
                        pointerMovement("down");
                    }
                }
            } else if (key == 521 || key == 107) {
                //+ key > key 521 = * + ] key close to enter, key 107 = + numerical number.
                if (toggleMapMovement) {
                    biggerMap.doClick();
                } else if (toggleSpriteMovement) {
                    biggerSprite.doClick();
                }
            } else if (key == 45 || key == 109) {
                //+ key > key 45 = - _ key close to shift under Enter, key 109 = - numerical number.
                if (toggleMapMovement) {
                    smallerMap.doClick();
                } else if (toggleSpriteMovement) {
                    smallerSprite.doClick();
                }
            } else if (key == KeyEvent.VK_ENTER) {
                fillingBrush = !fillingBrush;
                Graphics2D pointerGraphics = spriteSheet.getPOINTER_LAYER().createGraphics();
                pointerGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                if (fillingBrush) {
                    pointerGraphics.setColor(Color.GREEN);
                } else {
                    pointerGraphics.setColor(Color.RED);
                }
                pointerGraphics.drawRect(x + 1, y + 1, 15, 15);
                pointerGraphics.dispose();
                updateMainCanvas(getMapScale());
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

    private void runInfoWindo(String menuName) {
        String messageS = "";
        int frameHeight = 150;
        int fontSize = 15;
        String frameName = "Alert";
        Font font = new Font("", Font.PLAIN, fontSize);

        switch (menuName) {
            case "invalidFile":
                messageS = "The file loaded is corrupted.";
                break;
            case "invalidLayerHelp":
                messageS = "Invalid layer name.\nThe layer name must be an ASCII string,or a" +
                        "\ncombination of several ASCII strings separated\nby a whitespace character.";
                break;
            case "invalidPath":
                messageS = "The path is invalid.";
                break;
            case "invalidscale":
                messageS = "The scale value is not an integer value.";
                break;
            case "empty":
                messageS = "All fields must be filled to perform the action.";
                break;
            case "spriteSideFail":
                messageS = "The sprite side value is not an integer value.";
                break;
            case "spriteSheetFail":
                messageS = "The sprite sheet side value is not an integer value.";
                break;
            case "sheetAndSprite":
                messageS = "\nThe sprite side and the sprite sheet side values are\ninvalid. Both values must be integer type.";
                break;
            case "help":
                frameHeight = 400;
                frameName = "Info";
                messageS = "Hotkeys:\n\n* Ctrl + directional / awsd keys:\n   Main canvas movement when zoomed." + "\n\n* Shift + directional / awsd keys:\n   Sprite canvas movement when zoomed." + "\n\n* Ctrl + key \"+\" or key \"-\":\n   Main canvas zoom + or zoom -." + "\n\n* Shift + key \"+\" or key \"-\":\n   Srite canvas zoom + or zoom -." + "\n\n* Ctrl + Enter:\n   Toggle continuous painting on / off.";
                break;
        }

        final JDialog infoFrame = new JDialog((Frame) null, frameName, true);
        infoFrame.setLayout(new BoxLayout(infoFrame.getContentPane(), BoxLayout.Y_AXIS));
        infoFrame.setLocationRelativeTo(null);
        infoFrame.setIconImage(null);
        infoFrame.setSize(new Dimension(400, frameHeight));
        infoFrame.setResizable(false);

        Component messageObject;
        Color translucentColor = new Color(255, 0, 0, 0);
        if (menuName.equals("help") || menuName.equals("invalidLayerHelp") || menuName.equals("sheetAndSprite")) {
            JTextArea message = new JTextArea();
            message.setText(messageS);
            message.setEditable(false);
            message.setBorder(null);
            message.setCaretColor(translucentColor);
            messageObject = message;
        } else {
            JTextField message = new JTextField();
            message.setText(messageS);
            message.setEditable(false);
            message.setBorder(null);
            message.setCaretColor(translucentColor);
            message.setHorizontalAlignment(JTextField.CENTER);
            messageObject = message;
        }
        messageObject.setFont(font);
        messageObject.setBackground(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(messageObject);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));

        infoFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        infoFrame.add(panel);
        infoFrame.add(Box.createRigidArea(new Dimension(0, 10)));

        infoFrame.setVisible(true);
        messageObject.setVisible(true);
    }

    private void runSubMenu(String menuName) {
        int fontSize = 15;
        Font font = new Font("", Font.PLAIN, fontSize);
        String frameName = "";
        String firstLabelS = "";
        String firstToolTip = "";
        String secondLabelS = "";
        String secondToolTip = "";
        String thirdLabelS = "";
        String thirdToolTip = "";
        int frameHeight = 200;
        int amountOfLabels = 3;
        boolean multiFieldMenu = true;
        JTextField secondTF = new JTextField();
        JTextField thirdTF = new JTextField();

        switch (menuName) {
            case "export":
                frameName = "Export canvas";
                firstLabelS = "Path:  ";
                firstToolTip = "Paste your path here.";
                secondLabelS = "Scale:";
                secondToolTip = "Eenter a valid scale factor. Type an integer greater than 0.\n" + "The actual canvas size will be multiplied by the scale.\nActual canvas size: " + spriteSheet.getINITIAL_CANVAS_SIZE() + ".";
                frameHeight = 170;
                break;
            case "generateNewCanvas":
                frameName = "Spriter: New canvas";
                firstLabelS = "Path:";
                firstToolTip = "The location path of the sprite sheet file.";
                secondLabelS = "Sprite side:";
                secondToolTip = "The side size of each individual sprite.";
                thirdLabelS = "Sheet size:";
                thirdToolTip = "The side size of the new canvas.";
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
        firstTF.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText("OK");
                int returnVal = fc.showOpenDialog(firstTF);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    firstTF.setText(fc.getSelectedFile().getAbsolutePath());
                    System.out.println("Path located: " + fc.getSelectedFile().getAbsolutePath());
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
            String thirdTFS = thirdTF.getText();
            if (menuName.equals("export")) {
                if (!firstTFS.isEmpty() && !secondTFS.isEmpty()) {
                    boolean secondNumber = secondTFS.matches("\\d+");
                    if (secondNumber) {
                        try {
                            ImageIO.write(spriteSheet.getFramelessScaledCanvas(Integer.parseInt(secondTFS)), "png", new File(firstTFS + ".png"));
                            subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                        } catch (IOException ex) {
                            runInfoWindo("invalidPath");
                        }
                    } else {
                        runInfoWindo("invalidscale");
                    }
                } else {
                    runInfoWindo("empty");
                }
            } else if (menuName.equals("generateNewCanvas")) {
                boolean notEmpty = (!firstTFS.isEmpty()) || (!secondTFS.isEmpty()) || (!thirdTFS.isEmpty());
                boolean condition = true;
                int side = 0;
                int newCanvasSize = 0;

                if (notEmpty) {
                    boolean secondNumber = secondTFS.matches("\\d+");
                    boolean thirdNumber = thirdTFS.matches("\\d+");
                    if (secondNumber && thirdNumber) {
                        side = Integer.parseInt(secondTFS);
                        newCanvasSize = Integer.parseInt(thirdTFS);
                    } else {
                        condition = false;
                        if (!secondNumber && !thirdNumber) {
                            runInfoWindo("sheetAndSprite");
                        } else if (!secondNumber) {
                            runInfoWindo("spriteSideFail");
                        } else {
                            runInfoWindo("spriteSheetFail");
                        }
                    }
                }
                if (condition) {
                    try {
                        spriteSheet = new SpriteSheet(firstTFS, side, newCanvasSize);
                        side = spriteSheet.getSPRITE_SIDE();
                        movementIncrement = side;
                        previousSprite = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
                        subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
                    } catch (RuntimeException ex) {
                        //Comprueba si puedes especificar más la excepción en el catch y que sea IOException
                        runInfoWindo("invalidPath");
                    }
                }
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.setMaximumSize(new Dimension(buttonWidth, buttonPHeight));
        cancel.setMinimumSize(cancel.getMaximumSize());
        cancel.addActionListener(e -> {
            subFrame.dispatchEvent(new WindowEvent(subFrame, WindowEvent.WINDOW_CLOSING));
        });


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


        firstTF.setText("G:\\Mi unidad\\Z_Variado\\Java\\SpriteSheeter\\SpriteSheeter\\Resources\\tiles.png");
        secondTF.setText("16");
        thirdTF.setText("80");
        //Export menu
        if (multiFieldMenu) {
            //Second panels
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

            if (menuName.equals("generateNewCanvas")) {
                //Third panels
                JPanel thirdP = new JPanel();
                thirdP.setLayout(new BoxLayout(thirdP, BoxLayout.X_AXIS));
                thirdP.setMaximumSize(new Dimension(labelPWidth, itemHeight));
                thirdP.setMinimumSize(thirdP.getMaximumSize());
                JLabel thirdLabel = new JLabel(thirdLabelS);
                thirdLabel.setFont(font);

                thirdP.add(thirdLabel);
                thirdP.add(Box.createHorizontalGlue());

                thirdTF.setToolTipText(thirdToolTip);
                thirdTF.setMaximumSize(new Dimension(textPWidth, itemHeight));
                thirdTF.setMinimumSize(thirdTF.getMaximumSize());

                labelsP.add(thirdP);
                labelsP.add(Box.createRigidArea(new Dimension(0, 5)));

                textP.add(thirdTF);
                textP.add(Box.createRigidArea(new Dimension(0, 5)));

                thirdP.setVisible(true);
                thirdLabel.setVisible(true);
                thirdTF.setVisible(true);
            }
        }
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
}