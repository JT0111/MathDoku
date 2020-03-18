package sample;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.paint.Color.*;

public class Cell extends Region{
    Label insertedNumber = new Label("");
    Font insertFont;
    private int fontSize=2;
    private char targetSign;
    private int target, number;
    private boolean showTarget;
    private int R, L, T, B;
    private int currentValue = -1;
    Pane region = new Pane();
    Label targetLabel;
    BorderWidths myWidths;
    Border normalBorder, activeBorder, highlightedBorder;
    Boolean active=false;
    Background yellowBackground = new Background(new BackgroundFill(LIGHTCYAN, CornerRadii.EMPTY, Insets.EMPTY));
    Background myBackground = new Background(new BackgroundFill(WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    Border currentBorder;

    /**
     * Constructor of cell
     * @param target is the target of given sphere
     * @param showTarget determines if the target should be shown in a cell
     * @param R if there is a border on the right
     * @param L or left
     * @param T or top
     * @param B or bottom
     */
    public Cell(char targetSign, int target, boolean showTarget, int R, int L, int T, int B, int number){
        this.targetSign=targetSign;
        this.L=L;
        this.R=R;
        this.B=B;
        this.T=T;
        this.target=target;
        this.showTarget=showTarget;
        this.number=number;
        create();
        addHandlers();
        this.getChildren().add(region);
    }

    /**
     * Creates a complete cell with borders,
     * shows the target and menages properties of Label for inserted number
     */
    public void create() {

        //size of border
        region.setPrefSize(50, 50);
        //withs of cell borders
        myWidths = new BorderWidths(0.5+T*1.8, 0.5+R*1.8, 0.5+B*1.8, 0.5+L*1.8);
        //properties of border
        BorderStroke normalStroke = new BorderStroke(BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, myWidths);
        BorderStroke highlightedStroke = new BorderStroke(RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 2, 2, 2));
        BorderStroke activeStroke = new BorderStroke(MEDIUMSPRINGGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3, 3, 3, 3));
        highlightedBorder = new Border(highlightedStroke);
        normalBorder = new Border(normalStroke);
        activeBorder = new Border(activeStroke);
        currentBorder=normalBorder;
        region.setBorder(normalBorder);

        //if this cell shows the target of the sphere
        if(showTarget==true) {
            targetLabel = new Label(" "+target+targetSign);
            targetLabel.setPrefSize(50, 25);
            targetLabel.setFont(new Font(10));
            region.getChildren().add(targetLabel);
            targetLabel.setAlignment(Pos.TOP_LEFT);
        }
        //properties of the label
        insertedNumber.setPrefSize(50, 50);
        insertFont = new Font(25);
        insertedNumber.setFont(insertFont);
        region.getChildren().add(insertedNumber);
        insertedNumber.setAlignment(Pos.CENTER);
    }

    public void setSize(double newSize){
        region.setPrefSize(newSize, newSize);
        if(showTarget){
            if(fontSize==3){
                targetLabel.setFont(new Font(0.20*newSize));
            }
            else
                targetLabel.setFont(new Font(fontSize*0.09*newSize));
            targetLabel.setPrefSize(newSize, newSize/2);
        }
        if(fontSize==3){
            insertedNumber.setFont(new Font(0.55*newSize));
        }
        else
            insertedNumber.setFont(new Font(fontSize*0.25*newSize));
        insertedNumber.setPrefSize(newSize, newSize);
    }
    /**
     * changing the font size
     * @param size 1-small, 2-medium, 3-big
     */
    public void setFont(int size){
        fontSize=size;
        if(size==3){
            if(showTarget==true){
                targetLabel.setFont(new Font(0.40*targetLabel.getPrefHeight()));
            }
            insertedNumber.setFont(new Font(0.55*insertedNumber.getPrefHeight()));
        }
        if(showTarget==true){
            targetLabel.setFont(new Font(size*0.17*targetLabel.getPrefHeight()));
        }
        insertedNumber.setFont(new Font(size*0.25*insertedNumber.getPrefHeight()));
    }
    /**
     * sets the inserted number to a given value
     * the value is always correct
     */
    public void setValue(int value){
        if(value==0){
            setLabel("");
            return;
        }
        char c=(char)(value+'0');
        String s=""+c;
        setLabel(s);
    }

    public void setHintValue(int value){
        setValue(value);
        insertedNumber.setTextFill(GREEN);
    }

    /**
     * Event handler activating the cell and turning it red after a mouse click
     */
    public void addHandlers(){
        region.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> activate());
    }

    /**
     * active cell is red and can be written into
     * @return if cell is active
     */
    public boolean getActive(){
        return active;
    }

    /**
     * sets a cell active
     * e.g. allows the user to give input
     */
    public void activate(){
        region.setBorder(activeBorder);
        active=true;
    }
    /**
     * sets border back to normal and marks cell as inactive
     */
    public void deactivate(){
        region.setBorder(currentBorder);
        active = false;
    }

    /**
     * changes text in a label to user input
     * but firs checks if the input is valid
     * sets currentValue
     * @param s - text to be written
     */
    public void setLabel(String s){
        insertedNumber.setText(s);
        insertedNumber.setTextFill(BLACK);
    }


    /**
     * gets the current Label text
     * @return string with current value
     */
    public String getLabel(){
        return insertedNumber.getText();
    }

    /**
     * sets borders to black ones - means that there is no mistake
     */
    public void setToNormal(){
        currentBorder=normalBorder;
        if(active==false)
            region.setBorder(currentBorder);
    }

    /**
     * sets cell color to chosen one
     * @param color
     */
    public void setBackgroundColor(Color color){
        region.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * sets label's color to chosen one
     * @param color
     */
    public void setLabelColor(Color color){
        insertedNumber.setTextFill(color);
    }

    /**
     * highlights the borders
     * means an error has been detected
     */
    public void highlight(){
        currentBorder=highlightedBorder;
        if(active==false)
            region.setBorder(currentBorder);
    }

}
