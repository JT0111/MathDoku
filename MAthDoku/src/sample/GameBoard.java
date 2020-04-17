package sample;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Transition;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javafx.scene.text.Text;
import javafx.util.Duration;
public class GameBoard extends Pane {

    //ArrayList keeping information about every sphere as:
    //target, sign, sorted in ascending order list of cells
    //every cell position describes fully its sphere
    private boolean showMistakes = false, correctInput = true;
    Stack<int[]>redoStack = new Stack<int[]>();
    Stack<int[]>undoStack = new Stack<int[]>();
    Color[] colorsList = new Color[10];
    Animation winAnimation;
    private ArrayList<int[]> cagesList = new ArrayList<int[]>(100);
    GridPane buttonsGrid = new GridPane();
    HBox board = new HBox();
    VBox fullBoard = new VBox();
    int[] array0 = new int[1];
    private int[] valuesList = new int[70];
    private int[] lastStateList = new int[70];
    private int[] correctValuesList;
    private boolean[] rowError = new boolean[70];
    private boolean[] sphereError = new boolean[70];
    private boolean[] columnError = new boolean[70];
    private ArrayList<Cell> cellsList = new ArrayList<Cell>(100);

    private int size, sqrtSize; //size of the grid and number of columns/rows
    private int lastClicked = 1; //position of cell that was recently clicked
    GridPane grid = new GridPane();
    GridPane buttonGrid = new GridPane();
    Button clear = new Button("Clear");
    Button undo = new Button("Undo");
    Button redo = new Button("Redo");
    Button solve = new Button("Solve");
    Button hint = new Button("Hint");
    Button mistakesVisibility = new Button("Show mistakes");

    /**
     * Creator of the board
     * sets all the important components
     *
     * @param filename where the board is described
     * @throws IOException
     */
    public GameBoard(String filename) throws IOException{
        setArray(filename);
        System.out.println(size);
        if(correctInput) {
            setGrid();
            setButtonsGrid();
            addHandler();
        }
        if(cellsList.size()<4 || cellsList.size()>66 || !correctInput){
            correctInput = false;
        }
        else{
            board.getChildren().add(grid);
            grid.setAlignment(Pos.TOP_LEFT);
            buttonsGrid.setAlignment(Pos.TOP_RIGHT);
            board.getChildren().add(buttonsGrid);
            fullBoard.getChildren().addAll(board, buttonGrid);
            board.setAlignment(Pos.TOP_CENTER);
            buttonGrid.setAlignment(Pos.BOTTOM_CENTER);

            this.getChildren().add(fullBoard);
            cellsList.get(1).activate();

        }
    }

    /**
     * Gameboard created by a random game builder
     * @param builder
     */
    public GameBoard(RandomBoardBuilder builder){
        size = builder.getSize();
        sqrtSize = builder.getSqrtSize();
        cagesList = builder.getCagesList();
        correctValuesList = builder.getSolutions();

        setGrid();
        setButtonsGrid();
        addHandler();

        board.getChildren().add(grid);
        grid.setAlignment(Pos.TOP_LEFT);
        buttonsGrid.setAlignment(Pos.TOP_RIGHT);
        board.getChildren().add(buttonsGrid);
        board.getChildren().add(buttonGrid);
        fullBoard.getChildren().addAll(board, buttonGrid);
        board.setAlignment(Pos.TOP_CENTER);
        buttonGrid.setAlignment(Pos.BOTTOM_CENTER);

        this.getChildren().add(fullBoard);
        cellsList.get(1).activate();
    }

    /**
     * Gameboard defined by a text
     */
    public GameBoard(){
        array0[0] = 0;
        for (int i = 0; i < 70; i++)
            cagesList.add(array0);
        size = 0; //looks for the biggest position value in a file
    }

