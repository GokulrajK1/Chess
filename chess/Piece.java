package chess;

public class Piece {
    public enum Kind { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

    public final Chess.Player color;
    public final Kind kind;

    public Piece(Chess.Player color, Kind kind) {
        this.color = color;
        this.kind = kind;
    }

    public Piece copy() {
        return new Piece(color, kind);
    }

    public boolean isWhite() { return color == Chess.Player.white; }
    public boolean isBlack() { return color == Chess.Player.black; }
}