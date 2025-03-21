package com.SpriteSheeter;

import javax.swing.*;
import java.awt.*;


public class SubWindow {
    public void runInfoWindo(String menuName) {
        String messageS = "";
        int frameHeight = 150;
        int fontSize = 15;
        String frameName = Strings.FRAME_NAME_SW;
        Font font = new Font("", Font.PLAIN, fontSize);

        //REPASA EL INGLÉS

        switch (menuName) {
            case "corruptedFile":
                messageS = Strings.CORRUPED_FILE;
                break;
            case "help":
                frameHeight = 400;
                frameName = "Info";
                messageS = Strings.HELP;
                break;
            case "invalidImage":
                messageS = Strings.INVALID_IMAGE;
                break;
            case "invalidImagePath":
                messageS = Strings.INVALID_IMAGE_PATH;
                break;
            case "invalidLayerHelp":
                messageS = Strings.INVALID_LAYER_HELP;
                break;
            case "invalidPath":
                messageS = Strings.INVALID_PATH;
                break;
            case "invalidscale":
                messageS = Strings.INVALID_SCALE;
                break;
            case "invalidText":
                messageS = Strings.INVALID_TEXT;
                break;
            case "sheetAndSprite":
                messageS = Strings.SHEET_AND_SPRITE;
                break;
            case "spriteSheetFail":
                messageS = Strings.SPRITE_SHEET_FAIL;
                break;
            case "spriteSideFail":
                messageS = Strings.SPRITE_SIDE_FAIL;
                break;
            case "unsupportedImage":
                messageS = Strings.UNSOPORTED_IMAGE;
                break;
            default:
                System.out.println("Default point reached for Subwindow.");
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

        messageObject.setVisible(true);
        infoFrame.setVisible(true);
    }
}