    /**
     * Created just for board defined by a file
     * it's technically a creator but not quite
     */
    public void allLinesIn(){
        for (int i = 1; i < 10; i++) {
            if (i * i == size) {
                sqrtSize = i;
                break;
            }
        }

        setGrid();
        setButtonsGrid();
        addHandler();
        if(cellsList.size()<4 || cellsList.size()>66 || !correctInput){
            correctInput = false;
            System.out.println(cellsList.size());
            return;
        }
        board.getChildren().add(grid);
        grid.setAlignment(Pos.TOP_LEFT);
        buttonsGrid.setAlignment(Pos.TOP_RIGHT);
        board.getChildren().add(buttonsGrid);
        fullBoard.getChildren().addAll(board, buttonGrid);
        board.setAlignment(Pos.TOP_CENTER);
        buttonGrid.setAlignment(Pos.BOTTOM_CENTER);

        this.getChildren().add(fullBoard);
        cellsList.get(1).activate();
    }
    /**
     * removes all the inserted values from the board
     */
    public void clearBoard() {
        int[] lastStateList = new int[size+2];
        for (int i = 1; i <= size; i++) {
            lastStateList[i] = valuesList[i];
            cellsList.get(i).setLabel("");
            cellsList.get(i).setToNormal();
            valuesList[i] = 0;
        }
        undoStack.push(lastStateList);
        undo.setDisable(false);
        redo.setDisable(true);
    }

    /**
     * shows all the correct values on the board
     */
    public void solve() {
        int[] lastStateList = new int[size+2];
        for (int i = 1; i <= size; i++) {
            lastStateList[i] = valuesList[i];
            cellsList.get(i).setValue(correctValuesList[i]);
            valuesList[i] = correctValuesList[i];
        }
        undoStack.push(lastStateList);
        undo.setDisable(false);
        redo.setDisable(true);
        fullMistakesCheck();
        //gameSolved();
    }

    /**
     * sets the array of correct values
     *
     * @param correctValues -> comes from Random board builder
     */
    public void setCorrectValues(int[] correctValues) {
        correctValuesList = correctValues;
    }

    /**
     * handles keyboards events
     * passes text to be written in a label
     * or changes the active cell
     *
     * @param s - string to be interpreted as an event
     */
    public void myKeyEvent(String s) {
        findActive();
        switch (s.charAt(0)) {
            case 'c': {
                if (cellsList.get(lastClicked).getLabel() != "") {
                    undo.setDisable(false);
                    redo.setDisable(true);
                    int[] lastStateList = new int[size+2];
                    for (int i = 1; i <= size; i++) {
                        lastStateList[i] = valuesList[i];
                    }
                    undoStack.push(lastStateList);
                }
                cellsList.get(lastClicked).setLabel("");
                valuesList[lastClicked] = 0;
                if (showMistakes)
                    mistakesCheck();
            }
            break;
            case 'w': {
                if (lastClicked > sqrtSize) {
                    cellsList.get(lastClicked).deactivate();
                    lastClicked -= sqrtSize;
                    cellsList.get(lastClicked).activate();
                }
            }
            break;
            case 's': {
                if (lastClicked + sqrtSize <= size) {
                    cellsList.get(lastClicked).deactivate();
                    lastClicked += sqrtSize;
                    cellsList.get(lastClicked).activate();
                }
            }
            break;
            case 'd': {
                if (lastClicked % sqrtSize != 0) {
                    cellsList.get(lastClicked).deactivate();
                    lastClicked += 1;
                    cellsList.get(lastClicked).activate();
                }
            }
            break;
            case 'a': {
                if (lastClicked % sqrtSize != 1) {
                    cellsList.get(lastClicked).deactivate();
                    lastClicked -= 1;
                    cellsList.get(lastClicked).activate();
                }
            }
            break;
        }

        if (s.charAt(0) <= sqrtSize + '0' && s.charAt(0) > '0') {
            if (s != cellsList.get(lastClicked).getLabel()) {
                undo.setDisable(false);
                redo.setDisable(true);
                int[] lastStateList = new int[size+2];
                for (int i = 1; i <= size; i++) {
                    lastStateList[i] = valuesList[i];
                }
                undoStack.push(lastStateList);
            }
            cellsList.get(lastClicked).setLabel(s);
            valuesList[lastClicked] = s.charAt(0) - '0';
            if (showMistakes)
                mistakesCheck();
            checkIfSolved();
        }
    }

