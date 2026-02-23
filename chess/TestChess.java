package chess;

import java.util.*;

/**
 * Local test harness for Chess.java.
 *
 * ✅ Keep this in your dev environment for testing.
 * 🚫 Do NOT include this file in your Autolab submission zip.
 *
 * How to run:
 *   javac chess/*.java
 *   java chess.TestChess
 */
public class TestChess {

    public static void main(String[] args) {
        int passed = 0;
        int total = 0;

        // --- GAME 1: illegal move + draw after castling ---
        total++;
        if (testGame1_IllegalAndDraw()) passed++;

        // --- GAME 2: long capture-heavy + castling (no early termination) ---
        total++;
        if (testGame2_LongCapturesAndCastle()) passed++;

        // --- GAME 3: en passant ---
        total++;
        if (testGame3_EnPassant()) passed++;

        // --- GAME 4: promotion explicit Q ---
        total++;
        if (testGame4_PromotionExplicit()) passed++;

        // --- GAME 5: promotion implicit default queen ---
        total++;
        if (testGame5_PromotionImplicit()) passed++;

        // --- GAME 6: checkmate (Scholar's mate) ---
        total++;
        if (testGame6_CheckmateWhiteWins()) passed++;

        // --- GAME 7: longer game with black castling + captures (no early termination) ---
        total++;
        if (testGame7_LongNoEarlyTermination()) passed++;

        // --- GAME 8: resign ---
        total++;
        if (testGame8_Resign()) passed++;

        System.out.println();
        System.out.println("=======================================");
        System.out.println("Passed " + passed + " / " + total + " test groups");
        System.out.println("=======================================");

        if (passed != total) {
            System.out.println("At least one group failed. See output above for the first failing move.");
            System.exit(1);
        }
    }

    // ----------------------------- Tests -----------------------------

    private static boolean testGame1_IllegalAndDraw() {
        System.out.println("Running Game 1: illegal move + castling + draw?");
        Chess.start();

        // First move is illegal because black cannot move first
        ReturnPlay r1 = Chess.play("e7 e5");
        if (!assertMsg("G1-M1 illegal black-first", r1, ReturnPlay.Message.ILLEGAL_MOVE)) return false;

        // White now plays a mini-opening and castles with draw?
        String[] moves = {
                "e2 e4",
                "e7 e5",
                "g1 f3",
                "b8 c6",
                "f1 b5",
                "a7 a6",
                "b5 a4",
                "g8 f6",
                "e1 g1 draw?"
        };

        ReturnPlay last = null;
        for (int i = 0; i < moves.length; i++) {
            last = Chess.play(moves[i]);
            if (last.message == ReturnPlay.Message.ILLEGAL_MOVE) {
                failWithBoard("G1 unexpected illegal at move " + (i + 2) + ": " + moves[i], last);
                return false;
            }
        }

        // Should end in DRAW
        if (!assertMsg("G1 final should be DRAW", last, ReturnPlay.Message.DRAW)) return false;

        // Confirm castling happened: white king on g1 and rook on f1
        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);
        if (!assertAt(pos, "g1", ReturnPiece.PieceType.WK, "G1 white king after castle")) return false;
        if (!assertAt(pos, "f1", ReturnPiece.PieceType.WR, "G1 white rook after castle")) return false;

