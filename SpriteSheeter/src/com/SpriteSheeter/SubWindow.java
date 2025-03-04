package com.SpriteSheeter;

import javax.swing.*;
import java.awt.*;


public class SubWindow {
    public void runInfoWindo(String menuName) {
        String messageS = "";
        int frameHeight = 150;
        int fontSize = 15;
        String frameName = "Alert";
        Font font = new Font("", Font.PLAIN, fontSize);

        switch (menuName) {
            case "newCanvasNeeded":
                messageS = "You need to create a new canvas first.";
                break;
            case "invalidFile":
                messageS = "The file loaded is corrupted.";
                break;
            case "invalidLayerHelp":
                messageS = "The layer name must be an ASCII string, or a" +
                        " combination of several ASCII strings separated by a whitespace character. " +
                        "Layer names cannot be duplicated.";
                break;
            case "invalidPath":
                messageS = "The path to the file is invalid.";
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
            message.setLineWrap(true);
            message.setWrapStyleWord(true);
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
}