    /**
     * un-dos users' action
     */
    public void undo(){
        int[]  lastStateList = new int[size+2];
        for (int i = 1; i <= size; i++) {
            lastStateList[i] = valuesList[i];
            if (valuesList[i] !=undoStack.peek()[i]) {
                cellsList.get(i).setValue(undoStack.peek()[i]);
                valuesList[i] = undoStack.peek()[i];
            }
        }
        redoStack.push(lastStateList);
        undoStack.pop();
        if(undoStack.empty())
            undo.setDisable(true);
        redo.setDisable(false);
        if (showMistakes)
            fullMistakesCheck();
    }

    /**
     * Re-dos user's undone action
     */
    public void redo(){
        int[]  lastStateList = new int[size+1];
        for (int i = 1; i <= size; i++) {
            lastStateList[i] = valuesList[i];
            if (valuesList[i] != redoStack.peek()[i]) {
                cellsList.get(i).setValue(redoStack.peek()[i]);
                valuesList[i] =redoStack.peek()[i];
            }
        }
        undoStack.push(lastStateList);
        redoStack.pop();
        undo.setDisable(false);
        if(redoStack.empty())
            redo.setDisable(true);
        if (showMistakes)
            fullMistakesCheck();
    }

    public void fullMistakesCheck(){
        int realLastClicked = lastClicked;
        for (int i = 1; i <= size; i++) {
            lastClicked = i;
            mistakesCheck();
        }
        lastClicked = realLastClicked;
    }

    /**
     * calls functions looking for mistakes
     * checks if the game is solved
     */
    public void mistakesCheck(){
        if(showMistakes){
            checkRow();
            checkColumn();
            checkSphere();
        }
        checkIfSolved();
    }

    /**
     * checks if a user solved the game
     */
    public void checkIfSolved(){
        for (int i = 1; i <= size; i++) {
            if (valuesList[i] == 0 || rowError[i] == true || columnError[i] == true || sphereError[i] == true)
                return;
        }
        gameSolved();
    }

    /**
     * changing the font size of all cells
     */
    public void setFont(int fontSize){
        for(int i=1; i<=size; i++){
            cellsList.get(i).setFont(fontSize);
        }
    }

    /**
     * Changes the size of all cells so the game can be resizable
     * @param newSize
     */
    public void setCellsSize(double newSize){
        for(int i=1; i<=size; i++){
            cellsList.get(i).setSize(newSize/sqrtSize);
        }
    }

    /**
     * Something will happen if a user wins the game, you can do it later
     */
    public void gameSolved(){
        setColorsList();
        Random random = new Random();
        winAnimation = new Transition(2) {

            @Override
            protected void interpolate(double v) {
            }
            public void play() {
                int j=random.nextInt(10);
                for (int i = 1; i <= size; i++) {
                    cellsList.get(i).setLabelColor(colorsList[j]);
                    if (j == 9)
                        j = 0;
                    cellsList.get(i).setBackgroundColor(colorsList[j + 1]);
                    j++;
                }
            }
        };
        winAnimation.setCycleCount(Transition.INDEFINITE);
        winAnimation.setAutoReverse(false);
        winAnimation.setDelay(Duration.millis(100));
        winAnimation.setOnFinished((e)->winAnimation.playFromStart());
        for(int k=0; k<10; k++)
            winAnimation.playFromStart();

    }

