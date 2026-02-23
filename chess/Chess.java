package chess;

import java.util.*;

public class Chess {

    enum Player { white, black }

    private static Board board;

    /**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
    public static ReturnPlay play(String move) {

        if (board == null) Chess.start();

        String s = (move == null) ? "" : move.trim();

        // If game ended, ignore further moves (safe behavior)
        if (board.gameOver) {
            return Engine.toReturnPlay(board, board.lastMessage);
        }

        if (s.equalsIgnoreCase("resign")) {
            board.gameOver = true;
            ReturnPlay.Message msg = (board.turn == Player.white)
                    ? ReturnPlay.Message.RESIGN_BLACK_WINS
                    : ReturnPlay.Message.RESIGN_WHITE_WINS;
            board.lastMessage = msg;
            return Engine.toReturnPlay(board, msg);
        }

        String[] toks = s.split("\\s+");
        if (toks.length < 2) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        Coord from = Coord.parse(toks[0]);
        Coord to = Coord.parse(toks[1]);

        boolean wantsDraw = false;
        Character promoChar = null;

        for (int i = 2; i < toks.length; i++) {
            if (toks[i].equalsIgnoreCase("draw?")) wantsDraw = true;
            else if (toks[i].length() == 1) promoChar = Character.toUpperCase(toks[i].charAt(0));
        }

        Piece mover = board.get(from);
        if (mover == null || mover.color != board.turn) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Generate pseudo-legal moves for this piece
        List<Move> pseudos = Engine.pseudoMoves(board, from);

        Move chosen = null;
        for (Move m : pseudos) {
            if (m.to.equals(to)) {
                chosen = m;
                break;
            }
        }
        if (chosen == null) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Promotion handling
        if (chosen.promotionCandidate) {
            chosen.promotion = Engine.promoFromCharOrDefault(promoChar);
        } else if (promoChar != null) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Simulate legality (can't leave own king in check)
        Board sim = board.copy();
        if (!Engine.applyMove(sim, chosen)) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }
        if (Engine.isKingInCheck(sim, board.turn)) {
            return Engine.toReturnPlay(board, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Commit
        Engine.applyMove(board, chosen);

        // Draw offer after executing move
        if (wantsDraw) {
            board.gameOver = true;
            board.lastMessage = ReturnPlay.Message.DRAW;
            return Engine.toReturnPlay(board, ReturnPlay.Message.DRAW);
        }

        // After move, gs.turn already flipped to next player
        boolean inCheck = Engine.isKingInCheck(board, board.turn);
        if (inCheck) {
            boolean hasReply = Engine.hasAnyLegalMove(board, board.turn);
            if (!hasReply) {
                board.gameOver = true;
                ReturnPlay.Message mateMsg =
                        (board.turn == Player.white)
                                ? ReturnPlay.Message.CHECKMATE_BLACK_WINS
                                : ReturnPlay.Message.CHECKMATE_WHITE_WINS;
                board.lastMessage = mateMsg;
                return Engine.toReturnPlay(board, mateMsg);
            } else {
                board.lastMessage = ReturnPlay.Message.CHECK;
                return Engine.toReturnPlay(board, ReturnPlay.Message.CHECK);
            }
        }

        board.lastMessage = null;
        return Engine.toReturnPlay(board, null);
    }

    /**
	 * This method should reset the game, and start from scratch.
	 */
    public static void start() {
        board = Board.initial();
    }
}
