package chess;

public class BoardState {
    private final Piece[][] b = new Piece[8][8]; // [rank][file], rank 0 = "1"

    public Chess.Player turn = Chess.Player.white;

    // Castling rights
    public boolean wK = true, wQ = true, bK = true, bQ = true;

    // En passant target square (the square a pawn could move INTO to capture ep)
    // If none: epFile = -1
    public int epFile = -1;
    public int epRank = -1;

    public boolean gameOver = false;
    public ReturnPlay.Message lastMessage = null;

    public Piece get(Coord c) { return b[c.r][c.f]; }
    public void set(Coord c, Piece p) { b[c.r][c.f] = p; }

    public Piece get(int f, int r) { return b[r][f]; }
    public void set(int f, int r, Piece p) { b[r][f] = p; }

    public BoardState copy() {
        BoardState g = new BoardState();
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                g.b[r][f] = (this.b[r][f] == null) ? null : this.b[r][f].copy();
            }
        }
        g.turn = this.turn;
        g.wK = this.wK; g.wQ = this.wQ; g.bK = this.bK; g.bQ = this.bQ;
        g.epFile = this.epFile; g.epRank = this.epRank;
        g.gameOver = this.gameOver;
        g.lastMessage = this.lastMessage;
        return g;
    }

    public static BoardState initial() {
        BoardState gs = new BoardState();
        // White back rank (rank 1 => r=0)
        gs.set(Coord.of('a',1), new Piece(Chess.Player.white, Piece.Kind.ROOK));
        gs.set(Coord.of('b',1), new Piece(Chess.Player.white, Piece.Kind.KNIGHT));
        gs.set(Coord.of('c',1), new Piece(Chess.Player.white, Piece.Kind.BISHOP));
        gs.set(Coord.of('d',1), new Piece(Chess.Player.white, Piece.Kind.QUEEN));
        gs.set(Coord.of('e',1), new Piece(Chess.Player.white, Piece.Kind.KING));
        gs.set(Coord.of('f',1), new Piece(Chess.Player.white, Piece.Kind.BISHOP));
        gs.set(Coord.of('g',1), new Piece(Chess.Player.white, Piece.Kind.KNIGHT));
        gs.set(Coord.of('h',1), new Piece(Chess.Player.white, Piece.Kind.ROOK));
        for (char file = 'a'; file <= 'h'; file++) {
            gs.set(Coord.of(file,2), new Piece(Chess.Player.white, Piece.Kind.PAWN));
        }

        // Black back rank (rank 8 => r=7)
        gs.set(Coord.of('a',8), new Piece(Chess.Player.black, Piece.Kind.ROOK));
        gs.set(Coord.of('b',8), new Piece(Chess.Player.black, Piece.Kind.KNIGHT));
        gs.set(Coord.of('c',8), new Piece(Chess.Player.black, Piece.Kind.BISHOP));
        gs.set(Coord.of('d',8), new Piece(Chess.Player.black, Piece.Kind.QUEEN));
        gs.set(Coord.of('e',8), new Piece(Chess.Player.black, Piece.Kind.KING));
        gs.set(Coord.of('f',8), new Piece(Chess.Player.black, Piece.Kind.BISHOP));
        gs.set(Coord.of('g',8), new Piece(Chess.Player.black, Piece.Kind.KNIGHT));
        gs.set(Coord.of('h',8), new Piece(Chess.Player.black, Piece.Kind.ROOK));
        for (char file = 'a'; file <= 'h'; file++) {
            gs.set(Coord.of(file,7), new Piece(Chess.Player.black, Piece.Kind.PAWN));
        }

        gs.turn = Chess.Player.white;
        gs.wK = gs.wQ = gs.bK = gs.bQ = true;
        gs.epFile = -1;
        gs.epRank = -1;
        gs.gameOver = false;
        gs.lastMessage = null;
        return gs;
    }
}