    public void setColorsList() {
        colorsList[0] = Color.PINK;
        colorsList[1] = Color.PURPLE;
        colorsList[2] = Color.DEEPPINK;
        colorsList[3] = Color.LIGHTPINK;
        colorsList[4] = Color.MAGENTA;
        colorsList[5] = Color.HOTPINK;
        colorsList[6] = Color.PALEVIOLETRED;
        colorsList[7] = Color.MEDIUMPURPLE;
        colorsList[8] = Color.DARKMAGENTA;
        colorsList[9] = Color.LIGHTSALMON;
    }

    /**
     * checks and marks mistakes/correction of mistakes
     * in a currently changed row
     */
    public void checkRow() {
        int start = (lastClicked - 1) / sqrtSize;
        int[] check = new int[sqrtSize + 1];
        for (int i = 1; i <= sqrtSize; i++) {
            check[i] = 0;
        }
        boolean error = false;
        start = (start * sqrtSize + 1);
        for (int i = 0; i < sqrtSize; i++) {
            check[valuesList[start + i]]++;
            if (check[valuesList[start + i]] > 1 && valuesList[start + i] != 0) {
                error = true;
            }
        }

        if (error == false) {
            for (int i = 0; i < sqrtSize; i++) {
                if (columnError[start + i] == false && sphereError[start + i] == false)
                    cellsList.get(start + i).setToNormal();
                rowError[start + i] = false;
            }
        } else {
            for (int i = 0; i < sqrtSize; i++) {
                cellsList.get(start + i).highlight();
                rowError[start + i] = true;
            }
        }
    }

    /**
     * checks and marks mistakes/correction of mistakes
     * in a currently changed column
     */
    public void checkColumn() {
        int start = lastClicked % sqrtSize;
        int[] check = new int[sqrtSize + 1];
        boolean error = false;
        for (int i = 0; i <= size; i += sqrtSize) {
            if(start+i<=size)
                check[valuesList[start + i]]++;
            if (check[valuesList[start + i]] > 1 && valuesList[start + i] != 0) {
                error = true;
            }
        }

        if (error == false) {
            for (int i = 0; i < size; i += sqrtSize) {
                if (rowError[start + i] == false && sphereError[start + i] == false)
                    cellsList.get(start + i).setToNormal();
                columnError[start + i] = false;
            }
            if (start == 0) {
                if (rowError[size] == false && sphereError[size] == false)
                    cellsList.get(size).setToNormal();
                columnError[size] = false;
            }
        } else {
            for (int i = 0; i < size; i += sqrtSize) {
                cellsList.get(start + i).highlight();
                columnError[start + i] = true;
            }
            if (start == 0) {
                cellsList.get(size).highlight();
                columnError[size] = true;
            }
        }
    }

    /**
     * checks if the input doesn't validate spheres' rules
     */
    public void checkSphere() {
        switch (cagesList.get(lastClicked)[1]) {
            case '÷':
                divisionCheck();
                break;
            case '-':
                subtractionCheck();
                break;
            case 'x':
                multiplicationCheck();
                break;
            default:
                additionCheck();
                break;
        }
    }

    /**
     * checking if input in addition sphere is valid
     */
    public void additionCheck() {
        int target = cagesList.get(lastClicked)[0], sum = 0;
        boolean ifAll = true;
        for (int i = 2; i < cagesList.get(lastClicked).length; i++) {
            sum += valuesList[cagesList.get(lastClicked)[i]];
            if (valuesList[cagesList.get(lastClicked)[i]] == 0)
                ifAll = false;
        }
        if (sum == target || (!ifAll && sum < target))
            cageCorrect();
        else
            cageIncorrect();
    }

    /**
     * checking if input in subtraction sphere is valid
     */
    public void subtractionCheck() {
        int target = cagesList.get(lastClicked)[0], sum = 0, max = valuesList[cagesList.get(lastClicked)[2]], i = 2;
        for (i = 2; i < cagesList.get(lastClicked).length; i++) {
            sum += valuesList[cagesList.get(lastClicked)[i]];
            if (valuesList[cagesList.get(lastClicked)[i]] > max) {
                max = valuesList[cagesList.get(lastClicked)[i]];
            }
            if (valuesList[cagesList.get(lastClicked)[i]] == 0) {
                cageCorrect();
                return;
            }
        }
        if ((2 * max) - sum == target)
            cageCorrect();
        else
            cageIncorrect();
    }

