package sample;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomBoardBuilder {
    int size;
    int[] board;
    int[] array = new int[1];
    boolean[] isUsed;
    boolean correctBoard = false;
    private ArrayList<int[]> cagesList = new ArrayList<int[]>(100);

    /**
     * creator taking boardSize as a parameter
     * @param size
     */
    public RandomBoardBuilder(int size){
        this.size=size;
        setArray();
        Solver mySolver;
        while (correctBoard == false)
            setRandomCombination();
        setCages();
        //mySolver = new Solver(spheresList, size*size, size); //not sure how to use it and make sure it has one solution
    }

    public  ArrayList<int[]> getCagesList(){
        return cagesList;
    }

    public int[] getSolutions(){
        return board;
    }

    public int getSize(){
        return size*size;
    }

    public int getSqrtSize(){
        return size;
    }

    /**
     * sets an empty spheres list to assign it's size
     */
    public void setArray(){
        board=new int[size*size+1];
        for(int i=0; i<=size*size; i++){
            cagesList.add(array);
            board[i]=0;
        }
    }

    /**
     * Creates a semi-random combination of numbers in grid
     * It's not completely random as it cannot validate rows/columns rules
     * That will be the solution of the board
     */
    public void setRandomCombination(){
        //if a column already has the value written
        isUsed = new boolean[size+1];
        //if a row is already filled with the value
        boolean[] isFilled = new boolean[size+1];
        Random random = new Random();
        //how many empty cells can't be filled with new value in given row
        int[] affectionLevel = new int[size+1];
        int randomPlace, mostAffected=0, position, highestAffectionLevel;
        //'i' iterates through the values I add to the array
        for(int i=1; i<=size; i++){
            //there are 1,2, ..., size options
            randomPlace=random.nextInt(size+1-i)+1;
            position=0;

            //isFilled holds information if the row has been filled
            //isUsed == true if i has been added to given row
            for (int j=1; j<=size; j++){
                isUsed[j]=false;
                isFilled[j]=false;
            }

            //assigns the value to a random empty place in first row
            for(int j=1; j<=size; j++){
                if(board[j]==0){
                    position++;
                    if(position==randomPlace){
                        board[j]=i;
                        isUsed[j]=true;
                        position=j;
                        break;
                    }
                }
            }

            //affection level is a number of empty cells in a row j,
            //which are in the same column as already added i
            for (int j=1; j<size; j++)
                affectionLevel[j]=0;

            int rowsFilled=1; //because the first one was filled above
            //fills all the other rows starting with the most affected ones
            //affection level is measured by the number of non-empty cells that can't be filled
            while (rowsFilled!=size){

                highestAffectionLevel=0;
                //searching for the first row affected by the change
                for(int j=1; j<size; j++){
                    if(!isFilled[j] && board[j*size+position]==0){
                        affectionLevel[j]++;
                        if(affectionLevel[j]>highestAffectionLevel){
                            highestAffectionLevel=affectionLevel[j];
                            mostAffected=j;
                        }
                    }
                }
                //in the last run no rows will be affected so I  need to fill the first unused row
                if(highestAffectionLevel==0){
                    for(int j=1; j<size; j++){
                        if(!isFilled[j]){
                            mostAffected=j;
                            highestAffectionLevel=size;
                        }
                    }
                }

                isFilled[mostAffected]=true;
                rowsFilled++;

                if(size+1-i-highestAffectionLevel>0)
                    randomPlace=random.nextInt(size+1-highestAffectionLevel-i)+1;
                    //if there is just one option (last run)
                else randomPlace=1;
                position=0;
                //assigning the value to a random empty position in most affected row
                for(int j=1; j<=size; j++){
                    if(board[size*mostAffected+j]==0 && !isUsed[j]){
                        position++;
                        if(position==randomPlace){
                            board[size*mostAffected+j]=i;
                            isUsed[j]=true;
                            position=j;
                            break;
                        }
                    }
                }
            }
        }
        correctBoard = true;
        for(int i=0; i<size; i++){
            for(int j=1; j<=size; j++){
                System.out.print(board[i*size+j]+ " ");
                if(board[i*size+j] == 0)
                    correctBoard=false;
            }
            System.out.println("");
        }
    }

    /**
     * puts pre-defined values in cages and puts the information in cagesList
     */
    public void setCages(){
        isUsed = new boolean[size*size+1];
        for(int i=1; i<=size*size; i++){
            if( cagesList.get(i) == array){
                makeCage(i);
            }
        }
    }

    /**
     * creates a cage with a cell in a given position
     * puts the result in fullCage
     * @param position
     */
    public void makeCage(int position) {
        Random random = new Random();
        int[] newCage = new int[size * size];
        int i = 0, dir;
        boolean changedPosition = true;
        while (changedPosition == true) {
            changedPosition = false;
            newCage[i] = position;
            isUsed[position]=true;
            i++;
            dir = random.nextInt(4);
            //0-direction = left
            if (dir == 0) {
                if (position % size != 1 && !isUsed[position - 1]){
                    position--;
                    changedPosition=true;
                }
                else if (i == 1)
                    dir = 1;
            }

            //1 - direction = up
            if (dir == 1) {
                if (position > size && !isUsed[position - size]){
                    changedPosition=true;
                    position -= size;
                }
                else if (i == 1)
                    dir = 2;
            }

            //2 - direction = right
            if (dir == 2) {
                if (position % size != 0 && !isUsed[position + 1]){
                    changedPosition=true;
                    position++;
                }
                else if (i == 1)
                    dir = 3;
            }
            //3 - direction = down
            if (dir == 3) {
                if (position + size < size * size && !isUsed[position + size]){
                    changedPosition=true;
                    position += size;
                }
            }
        }
        Arrays.sort(newCage, 0, i);
        int[] fullCage = new int[i + 2];
        boolean isSet = false;

        //if there is just one cell
        if (i < 2) {
            fullCage[1] = '+'; //it is " " but screw it, i want to fucking finish it
            fullCage[0] = board[newCage[0]];
            fullCage[2] = newCage[0];
            cagesList.set(newCage[0], fullCage);
            return;
        }

        int multiplicationSum = 1, additionSum = 0, max = 0;
        for (int j = 0; j < i; j++) {
            fullCage[j + 2] = newCage[j];
            multiplicationSum *= board[newCage[j]];
            additionSum += board[newCage[j]];
            if (board[newCage[j]] > max){
                max = board[newCage[j]];
            }
        }

        int sign = random.nextInt(8);
        //<0, 2> is for division (it's the least likely to be possible)
        if(sign<=2){
            if(multiplicationSum!=0 && (max*max)%multiplicationSum==0){
                fullCage[0]=(max*max)/multiplicationSum;
                fullCage[1]='÷';
                isSet=true;
            }
            else
                sign=3;
        }

        //3, 4 and all that cannot be divided goes to -
        if(!isSet && sign<5){
            if((2*max)-additionSum>=0){
                fullCage[0]=(2*max)-additionSum;
                fullCage[1]='-';
                isSet=true;
            }
            else sign=5;
        }

        //5, 6 and all that cannot be subtracted goes to x
        if(!isSet && sign<7){
            if(multiplicationSum<100){
                fullCage[0]=multiplicationSum;
                fullCage[1]='x';
                isSet=true;
            }
        }

        if(!isSet){
            fullCage[0]=additionSum;
            fullCage[1]='+';
        }

        for(int j=0; j<i; j++){
            cagesList.set(newCage[j], fullCage);
        }
    }

}