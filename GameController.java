package controller;

import components.ChessGridComponent;
import model.ChessPiece;
import view.ChessBoardPanel;
import view.GameFrame;
import view.StatusPanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Timer;
import java.util.*;


public class GameController {


    private ChessBoardPanel gamePanel;
    private StatusPanel statusPanel;
    private ChessPiece currentPlayer;
    private int blackScore;
    private int whiteScore;
    private boolean blackHasMoves;
    private boolean whiteHasMoves;
    private boolean cheatMode;
    private List<List<Integer>> previousMoves;
    private GameFrame gameFrame;
    private int opponentLevel;
    private boolean isGameOver;

    public GameController(ChessBoardPanel gamePanel, StatusPanel statusPanel,
                          GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.gamePanel = gamePanel;
        this.statusPanel = statusPanel;
        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
        blackHasMoves = true;
        whiteHasMoves = true;
        previousMoves = new ArrayList<>();
    }

    public void swapPlayer() {
        if (isGameOver) {
            return;
        }
        countScore();
        currentPlayer = (currentPlayer == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
        statusPanel.setPlayerText(currentPlayer.name());
        statusPanel.setScoreText(blackScore, whiteScore);
        if (opponentLevel > 0 && currentPlayer == ChessPiece.WHITE) {
            mAIClick();
        }
        if (opponentLevel > 0 && currentPlayer == ChessPiece.BLACK
                && gamePanel.checkNoMoves(currentPlayer)) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    swapPlayer();
                }
            }, 10);
        }
        checkWinner();
    }

    public void countScore() {
        //todo: modify the countScore method
        int bs = 0, ws=0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(gamePanel.getChessGrids()[i][j].getChessPiece()==ChessPiece.BLACK){
                    bs++;
                }if(gamePanel.getChessGrids()[i][j].getChessPiece()==ChessPiece.WHITE){
                    ws++;
                }
            }
        }
        blackScore=bs;
        whiteScore=ws;
