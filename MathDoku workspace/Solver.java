package sample;

import java.util.ArrayList;
import java.util.Iterator;
import sample.GameBoard;

public class Solver {
    private GameBoard boardToSolve;
    private int noOfAnswers=0;
    private ArrayList<int[]> cagesList;
    private int[] valuesList = new int[70];
    private int[] firstSolution = new int[70];
    private int size, sqrtSize, iterator=1;
    private int differencePlace, firstValue, secondValue;

    private boolean checkIfOnlyOne;

    /**
     * creator for solver for a provided game
     * checkIfOnlyOne=false because we need to check it only while generating new board
     * @param boardToSolve
     */
    public Solver(GameBoard boardToSolve){
        this.boardToSolve=boardToSolve;
        checkIfOnlyOne=false;
        cagesList=boardToSolve.getCagesList();
        size=boardToSolve.getSize();
        sqrtSize = boardToSolve.getSqrtSize();
        solveBoard();
    }

    /**
     * second creator for auto-generated board
     * @param cagesList
     * @param size
     */
    public Solver(ArrayList<int[]> cagesList, int size, int sqrtSize){
        this.sqrtSize=sqrtSize;
        this.size=size;
        this.cagesList=cagesList;
        checkIfOnlyOne = true;
        solveBoard();
    }

    public void solveBoard(){
        while(iterator<=size && iterator>0){
            if(valuesList[iterator]>=sqrtSize){
                valuesList[iterator]=0;
                iterator--;
                continue;
            }
            do {
                valuesList[iterator]++;
            }
            while (!isCorect() && valuesList[iterator]<=sqrtSize);
            if( valuesList[iterator]>sqrtSize){
                valuesList[iterator]=0;
                iterator--;
                continue;
            }
            iterator++;
        }

        if(iterator==0){
            return;
        }
        noOfAnswers++;
        if(checkIfOnlyOne){
            if(noOfAnswers==1){
                firstSolution = valuesList;
                iterator--;
                valuesList[iterator]++;
                solveBoard();
                return;
            }
            checkFirstDifference();
        }
    }

    /**
     * calls functions checking if a new value doesn't validate any of the rules
     * @return if a new value can be inserted in a given place
     */
    public boolean isCorect(){
        return checkUp() && checkLeft() && checkCage();
    }

    /**
     * Checks if a new value has already been put in its column
     * @return if input can be in given column
     */
    public boolean checkUp(){
        for(int i=iterator-sqrtSize; i>0; i-=sqrtSize){
            if(valuesList[i]==valuesList[iterator])
                return false;
        }
        return true;
    }

    /**
     * Checks if a new value has already been put in its row
     * @return if input can be in given row
     */
    public boolean checkLeft(){
        for(int i=iterator-1; i%sqrtSize>0; i--){
            if(valuesList[i]==valuesList[iterator])
                return false;
        }
        return true;
    }

    /**
     * checks if the input doesn't validate sphres' rules
     */
    public boolean checkCage(){
        switch (cagesList.get(iterator)[1]){
            case '÷':
                return divisionCheck();
            case '-':
                return subtractionCheck();
            case 'x':
                return multiplicationCheck();
            default:
                return additionCheck();
        }
    }

    /**
     * checking if input in addition cage is valid
     */
    public boolean additionCheck(){
        int target = cagesList.get(iterator)[0], sum=0;
        boolean ifAll = true;
        for(int i=2; i<cagesList.get(iterator).length; i++){
            sum+=valuesList[cagesList.get(iterator)[i]];
            if(valuesList[cagesList.get(iterator)[i]]==0)
                ifAll=false;
        }
        if(sum==target ||  (!ifAll && sum<target))
            return true;
        else
            return false;
    }

    /**
     * checking if input in subtraction cage is valid
     */
    public boolean subtractionCheck(){
        int target = cagesList.get(iterator)[0], sum=0, max=valuesList[cagesList.get(iterator)[2]], i=2;
        for(i=2; i<cagesList.get(iterator).length; i++){
            sum+=valuesList[cagesList.get(iterator)[i]];
            if(valuesList[cagesList.get(iterator)[i]]>max){
                max=valuesList[cagesList.get(iterator)[i]];
            }
            if(valuesList[cagesList.get(iterator)[i]]==0){
                return true;
            }
        }
        if((2*max)-sum==target)
            return true;
        else
            return false;
    }

    public boolean divisionCheck(){
        int target = cagesList.get(iterator)[0], sum=1, max=valuesList[cagesList.get(iterator)[2]], i;
        boolean ifAll = true;
        for(i=2; i<cagesList.get(iterator).length; i++){
            sum*=valuesList[cagesList.get(iterator)[i]];
            if(valuesList[cagesList.get(iterator)[i]]>max){
                max=valuesList[cagesList.get(iterator)[i]];
            }
            if(valuesList[cagesList.get(iterator)[i]]<1){
                return true;
            }
        }
        sum/=max;
        if(max/sum==target )
            return true;
        else
            return false;
    }

    /**
     * checking if input in multiplication sphere is valid
     */
    public boolean multiplicationCheck(){
        int target = cagesList.get(iterator)[0], sum=1;
        boolean ifAll = true;
        for(int i=2; i<cagesList.get(iterator).length; i++){
            sum*=valuesList[cagesList.get(iterator)[i]];
            if(valuesList[cagesList.get(iterator)[i]]<1)
                ifAll=false;
        }
        if(sum==target ||  (ifAll==false && sum<=target))
            return true;
        else
            return false;
    }


    /**
     * Looks for the first place in witch the solutions differ
     */
    public void checkFirstDifference(){
        for(int i=1; i<=size; i++){
            if(valuesList[i]!=firstSolution[i]){
                differencePlace=i;
                firstValue=firstSolution[i];
                secondValue=valuesList[i];
                return;
            }
        }
    }

    public int getNoOfAnswers(){
        return noOfAnswers;
    }
    public int[] getSolution(){
        return valuesList;
    }

    public int[] getFirstSolution(){
        return firstSolution;
    }
}
