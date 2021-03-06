package uk.ac.soton.jt1g19.sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;

public class Main extends Application {

    private Scene startScene, gameScene;
    private GameBoard myBoard;
    private Stage primaryStage;
    private TextArea inputField;
    private Label errorMessagesLine=new Label("");

    /**
     * Sets the Stage and builds all the components
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage=primaryStage;
        primaryStage.setTitle("Mathdoku");
        setMenuScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * sets a Menu stage
     * allows the user to choose the source of the game
     */
    private void setMenuScene() throws IOException{
        GridPane menuGrid = new GridPane();
        Label helloLabel = new Label("Hi! What do you want to do?");
        Label orLabel = new Label("OR...");
        Button loadFromFileButton = new Button("Load game from file");
        Button loadFromTextButton = new Button("Load game from text");
        Button generateNewGameButton = new Button("Generate random game");
        generateNewGameButton.setFont(new Font(25));
        loadFromFileButton.setFont(new Font(25));
        loadFromTextButton.setFont(new Font(25));
        helloLabel.setFont(new Font(25));
        orLabel.setFont(new Font(25));
        inputField = new TextArea();
        errorMessagesLine.setFont(new Font(10));
        errorMessagesLine.setTextFill(Color.RED);
        //loadFromFileButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->loadFile());

        menuGrid.add(helloLabel, 0, 0);
        menuGrid.add(generateNewGameButton, 0, 1);
        menuGrid.add(orLabel, 0, 2);
        menuGrid.add(inputField, 0, 3);
        menuGrid.add(errorMessagesLine, 0, 4);
        menuGrid.add(loadFromTextButton, 0, 5);
        menuGrid.add(loadFromFileButton, 0, 6);

        /*menuGrid.setColumnSpan(helloLabel, 2);
        //menuGrid.setColumnSpan(generateNewGameButton, 2);
        menuGrid.setColumnSpan(inputField, 2);
        menuGrid.setColumnSpan(orLabel, 2);
        menuGrid.setColumnSpan(loadFromTextButton, 2);
        menuGrid.setColumnSpan(loadFromFileButton, 2);
         */
        menuGrid.setHalignment(loadFromTextButton, HPos.CENTER);
        menuGrid.setHalignment(orLabel, HPos.CENTER);
        menuGrid.setHalignment(loadFromFileButton, HPos.CENTER);
        menuGrid.setHalignment(generateNewGameButton, HPos.CENTER);
        menuGrid.setHalignment(helloLabel, HPos.CENTER);

        loadFromFileButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)-> {
            try{
                fileNameSet();
            } catch(IOException e2){
                errorMessagesLine.setText("Illegal filename");
                new RuntimeException(e2);
            }
        });

        loadFromTextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)-> {
            try{
                boardSetAsText();
            } catch(IOException e1) {
                e1.printStackTrace();
            }
        });


        generateNewGameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->generateBoard());

        menuGrid.setVgap(15);
        menuGrid.setHgap(15);
        startScene = new Scene(menuGrid, 450, 550);
        menuGrid.setAlignment(Pos.CENTER);
        primaryStage.setScene(startScene);
    }

    private void generateBoard(){
        int size;
        if(inputField.getText().length()>0){
            size = inputField.getText().charAt(0)-'0';
            if(size<2 || size>8){
                errorMessagesLine.setText("Invalid size");
                return;
            }
        }
        else {
            size=6;
        }
        RandomBoardBuilder myBuilder = new RandomBoardBuilder(size);
        myBoard = new GameBoard(myBuilder);
        myBoard.setCellsSize(500);
        gameScene = new Scene(myBoard, 550, 650);
        addHandler();
        primaryStage.setScene(gameScene);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()>primaryStage.getHeight()-100){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()+100>primaryStage.getHeight()){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });
    }

    private void fileNameSet() throws IOException{
        String filename = inputField.getText();
        myBoard = new GameBoard(filename);
        if(myBoard.getIfCorrect()==false){
            errorMessagesLine.setText("Provided board is invalid");
            return;
        }
        Solver boardSolver = new Solver(myBoard);
        if(boardSolver.getNoOfAnswers()==0){
            errorMessagesLine.setText("Provided board has no solutions");
            return;
        }
        myBoard.setCorrectValues(boardSolver.getSolution());
        myBoard.setCellsSize(500);
        gameScene = new Scene(myBoard, 550, 650);
        addHandler();
        primaryStage.setScene(gameScene);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()>primaryStage.getHeight()-100){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()+100>primaryStage.getHeight()){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });
    }

    private void boardSetAsText() throws IOException{
        myBoard = new GameBoard();
        for (String line : inputField.getText().split("\\n")){
            myBoard.addLine(line);
        }
        if(myBoard.getIfCorrect()==false){
            errorMessagesLine.setText("Provided board is invalid");
            return;
        }
        myBoard.allLinesIn();
        if(myBoard.getIfCorrect()==false){
            errorMessagesLine.setText("Provided board is invalid");
            return;
        }
        Solver boardSolver = new Solver(myBoard);
        if(boardSolver.getNoOfAnswers()==0){
            errorMessagesLine.setText("Provided board has no solutions");
            return;
        }
        myBoard.setCorrectValues(boardSolver.getSolution());
        myBoard.setCellsSize(500);
        gameScene = new Scene(myBoard, 550, 650);
        addHandler();
        primaryStage.setScene(gameScene);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()>primaryStage.getHeight()-100){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(primaryStage.getWidth()+100>primaryStage.getHeight()){
                myBoard.setCellsSize(primaryStage.getHeight()-150);
            }
            else
                myBoard.setCellsSize(primaryStage.getWidth()-50);
        });
    }

    /**
     * handles user input
     */
    private void addHandler(){
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if(e.getCode()== KeyCode.BACK_SPACE){
                myBoard.myKeyEvent("c");
            }
            else{
                myBoard.myKeyEvent(e.getText());
            }
        });
    }
}