//        if (currentPlayer.getIncrement() > 0) {
//            if (currentPlayer == ChessPiece.BLACK) {
//                blackScore += currentPlayer.getIncrement();
//                whiteScore -= (currentPlayer.getIncrement() - 1);
//            } else {
//                whiteScore += currentPlayer.getIncrement();
//                blackScore -= (currentPlayer.getIncrement() - 1);
//            }
//            currentPlayer.setIncrement(0);
//        }
    }


    public ChessPiece getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessBoardPanel getGamePanel() {
        return gamePanel;
    }


    public void setGamePanel(ChessBoardPanel gamePanel) {
        this.gamePanel = gamePanel;
        statusPanel.setPlayerText(currentPlayer.name());
    }


    public boolean readFileData(String fileName) {
        //todo: read date from file
        try {
            // 检查棋盘
            if (!fileName.endsWith(".txt")) {
                JOptionPane.showMessageDialog(gamePanel, "error 104, the format isn't txt", "Tips", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            FileReader fileReader = new FileReader(fileName);
            BufferedReader rd = new BufferedReader(fileReader);
            String line;
            // 分数
            int mBlackScore = Integer.parseInt(rd.readLine());
            int mWhiteScore = Integer.parseInt(rd.readLine());
            line = rd.readLine();
            // 行棋方
            ChessPiece mCurrentPlayer;
            if (line.equals("WHITE")) {
                mCurrentPlayer = ChessPiece.WHITE;
            } else if (line.equals("BLACK")) {
                mCurrentPlayer = ChessPiece.BLACK;
            } else {
                rd.close();
                fileReader.close();
                JOptionPane.showMessageDialog(gamePanel, "error 103, missing current player", "Tips", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            // level
            int mOpponentLevel = Integer.parseInt(rd.readLine());
            if (mOpponentLevel < 0 || mOpponentLevel > 2) {
                rd.close();
                fileReader.close();
                JOptionPane.showMessageDialog(gamePanel, "error 106, other error", "Tips", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            line = rd.readLine();
            // normal or cheat mode
            boolean isCheat = false;
            if (line.equals("cheat")) {
                isCheat = true;
            }
            // bool variables
            boolean mIsGameOver = Boolean.parseBoolean(rd.readLine());
            boolean mwhiteHasMoves = Boolean.parseBoolean(rd.readLine());
            boolean mblackHasMoves = Boolean.parseBoolean(rd.readLine());
            // 先前步骤
            int movesNum = Integer.parseInt(rd.readLine());
            List<String> movesStr = new ArrayList<>();
            for (int i = 0; i < movesNum; i++) {
                line = rd.readLine();
                movesStr.add(line);
            }
            if (!checkValidityOfMoves(movesStr)) {
                JOptionPane.showMessageDialog(gamePanel, "error 105, illegal move", "Tips", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            // 棋盘
            List<String> boardStr = new ArrayList<>();
            boolean invalidBoard = false;
            while ((line = rd.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                boardStr.add(line);
                if (line.length() != 8) {
                    invalidBoard = true;
                    break;
                }
            }
            if (invalidBoard || boardStr.size() != 8) {
                rd.close();
                fileReader.close();
                JOptionPane.showMessageDialog(gamePanel, "error 101, the size of the board isn't 8x8", "Tips", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (boardStr.get(i).charAt(j) < '0' ||
                            boardStr.get(i).charAt(j) > '2') {
                        rd.close();
                        fileReader.close();
                        JOptionPane.showMessageDialog(gamePanel, "error 102, containing other kinds of pieces \nexcept for black, white and blank",
                                "Tips", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }
            gameFrame.setWhitePlayerLevel(mOpponentLevel);
            gamePanel.clearPossibleMoves();
            blackScore = mBlackScore;
            whiteScore = mWhiteScore;
            currentPlayer = mCurrentPlayer;
            opponentLevel = mOpponentLevel;
            isGameOver = mIsGameOver;
            whiteHasMoves = mwhiteHasMoves;
            blackHasMoves = mblackHasMoves;
            gamePanel.setBoards(boardStr);
            loadPreviousMoves(movesStr);
            setCheatMode(isCheat);
            gameFrame.setCheatMode(isCheat);
            statusPanel.setScoreText(blackScore, whiteScore);
            statusPanel.setPlayerText(currentPlayer.name());
            isGameOver = false;
            rd.close();
            fileReader.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(gamePanel, "error 106, other error", "Tips", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void addMove(List<Integer> list) {
        previousMoves.add(list);
    }

    public void writeDataToFile(String fileName) {
        //todo: write data into file
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(fileName));
            wr.write(blackScore + "\n");
            wr.write(whiteScore + "\n");
            if (currentPlayer == ChessPiece.WHITE) {
                wr.write("WHITE\n");
            } else {
                wr.write("BLACK\n");
            }
            wr.write(opponentLevel + "\n");
            wr.write(cheatMode ? "cheat\n" : "normal\n");
            wr.write((isGameOver ? 1 : 0) + "\n");
            wr.write((whiteHasMoves ? 1 : 0) + "\n");
            wr.write((blackHasMoves ? 1 : 0) + "\n");
            wr.write(savePreviousMoves());
            wr.write(gamePanel.getBoardString());
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the validity of previous moves
     */
    public boolean checkValidityOfMoves(List<String> movesStr) {
        ChessBoardPanel panel = new ChessBoardPanel(640, 560);
        ChessPiece current = currentPlayer;
        List<String> originalMoveStr = new ArrayList<>();
        if (!previousMoves.isEmpty()) {
            originalMoveStr.addAll(Arrays.asList(savePreviousMoves().split("\n")));
            originalMoveStr.remove(0);
        }
        boolean hasError = false;
        currentPlayer = ChessPiece.BLACK;
        loadPreviousMoves(movesStr);
        for (List<Integer> move : previousMoves) {
            if (move.get(0) == 1) {
                setCheatMode(true);
            } else {
                setCheatMode(false);
            }
            if (move.get(1) == 1) {
                currentPlayer = ChessPiece.WHITE;
            } else {
                currentPlayer = ChessPiece.BLACK;
            }
            panel.possibleMoves(currentPlayer);
            int row = move.get(2);
            int col = move.get(3);

            if (!panel.canClickGrid(row, col, currentPlayer)) {
                hasError = true;
                break;
            } else {
                panel.getChess(row, col).setChessPiece(currentPlayer);
            }
        }
        currentPlayer = current;
        previousMoves.clear();
        if (!originalMoveStr.isEmpty()) {
            loadPreviousMoves(originalMoveStr);
        }
        return !hasError;
    }

    public boolean canClick(int row, int col) {
        boolean ret = gamePanel.canClickGrid(row, col, currentPlayer);
        if (ret) {
            addMove(Arrays.asList(cheatMode ? 1 : 0, currentPlayer
                    == ChessPiece.WHITE ? 1 : 2, row, col));
        } else if (gamePanel.checkNoMoves(currentPlayer)) {
            swapPlayer();
        }
        return ret;
    }

    public void mAIClick() {
        // if the white player is the computer, place a white disk
        if (opponentLevel > 0) {
            currentPlayer = getCurrentPlayer();
            ChessBoardPanel gamePanel = getGamePanel();
            gamePanel.possibleMoves(currentPlayer);
            // check whether the white has no moves
            if (gamePanel.checkNoMoves(currentPlayer)) {
                printMsg("AI(WHITE) has no moves. It's BLACK's turn.");
                setWhiteHasScore(false);
                swapPlayer();
                return;
            }
            List<List<Integer>> moves = gamePanel.getPossibleMoves();
            boolean cheatMode = isCheatMode();
            List<Integer> move;
            if (cheatMode) {
                // choose a random blank grid cell
                move = gamePanel.getRandomBlankGridCell();
                if (move == null) {
                    return;
                }
            } else {
                // find a proper move
                if (opponentLevel == 1) {
                    // choose the move which gets the minimum score
                    int minIndex = 0;
                    int minScore = moves.get(0).get(2);
                    for (int i = 0; i < moves.size(); i++) {
                        List<Integer> mMove = moves.get(i);
                        if (mMove.get(2) < minScore) {
                            minIndex = i;
                            minScore = mMove.get(2);
                        }
                    }
                    move = moves.get(minIndex);
                } else {
                    // choose the move which gets the maximum score
                    int maxIndex = 0;
                    int maxScore = moves.get(0).get(2);
                    for (int i = 0; i < moves.size(); i++) {
                        List<Integer> mMove = moves.get(i);
                        if (mMove.get(2) > maxScore) {
                            maxIndex = i;
                            maxScore = mMove.get(2);
                        }
                    }
                    move = moves.get(maxIndex);
                }
            }
            // place a white disk
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int mRow = move.get(0);
                    int mCol = move.get(1);
                    if (canClick(mRow, mCol)) {
                        printMsg(String.format("AI(%s) clicked (%d, %d)\n", currentPlayer, mRow, mCol));
                        ChessGridComponent chess = gamePanel.getChess(mRow, mCol);
                        if (chess.getChessPiece() == null) {
                            chess.setChessPiece(currentPlayer);
                            swapPlayer();
                        }
                        chess.repaint();
                        setWhiteHasScore(true);
                    }
                }
            },700);
        }
    }

    public boolean isCheatMode() {
        return cheatMode;
    }

    public void setCheatMode(boolean cheatMode) {
        this.cheatMode = cheatMode;
    }

    /**
     * Display possible moves
     */
    public void displayPossibleMoves() {
        // curren player has possible moves
        if (gamePanel.possibleMoves(currentPlayer)) {
            if (currentPlayer.getColor() == Color.WHITE) {
                whiteHasMoves = true;
            } else {
                blackHasMoves = true;
            }
        } else if (gamePanel.checkNoMoves(currentPlayer)) {
            // curren player has no possible moves
            if (currentPlayer.getColor() == Color.WHITE) {
                whiteHasMoves = false;
                checkWinner();
                if (!isGameOver) {
                    GameFrame.controller.printMsg("WHITE has no moves. It's BLACK's turn.");
                    swapPlayer();
                }
            } else if (currentPlayer.getColor() == Color.BLACK) {
                blackHasMoves = false;
                checkWinner();
                if (!isGameOver) {
                    GameFrame.controller.printMsg("BLACK has no moves. It's WHITE's turn.");
                    swapPlayer();
                }
            }
        }
    }

    public void setWhiteHasScore(boolean hasMoves) {
        whiteHasMoves = hasMoves;
    }

    /**
     * Check who won the game
     */
    public void checkWinner() {
        if (!isGameOver && !whiteHasMoves && !blackHasMoves) {
            isGameOver = true;
            statusPanel.setPlayerText(currentPlayer.name());
            statusPanel.setScoreText(blackScore, whiteScore);
            // Game over, check who win the game and show the tips
            if (blackScore > whiteScore) {
                JOptionPane.showMessageDialog(getGamePanel(), "Black player won the game.", "Tips", JOptionPane.WARNING_MESSAGE);
            } else if (whiteScore > blackScore) {
                JOptionPane.showMessageDialog(getGamePanel(), "White player won the game.", "Tips", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(getGamePanel(), "It was a tie.", "Tips", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Undo the operation
     */
    public void undoOperation() {
        int num = gamePanel.undoOperation(currentPlayer);
        if (num > 0) {
            // update the score
            if (currentPlayer == ChessPiece.WHITE) {
                whiteScore += num - 1;
                blackScore -= num;
            } else {
                blackScore += num - 1;
                whiteScore -= num;
            }
            currentPlayer.setIncrement(0);
            previousMoves.remove(previousMoves.size() - 1);
            swapPlayer();
        }
    }

    /**
     * Reset the game
     */
    public void resetGame() {
        gamePanel.resetChessBoard();

        this.currentPlayer = ChessPiece.BLACK;
        blackScore = 2;
        whiteScore = 2;
        statusPanel.setPlayerText(currentPlayer.name());
        statusPanel.setScoreText(blackScore, whiteScore);

        opponentLevel = gameFrame.getWhitePlayerLevel();
        setCheatMode(false);
        gameFrame.setCheatMode(false);
        previousMoves.clear();
        isGameOver = false;
        blackHasMoves = true;
        whiteHasMoves = true;
    }

    /**
     * Convert previous moves to string representation
     *
     * @return string representation
     */
    public String savePreviousMoves() {
        StringBuilder builder = new StringBuilder();
        builder.append(previousMoves.size()).append("\n");
        for (List<Integer> ll : previousMoves) {
            builder.append(String.format("%d,%d,%d,%d\n", ll.get(0),
                    ll.get(1), ll.get(2), ll.get(3)));
        }
        return builder.toString();
    }

    /**
     * Restore previous moves from string representation
     *
     * @param movesStr string representation
     */
    public void loadPreviousMoves(List<String> movesStr) {
        previousMoves.clear();
        for (String line : movesStr) {
            String[] split = line.split(",");
            addMove(Arrays.asList(
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3])
            ));
        }
    }

    public int getWhitePlayerLevel() {
        return opponentLevel;
    }

    public void printMsg(String msg) {
        gameFrame.printLog(msg);
        System.out.println(msg);
    }
}