    public void divisionCheck() {
        int target = cagesList.get(lastClicked)[0], sum = 1, max = valuesList[cagesList.get(lastClicked)[2]], i;
        for (i = 2; i < cagesList.get(lastClicked).length; i++) {
            sum *= valuesList[cagesList.get(lastClicked)[i]];
            if (valuesList[cagesList.get(lastClicked)[i]] > max) {
                max = valuesList[cagesList.get(lastClicked)[i]];
            }
            if (valuesList[cagesList.get(lastClicked)[i]] < 1) {
                cageCorrect();
                return;
            }
        }
        sum /= max;
        if (max / sum == target)
            cageCorrect();
        else
            cageIncorrect();
    }

    /**
     * checking if input in multiplication sphere is valid
     */
    public void multiplicationCheck() {
        int target = cagesList.get(lastClicked)[0], sum = 1;
        boolean ifAll = true;
        for (int i = 2; i < cagesList.get(lastClicked).length; i++) {
            sum *= valuesList[cagesList.get(lastClicked)[i]];
            if (valuesList[cagesList.get(lastClicked)[i]] < 1)
                ifAll = false;
        }
        if (sum == target || (ifAll == false && sum <= target))
            cageCorrect();
        else
            cageIncorrect();
    }

    /**
     * If spheres rules weren't broken
     */
    public void cageCorrect() {
        for (int i = 2; i < cagesList.get(lastClicked).length; i++) {
            sphereError[cagesList.get(lastClicked)[i]] = false;
            if (rowError[cagesList.get(lastClicked)[i]] == false && columnError[cagesList.get(lastClicked)[i]] == false)
                cellsList.get(cagesList.get(lastClicked)[i]).setToNormal();
        }
    }

    /**
     * If spheres rules were broken
     */
    public void cageIncorrect() {
        for (int i = 2; i < cagesList.get(lastClicked).length; i++) {
            cellsList.get(cagesList.get(lastClicked)[i]).highlight();
            sphereError[cagesList.get(lastClicked)[i]] = true;
        }
    }

    /**
     * getter of board size
     *
     * @return rnumber of all cells
     */
    public int getSize() {
        return size;
    }

    /**
     * geter of sqrtSize
     *
     * @return rows/columns size
     */
    public int getSqrtSize() {
        return sqrtSize;
    }

