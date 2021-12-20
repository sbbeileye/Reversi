package view;

import components.ChessGridComponent;
import model.ChessPiece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChessBoardPanel extends JPanel {
    private final int CHESS_COUNT = 8;
    private ChessGridComponent[][] chessGrids;

    public ChessGridComponent[][] getChessGrids() {
        return chessGrids;
    }

    List<List<Integer>> moves;
    List<List<Integer>> pieces;

    public ChessBoardPanel(int width, int height) {
        this.setVisible(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        int length = Math.min(width, height);
        this.setSize(length, length);
        ChessGridComponent.gridSize = length / CHESS_COUNT;
        ChessGridComponent.chessSize = (int) (ChessGridComponent.gridSize * 0.8);
        ChessGridComponent.dotSize = (int) (ChessGridComponent.chessSize * 0.4);
        moves = new ArrayList<>();
        pieces = new ArrayList<>();
        initialChessGrids();//return empty chessboard
        initialGame();//add initial four chess
        repaint();
    }

    /**
     * set an empty chessboard
     */
    public void initialChessGrids() {
        chessGrids = new ChessGridComponent[CHESS_COUNT][CHESS_COUNT];

        //draw all chess grids
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                ChessGridComponent gridComponent = new ChessGridComponent(i, j);
                gridComponent.setLocation(j * ChessGridComponent.gridSize, i * ChessGridComponent.gridSize);
                chessGrids[i][j] = gridComponent;
                this.add(chessGrids[i][j]);
            }
        }
    }

    /**
     * initial origin four chess
     */
    public void initialGame() {
        chessGrids[3][3].setChessPiece(ChessPiece.BLACK);
        chessGrids[3][4].setChessPiece(ChessPiece.WHITE);
        chessGrids[4][3].setChessPiece(ChessPiece.WHITE);
        chessGrids[4][4].setChessPiece(ChessPiece.BLACK);
        pieces.clear();
        moves.clear();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    public boolean canClickGrid(int row, int col, ChessPiece currentPlayer) {
        //todo: complete this method

        if (chessGrids[row][col].getChessPiece() == ChessPiece.BLACK ||
                chessGrids[row][col].getChessPiece() == ChessPiece.WHITE) {
            return false;
        }

        // return false if the grid has chess piece
        if ((currentPlayer == ChessPiece.BLACK &&
                chessGrids[row][col].getChessPiece() != ChessPiece.LIGHT_BLACK) ||
                (currentPlayer == ChessPiece.WHITE &&
                        chessGrids[row][col].getChessPiece() != ChessPiece.LIGHT_WHITE)) {
            // cheat mode
            if (GameFrame.controller.isCheatMode()) {
                clearPossibleMoves();
                pieces.clear();
                pieces.add(Arrays.asList(row, col));
                currentPlayer.setIncrement(1);
                repaint();
                return true;
            }
            return false;
        }
        clearPossibleMoves();
        pieces.clear();

        // check the validity
        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

        boolean temp = false;
        for (int k = 0, len = dx.length; k < len; k++) {
            int iRow = row + dy[k];
            int iCol = col + dx[k];
            // the position is invalid
            if (iRow < 0 || iRow >= CHESS_COUNT || iCol < 0 || iCol >= CHESS_COUNT) {
                continue;
            }
            // the direction is invalid
            if (chessGrids[iRow][iCol].getChessPiece() == null ||
                    chessGrids[iRow][iCol].getChessPiece().getColor() == currentPlayer.getColor()) {
                continue;
            }
            // find the piece of current player along this direction
            while (iRow >= 0 && iRow < CHESS_COUNT && iCol >= 0 && iCol < CHESS_COUNT &&
                    chessGrids[iRow][iCol].getChessPiece() != null ) {
                if (chessGrids[iRow][iCol].getChessPiece().getColor() == currentPlayer.getColor()) {
                    // the click is valid
                    int mRow = iRow;
                    int mCol = iCol;
                    pieces.add(Arrays.asList(row, col));
                    iRow = row + dy[k];
                    iCol = col + dx[k];
                    int cnt = 1;
                    while (mRow != iRow || mCol != iCol) {
                        cnt++;
                        chessGrids[iRow][iCol].setChessPiece(currentPlayer);
                        if(!pieces.contains(Arrays.asList(iRow,iCol))){
                            pieces.add(Arrays.asList(iRow, iCol));
                        }
                        iRow = iRow + dy[k];
                        iCol = iCol + dx[k];
                    }
                    currentPlayer.setIncrement(cnt);
                    repaint();
                    temp=true;
                    break;
                }
                iRow = iRow + dy[k];
                iCol = iCol + dx[k];
            }
        }
        return temp;
    }

    /**
     * Undo last operation
     *
     * @param currentPlayer current player
     * @return the number of flipped pieces
     */
    public int undoOperation(ChessPiece currentPlayer) {
        if (!pieces.isEmpty()) {
            for (int i = 0; i < pieces.size(); i++) {
                int row = pieces.get(i).get(0);
                int col = pieces.get(i).get(1);
                if (i == 0) {
                    chessGrids[row][col].setChessPiece(null);
                } else {
                    if (currentPlayer == ChessPiece.WHITE) {
                        chessGrids[row][col].setChessPiece(ChessPiece.WHITE);
                    } else {
                        chessGrids[row][col].setChessPiece(ChessPiece.BLACK);
                    }
                }
            }
            int num = pieces.size();
            pieces.clear();
            repaint();
            return num;
        }
        return 0;
    }

    /**
     * Check whether current player has no moves
     *
     * @param currentPlayer current player
     * @return the result
     */
    public boolean checkNoMoves(ChessPiece currentPlayer) {
        boolean cheatMode = GameFrame.controller.isCheatMode();
        if (cheatMode) {
            // find a blank grid cell
            for (int i = 0; i < CHESS_COUNT; i++) {
                for (int j = 0; j < CHESS_COUNT; j++) {
                    ChessPiece piece = chessGrids[i][j].getChessPiece();
                    if (piece != ChessPiece.WHITE && piece != ChessPiece.BLACK) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return moves.isEmpty();
        }
    }

    /**
     * Get possible moves of current player
     *
     * @param currentPlayer current player
     * @return whether current player has possible moves
     */
    public boolean possibleMoves(ChessPiece currentPlayer) {
        clearPossibleMoves();
        for (int row = 0; row < CHESS_COUNT; row++) {
            for (int col = 0; col < CHESS_COUNT; col++) {
                // find possible moves
                if (chessGrids[row][col].getChessPiece() != null) {
                    continue;
                }

                // check the validity
                boolean checked = false;
                int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
                int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

                for (int k = 0, len = dx.length; k < len; k++) {
                    int iRow = row + dy[k];
                    int iCol = col + dx[k];
                    // the position is invalid
                    if (iRow < 0 || iRow >= CHESS_COUNT || iCol < 0 || iCol >= CHESS_COUNT) {
                        continue;
                    }
                    // the direction is invalid
                    if (chessGrids[iRow][iCol].getChessPiece() == null ||
                            chessGrids[iRow][iCol].getChessPiece().getColor() == currentPlayer.getColor()) {
                        continue;
                    }
                    // find the piece of current player along this direction
                    while (iRow >= 0 && iRow < CHESS_COUNT && iCol >= 0 && iCol < CHESS_COUNT &&
                            chessGrids[iRow][iCol].getChessPiece() != null) {
                        if (chessGrids[iRow][iCol].getChessPiece().getColor() == currentPlayer.getColor()) {
                            // the click is valid

                            int mRow = iRow;
                            int mCol = iCol;
                            iRow = row + dy[k];
                            iCol = col + dx[k];
                            int cnt = 1;
                            while (mRow != iRow || mCol != iCol) {
                                cnt++;
                                iRow = iRow + dy[k];
                                iCol = iCol + dx[k];
                            }

                            moves.add(Arrays.asList(row, col, cnt));
                            checked = true;
                            break;
                        }
                        iRow = iRow + dy[k];
                        iCol = iCol + dx[k];
                    }
                    if (checked) {
                        break;
                    }
                }
            }
        }

        // display the possible moves
        for (List<Integer> move : moves) {
            int row = move.get(0);
            int col = move.get(1);
            if (currentPlayer == ChessPiece.WHITE) {
                chessGrids[row][col].setChessPiece(ChessPiece.LIGHT_WHITE);
            } else {
                chessGrids[row][col].setChessPiece(ChessPiece.LIGHT_BLACK);
            }
        }
        return !moves.isEmpty();
    }

    /**
     * Get possible moves
     *
     * @return possible moves
     */
    public List<List<Integer>> getPossibleMoves() {
        return moves;
    }

    /**
     * Get a random blank grid cell
     *
     * @return blank grid cell
     */
    public List<Integer> getRandomBlankGridCell() {
        List<List<Integer>> blankCells = new ArrayList<>();
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                ChessPiece piece = chessGrids[i][j].getChessPiece();
                if (piece != ChessPiece.WHITE && piece != ChessPiece.BLACK) {
                    int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
                    int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
                    for (int k = 0, len = dx.length; k < len; k++) {
                        int iRow = i + dy[k];
                        int iCol = j + dx[k];
                        // the position is invalid
                        if (iRow < 0 || iRow >= CHESS_COUNT || iCol < 0 || iCol >= CHESS_COUNT) {
                            continue;
                        }
                        if (chessGrids[iRow][iCol].getChessPiece() == ChessPiece.BLACK) {
                            blankCells.add(Arrays.asList(i, j));
                            break;
                        }
                    }
                }
            }
        }
        if (blankCells.size() == 0) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(blankCells.size());
        return blankCells.get(index);
    }

    /**
     * Clear possible moves
     */
    public void clearPossibleMoves() {
        if (!moves.isEmpty()) {
            for (List<Integer> move : moves) {
                int row = move.get(0);
                int col = move.get(1);
                chessGrids[row][col].setChessPiece(null);
            }
            moves.clear();
        }
    }

    /**
     * reset the chess board
     */
    public void resetChessBoard() {

        // reset all grids
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                chessGrids[i][j].setChessPiece(null);
            }
        }

        initialGame();//add initial four chess

        repaint();
    }

    /**
     * Convert the board to string representation
     *
     * @return string representation
     */
    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (chessGrids[i][j].getChessPiece() == ChessPiece.WHITE) {
                    sb.append(1);
                } else if (chessGrids[i][j].getChessPiece() == ChessPiece.BLACK) {
                    sb.append(2);
                } else {
                    sb.append(0);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Set the board according to string representation
     *
     * @param boardStr string representation of the board
     */
    public void setBoards(List<String> boardStr) {
        for (int i = 0; i < CHESS_COUNT; i++) {
            for (int j = 0; j < CHESS_COUNT; j++) {
                if (boardStr.get(i).charAt(j) == '0') {
                    chessGrids[i][j].setChessPiece(null);
                } else if (boardStr.get(i).charAt(j) == '1') {
                    chessGrids[i][j].setChessPiece(ChessPiece.WHITE);
                } else if (boardStr.get(i).charAt(j) == '2') {
                    chessGrids[i][j].setChessPiece(ChessPiece.BLACK);
                }
            }
        }
        repaint();
    }

    public ChessGridComponent getChess(int row, int col) {
        return chessGrids[row][col];
    }
}
