import java.util.Scanner;

public class Main {

    //setup for colored board
    public static final String ANSI_RESET = "\u001B[0m";
    public static String ANSI_RED = "\u001B[31m";
    public static String ANSI_REDBG = "\u001B[41m";
    public static String ANSI_WHITE = "\u001B[37m";
    public static String ANSI_BLACK = "\u001B[30m";
    public static String ANSI_BLACKBG = "\u001B[40m";
    public static String ANSI_BLUE = "\u001B[34m";
    public static String ANSI_BLUEBG = "\u001B[44m";

    //the int[][] array that stores checker positions
    public static int[][] CheckerBoard = new int[8][8]; //makes board available to GUI class as well as Main class

    //x coordinates for moving pieces
    public static int xSel = 0; //x-coord of selected piece. Set to 0 by default due to multiturn logic
    public static int ySel = 0; //y-coord of selected piece
    public static int xDest = 0; //x-coord of destination (only used if not jumping a piece)
    public static int yDest = 0; //y-coord of destination (only used if not jumping a piece)

    //determine whose turn it is
    public static boolean blackTurn = true;
    public static boolean redTurn = false;

    //log number of pieces captured
    public static int blackCaptured = 0;
    public static int redCaptured = 0;

    public static void main(String[] args) throws NumberFormatException {

        //credits
        System.out.println("Checkers, written in Java, made by WhitetailAni");
        System.out.println("Copyright (c) 2023 WhitetailAni under the MIT license");


        //explain what each piece is
        System.out.println(ANSI_WHITE + "0 = empty space" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "1 = black piece" + ANSI_RESET);
        System.out.println(ANSI_RED + "2 = red piece" + ANSI_RESET);
        System.out.println(ANSI_BLACKBG + ANSI_BLUE + "3 = black king" + ANSI_RESET);
        System.out.println(ANSI_REDBG + ANSI_BLACK + "4 = red king" + ANSI_RESET);

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

        //debug stuff
        boolean cheatModeBlack = false;
        boolean cheatModeRed = false;

        //checkers managing
        int misclick = 0; //used in case of misinput (prevent game softlocks)
        boolean multiturn; //used to determine if the player can take another turn or not (only used if a piece was jumped)
        boolean errorCheck = true;

        Scanner coordIn = new Scanner(System.in); //this Scanner handles all input during the game
        while(true) {
            String intCoordIn;
            if (blackTurn) {
                errorCheck = false;
                multiturn = false;
                printBoard(CheckerBoard, false);
                System.out.println("It is " + ANSI_BLUE + "BLACK's" + ANSI_RESET + " turn");
                masterBlack:
                while (true) { //masterBlack
                    pieceSelectBlack:
                    while (true) { //pieceSelectBlack
                        //get coordinates of desired piece
                        if(multiturn) {
                            if (xSel == 0 && CheckerBoard[xSel][ySel] != 3) {
                                System.out.println("Black piece at " + xSel + "," + ySel + " is now a king, and can move any direction");
                                CheckerBoard[xSel][ySel] = 3;
                            }
                            break pieceSelectBlack;
                        } else {
                            System.out.println("Input the coordinates of the piece you would like to move, format 'x,y'");
                            intCoordIn = coordIn.nextLine();
                            try{
                                xSel = Integer.parseInt(intCoordIn.substring(0, 1));
                                ySel = Integer.parseInt(intCoordIn.substring(2, 3));
                                //check if coords are invalid
                                if (xSel == 6 && ySel == 9) {
                                    System.out.println("Cheat mode toggled");
                                    cheatModeBlack = !cheatModeBlack;
                                } else if (CheckerBoard[xSel][ySel] == 2 || CheckerBoard[xSel][ySel] == 4) {
                                    System.out.println("You cannot move RED's pieces");
                                } else if (CheckerBoard[xSel][ySel] == 0) {
                                    System.out.println("You cannot move an empty space");
                                } else {
                                    break pieceSelectBlack;
                                }
                            }
                            catch(NumberFormatException | StringIndexOutOfBoundsException e){
                                System.out.println("You can only input coordinates in the format 'x,y'!");
                            }
                        }
                    }
                    pieceMoveBlack:
                    while (true) { //pieceMoveBlack, destination select and jump loop
                        if (ySel <= 1) {
                            if (!(xSel <= 1) && (CheckerBoard[xSel - 1][ySel + 1] == 2 || CheckerBoard[xSel - 1][ySel + 1] == 4) //there is a jumpable piece that is RED's
                                    && //AND
                                    (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeBlack)) //the destination if the piece is jumped is open
                            {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel + 2;
                                break pieceMoveBlack; //labels aren't necessary, but they make the code easier to read
                            }
                            if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel + 1] == 2 || CheckerBoard[xSel + 1][ySel + 1] == 4) && (CheckerBoard[xSel + 2][ySel + 2] == 0 || cheatModeBlack)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel + 2;
                                break;
                            }
                        } else if (ySel >= 6) {
                            if (!(xSel <= 1) && (CheckerBoard[xSel - 1][ySel - 1] == 2 || CheckerBoard[xSel - 1][ySel - 1] == 4) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeBlack)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0; //moves piece
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel - 2;
                                break pieceMoveBlack;
                            }
                            if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel - 1] == 2 || CheckerBoard[xSel + 1][ySel - 1] == 4) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeBlack)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0; //moves piece
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel - 2;
                                break pieceMoveBlack;
                            }
                        } else {
                            if (!(xSel <= 1) && (CheckerBoard[xSel - 1][ySel + 1] == 2 || CheckerBoard[xSel - 1][ySel + 1] == 4) && (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeBlack)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel + 2;
                                break pieceMoveBlack;

                            } else if (!(xSel <= 1) && (CheckerBoard[xSel - 1][ySel - 1] == 2 || CheckerBoard[xSel - 1][ySel - 1] == 4) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeBlack)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel - 2;
                                break pieceMoveBlack;
                            } else if ((CheckerBoard[xSel][ySel] == 3 && !(xSel >= 6 || xSel <= 1)) //piece is a king AND xSel <= 5 (prevents out of bounds errors)
                                    //this statement is placed first to abuse java logic rules, if the first condition isn't met it skips checking the rest, thereby avoiding an out of bounds error.
                                    && // AND
                                    ((CheckerBoard[xSel + 1][ySel + 1] == 2 || CheckerBoard[xSel + 1][ySel + 1] == 4) //adjacent piece is red's (can be jumped)
                                            &&  //AND
                                            (CheckerBoard[xSel + 2][ySel + 2] == 0 || cheatModeBlack)))  //destination spot is open OR cheatMode is enabled
                            {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel + 2;
                                break pieceMoveBlack;
                            } else if ((CheckerBoard[xSel][ySel] == 3 && !(xSel >= 6)) && ((CheckerBoard[xSel + 1][ySel - 1] == 2 || CheckerBoard[xSel + 1][ySel - 1] == 4) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeBlack))) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                blackCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel - 2;
                                break pieceMoveBlack;
                            }
                        }
                        if(multiturn){
                            break masterBlack;
                        }
                        System.out.println("Input the coordinates of the spot you would like to move to, format 'x,y'");
                        intCoordIn = coordIn.nextLine();
                        try {
                            xDest = Integer.parseInt(intCoordIn.substring(0, 1));
                            yDest = Integer.parseInt(intCoordIn.substring(2, 3));
                        }
                        catch(NumberFormatException | StringIndexOutOfBoundsException e){
                            System.out.println("You can only input coordinates in the format 'x,y'!");
                        }

                        if (((((CheckerBoard[xSel][ySel] == 3 && //if the piece is a king AND
                                ((xDest == xSel - 1 || xDest == xSel + 1) //destination x-coord is +/- 1 of the piece's x-coord
                                        && // AND
                                        (yDest == ySel + 1 || yDest == ySel - 1))) //destination y-coord is +/- 1 of the piece's y-coord
                                || // OR
                                (xDest == xSel - 1 //destination x-coord is -1 of piece's x-coord (piece is moving towards the other side)
                                        && // AND
                                        (yDest == ySel + 1 || yDest == ySel - 1)))) //destination y-coord is +/- 1 of piece's y-coord
                                && // AND
                                CheckerBoard[xDest][yDest] == 0) //destination is empty
                                || // OR
                                cheatModeBlack) //cheatMode is enabled (skip destination checks)
                        { //this MASSIVE if statement makes sure the destination is valid.
                            //split into multiple lines for readability
                            //have fun!
                            CheckerBoard[xDest][yDest] = CheckerBoard[xSel][ySel];
                            if (xSel != xDest && ySel != yDest) {
                                CheckerBoard[xSel][ySel] = 0;
                            }
                            if (xDest == 0 && CheckerBoard[xDest][yDest] != 3) {
                                System.out.println("Black piece at " + xDest + "," + yDest + " is now a king, and can move any direction");
                                CheckerBoard[xDest][yDest] = 3;
                            }
                            break pieceMoveBlack;
                        } else {
                            try {
                                System.out.println("Invalid movement");
                                System.out.println("Select new piece? '1' for yes, '0' for no");
                                misclick = Integer.parseInt(coordIn.nextLine().substring(0, 1));
                                if (misclick == 1) {
                                    errorCheck = true;
                                    break pieceMoveBlack;
                                }
                            }
                            catch(NumberFormatException | StringIndexOutOfBoundsException e){
                                System.out.println("You can only input coordinates in the format 'x,y'!");
                                //actually, the character in between the first and second numbers can be whatever you want
                                //dont tell anyone
                            }
                        }
                    }
                    System.out.println(errorCheck);
                    if (!multiturn && !errorCheck) {
                        break masterBlack;
                    }
                }
                blackTurn = false;
                redTurn = true;

                if(blackWon(CheckerBoard) || redWon(CheckerBoard)){
                    break;
                }
            } else if (redTurn) {
                errorCheck = false;
                multiturn = false;
                printBoard(CheckerBoard, false);
                System.out.println("It is " + ANSI_RED + "RED's" + ANSI_RESET + " turn");
                masterRed:
                while (true) {
                    pieceSelectRed:
                    while (true) { //piece select loop
                        if(multiturn) {
                            if (xSel == 7 && CheckerBoard[xSel][ySel] != 4) {
                                System.out.println("Red piece at " + xSel + "," + ySel + " is now a king, and can move any direction");
                                CheckerBoard[xSel][ySel] = 4;
                            }
                            break pieceSelectRed;
                        } else {
                            System.out.println("Input the coordinates of the piece you would like to move, format 'x,y'");
                            intCoordIn = coordIn.nextLine();
                            try {
                                xSel = Integer.parseInt(intCoordIn.substring(0, 1));
                                ySel = Integer.parseInt(intCoordIn.substring(2, 3));

                                if (xSel == 420 && ySel == 69) {
                                    System.out.println("Cheat mode toggled");
                                    cheatModeRed = !cheatModeRed;
                                } else if (CheckerBoard[xSel][ySel] == 1 || CheckerBoard[xSel][ySel] == 3) {
                                    System.out.println("You cannot move BLACK's pieces");
                                } else if (CheckerBoard[xSel][ySel] == 0) {
                                    System.out.println("You cannot move an empty space");
                                } else {
                                    break pieceSelectRed;
                                }
                            }
                            catch(NumberFormatException | StringIndexOutOfBoundsException e){
                                System.out.println("You can only input coordinates in the format 'x,y'!");
                            }
                        }
                    }
                    pieceMoveRed:
                    while (true) { // destination select loop
                        if (ySel <= 1) {
                            if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel + 1] == 1 || CheckerBoard[xSel + 1][ySel + 1] == 3) && CheckerBoard[xSel + 2][ySel + 2] == 0) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel + 2;
                                break pieceMoveRed;
                            } else if ((xSel > 1) && ((CheckerBoard[xSel - 1][ySel + 1] == 1 || CheckerBoard[xSel - 1][ySel + 1] == 3) && CheckerBoard[xSel - 2][ySel + 2] == 0) && CheckerBoard[xSel][ySel] == 4) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel + 2;
                                break pieceMoveRed;
                            }
                        } else if (ySel >= 6) {
                            if (CheckerBoard[xSel][ySel] == 4 && (xSel >= 1 && (CheckerBoard[xSel - 1][ySel - 1] == 1 || CheckerBoard[xSel - 1][ySel - 1] == 3) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeRed))) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel - 2;
                                break pieceMoveRed;
                            }
                            if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel - 1] == 1 || CheckerBoard[xSel + 1][ySel - 1] == 3) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeRed)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel - 2;
                                break pieceMoveRed;
                            }
                        } else {
                            if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel + 1] == 1 || CheckerBoard[xSel + 1][ySel + 1] == 3) && (CheckerBoard[xSel + 2][ySel + 2] == 0 || cheatModeRed)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel + 2;
                                break pieceMoveRed;
                            } else if (!(xSel >= 6) && (CheckerBoard[xSel + 1][ySel - 1] == 1 || CheckerBoard[xSel + 1][ySel - 1] == 3) && (CheckerBoard[xSel + 2][ySel - 2] == 0 || cheatModeRed)) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel + 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel + 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel + 2;
                                ySel = ySel - 2;
                                break pieceMoveRed;
                            } else if (!(xSel >= 6) && (CheckerBoard[xSel][ySel] == 4) && ((CheckerBoard[xSel - 1][ySel + 1] == 1 || CheckerBoard[xSel - 1][ySel + 1] == 3) && (CheckerBoard[xSel - 2][ySel + 2] == 0 || cheatModeRed))) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel + 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel + 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel + 2;
                                break pieceMoveRed;
                            } else if ((CheckerBoard[xSel][ySel] == 4 && !(xSel <= 1)) && ((CheckerBoard[xSel - 1][ySel - 1] == 1 || CheckerBoard[xSel - 1][ySel - 1] == 3) && (CheckerBoard[xSel - 2][ySel - 2] == 0 || cheatModeRed))) {
                                System.out.println("You must make a forced jump");
                                CheckerBoard[xSel - 2][ySel - 2] = CheckerBoard[xSel][ySel];
                                CheckerBoard[xSel - 1][ySel - 1] = 0;
                                CheckerBoard[xSel][ySel] = 0;
                                redCaptured++;
                                multiturn = true;
                                xSel = xSel - 2;
                                ySel = ySel - 2;
                                break pieceMoveRed;
                            }
                        }
                        if (multiturn) {
                            break masterRed;
                        }
                        System.out.println("Input the coordinates of the spot you would like to move to, format 'x,y'");
                        intCoordIn = coordIn.nextLine();
                        try {
                            xDest = Integer.parseInt(intCoordIn.substring(0, 1));
                            yDest = Integer.parseInt(intCoordIn.substring(2, 3));
                        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                            System.out.println("You can only input coordinates in the format 'x,y'!");
                        }
                        if ((((CheckerBoard[xSel][ySel] == 4 && //if the piece is a king AND
                                ((xDest == xSel - 1 || xDest == xSel + 1) //destination x-coord is +/- 1 of the piece's x-coord
                                        && //AND
                                        (yDest == ySel + 1 || yDest == ySel - 1))) //destination y-coord is +/- 1 of the piece's y-coord
                                || // OR
                                (xDest == xSel + 1 //destination x-coord is +1 of piece's x-coord (piece is moving towards the other side)
                                        && //AND
                                        (yDest == ySel + 1 || yDest == ySel - 1))) //destination y-coord is +/- 1 of piece's y-coord
                                && // AND
                                CheckerBoard[xDest][yDest] == 0) //destination is empty
                                || // OR
                                cheatModeRed) //cheatMode is enabled (skip destination checks)
                        {
                            CheckerBoard[xDest][yDest] = CheckerBoard[xSel][ySel];
                            if (xSel != xDest && ySel != yDest) {
                                CheckerBoard[xSel][ySel] = 0;
                            }
                            if (xDest == 7) {
                                System.out.println("Red piece at " + xDest + "," + yDest + " is now a king, and can move any direction");
                                CheckerBoard[xDest][yDest] = 4;
                            }
                            break pieceMoveRed;
                        } else {
                            try {
                                System.out.println("The destination coordinates must be empty");
                                System.out.println("Select new piece? '1' for yes, '0' for no");
                                misclick = Integer.parseInt(coordIn.nextLine().substring(0, 1));
                                if (misclick == 1) {
                                    errorCheck = true;
                                    break pieceMoveRed;
                                }
                            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                                System.out.println("You can only input coordinates in the format 'x,y'!");
                            }
                        }
                    }
                    if (!multiturn && !errorCheck) {
                        break masterRed;
                    }
                }
                redTurn = false;
                blackTurn = true;
            }
            if(blackWon(CheckerBoard) || redWon(CheckerBoard)){ //if game is won, break loop
                break;
            }
        }
        if(blackWon(CheckerBoard)){
            System.out.println("Black has won the game");
        } else if(redWon(CheckerBoard)){
            System.out.println("Red has won the game");
        }
        printBoard(CheckerBoard, true);
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
        return !blackExists;
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
        return !redExists;
    }
    static void printBoard(int[][] CheckerBoard, boolean endgame){

        if(endgame){
            System.out.println("Final board state:");
        } else {
            System.out.println("Current board state:");
        } //leftover from CLI but handy if viewing debug log
        System.out.print("x ");
        System.out.print((char)27 + "[4m| 0 1 2 3 4 5 6 7"); //underlines text
        System.out.println((char)27 + "[0m"); //resets text formatting so the rest of the board is not underlined
        int newline = 0;

        for (int i = 0; i<8; i++) { //prints out checkerboard. now uses nested for loop instead of eight single for loops
            System.out.print(newline + " | ");
            for (int j = 0; j<8; j++) {
                if(CheckerBoard[i][j] == 0){
                    System.out.print(ANSI_WHITE + CheckerBoard[i][j] + ANSI_RESET + " ");
                } else if(CheckerBoard[i][j] == 1){
                    System.out.print(ANSI_BLUE + CheckerBoard[i][j] + ANSI_RESET + " ");
                } else if(CheckerBoard[i][j] == 2){
                    System.out.print(ANSI_RED + CheckerBoard[i][j] + ANSI_RESET + " ");
                } else if(CheckerBoard[i][j] == 3){
                    System.out.print(ANSI_BLACKBG + ANSI_BLUE + CheckerBoard[i][j] + ANSI_RESET + " ");
                } else if(CheckerBoard[i][j] == 4){
                    System.out.print(ANSI_REDBG + ANSI_BLACK + CheckerBoard[i][j] + ANSI_RESET + " ");
                }
            }
            newline++;
            System.out.println();
        }
    }
}