    /**
     * Event handler deactivating recently clicked cell
     * and calling function finding the new one
     */
    public void addHandler() {
        grid.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            cellsList.get(lastClicked).deactivate();
            findActive();
        });
    }

    /**
     * finds new active cell so that it can be deactivated
     * if all cells are inactive sets lastActive to 0
     */
    public void findActive() {
        for (int i = 1; i <= size; i++) {
            if (cellsList.get(i).getActive() == true) {
                lastClicked = i;
                break;
            }
        }
    }

    /**
     * passes the spheresList to solver
     */
    public ArrayList<int[]> getCagesList() {
        return cagesList;
    }

    public void giveHint() {
        int[] lastStateList = new int[size+1];
        Random random = new Random();
        int randomPosition, noOfIterations = 0;
        randomPosition = random.nextInt(size - 1) + 2;
        while (valuesList[randomPosition] == correctValuesList[randomPosition]) {
            if (randomPosition >= size)
                randomPosition = 1;
            randomPosition += random.nextInt(size - randomPosition);
            noOfIterations++;
            if (noOfIterations > 5)
                break;
        }
        while (valuesList[randomPosition] == correctValuesList[randomPosition]) {
            randomPosition++;
            if (randomPosition > size)
                randomPosition = 1;
        }
        for (int i = 1; i <= size; i++)
            lastStateList[i] = valuesList[i];
        undoStack.push(lastStateList);
        valuesList[randomPosition] = correctValuesList[randomPosition];
        cellsList.get(randomPosition).setHintValue(correctValuesList[randomPosition]);
        undo.setDisable(false);
        redo.setDisable(true);
        redoStack.clear();
        fullMistakesCheck();
    }

    /**
     * Show mistakes button event handler
     * Starts/stops showing mistakes
     */
    public void showMistakesClicked() {
        if (showMistakes) {
            showMistakes = false;
            mistakesVisibility.setText("Show mistakes");
            for (int i = 1; i <= size; i++) {
                cellsList.get(i).setToNormal();
            }
        } else {
            showMistakes = true;
            mistakesVisibility.setText("Don't show mistakes");
            fullMistakesCheck();
        }
    }

    /**
     * Gives information if input is correct
     *
     * @return if input is correct
     */
    public boolean getIfCorrect() {
        return correctInput;
    }


    /**
     * uses the information gathered while calling "setArray" function
     * creates all individual cells and puts them onto the grid
     */
    public void setGrid() {
        //it's here just so the program doesn't crush after adding to 1st position
        Cell cell0 = new Cell('1', 1, false, 1, 1, 1, 1, 0);
        cellsList.add(cell0);
        for (int i = 1; i <= size; i++) {
            int target = cagesList.get(i)[0];
            char targetSign = (char) cagesList.get(i)[1];
            if(cagesList.get(i).length==3)
                targetSign=' ';
            boolean showTarget = false;
            int R = 1, L = 1, T = 1, B = 1;
            if (i == cagesList.get(i)[2])
                showTarget = true;
            if (cagesList.get(i).length == 3) {
                cellsList.add(i, new Cell(targetSign, target, showTarget, 1, 1, 1, 1, i));
                i--;
                int row = i / sqrtSize;
                int column = i % sqrtSize;
                grid.add(cellsList.get(i + 1), column, row);
                i++;
                continue;
            }
            int j = 2;
            //setting borders
            while (j < cagesList.get(i).length && cagesList.get(i)[j] < i - sqrtSize)
                j++;
            if (j < cagesList.get(i).length && cagesList.get(i)[j] == i - sqrtSize)
                T = 0;
            while (j < cagesList.get(i).length && cagesList.get(i)[j] < i - 1)
                j++;
            if (j < cagesList.get(i).length && cagesList.get(i)[j] == i - 1)
                L = 0;
            while (j < cagesList.get(i).length && cagesList.get(i)[j] < i + 1)
                j++;
            if (j < cagesList.get(i).length && cagesList.get(i)[j] == i + 1)
                R = 0;
            while (j < cagesList.get(i).length && cagesList.get(i)[j] < i + sqrtSize)
                j++;
            if (j < cagesList.get(i).length && cagesList.get(i)[j] == i + sqrtSize)
                B = 0;
            if (R == 1 && B == 1 && L == 1 && T == 1 && cagesList.get(i).length > 1) {
                correctInput = false;
                return;
            }
            cellsList.add(i, new Cell(targetSign, target, showTarget, R, L, T, B, i));
            i--;
            int row = i / sqrtSize;
            int column = i % sqrtSize;
            grid.add(cellsList.get(i + 1), column, row);
            i++;
        }

    }

    /**
     * Very long and unpleasant to read method - goes through the file and
     * takes all the information needed to later construct a grid
     *
     * @param filename name of the file I take the game description from
     * @throws IOException because its input from file and things can go wrong
     */
    public void setArray(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String newLine;
        int[] array0 = new int[1];
        array0[0] = 0;
        for (int i = 0; i < 70; i++)
            cagesList.add(array0);
        size = 0; //looks for the biggest position value in a file
        int target;
        while (!filename.isEmpty() && (newLine = reader.readLine()) != null) {
            int i = 0, sign, posInCurrent = 0;
            target = 0;
            int[] current = new int[70];
            //finding a target value in a line
            while (i < newLine.length() - 1 && newLine.charAt(i) <= '9' && newLine.charAt(i) >= '0') {
                target *= 10;
                target = target + newLine.charAt(i) - '0';
                i++;
            }

            //defines sign as an easy to use later integer
            sign = newLine.charAt(i);
            if(sign != '÷' && sign != '-' && sign != '+' && sign != 'x' && sign != ' '){
                correctInput=false;
                return;
            }

            //getting a list of all cells in a sphere and putting it in a "current" array
            for (i = i + 1; i < newLine.length(); i++) {
                int position = 0;
                if (newLine.charAt(i) >= '0' && newLine.charAt(i) <= '9') {
                    position = newLine.charAt(i) - '0';
                    if (newLine.length() > i + 1 && newLine.charAt(i + 1) >= '0' && newLine.charAt(i + 1) <= '9') {
                        position = (position * 10) + newLine.charAt(++i) - '0';
                    }
                    current[posInCurrent] = position;
                    posInCurrent++;
                    if (position > size)
                        size = position;
                }
            }
            //sorting current array so later its easier to tell
            // on which sides of a cell are other cells from its sphere
            Arrays.sort(current, 0, posInCurrent);
            //getting positions from array and using them to describe a sphere in sphereList
            int[] newCage = new int[posInCurrent + 2];
            newCage[0] = target;
            newCage[1] = sign;
            for (int j = 0; j < posInCurrent; j++) {
                newCage[2 + j] = current[j];
            }
            for (i = 0; i < posInCurrent; i++) {
                if (cagesList.get(current[i]) != array0) {
                    correctInput = false;
                    return;
                }
                cagesList.set(current[i], newCage);
            }

        }
        for (int i = 2; i < 10; i++) {
            if (i * i == size) {
                sqrtSize = i;
                break;
            }
        }
    }

    /**
     *the same function as the last but gets  a single line as a parameter
     */
    public void addLine(String newLine){
        int target=0 , i = 0, sign, posInCurrent = 0;
        int[] current = new int[70];
        //finding a target value in a line
        while (i < newLine.length() - 1 && newLine.charAt(i) <= '9' && newLine.charAt(i) >= '0') {
            target *= 10;
            target = target + newLine.charAt(i) - '0';
            i++;
        }

        sign =  newLine.charAt(i);

        if(sign != '÷' && sign != '-' && sign != '+' && sign != 'x' && sign != ' ' ){
            correctInput=false;
            System.out.println(sign);
            return;
        }

        //getting a list of all cells in a sphere and putting it in a "current" array
        for (i = i + 1; i < newLine.length(); i++) {
            int position = 0;
            if (newLine.charAt(i) >= '0' && newLine.charAt(i) <= '9') {
                position = newLine.charAt(i) - '0';
                if (newLine.length() > i + 1 && newLine.charAt(i + 1) >= '0' && newLine.charAt(i + 1) <= '9') {
                    position = (position * 10) + newLine.charAt(++i) - '0';
                }
                current[posInCurrent] = position;
                posInCurrent++;
                if (position > size)
                    size = position;
            }
        }
        //sorting current array so later its easier to tell
        // on which sides of a cell are other cells from its sphere
        Arrays.sort(current, 0, posInCurrent);
        //getting positions from array and using them to describe a sphere in sphereList
        int[] newCage = new int[posInCurrent + 2];
        newCage[0] = target;
        newCage[1] = sign;
        for (int j = 0; j < posInCurrent; j++) {
            newCage[2 + j] = current[j];
        }
        //if any of the cells is
        for (i = 0; i < posInCurrent; i++) {
            if (cagesList.get(current[i]) != array0) {
                correctInput = false;
                System.out.println(current[i]);
                return;
            }
            cagesList.set(current[i], newCage);
        }
    }

    /**
     * Creates grid with buttons and adds its handlers
     */
    public void setButtonsGrid(){
        ArrayList<Button> buttonArrayList = new ArrayList<Button>();
        Button button0 = new Button("  ");
        button0.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->myKeyEvent("c"));
        button0.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                myKeyEvent("c");
            }
        });
        buttonArrayList.add(button0);
        Button button1 = new Button("1");
        button1.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> myKeyEvent("1"));
        buttonArrayList.add(button1);
        button1.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)
                myKeyEvent("1");
        });
        Button button2 = new Button("2");
        button2.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->myKeyEvent("2"));
        buttonArrayList.add(button2);
        button2.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER){
                myKeyEvent("2");
            }
        });
        Button button3 = new Button("3");
        button3.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->myKeyEvent("3"));
        buttonArrayList.add(button3);
        button3.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)
                myKeyEvent("3");
        });
        Button button4 = new Button("4");
        button4.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> myKeyEvent("4"));
        buttonArrayList.add(button4);
        button4.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)
                myKeyEvent("4");
        });
        Button button5 = new Button("5");
        button5.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> myKeyEvent("5"));
        buttonArrayList.add(button5);
        button5.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                myKeyEvent("5");
            }
        });
        Button button6 = new Button("6");
        button6.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> myKeyEvent("6"));
        buttonArrayList.add(button6);
        button6.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                myKeyEvent("6");
            }
        });
        Button button7 = new Button("7");
        button7.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> myKeyEvent("7"));
        buttonArrayList.add(button7);
        button7.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                myKeyEvent("7");
            }
        });
        Button button8 = new Button("8");
        button8.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->myKeyEvent("8"));
        button8.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                myKeyEvent("8");
            }
        });
        buttonArrayList.add(button8);
        for (int i = 0; i <= sqrtSize; i++) {
            buttonArrayList.get(i).setFont(new Font(20));
            buttonsGrid.add(buttonArrayList.get(i), 0, i);
        }
        buttonsGrid.getColumnConstraints().add(new ColumnConstraints(50));
        Button mediumSize = new Button("Medium");
        Button smallSize = new Button("Small");
        Button bigSize = new Button("Big");
        buttonGrid.setVgap(10);
        buttonGrid.setHgap(10);
        buttonGrid.add(mistakesVisibility, 0, 0);
        buttonGrid.add(clear, 0, 1);
        buttonGrid.add(undo, 1, 0);
        buttonGrid.add(redo, 1, 1);
        buttonGrid.add(hint, 2, 0);
        buttonGrid.add(solve, 2, 1);
        buttonGrid.add(mediumSize, 0, 2);
        buttonGrid.add(smallSize, 1, 2);
        buttonGrid.add(bigSize, 2, 2);

        mistakesVisibility.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                showMistakesClicked();
            }
        });
        solve.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                solve();
            }
        });
        clear.setOnAction(new EventHandler<ActionEvent>()  {

            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "This action can be later undone");

                alert.setTitle("Clearing confirmation");
                alert.setHeaderText("Are you sure you want to clear the board?");

                Optional<ButtonType> result = alert.showAndWait();


                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clearBoard();
                }
            }
        });

        undo.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                undo();
            }
        });
        redo.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                redo();
            }
        });
        hint.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                giveHint();
            }
        });
        mediumSize.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                setFont(2);
            }
        });
        smallSize.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                setFont(1);
            }
        });
        bigSize.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                setFont(3);
            }
        });

        redo.setDisable(true);
        undo.setDisable(true);

        undo.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->undo());
        redo.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> redo());

        bigSize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> setFont(3));
        smallSize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> setFont(1));
        mediumSize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> setFont(2));
        solve.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> solve());
        clear.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> clearBoard());
        mistakesVisibility.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> showMistakesClicked());
        hint.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) ->giveHint());
    }
}

