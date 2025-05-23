package com.SpriteSheeter;

public class Strings {

    //UserInterface

    public static final String NO_LAYER = "No layer";
    public static final String FRAME_NAME_UI = "Spriter";
    public static final String SPRITE_LABEL_NAME = "Sprites list";
    public static final String ACTUAL_LAYER_LABEL = "Current layer:";
    public static final String NEW_LAYER_BUTTON = "New layer";
    public static final String BIGGER_SPRITE_BUTTON = "Zoom in sprite";
    public static final String SMALLER_SPRITE_BUTTON = "Zoom out sprite";
    public static final String BIGGER_MAP_BUTTON = " Zoom in map  ";
    public static final String SMALLER_MAP_BUTTON = " Zoom out map  ";
    public static final String NEW_CANVAS_MENU = "Create a new canvas           ";
    public static final String LOAD_SPRITESHEET_ITEM = "Load spritesheet";
    public static final String LAYER_MANAGEMENT_MENU = "Layer management           ";
    public static final String CLEAR_LAYER_MENU = "Clear layer           ";
    public static final String CLEAR_LAYER_ITEM = "Clear actual layer           ";
    public static final String CLEAR_ALL_LAYER_ITEM = "Clear all layers           ";
    public static final String DELETE_LAYER_MENU = "Delete layer           ";
    public static final String DELETE_LAYER_ITEM = "Delete actual layer           ";
    public static final String DELETE_ALL_LAYER_ITEM = "Delete all layers           ";
    public static final String IMPORT_EXPORT_MENU = "Import / export code";
    public static final String EXPORT_SAVED_MESSAGE = "Successfully saved the file.\n";
    public static final String EXPORT_CANVAS_ITEM = "Export canvas           ";
    public static final String HELP_ITEM = "Help           ";
    public static final String IMPORTED_FIRST_REGEX = "(?<=//Sprites\\sin\\sside\\s=\\s)\\d+(?=\\n)";
    public static final String IMPORTED_SECOND_REGEX = "(?<=//Sprite\\sside\\s=\\s)\\d+(?=\\n)";
    public static final String IMPORTED_FOURTH_REGEX = "(?<=//Layer:\\s).+(?=\\nint\\[\\]\\[\\])";
    public static final String IMPORTED_FIFTH_REGEX_1 = "(?<=};\\n//";
    public static final String IMPORTED_FIFTH_REGEX_2 = ":)((\\d)+\\s)+(?=\\n)";
    public static final String LOADED_PATH_REGEX = "(?<=\\n##)(\\w:(.*))(?=##)";
    public static final String NEW_LAYER_REQUIRED = "You have to create a new canvas. Create a new" +
            " canvas through the \"Options\" menu.";

    //runSubMenu

    public static final String SUBMENU_EXPORTCANVAS_TOOLTIP = "Enter a valid scale factor. Type an integer greater" +
            " than 0.\nThe actual canvas size will be multiplied by the scale.\nActual canvas size: ";


    //SubWindow

    public static final String FRAME_NAME_SW = "Alert";
    public static final String CORRUPTED_FILE = "The file loaded is corrupted.";
    public static final String HELP = "You will first need to create a new canvas or load a saved file, in order" +
            " to unlock all the options. If you load a saved file, you will be able to continue from where you left " +
            "off last time. If you need to create a new canvas, you will also need to load a new spritesheet.\n\n" +
            "Hotkeys:\n\n* Ctrl + arrow / a-w-s-d keys:\n   Move the main canvas when zoomed.\n\n* Shift + arrow / " +
            "a-w-s-d keys:\n   Move the sprites list when zoomed.\n\n* Ctrl + key \"+\" or key \"-\":\n   Zoom in or" +
            " out the main canvas.\n\n* Shift + key \"+\" or key \"-\":\n   Zoom in or out the sprites list\n\n* Ctrl" +
            " + Enter:\n   Toggle continuous painting on / off.";
    public static final String INVALID_IMAGE = "The file must be an image file.";
    public static final String INVALID_IMAGE_PATH = "The image path in the loaded text file is invalid.";
    public static final String INVALID_LAYER_NAME = "Layer names cannot be duplicated or empty.";
    public static final String INVALID_PATH = "The path to the file is invalid.";
    public static final String INVALID_SCALE = "The scale value must be an integer.";
    public static final String INVALID_TEXT = "The uploaded file must be a .txt file.";
    public static final String SHEET_AND_SPRITE = "\nThe sprite side and the spritesheet side values are invalid. " +
            "Both values must be positive integers.";
    public static final String SPRITE_SHEET_FAIL = "\nThe spritesheet side value must be a positive integer value.";
    public static final String SPRITE_SIDE_FAIL = "\nThe sprite side value must be a positive integer value.";
    public static final String SQUARE_ERROR = "\nThe number of IDs per layer must be equal to \"Sprites in side\" squared.";
    public static final String UNSUPPORTED_IMAGE = "Unsupported image format.";
}