package com.SpriteSheeter;

import com.SpriteSheeter.Enums.SubWindowOptionsEnum;

import javax.swing.*;
import java.awt.*;


public class SubWindow {
    public static void runInfoWindow(SubWindowOptionsEnum value) {
        boolean textArea = false;
        String messageS = "";
        int frameHeight = 150;
        int fontSize = 15;
        String frameName = Strings.FRAME_NAME_SW;
        Font font = new Font("", Font.PLAIN, fontSize);

        //todo REPASA EL INGLÉS

        switch (value) {
            case CORRUPTED_FILE:
                messageS = Strings.CORRUPED_FILE;
                break;
            case HELP:
                frameHeight = 400;
                frameName = "Info";
                messageS = Strings.HELP;
                textArea = true;
                break;
            case INVALID_IMAGE:
                messageS = Strings.INVALID_IMAGE;
                break;
            case INVALID_IMAGE_PATH:
                messageS = Strings.INVALID_IMAGE_PATH;
                break;
            case INVALID_LAYER_HELP:
                messageS = Strings.INVALID_LAYER_NAME;
                break;
            case INVALID_PATH:
                messageS = Strings.INVALID_PATH;
                break;
            case INVALID_SCALE:
                messageS = Strings.INVALID_SCALE;
                break;
            case INVALID_TEXT:
                messageS = Strings.INVALID_TEXT;
                break;
            case SHEET_AND_SPRITE:
                messageS = Strings.SHEET_AND_SPRITE;
                textArea = true;
                break;
            case SPRITESHEET_FAIL:
                messageS = Strings.SPRITE_SHEET_FAIL;
                break;
            case SPRITE_SIDE_FAIL:
                messageS = Strings.SPRITE_SIDE_FAIL;
                break;
            case SQUARE_ERROR:
                messageS = Strings.SQUARE_ERROR;
                textArea = true;
                break;
            case UNSUPPORTED_IMAGE:
                messageS = Strings.UNSUPPORTED_IMAGE;
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
        if (textArea) {
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
