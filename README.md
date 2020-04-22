# MathDoku
JavaFX MathDoku game application

Starting a Game:
To start a game one can click on the “Generate random game” button or load one from file/text (see “Loading files”)

Cell CompletionL
By Keyboard: 
Active cell (the one highlighted in green, at the beginning of the game it is the one in the top right corner of the grid) accepts numerical input from keyboard in the rage <1, size>, the active cell can be changed using ‘A’ (left), ‘W’ (up), ‘S’ (down), ‘D’ (right) keys. Clearing an active cell is handled by the “backspace” key or a “blank” button. All the buttons around the grid can also be used by clicking “Enter” when a button is highlighted in blue, button navigation can be done by using the arrows.

By Mouse:
Click on the cell to activate it and click on the button with a right number to enter/clear it.

Mistace detection:
If you want the application to detect your mistakes, click the “Show mistakes” button, it will change label to “Don’t show mistakes” so the next click will disable the mistake detection

Win detection:
When the game is won, all cells will change to some shade of pink and start fading in different speed.
NOTE: The “Solve” button won’t automatically activate the animation as it would make it impossible to see the solution, the easiest way to see the animation is to click “Solve” → “Show Mistakes”

Clearing:
Click the “clear” button and when a confirmation window pops up, click “ok”, note that clearing the board can be undone with the “undo” button

Undo/Redo action:
Click the “undo”/”redo” button 

Loading game:
-From file:
Write path to the .txt file in the text area of the game menu and click “Load game from file” button.

-From text:
Put the puzzle description in the text area and click “Load game from text” button 

-Incorrect input detection:
If invalid path/puzzle is detected, a red message will appear below the text area

Changing the font size:
Click one of the 3 buttons “Small”, “Medium” or “Big”, the default size is medium. The font will change relatively to the grid so the size of the values in cells will also change during resizing.

Displaying the solution:
Solve puzzle:
Click “Solve” button
Clicking Solve doesn’t automatically lead to win animation and this action can be undone.

Get hint:
Click “Hint” button

Files / lines for solver:
Solver.java file

Random Game Generator:
Write a size (between 2 and 8) in the menu text area and click “Generate random game”. If no value is provided, application will generate a grid of size 6.

Files / lines for generator:
RandomBoardBuilder.java
