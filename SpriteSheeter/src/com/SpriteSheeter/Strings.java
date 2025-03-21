package com.SpriteSheeter;

public class Strings {

    //UserInterface
    public static final String NO_LAYER = "noLayer";
    public static final String FRAME_NAME_UI = "Spriter";
    public static final String SPRITE_LABEL_NAME = "Sprites list:";
    public static final String ACTUAL_LAYER_LABEL = "Actual layer:";
    public static final String NEW_LAYER_BUTTON = "New layer";
    public static final String BIGGER_SPRITE_BUTTON = "Sprite zoom +";
    public static final String SMALLER_SPRITE_BUTTON = "Sprite zoom -";
    public static final String BIGGER_MAP_BUTTON = " Map zoom +  ";
    public static final String SMALLER_MAP_BUTTTON = " Map zoom -  ";
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
    public static final String EXPORT_SAVED_MESSAGE = "Successfully saved to the file.\n";
    public static final String EXPORT_CANVAS_ITEM = "Export canvas           ";
    public static final String HELP_ITEM = "Help           ";
    public static final String IMPORTED_FIRST_REGEX = "(?<=//Sprites\\sin\\sside\\s=\\s)\\d+(?=\\n)";
    public static final String IMPORTED_SECOND_REGEX = "(?<=//Sprite\\sside\\s=\\s)\\d+(?=\\n)";
    public static final String IMPORTED_THIRD_REGEX = "(?<=//Canvas\\sside\\ssize\\s=\\s)\\d+(?=\\n)";
    public static final String IMPORTED_FOURTH_REGEX = "(?<=//Layer:\\s)(\\w+(_+\\w+)*)(?=\\nint\\[\\]\\[\\])";
    public static final String IMPORTED_FIFTH_REGEX_1 = "(?<=};\\n//";
    public static final String IMPORTED_FIFTH_REGEX_2 = ":)((\\d)+\\s)+(?=\\n)";
    public static final String LOADED_REGEX = "(?<=\\n##)(\\w:(.*))(?=##)";
    public static final String NEW_LAYER_REQUIRED = "You have to create a new canvas. Create a new" +
            " canvas throught the \"Options\" menu.";

    //runSubMenu

    public static final String SUBMENU_EXPORTCANVAS_TOOLTIP = "Eenter a valid scale factor. Type an integer greater than 0.\nThe actual canvas size will be multiplied by the scale.\nActual canvas size: ";


    //SubWindow

    public static final String FRAME_NAME_SW = "Alert";
    public static final String CORRUPED_FILE = "The file loaded is corrupted.";
    public static final String HELP = "Hotkeys:\n\n* Ctrl + directional / awsd keys:\n   Main canvas movement when zoomed." + "\n\n* Shift + directional / awsd keys:\n   Sprite canvas movement when zoomed." + "\n\n* Ctrl + key \"+\" or key \"-\":\n   Main canvas zoom + or zoom -." + "\n\n* Shift + key \"+\" or key \"-\":\n   Srite canvas zoom + or zoom -." + "\n\n* Ctrl + Enter:\n   Toggle continuous painting on / off.";
    public static final String INVALID_IMAGE = "The file must be an image file.";
    public static final String INVALID_IMAGE_PATH = "The image path in the loaded text file is invalid.";
    public static final String INVALID_LAYER_HELP = "The layer name must be an ASCII string, or a" +
            " combination of several ASCII strings separated by a whitespace character. " +
            "Layer names cannot be duplicated.";
    public static final String INVALID_PATH = "The path to the file is invalid.";
    public static final String INVALID_SCALE = "The scale value is not an integer value.";
    public static final String INVALID_TEXT = "The uploaded file must be a .txt file.";
    public static final String SHEET_AND_SPRITE = "\nThe sprite side and the spritesheet side values are invalid. Both values must be an integer type.";
    public static final String SPRITE_SHEET_FAIL = "The spritesheet side value must ba an integer value.";
    public static final String SPRITE_SIDE_FAIL = "The sprite side value must be an integer value.";
    public static final String UNSOPORTED_IMAGE = "Unsupported image format.";
}
