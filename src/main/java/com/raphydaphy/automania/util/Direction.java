package main.java.com.raphydaphy.automania.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Direction
{
    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0), NONE(0, 0);

    public static List<Direction> ADJACENT = Arrays.asList(UP, DOWN, LEFT, RIGHT);
    public static List<Direction> ADJACENT_INCLUDING_NONE = Arrays.asList(UP, DOWN, LEFT, RIGHT, NONE);

    public final int x;
    public final int y;

    private Direction(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}
