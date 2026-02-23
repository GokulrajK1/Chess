package chess;

import java.util.*;

public class Chess {

    public enum Player { white, black }

    private static BoardState gs;

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
        if (gs == null) start();
        String s = (move == null) ? "" : move.trim();

        // If game ended, ignore further moves (safe behavior)
        if (gs.gameOver) {
            return Engine.toReturnPlay(gs, gs.lastMessage);
        }

        if (s.equalsIgnoreCase("resign")) {
            gs.gameOver = true;
            ReturnPlay.Message msg = (gs.turn == Player.white)
                    ? ReturnPlay.Message.RESIGN_BLACK_WINS
                    : ReturnPlay.Message.RESIGN_WHITE_WINS;
            gs.lastMessage = msg;
            return Engine.toReturnPlay(gs, msg);
        }

        String[] toks = s.split("\\s+");
        if (toks.length < 2) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        Coord from = Coord.parse(toks[0]);
        Coord to = Coord.parse(toks[1]);

        boolean wantsDraw = false;
        Character promoChar = null;

        for (int i = 2; i < toks.length; i++) {
            if (toks[i].equalsIgnoreCase("draw?")) wantsDraw = true;
            else if (toks[i].length() == 1) promoChar = Character.toUpperCase(toks[i].charAt(0));
        }

        Piece mover = gs.get(from);
        if (mover == null || mover.color != gs.turn) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Generate pseudo-legal moves for this piece
        List<Move> pseudos = Engine.pseudoMoves(gs, from);

        Move chosen = null;
        for (Move m : pseudos) {
            if (m.to.equals(to)) {
                chosen = m;
                break;
            }
        }
        if (chosen == null) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Promotion handling
        if (chosen.promotionCandidate) {
            chosen.promotion = Engine.promoFromCharOrDefault(promoChar);
        } else if (promoChar != null) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Simulate legality (can't leave own king in check)
        BoardState sim = gs.copy();
        if (!Engine.applyMove(sim, chosen)) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }
        if (Engine.isKingInCheck(sim, gs.turn)) {
            return Engine.toReturnPlay(gs, ReturnPlay.Message.ILLEGAL_MOVE);
        }

        // Commit
        Engine.applyMove(gs, chosen);

        // Draw offer after executing move
        if (wantsDraw) {
            gs.gameOver = true;
            gs.lastMessage = ReturnPlay.Message.DRAW;
            return Engine.toReturnPlay(gs, ReturnPlay.Message.DRAW);
        }

        // After move, gs.turn already flipped to next player
        boolean inCheck = Engine.isKingInCheck(gs, gs.turn);
        if (inCheck) {
            boolean hasReply = Engine.hasAnyLegalMove(gs, gs.turn);
            if (!hasReply) {
                gs.gameOver = true;
                ReturnPlay.Message mateMsg =
                        (gs.turn == Player.white)
                                ? ReturnPlay.Message.CHECKMATE_BLACK_WINS
                                : ReturnPlay.Message.CHECKMATE_WHITE_WINS;
                gs.lastMessage = mateMsg;
                return Engine.toReturnPlay(gs, mateMsg);
            } else {
                gs.lastMessage = ReturnPlay.Message.CHECK;
                return Engine.toReturnPlay(gs, ReturnPlay.Message.CHECK);
            }
        }

        gs.lastMessage = null;
        return Engine.toReturnPlay(gs, null);
    }

    /**
	 * This method should reset the game, and start from scratch.
	 */
    public static void start() {
        gs = BoardState.initial();
    }
}
