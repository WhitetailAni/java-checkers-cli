import java.util.Scanner;

public class Main {

    //setup for colored board
    public static final String ANSI_RESET = "\u001B[0m";
    public static String ANSI_RED = "\u001B[31m";
    public static String ANSI_WHITE = "\u001B[37m";
    public static String ANSI_BLACK = "\u001B[30m";

    public static void main(String[] args){
        int[][] CheckerBoard = new int[8][8];
	    System.out.println(ANSI_WHITE + "0 = empty space" + ANSI_RESET);
	    System.out.println(ANSI_BLACK + "1 = black piece" + ANSI_RESET);
	    System.out.println(ANSI_RED + "2 = red piece" + ANSI_RESET);
	    System.out.println(ANSI_BLACK + "3 = black king" + ANSI_RESET);
	    System.out.println(ANSI_RED + "4 = red king" + ANSI_RESET);

        //setup board, topside
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 || i == 2) {
                    if (j % 2 == 0) {
                        CheckerBoard[i][j] = 0;
                    } else {
                        CheckerBoard[i][j] = 2;
                    }
                } else {
                    if (j % 2 == 0) {
                        CheckerBoard[i][j] = 2;
                    } else {
                        CheckerBoard[i][j] = 0;
                    }
                }
            }
        }

        //board setup, otherside
        for (int k = 5; k < 8; k++) {
            for (int m = 0; m < 7; m++) {
                if (k == 6) {
                    if (m % 2 == 0) {
                        CheckerBoard[k][m] = 0;
                    } else {
                        CheckerBoard[k][m] = 1;
                    }
                } else {
                    if (m % 2 == 0) {
                        CheckerBoard[k][m] = 1;
                    } else {
                        CheckerBoard[k][m] = 0;
                    }
                }
            }
        }
        CheckerBoard[0][7] = 2; //fix for a weird bug
        CheckerBoard[2][7] = 2;
        CheckerBoard[6][7] = 1;

        //determine whose turn it is
        boolean redTurn = false;
        boolean blackTurn = true;
        boolean cheatModeBlack = false;
        boolean cheatModeRed = false;

        //checkers managing
        int xSel; //x-coord of selected piece
        int ySel; //y-coord of selected piece
        int xDest; //x-coord of destination
        int yDest; //y-coord of destination
        int misclick; //used in case of misinput (prevent game softlocks)

        //log number of pieces captured
        int blackCaptured = 0;
        int redCaptured = 0;

        //manage getting multiple turns
        boolean multiTurn = true;

        boolean blackWon = false;
        boolean redWon = false;
        Scanner coordIn = new Scanner(System.in);
        while(true) {
            if (blackTurn) {
                printBoard(CheckerBoard);
                System.out.println("It is " + ANSI_BLACK + "BLACK's" + ANSI_RESET + " turn");
                while (true) { //piece select loop
                    //get coordinates of desired piece
                    System.out.println("Input the row number of the piece you would like to move");
                    xSel = coordIn.nextInt();
                    System.out.println("Input the column number of the piece you would like to move");
                    ySel = coordIn.nextInt();

                    //check if coords are invalid
                    if (xSel == 69 && ySel == 420) {
                        System.out.println("Cheat mode toggled");
                        if(cheatModeBlack){
                            cheatModeBlack = false;
                        } else {
                            cheatModeBlack = true;
                        }
                    } else if (CheckerBoard[xSel][ySel] == 2 || CheckerBoard[xSel][ySel] == 4) {
                        System.out.println("You cannot move red's pieces");
                    } else if (CheckerBoard[xSel][ySel] == 0) {
                        System.out.println("You cannot move an empty space");
                    } else {
                        break;
                    }
                }
                while (true) { // destination select loop
                    if (ySel <= 1) {
                        if (
                            (CheckerBoard[xSel - 1][ySel + 1] == 2 || CheckerBoard[xSel - 1][ySel + 1] == 4) //makes sure a piece is jumpable
                                &&
                            (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeBlack)) //makes sure destination is open OR cheatMode is enabled
                        {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        }
                    } else if (ySel >= 6) {
                        if ((CheckerBoard[xSel - 1][ySel - 1] == 2 || CheckerBoard[xSel - 1][ySel - 1] == 4) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeBlack)){
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        }
                    } else {
                        if ((CheckerBoard[xSel - 1][ySel + 1] == 2 || CheckerBoard[xSel - 1][ySel + 1] == 4) && (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeBlack)) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        } else if ((CheckerBoard[xSel - 1][ySel - 1] == 2 || CheckerBoard[xSel - 1][ySel - 1] == 4) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeBlack)) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        } else if (((CheckerBoard[xSel + 1][ySel + 1] == 2 || CheckerBoard[xSel + 1][ySel + 1] == 4) && (CheckerBoard[xSel + 2][ySel + 2] == 0 || cheatModeBlack)) && CheckerBoard[xSel][ySel] == 3) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel + 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        } else if (((CheckerBoard[xSel + 1][ySel - 1] == 2 || CheckerBoard[xSel + 1][ySel - 1] == 4 ) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeBlack)) && CheckerBoard[xSel][ySel] == 3) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel + 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            blackCaptured++;
                            break;
                        }
                    }
                    System.out.println("Input the row number of the spot you would like to move to");
                    xDest = coordIn.nextInt();
                    System.out.println("Input the column number of the spot you would like to move to");
                    yDest = coordIn.nextInt();

                    if((CheckerBoard[xSel][ySel] == 3 && //if the piece is a king AND
                            ((xDest == xSel - 1 || xDest == xSel + 1) && //destination x-coord is +/- 1 of the piece's x-coord AND
                            (yDest == ySel + 1 || yDest == ySel - 1))) //destination y-coord is +/- 1 of the piece's y-coord
                                    || // OR
                            (xDest == xSel - 1 && //destination x-coord is -1 of piece's x-coord (piece is moving towards the other side) AND
                            (yDest == ySel + 1 || yDest == ySel - 1)) //destination y-coord is +/- 1 of piece's y-coord
                                    || // OR
                            cheatModeBlack) //cheatMode is enabled (skip destination checks)
                    {
                        if (CheckerBoard[xDest][yDest] == 0) {
                            CheckerBoard[xDest][yDest] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel][ySel] = 0;
                            if (xDest == 0) {
                                System.out.println("Black piece at " + xDest + "," + yDest + " is now a king, and can move any direction");
                                CheckerBoard[xDest][yDest] = 3;
                            }
                            break;
                        } else {
                            System.out.println("The destination coordinates must be empty");
                            System.out.println("Select new piece? '1' for yes, '0' for no");
                            misclick = coordIn.nextInt();
                            if (misclick == 1) {
                                System.out.println("Input the row number of the piece you would like to move");
                                xSel = coordIn.nextInt();
                                System.out.println("Input the column number of the piece you would like to move");
                                ySel = coordIn.nextInt();

                                if (xSel == 69 && ySel == 420) {
                                    System.out.println("Cheat mode activated");
                                    cheatModeBlack = true;
                                } else if (CheckerBoard[xSel][ySel] == 2 || CheckerBoard[xSel][ySel] == 4) {
                                    System.out.println("You cannot move red's pieces");
                                } else if (CheckerBoard[xSel][ySel] == 0) {
                                    System.out.println("You cannot move an empty space");
                                }
                            }
                        }
                    } else {
                        System.out.println("Checkers pieces cannot teleport!");
                    }
                }
                blackTurn = false;
                redTurn = true;

                if(blackWon(CheckerBoard)){
                    blackWon = true;
                }
                if(redWon(CheckerBoard)){
                    redWon = true;
                }
                if(blackWon || redWon){ //if game is won, break loop
                    break;
                }
            } else if (redTurn) {
                printBoard(CheckerBoard);
                while (true) { //piece select loop
                    System.out.println("It is RED's turn");
                    System.out.println("Input the row number of the piece you would like to move");
                    xSel = coordIn.nextInt();
                    System.out.println("Input the column number of the piece you would like to move");
                    ySel = coordIn.nextInt();

                    if (xSel == 420 && ySel == 69) {
                        System.out.println("Cheat mode toggled");
                        if(cheatModeRed){
                            cheatModeRed = false;
                        } else {
                            cheatModeRed = true;
                        }
                    } else if (CheckerBoard[xSel][ySel] == 1 || CheckerBoard[xSel][ySel] == 3) {
                        System.out.println("You cannot move black's pieces");
                    } else if (CheckerBoard[xSel][ySel] == 0) {
                        System.out.println("You cannot move an empty space");
                    } else {
                        break;
                    }
                }
                while (true) { // destination select loop
                    if (ySel <= 1) {
                        if ((CheckerBoard[xSel + 1][ySel + 1] == 1 || CheckerBoard[xSel + 1][ySel + 1] == 3) && CheckerBoard[xSel + 2][ySel + 2] == 0) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel + 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        }
                    } else if (ySel >= 6) {
                        if((CheckerBoard[xSel - 1][ySel - 1] == 1 || CheckerBoard[xSel - 1][ySel - 1] == 3) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeRed) && CheckerBoard[xSel][ySel] == 3){
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        }
                    } else {
                        if ((CheckerBoard[xSel + 1][ySel + 1] == 1 || CheckerBoard[xSel + 1][ySel + 1] == 3) && (CheckerBoard[xSel + 2][ySel + 2] == 0 || cheatModeRed)) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel + 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        } else if ((CheckerBoard[xSel + 1][ySel - 1] == 1 || CheckerBoard[xSel + 1][ySel - 1] == 3) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeRed)) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel + 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        } else if (((CheckerBoard[xSel - 1][ySel + 1] == 1 || CheckerBoard[xSel - 1][ySel + 1] == 3) && (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeRed)) && CheckerBoard[xSel][ySel] == 3) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel + 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        } else if (((CheckerBoard[xSel - 1][ySel - 1] == 1 || CheckerBoard[xSel - 1][ySel - 1] == 3) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeRed)) && CheckerBoard[xSel][ySel] == 3) {
                            System.out.println("You must make a forced jump");
                            CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel - 1][ySel - 1] = 0;
                            CheckerBoard[xSel][ySel] = 0;
                            redCaptured++;
                            break;
                        }
                    }
                    System.out.println("Input the row number of the spot you would like to move to");
                    xDest = coordIn.nextInt();
                    System.out.println("Input the column number of the spot you would like to move to");
                    yDest = coordIn.nextInt();
                    if((CheckerBoard[xSel][ySel] == 4 && //if the piece is a king AND
                            ((xDest == xSel - 1 || xDest == xSel + 1) && //destination x-coord is +/- 1 of the piece's x-coord AND
                                    (yDest == ySel + 1 || yDest == ySel - 1))) //destination y-coord is +/- 1 of the piece's y-coord
                            || // OR
                            (xDest == xSel + 1 && //destination x-coord is +1 of piece's x-coord (piece is moving towards the other side) AND
                                    (yDest == ySel + 1 || yDest == ySel - 1)) //destination y-coord is +/- 1 of piece's y-coord
                            || // OR
                            cheatModeRed) //cheatMode is enabled (skip destination checks)
                    {
                        if (CheckerBoard[xDest][yDest] == 0) {
                            CheckerBoard[xDest][yDest] = CheckerBoard[xSel][ySel];
                            CheckerBoard[xSel][ySel] = 0;
                            if(xDest == 7){
                                System.out.println("Red piece at " + xDest + "," + yDest + " is now a king, and can move any direction");
                                CheckerBoard[xDest][yDest] = 4;
                            }
                            break;
                        } else {
                            System.out.println("The destination coordinates must be empty");
                            System.out.println("Select new piece? '1' for yes, '0' for no");
                            misclick = coordIn.nextInt();
                            if (misclick == 1) {
                                System.out.println("Input the row number of the piece you would like to move");
                                xSel = coordIn.nextInt();
                                System.out.println("Input the column number of the piece you would like to move");
                                ySel = coordIn.nextInt();

                                if (xSel == 420 && ySel == 69) {
                                    System.out.println("Cheat mode activated");
                                    cheatModeRed = true;
                                } else if (CheckerBoard[xSel][ySel] == 1 || CheckerBoard[xSel][ySel] == 3) {
                                    System.out.println("You cannot move black's pieces");
                                } else if (CheckerBoard[xSel][ySel] == 0) {
                                    System.out.println("You cannot move an empty space");
                                }
                            }
                        }
                    } else {
                        System.out.println("Checkers pieces cannot teleport!");
                    }
                }
                redTurn = false;
                blackTurn = true;
            }
            if(blackWon(CheckerBoard)){
                blackWon = true;
            }
            if(redWon(CheckerBoard)){
                redWon = true;
            }
            if(blackWon || redWon){ //if game is won, break loop
                break;
            }
        }
        if(blackWon){
            System.out.println("Black has won the game");
        } else if(redWon){
            System.out.println("Red has won the game");
        }
        if(blackCaptured == 1){
            System.out.println("Black captured 1 piece");
        } else {
            System.out.println("Black captured " + blackCaptured + " pieces");
        }
        if(redCaptured == 1){
            System.out.println("Red captured 1 piece");
        } else {
            System.out.println("Red captured " + redCaptured + " pieces");
        }
    }
    public static boolean redWon(int [][] CheckerBoard){
        boolean blackExists = false;
        for (int i = 0; i < CheckerBoard.length; i++) {
            for (int j = 0; j < CheckerBoard[i].length; j++) {
                if(CheckerBoard[i][j] == 1 || CheckerBoard[i][j] == 3) {
                    blackExists = true;
                    break;
                }
            }
            System.out.println();
        }
        if(!blackExists){
            return true;
        }
        return false;
    }
    public static boolean blackWon(int [][] CheckerBoard){
        boolean redExists = false;
        for (int i = 0; i < CheckerBoard.length; i++) {
            for (int j = 0; j < CheckerBoard[i].length; j++) {
                if(CheckerBoard[i][j] == 2 || CheckerBoard[i][j] == 4) {
                    redExists = true;
                    break;
                }
            }
            System.out.println();
        }
        if(!redExists){
            return true;
        }
        return false;
    }
    static void printBoard(int[][] CheckerBoard){

        System.out.println("Current board state:");
        System.out.print("x ");
        System.out.print((char)27 + "[4m| 0 1 2 3 4 5 6 7"); //underlines text
        System.out.println((char)27 + "[0m"); //resets text formatting so the rest of the board is not underlined

        System.out.print("0 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[0][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[0][i] + ANSI_RESET);
            } else if(CheckerBoard[0][i] == 1 || CheckerBoard[0][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[0][i] + ANSI_RESET);
            } else if(CheckerBoard[0][i] == 2 || CheckerBoard[0][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[0][i] + ANSI_RESET);
            }
            //there's definitely better ways to do this
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("1 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[1][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[1][i] + ANSI_RESET);
            } else if(CheckerBoard[1][i] == 1 || CheckerBoard[1][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[1][i] + ANSI_RESET);
            } else if(CheckerBoard[1][i] == 2 || CheckerBoard[1][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[1][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("2 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[2][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[2][i] + ANSI_RESET);
            } else if(CheckerBoard[2][i] == 1 || CheckerBoard[2][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[2][i] + ANSI_RESET);
            } else if(CheckerBoard[2][i] == 2 || CheckerBoard[2][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[2][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("3 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[3][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[3][i] + ANSI_RESET);
            } else if(CheckerBoard[3][i] == 1 || CheckerBoard[3][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[3][i] + ANSI_RESET);
            } else if(CheckerBoard[3][i] == 2 || CheckerBoard[3][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[3][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("4 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[4][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[4][i] + ANSI_RESET);
            } else if(CheckerBoard[4][i] == 1 || CheckerBoard[4][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[4][i] + ANSI_RESET);
            } else if(CheckerBoard[4][i] == 2 || CheckerBoard[4][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[4][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("5 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[5][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[5][i] + ANSI_RESET);
            } else if(CheckerBoard[5][i] == 1 || CheckerBoard[5][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[5][i] + ANSI_RESET);
            } else if(CheckerBoard[5][i] == 2 || CheckerBoard[5][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[5][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("6 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[6][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[6][i] + ANSI_RESET);
            } else if(CheckerBoard[6][i] == 1 || CheckerBoard[6][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[6][i] + ANSI_RESET);
            } else if(CheckerBoard[6][i] == 2 || CheckerBoard[6][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[6][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("7 | ");
        for(int i=0; i<8; i++){
            if(CheckerBoard[7][i] == 0){
                System.out.print(ANSI_WHITE + CheckerBoard[7][i] + ANSI_RESET);
            } else if(CheckerBoard[7][i] == 1 || CheckerBoard[7][i] == 3){
                System.out.print(ANSI_BLACK + CheckerBoard[7][i] + ANSI_RESET);
            } else if(CheckerBoard[7][i] == 2 || CheckerBoard[7][i] == 4){
                System.out.print(ANSI_RED + CheckerBoard[7][i] + ANSI_RESET);
            }
            System.out.print(" ");
        }
        System.out.println();
    }
}
