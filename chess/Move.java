package chess;

public class Move {
    public final Coord from;
    public final Coord to;

    public boolean castle = false;
    public boolean enPassant = false;

    public boolean promotionCandidate = false;
    public Piece.Kind promotion = null;

    // for en passant: which pawn square gets removed
    public Coord epCaptured = null;

    public Move(Coord from, Coord to) {
        this.from = from;
        this.to = to;
    }
}