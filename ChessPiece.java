package model;

import java.awt.*;

public enum ChessPiece {
    BLACK(Color.BLACK), WHITE(Color.WHITE),
    LIGHT_BLACK(new Color(233, 39, 39)),
    LIGHT_WHITE(new Color(248, 233, 17));

    private final Color color;
    private int increment;

    ChessPiece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setIncrement(int value) {
        this.increment = value;
    }

    public int getIncrement() {
        return increment;
    }
}
