package chess;

import java.util.Objects;

public class Coord {
    public final int f; // 0..7 for a..h
    public final int r; // 0..7 for 1..8

    public Coord(int f, int r) {
        this.f = f; this.r = r;
    }

    public static Coord parse(String s) {
        char file = s.charAt(0);
        int rank = s.charAt(1) - '0';
        return of(file, rank);
    }

    public static Coord of(char file, int rank1to8) {
        return new Coord(file - 'a', rank1to8 - 1);
    }

    public static boolean inBounds(int f, int r) {
        return f >= 0 && f < 8 && r >= 0 && r < 8;
    }

    public int rank1() { return r + 1; }
    public char fileChar() { return (char)('a' + f); }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Coord c)) return false;
        return f == c.f && r == c.r;
    }
    @Override public int hashCode() { return Objects.hash(f, r); }
}