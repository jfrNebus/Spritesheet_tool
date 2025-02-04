package com.SpriteSheeter;
//https://opengameart.org/content/the-field-of-the-floating-islands
//https://kenney.nl/assets/cursor-pixel-pack
/*
DONE cargar mapas ya creados
DONE guardar mapas
DONE ambas ideas se lograrán, seguramente, generando el mapa a partir del codigo

DONE generar sprites en blanco cuando al cargar un mapa a partir del código, algún id no
DONE esté presente en el spritesheet.

DONE generar una hoja en blanco de x medidas al introducir algún comando.

DONE ImageIO.write(subimage, "png", new File("outputPath"));

DONE hacer que la lista de sprites no se mueva cuando se usen las teclas de dirección: Se solventó quitandole el fócus
DONE a los botones de los sprites

DONE Crea capas y un selector de capas.

DONE revisa el tema de los nombres de capa y las restricciones por regex

DONE agrega al archivo guardado la ruta al spritesheet


todo Maneja la excepción cuando el archivo txt no tiene ruta

todo revisa aquellas variables de tipo string que sirvan para indicar el estado, por ejemplo una variable cuyos valores
 sean "empty" y "full" para indicar el estado de algo, y sustituyelos por valores int del tipo i = 1 para full, i = 0
 para empty.

todo revisa el por qué de la línea "File notePad = new File(fc.getSelectedFile() + ".txt");" en el JMenuItem exportCode

//Check commit


 */


public class Main {
    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface();
        userInterface.setUpEverything();
    }
}