        System.out.println("✅ Game 1 passed");
        return true;
    }

    private static boolean testGame2_LongCapturesAndCastle() {
        System.out.println("Running Game 2: long capture-heavy + castling");
        Chess.start();

String[] moves = {
    "e2 e4",
    "e7 e5",
    "g1 f3",
    "b8 c6",
    "f1 c4",
    "g8 f6",
    "d2 d3",
    "f8 c5",
    "c2 c3",
    "d7 d6",
    "b1 d2",
    "c8 g4",
    "h2 h3",
    "g4 h5",
    "c4 f7",
    "e8 f7",
    "d1 b3",
    "f7 f8",
    "b3 b7",
    "a8 b8",
    "e1 g1",
    "b8 b7"   // ✅ rook captures queen (was b8 b2)
};

        int startPieces = 32;
        ReturnPlay last = runNoIllegal("G2", moves);
        if (last == null) return false;

        // Ensure white castled: king g1 rook f1
        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);
        if (!assertAt(pos, "g1", ReturnPiece.PieceType.WK, "G2 white king after castle")) return false;
        if (!assertAt(pos, "f1", ReturnPiece.PieceType.WR, "G2 white rook after castle")) return false;

        // Ensure captures happened: piece count should be < 32
        int endPieces = (last.piecesOnBoard == null) ? 0 : last.piecesOnBoard.size();
        if (endPieces >= startPieces) {
            System.out.println("❌ G2 expected captures (piece count < 32), got " + endPieces);
            printBoard(last.piecesOnBoard);
            return false;
        }

        System.out.println("✅ Game 2 passed");
        return true;
    }

    private static boolean testGame3_EnPassant() {
        System.out.println("Running Game 3: en passant");
        Chess.start();

        String[] moves = {
                "e2 e4",
                "a7 a6",
                "e4 e5",
                "d7 d5",
                "e5 d6"
        };

        ReturnPlay last = runNoIllegal("G3", moves);
        if (last == null) return false;

        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);

        // En passant result: white pawn from e5 should now be on d6
        if (!assertAt(pos, "d6", ReturnPiece.PieceType.WP, "G3 pawn after en passant")) return false;

        // The pawn that moved d7->d5 should be removed, so d5 should be empty
        if (pos.containsKey("d5")) {
            System.out.println("❌ G3 expected d5 empty after en passant, found " + pos.get("d5"));
            printBoard(last.piecesOnBoard);
            return false;
        }

        System.out.println("✅ Game 3 passed");
        return true;
    }

    private static boolean testGame4_PromotionExplicit() {
        System.out.println("Running Game 4: promotion explicit Q");
        Chess.start();

        String[] moves = {
                "a2 a4",
                "h7 h5",
                "a4 a5",
                "b7 b5",
                "a5 b6",
                "g7 g6",
                "b6 b7",
                "b8 c6",
                "b7 b8 Q"
        };

        ReturnPlay last = runNoIllegal("G4", moves);
        if (last == null) return false;

        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);

        // b8 should now be a white queen
        if (!assertAt(pos, "b8", ReturnPiece.PieceType.WQ, "G4 promoted piece at b8")) return false;

        System.out.println("✅ Game 4 passed");
        return true;
    }

    private static boolean testGame5_PromotionImplicit() {
        System.out.println("Running Game 5: promotion implicit default queen");
        Chess.start();

        String[] moves = {
                "a2 a4",
                "b7 b5",
                "a4 b5",
                "a7 a6",
                "b5 b6",
                "b8 c6",
                "b6 b7",
                "a6 a5",
                "b7 b8"
        };

        ReturnPlay last = runNoIllegal("G5", moves);
        if (last == null) return false;

        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);

        // default promotion should be a queen
        if (!assertAt(pos, "b8", ReturnPiece.PieceType.WQ, "G5 default promoted piece at b8")) return false;

        System.out.println("✅ Game 5 passed");
        return true;
    }

    private static boolean testGame6_CheckmateWhiteWins() {
        System.out.println("Running Game 6: checkmate (Scholar's mate)");
        Chess.start();

        String[] moves = {
                "e2 e4",
                "e7 e5",
                "f1 c4",
                "b8 c6",
                "d1 h5",
                "g8 f6",
                "h5 f7"
        };

        ReturnPlay last = null;
        for (int i = 0; i < moves.length; i++) {
            last = Chess.play(moves[i]);
            if (last.message == ReturnPlay.Message.ILLEGAL_MOVE) {
                failWithBoard("G6 illegal at move " + (i + 1) + ": " + moves[i], last);
                return false;
            }
        }

        if (!assertMsg("G6 should be CHECKMATE_WHITE_WINS", last, ReturnPlay.Message.CHECKMATE_WHITE_WINS)) {
            System.out.println("Note: If you got CHECK instead of CHECKMATE, your mate detection likely missed \"no legal replies\".");
            return false;
        }

        System.out.println("✅ Game 6 passed");
        return true;
    }

    private static boolean testGame7_LongNoEarlyTermination() {
        System.out.println("Running Game 7: longer game with black castling + captures (no early termination)");
        Chess.start();

String[] moves = {
    "d2 d4",
    "g8 f6",
    "c2 c4",
    "e7 e6",
    "b1 c3",
    "f8 b4",
    "e2 e3",
    "e8 g8",
    "g1 f3",
    "d7 d5",
    "f1 d3",
    "c7 c5",
    "e1 g1",
    "d5 c4",
    "d3 c4",
    "c5 d4",
    "e3 d4",
    "b4 c3",
    "b2 c3",
    "b8 c6",
    "c4 e6"   // ✅ bishop captures pawn on e6 (was c4 f7)
};

        ReturnPlay last = runNoIllegal("G7", moves);
        if (last == null) return false;

        // Verify black castled: king on g8, rook on f8
        Map<String, ReturnPiece.PieceType> pos = toPosMap(last.piecesOnBoard);
        if (!assertAt(pos, "g8", ReturnPiece.PieceType.BK, "G7 black king after castle")) return false;
        if (!assertAt(pos, "f8", ReturnPiece.PieceType.BR, "G7 black rook after castle")) return false;

        // Verify captures happened
        int endPieces = (last.piecesOnBoard == null) ? 0 : last.piecesOnBoard.size();
        if (endPieces >= 32) {
            System.out.println("❌ G7 expected captures (piece count < 32), got " + endPieces);
            printBoard(last.piecesOnBoard);
            return false;
        }

        System.out.println("✅ Game 7 passed");
        return true;
    }

    private static boolean testGame8_Resign() {
        System.out.println("Running Game 8: resign");
        Chess.start();

        Chess.play("e2 e4");
        Chess.play("e7 e5");
        ReturnPlay last = Chess.play("g1 f3");
        if (last.message == ReturnPlay.Message.ILLEGAL_MOVE) {
            failWithBoard("G8 illegal before resign", last);
            return false;
        }

        // Now resign (it should be black to move after 3 plies)
        ReturnPlay r = Chess.play("resign");
        // If it's black to move, resign => RESIGN_WHITE_WINS (because black resigned)
        if (!assertMsg("G8 resign message", r, ReturnPlay.Message.RESIGN_WHITE_WINS)) return false;

        System.out.println("✅ Game 8 passed");
        return true;
    }

    // ----------------------------- Helpers -----------------------------

    private static ReturnPlay runNoIllegal(String tag, String[] moves) {
        ReturnPlay last = null;
        for (int i = 0; i < moves.length; i++) {
            last = Chess.play(moves[i]);
            if (last.message == ReturnPlay.Message.ILLEGAL_MOVE) {
                failWithBoard(tag + " illegal at move " + (i + 1) + ": " + moves[i], last);
                return null;
            }
        }
        return last;
    }

    private static boolean assertMsg(String label, ReturnPlay rp, ReturnPlay.Message expected) {
        ReturnPlay.Message got = (rp == null) ? null : rp.message;
        if (got != expected) {
            System.out.println("❌ " + label + " expected message=" + expected + " but got=" + got);
            if (rp != null) printBoard(rp.piecesOnBoard);
            return false;
        }
        return true;
    }

    private static boolean assertAt(Map<String, ReturnPiece.PieceType> pos,
                                    String sq,
                                    ReturnPiece.PieceType expected,
                                    String label) {
        ReturnPiece.PieceType got = pos.get(sq);
        if (got != expected) {
            System.out.println("❌ " + label + " expected " + expected + " at " + sq + " but got " + got);
            return false;
        }
        return true;
    }

    private static void failWithBoard(String label, ReturnPlay rp) {
        System.out.println("❌ " + label);
        if (rp != null) {
            System.out.println("Message: " + rp.message);
            printBoard(rp.piecesOnBoard);
        }
    }

    /**
     * Converts ReturnPlay pieces to a map like "e4" -> WP
     */
    private static Map<String, ReturnPiece.PieceType> toPosMap(ArrayList<ReturnPiece> pieces) {
        Map<String, ReturnPiece.PieceType> m = new HashMap<>();
        if (pieces == null) return m;
        for (ReturnPiece rp : pieces) {
            String sq = "" + rp.pieceFile + rp.pieceRank;
            m.put(sq, rp.pieceType);
        }
        return m;
    }

    /**
     * Small board printer (independent of PlayChess).
     */
    private static void printBoard(ArrayList<ReturnPiece> pieces) {
        String[][] board = new String[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r % 2 == 0) board[r][c] = (c % 2 == 0) ? "  " : "##";
                else board[r][c] = (c % 2 == 0) ? "##" : "  ";
            }
        }

        if (pieces != null) {
            for (ReturnPiece rp : pieces) {
                int file = ("" + rp.pieceFile).charAt(0) - 'a';
                String pieceStr = "" + rp.pieceType; // e.g. WP, BK
                String pp = "";
                pp += Character.toLowerCase(pieceStr.charAt(0)); // w/b
                pp += (pieceStr.charAt(1) == 'P') ? 'p' : pieceStr.charAt(1);
                board[8 - rp.pieceRank][file] = pp;
            }
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println(8 - r);
        }
        System.out.println(" a  b  c  d  e  f  g  h");
        System.out.println();
    }
}