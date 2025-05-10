# Introduction

<br>

> [!IMPORTANT]
> English is not my mother language and I try to not use translators, bear with me 😅. Be ready to wild mistakes haha

<br>

SpriteSheeter is a tool meant to make me be able to deal, in an easier way, with an issue related with a project I am occasionally working on.
I started working in a 2D graphics pixel art game, which it's developed for Android, with a test mode basic user interface. Although the project was in a very early stage, I developed a system to get individual objects, sprites, out of the provided spritesheet. In addition, this system allows me to set the size of a new canvas, fitting the itsresolution to the screen's resolution, keeping in mind the DPI value of the screen.

The project was left unfinished, and it was only a few weeks ago when I got it back. The first thing i thought about, when i was trying to remember how the code works, was that the way to make the map was really annoying and that I needed a tool to deal with it.

<br>

### The problem :finnadie:

<br>

I've never searched on the internet how to make a 2D graphics pixel art game. I just got the idea of splitting into sameller images the spritesheet, to be able able to use them inside the code. This means that there's just one image in the resources folder in the proyect.

A spritesheet is an image that's made up of smaller images that represent every possible element in the game. In a spritesheet, we can also find all the frames needed to make an animation. This way, in a game like Mario Bros, the ground is created by repeating a single stone sprite, and the floating brick platforms are created from a single brick sprite.

I finally got a system which splits the spritesheet into smaller images that can be used as sprites. The system creates anonymous sprites, they get their id from the map where they are stored, which assigns a number _key_ to them. This way, the top left corner sprite is sprite number 0, the next one in that row is sprite number 1, and so on, considering all the rows and columns in the spritesheet. Understanding this idea, a bigger picture can be created from smaller pics, knowing their ID.

<br>

<p align="center">
  <img src="docs/sprites_example.png" />
</p>

<br>

Let's consider the previous drawing, a tree... 😵. The left tree is the spritesheet, it holds all the sprites needed to draw it. By repeating some of those sprites we made a bigger tree in the right part of the picture. If we make a system which is able to make a picture from an ID list, where each of those IDs in the list represents a specific sprite, we will be able to make maps out of two-dimensional arrays, with just one media image, the original spritesheet.

<br>

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

<br>

The system iterates over the array newMap, each array inside newMap represents a row in the canvas, and its elements represent each sprite in that row. All the elements in the position 0 at each row array, are the sprites in the column number 0; the sprites in the position number 1 at each array are the elements in the column number 1, and all the sprites in the position 2 at each array are the elements in the column number 2. This example is easy to follow, it is a small image, creating two-dimensional arrays get complicated when the map to be created is either big or complex. 

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

The last picture on the right shown above, is created by mixing all the previous layers shown on its left. Each layer has its own sprites.

<br>

```
//Layer: water
int[][] water = {
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170}
};

//Layer: ground
int[][] ground = {
{0,0,0,0,0},
{108,108,108,109,0},
{159,159,159,262,0},
{310,310,310,313,0},
{411,412,413,414,0}
};

//Layer: bridge
int[][] bridge = {
{0,0,0,0,0},
{0,378,379,0,0},
{0,429,430,475,476},
{0,0,0,526,527},
{0,0,0,0,0}
};

//Layer: tree
int[][] tree = {
{72,73,0,0,0},
{123,124,125,0,0},
{174,175,0,0,0},
{225,228,0,0,0},
{0,0,0,0,0}
};

```

<br>

It's easy to realize that, trying to make all the two-dimensional arrays of such an easy picture, 80x80 pixels with 5 sprites on each row, each sprite 16 pixels in side size, becomes quite complicated. It's something not to even consider, if the map to be created has to be several hundreds of sprites big.

<br>

### The tool 🧰
 
<br>

<p align="center">
 <img src="docs/Main_window_not_enabled.png"/>
</p>

<br>

Yep, I am absolutely not a professional making user interfaces 😆. Nevertheless, it works!

The first thing shown when the program starts is a window with all its fields and options inactive, except for the following options: _Create a new canvas_, the option _Import_ inside the menu _Import / export code_ and _Help_. This is because the program restricts the user's actions to creating the canvas first, in order to activate all the actions, or to import any previously saved file. Finally, the user can check the help window, where everything mentioned above is explained , as well as the list of hotkeys.

