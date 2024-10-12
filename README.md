# Sprite sheet tool, by jfrNebus
 
> [!IMPORTANT]
> El proyecto no está terminado. Aun queda mucho código que limpiar y corregir.
> 
> The proyect is still under developement. There's still a lot of code to clean and fix.
>
> For english speakers [click here](#intro) 🇬🇧

<br>

<hr> 

### 🇪🇸

### Introducción

SpriteSheeter es una herramienta pasada para poder gestionar de una forma más sencilla, un aspecto de uno de los 
proyectos en los que estoy trabajando.

Empecé un juego 2D de gráficos de tipo pixel art, lo estaba desarrollando para Android con una interfaz de usuario
básica a modo de prueba. Aunque el proyecto estaba en una etapa muy inicial, desarrollé el sistema para convertir 
en objetos individuales cada uno de los sprites de un sprite sheet dado. Adicionalmente, este sistema me permite 
gestionar un canvas de X medidas, adecuando la resolución del mismo a la pantalla del dispositivo, teniendo en 
cuenta el valor DPI de la misma.

Hace poco recuperé este proyecto después de dejarlo abandonado por un tiempo largo. Al intentar acordarme de cómo
funcionaba todo, lo primero en lo que pensé fue que la forma de crear el mapa era muy tediosa, necesitaba una 
herramienta para gestionarlo.


<br>

### El problema :finnadie:

Nunca he mirado de qué forma se crea un juego 2D de gráficos tipo pixel art. Lo única información al respecto que he 
recibido de internet, es que la idea principal es trocear la imagen del sprite sheet en imagenes más pequeñas para 
usarlas como objetos dentro del código. Es decir, solo existe un único elemento multimedia. Desde este punto en adelante, 
simplemente me puse a pensar en lo que necesitaba para obtener el objetivo.

Entendemos como sprite sheet aquella imagen que está compuesta por imágenes más pequeñas, que representan un elemento de
cada tipo posible dentro del juego. En un sprite sheet también podemos encontrar el conjunto de frames que forman una
animación. De esta forma, en un juego como Mario Bros, el suelo está formado por la repetición de un único cuadrado de
piedra, las largas plataformas flotantes de ladrillo están formadas por el mismo bloque de ladrillo repetido un número
determinado de veces, etc.

Al final terminé con un sistema que dividía el sprite sheet en imágenes más pequeñas que podía usar como sprites. El 
sistema los creaba de forma anónima, la identidad la recibían por parte de una colección de tipo Map, que asignaría a
cada sprite un elemento _key_ cuyo valor sería un número. De esta forma, el primer sprite de la esquina superior izquierda
sería el sprite 0, el siguiente sprite de esa primera fila sería el sprite 1, y así sucesivamente, en función de las filas
y columnas del sprite sheet. Entendiendo esto, se puede construir una imagen más grande, a partir de imagenes pequeñas, 
mediante el ID de cada sprite.

<br>
<br>

<p align="center">
  <img src="docs/sprites_example.png" />
</p>

Imaginemos que lo que se ve en la imagen anterior es, un arbol... 😵. El arbol de la izquierda es el sprite sheet, tiene
un modelo de cada sprite que forma el arbol. A base de repetir algunos de esos sprites se ha creado un arbol mayor en la
imagen de la derecha. Si construimos el sistema adecuado, uno que permita, a partir de un listado de ids, crear una 
imagen con los sprites correspondientes a cada id, podremos crear mapas a partir de arrays bidimensionales, teniendo un 
único elemento multimedia, el sprite sheet original.

```
int[][] newMap = {
{0, 1, 2},
{3, 4, 5},
{3, 4, 5},
{6, 7, 8},
{9, 10, 11},
{9, 10, 11},
{12, 13, 14},
};
```

El sistema iteraría sobre el array newMap, siendo que cada array dentro newMap representaría una fila del canvas, y
los elementos de cada fila serían los sprites de la misma. De esta forma, todos los elementos número 0 de cada array
forman la columna 0, los elementos número 1 forman la columna 1, y los elementos número 2 forman la columna 2. Este 
ejemplo es fácil de seguir, es una imagen pequeña, crear arrays bidimensionales se complica cuando el mapa a crear es
más grande y/o complejo.

<br>

<p align="center", background="black">
 <b>Test canvas</b>
 <br>
 <br>
 <img src="docs/default_layer.png" width="160" height="160"/> <img src="docs/new_layer.png" width="160" height="160"/>
 <img src="docs/new_layer_2.png" width="160" height="160"/> <img src="docs/new_layer_3.png" width="160" height="160"/> 
 <img src="docs/full_canvas.png" width="160" height="160"/>
</p>

<br>

La última imagen de la derecha, arriba mostrada, se forma a partir de la superposición de las cuatro capas previas. Cada
capa consta de una serie de sprites.

<br>

```
int[][] default_layer = {
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169}
};

int[][] new_layer = {
{0,0,0,0,0},
{104,104,104,108,56},
{209,209,209,159,56},
{311,311,311,312,0},
{412,412,412,413,0}
};

int[][] new_layer_2 = {
{216,216,216,0,0},
{216,377,378,0,0},
{216,428,429,474,475},
{216,216,216,525,526},
{0,0,0,0,0}
};

int[][] new_layer_3 = {
{71,72,216,216,66},
{122,123,124,0,0},
{173,174,0,0,0},
{224,227,0,0,0},
{0,0,0,0,0}
};

```

<br>

Como se puede apreciar, intentar crear los arrays bidimensionales de una imagen tan simeple como esa, un canvas de
80x80 pixels con 5 sprites por fila, cada sprite de 16 pixels de lado, puede llegar a ser bastante complicado. 
Intentar lograr dicha hazaña para mapas grandes, de varios cientos de sprites por lado e incluso más, queda
descartado.

<br>

### La herramienta 🧰
 
<br>
  
 <p align="center", background="black">
  <img src="docs/Main_window.png"/>
 </p>

A la vista queda que no soy ningún profesional a la hora de crear interfaces de usuario 😆. Pese a ello, cumple
con lo que buscaba.

Lo primero que se nos muestra al ejecutar la aplicación, es una ventana pequeña donde se podrá seleccionar, mediante
un cuadro de busqueda al hacer clic sobre el campo de texto del apartado _path_, la imagen que deseamos usar como 
sprite sheet. Adicionalmente, se tendrá que establecer el lado en pixels de cada spritre, y el lado en pixels del 
canvas con el que vamos a trabajar. 

Una vez se han rellenado los campos solicitados y se accede a la pantalla principal, se observan una serie de partes 
bien definidas. A la izquierda se encuentra un apartado en el que se muestran un conjunto de botones con la imagen 
de cada sprite, del sprite sheet facilitado. En este caso se está usando un [sprite sheet](SpriteSheeter/Resources/tiles.png)
del artista ![Buch](docs/CreditsToBuch.txt). Haciendo clic en cada uno de ellos, se podrá imprimir dicho sprite en el
cuadrado rojo dentro del canvas que aparece en la parte derecha de programa. En el medio, se encuentra una lista con los 
botones de acción de cada capa. Cada check box radial ocultará la capa a la que acompañe, y cada botón de capa 
establecerá la capa actual sobre la que se imprimirá cada sprite.

Se podrá mover el cursor, el cuadrado rojo, sobre el mapa, mediante las teclas de dirección o mediante las teclas _a w
s d_. Adicionalmente, manteniendo la tecla Shift y presionando alguna de las teclas dereccionales mencionadas 
anteriormente, se podrá mover el sprite sheet dentro del apartado _Sprites list_. Presionando Shift más la tecla +,
se aumentará el tamaño de visualización de los sprite dentro de la lista de sprites; ocurrirá lo contrario si la 
combinación de teclas es Shift y la tecla -. Se podrá modificar el tamaño del canvas si se presiona la tecla Ctrl y
las teclas + y -. La tecla Enter modificará el estado del cursor, pasando de color rojo a color verde. Esto indica,
que se imprimirá, automáticamente, el último sprite que se haya seleccionado con el ratón en la lista de sprites, en 
cualquier ubicación a la que se mueva el cursor, hasta que el cursor vuelva a ser de color rojo. 

En la parte superior izquierda se encuentra un menú desplegable que permitirá realizar diversas acciones. Se podrán
gestionar las capas, pudiendo borrar o eliminar la capa actual, o todas las capas a la vez; importar o exportar un
archivo de texto _.txt_ para poder guardar el trabajo realizado; exportar en formato _png_ el canvas actual, siendo
que las capas ocultas no se imprimirán; o leer una pequeña leyenda donde se informa de los atajos de teclado.

Por último, en la parte inferior se encuentran una serie de botones para controlar el nivel de zoom aplicado sobre la 
lista de sprites y sobre el mapa; un bloque de texto donde podremos escribir el nombre de las capas que deseemos 
agregar, o donde se mostrará el texto generado cuando se seleccione la opción de exportar código; y el botón de nueva 
capa, que creará una nueva capa tomando como nombre el texto que hayamos introducido, en formato ASCII, en el campo
de texto.

<br>

### El fruto 🍓🍌🥝
 
<br>

El objetivo final de la aplicación no es conseguir la imagen final, la imagen final que se muestra en el canvas es solo
la guía para que aquel que está creando el mapa pueda visualizar lo que está creando. El objetivo final es obtener el bloque
de codigo generado en el archivo _.txt_ al seleccionar la opción _Export code_. 

```
//Sprites in side = 5

##PATH##

-
//Layer: default_layer
int[][] default_layer = {
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169},
{169,169,169,169,169}
};
//default_layer:169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 169 
-
-
//Layer: new_layer
int[][] new_layer = {
{0,0,0,0,0},
{104,104,104,108,56},
{209,209,209,159,56},
{311,311,311,312,0},
{412,412,412,413,0}
};
//new_layer:0 0 0 0 0 104 104 104 108 56 209 209 209 159 56 311 311 311 312 0 412 412 412 413 0 
-
-
//Layer: new_layer_2
int[][] new_layer_2 = {
{216,216,216,0,0},
{216,377,378,0,0},
{216,428,429,474,475},
{216,216,216,525,526},
{0,0,0,0,0}
};
//new_layer_2:216 216 216 0 0 216 377 378 0 0 216 428 429 474 475 216 216 216 525 526 0 0 0 0 0 
-
-
//Layer: new_layer_3
int[][] new_layer_3 = {
{71,72,216,216,66},
{122,123,124,0,0},
{173,174,0,0,0},
{224,227,0,0,0},
{0,0,0,0,0}
};
//new_layer_3:71 72 216 216 66 122 123 124 0 0 173 174 0 0 0 224 227 0 0 0 0 0 0 0 0 
-
-
```

Este es el contenido del archivo _.txt_ para la imagen _**Test Canvas**_ arriba mostrada. El campo ``"##Path##"`` mostrará
la ruta hacia el directorio donde tengamos almacenado el sprite sheet en nuestro equipo. La línea "//Sprites in side = 5",
y las líneas "//_nombre de capa_=_sucesión de numeros_", son las que se usan para reconstruir el trabajo, al importar el archivo. 
Y finalmente, la declaración del array, así como el comentario con el nombre de capa, es lo que se copiará y pegará en la declaración
de mapas en el proyecto del juego mencionado en la introducción.

<br>

### Conclusión 🙌

<br>

No es un proyecto brillante, aun hay muchas cosas que pulir, estructuras que mejor, funcionalidad que se le podría añadir o retirar, 
fallos que corregir, y un largo etc de consideraciones que cualquier experto podría agregar. No obstante, aun cuando ya existen
herramientas como esta, mejor desarrolladas, este proyecto me ha servido para seguir mejorando, para seguir practicando la gestión de 
un layout, la exploración y manejo de archivos, continuar mejorando mis buenas prácticas, etc. Además, si acaso en algún momento continuo
desarrollando dicho juego, cosa que no es mi prioridad, me será de gran ayuda 😃


<br>
<br>
<br>
<br>
<br>
<br>

<hr>

<br>

### 🇬🇧

### Intro

SpriteSheeter is a tool developed to make my life easier while working in my main project. I started working in a 2D 
graphics pixel art game which it's developed for Android. I built a basic user interface. It has a main picture object
which covers the full screen, and that works as the game map; an smaller picture over the main map that serves as a
mini map; a picture in the middle of the screen placing the character; four directional buttons to move the main map
under the character; and two extra buttons to control the zoom made over the map.

Although the project was in a very early stage, I developed a system to get individual objects, sprites, out of the
provided sprite sheet. In addition, this system allow me to set the size of a new canvas, fitting the its resolution
to the screen's resolution, keeping in mind the DPI value of the screen.

The proyect was left unfinished, and it was only few weeks ago when I got it back. The first thing i thought about,
when i was trying to remember how the code works, was that the way to make the map was really annoying and that i
needed a tool to deal with it. Here's when I make a break on the game proyect, which was already a break from a third 
proyect, to develope this tool.




