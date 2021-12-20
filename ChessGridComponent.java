package components;

import model.ChessPiece;
import model.GameSound;
import view.GameFrame;

import java.awt.*;

public class ChessGridComponent extends BasicComponent {
    public static int chessSize;
    public static int gridSize;
    public static int dotSize;
    public static Color gridColor = new Color(103, 137, 163);

    private ChessPiece chessPiece;
    private int row;
    private int col;

    public ChessGridComponent(int row, int col) {
        this.setSize(gridSize, gridSize);

        this.row = row;
        this.col = col;
    }

    @Override
    public void onMouseClicked() {
        int level = GameFrame.controller.getWhitePlayerLevel();
        ChessPiece currentPlayer = GameFrame.controller.getCurrentPlayer();
        if (level > 0 && currentPlayer == ChessPiece.WHITE) {
            return;
        }
        GameFrame.controller.printMsg(String.format("%s clicked (%d, %d)\n", currentPlayer, row, col));
        //todo: complete mouse click method
        if (GameFrame.controller.canClick(row, col)) {
            GameSound.playSoundEffect();
            if (this.chessPiece == null) {
                this.chessPiece = currentPlayer;
                GameFrame.controller.swapPlayer();
            }
            repaint();
        }
    }


    public ChessPiece getChessPiece() {
        return chessPiece;
    }

    public void setChessPiece(ChessPiece chessPiece) {
        this.chessPiece = chessPiece;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void drawPiece(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(gridColor);
        g2d.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
        if (this.chessPiece != null) {
            if (chessPiece.getColor() == ChessPiece.LIGHT_BLACK.getColor() ||
                chessPiece.getColor() == ChessPiece.LIGHT_WHITE.getColor()) {
                g2d.setColor(chessPiece.getColor());
                g2d.fillOval((gridSize - chessSize) / 2+17, (gridSize - chessSize) / 2+17, dotSize, dotSize);

        }else{
                g2d.setColor(chessPiece.getColor());
                g2d.fillOval((gridSize - chessSize) / 2, (gridSize - chessSize) / 2, chessSize, chessSize);
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.printComponents(g);
        drawPiece(g);
    }

}