When a file is loaded, the program takes from the file all the necessary data in order to configure the canvas, the spritesheet, and the layers. When a new canvas is created, a new window pops up requesting the sprite side size and the canvas side size. After entering valid positive values, the canvas is created. The next step is to add a valid spritesheet, an image file. Once the spritesheet is loaded, on the left side, there's an area where all the buttons, with each sprite of the spritesheet as an icon, are located. On the top of the list there's a bigger sprite button with the name _Empty sprite_, which will be used whenever the user needs to print an "empty field" on the canvas. For this example it was used the [spritesheet](SpriteSheeter/Resources/tiles.png) whose author is ![Buch](docs/CreditsToBuch.txt). By clicking on a sprite, the sprite will be displayed in the red square inside the canvas on the right part of the program, outlined in black. In between the sprite list and the canvas, there's a list with each layer's buttons. Each radial checkbox will hide the layer it is associated with, and each layer's button will set the canvas where the sprites will be drawn.

The cursor will be moved by using the directional keys, or by using the keys _a w s d_. Using the key Shift together with the keys + or - will zoom the spritesheet in and out inside the _Sprites list_ area. The same will happen to the canvas by using the key Ctrl together with the keys + and -. The Intro key will change the cursor's state, toggling the green behaviour, which will print in the canvas, the last mouse clicked sprite in the _Sprites List, each time the cursor is moved to a new location. This behaviour will remain till the intro key is pressed again, setting the cursor state back to red mode.

In the top left corner of the window there's a dropdown menu. This menu offers the option to make a new canvas; to load a new spritesheet; to clear or delete the current layer, or all the layers at onces; the option to import or export a _.txt_ file in order to save or load the work done; it allows to export the current canvas as a _.png_ file, keeping in mind that any hidden layer won't be printed; or the option to read a brief list of keyboard shortcuts.

Finally, in the bottom left part of the window there are buttons to control the zoom level applied to the spritesheet and to the canvas; a text field where the name of new layers will be written, or where the text generated will be written, when the code is exported; and the new layer button, which will generate the new layer, setting as its name the text written in the text block.

<br>

<p align="center">
 <img src="docs/Main_window.png"/>
</p>

<br>

### The fruit 🍓🍌🥝
 
<br>

The goal of this program is not to get the final image, the canvas is there just to guide the one who is creating the picture. The final goal is to get the code bloc written in the _.txt_ file, once the _Export code_ option is selected.

<br>

```
//Sprites in side = 5

//Sprite side = 16

##PATH##

-
//Layer: water
int[][] water = {
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170},
{170,170,170,170,170}
};
//water:170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 170 
-
-
//Layer: ground
int[][] ground = {
{0,0,0,0,0},
{108,108,108,109,0},
{159,159,159,262,0},
{310,310,310,313,0},
{411,412,413,414,0}
};
//ground:0 0 0 0 0 108 108 108 109 0 159 159 159 262 0 310 310 310 313 0 411 412 413 414 0 
-
-
//Layer: bridge
int[][] bridge = {
{0,0,0,0,0},
{0,378,379,0,0},
{0,429,430,475,476},
{0,0,0,526,527},
{0,0,0,0,0}
};
//bridge:0 0 0 0 0 0 378 379 0 0 0 429 430 475 476 0 0 0 526 527 0 0 0 0 0 
-
-
//Layer: tree
int[][] tree = {
{72,73,0,0,0},
{123,124,125,0,0},
{174,175,0,0,0},
{225,228,0,0,0},
{0,0,0,0,0}
};
//tree:72 73 0 0 0 123 124 125 0 0 174 175 0 0 0 225 228 0 0 0 0 0 0 0 0 
-
-
```

<br>

The previous code was written in the _.txt_ file generated for the _Test canvas_ picture shown above. The first field indicates the number of pixels on each side of the canvas. The second field shows the number of pixels on each side of each sprite. The field ##Path## will show the path to the directory where the spritesheet is located. The first three lines, and the lines "//name_of_layer:array_of_numbers", are the lines that will be used to rebuild the workflow in the program. Finally, all the arrays and the commented layer names, are the elements that will be copied and pasted in the game project.

<br>

### Conclusion 🙌

<br>

This project is not a masterpiece; each time I check the code I find lots of points to improve and mistakes to fix. There are features to be added or removed, and a long etc of considerations that any professional could add. Honestly, I feel it is incomplete. I usually finish the project once I've done an in-depth documentation, explaining line by line, and this project, as well as others in my GitHub, won't have this kind of documentation, mainly because I have no time to do it. . 
Nevertheless, even when there are already professional tools to achieve the goal of this program, this project allowed me to keep improving my coding skills and my poor layout programming. It allowed me to learn a bit more about how to browse and deal with files, and it allowed me to keep improving my _best practices_. And, in case I ever feel like continuing to develop this game, which is not my priority, this tool will be a great help 😃.

<br>
