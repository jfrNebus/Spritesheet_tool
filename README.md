# Sprite sheet tool, by jfrNebus
 
> [!IMPORTANT]
> El proyecto no está terminado. Aun queda mucho código que limpiar y que corregir.
> 
> The proyect is still under developement. There's still a lot of code to clean and fix.
>
> For english speakers [click here](#intro)

<br>

### :es:

### Introducción

SpriteSheeter es una herramienta desarrollada para hacerme la vida un poco más facil a la hora de trabajar en otro
proyecto en el que he trabajado durante un tiempo. 

Empecé un juego 2D de gráficos de tipo pixel art, lo estaba desarrollando para Android. Cree una interfaz de usuario
básica. Estaba formada por una imagen que constituía el mapa, y que cubría toda la pantalla; una imagen más 
pequeña ubicada encima del mapa, que hacía las veces de mini mapa; una imagen ubicada en el centro de la pantalla 
mostrando una versión primitiva de un personaje; cuatro botones de movimiento, los encargados de mover el 
mapa por debajo del personaje; y dos botones adicionales encargados de probar si el sistema de zoom del mapa funcionaba.

Aunque el proyecto estaba en una etapa muy inicial, desarrollé el sistema para convertir en objetos individuales cada 
uno de los sprites de un sprite sheet dado. Adicionalmente, este sistema me permite gestionar un canvas de X medidas,
adecuando la resolución del mismo a la pantalla del dispositivo, teniendo en cuenta el valor DPI de la misma.

Ese proyecto se quedó en esa fase de desarrolló, y no fue hasta hace unas semanas cuando lo recuperé. Al intentar 
acordarme de cómo funcionaba todo, lo primero en lo que pensé es que la forma de crear el mapa era muy tediosa y que
necesitaba una herramienta para gestionarlo. Y es aquí, donde surge la idea de hacer un stop en ese proyecto, que a
su vez era un descanso que me estaba tomando de un tercer proyecto inicial, para desarrollar dicha herramienta.


<br>

### La herramienta

Nunca he mirado de qué forma se crea un juego 2D de gráficos tipo pixel art. Lo única información al respecto que he 
recibido de internet, es que la idea principal es trocear la imagen del sprite sheet en imagenes más pequeñas para 
usarlas como objetos dentro del código. Es decir, solo existe un único elemento multimedia. Desde este punto en adelante, 
simplemente me puse a pensar en lo que necesitaba para obtener el objetivo. Seguramente, mirando más información hubise 
ahorrado tiempo, pero así lo hice. 

Al final terminé con un sistema que dividía el sprite sheet en imágenes más pequeñas que podía usar como sprites. El 
sistema los creaba de forma anónima, con lo que la identidad la recibían por parte de una colección de tipo Map, que
asignaría a cada sprite un elemento _key_ cuyo valor sería un número. De esta forma, el primer sprite de la esquina
superior izquierda sería el sprite 0, el siguiente sprite de esa primera fila sería el sprite 1, y así sucesivamente, 
en función de las filas y columnas del sprite sheet.

Entendiendo esto, se puede construir una imagen más grande, a partir de imagenes pequeñas, mediante las ID de cada
sprite.

<br>
<br>
<br>

### :uk:

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




