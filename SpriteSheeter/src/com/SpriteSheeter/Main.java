package com.SpriteSheeter;//https://opengameart.org/content/the-field-of-the-floating-islands
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

//Changegit

 */


public class Main {
    public static void main(String[] args) {
//      C:\Users\codin\Desktop
//      G:\Mi unidad\Z Variado\Java\SpriteSheeter\SpriteSheeter\Resources\sewer_1.png
//      H:\Mi unidad\Z Variado\Java\SpriteSheeter\SpriteSheeter\Resources\sewer_1.png
        UserInterface userInterface = new UserInterface();
        userInterface.getStartValues();
        userInterface.setUpEverything();
    }
}
