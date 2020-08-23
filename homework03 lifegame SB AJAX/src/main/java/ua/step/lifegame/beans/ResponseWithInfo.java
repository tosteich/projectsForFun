package ua.step.lifegame.beans;

public class ResponseWithInfo {
    private final Object [] cells;
    private final int generation;
    private final int aliveCells;
    private final int isGameOver;

    public ResponseWithInfo(Object[] cells, int generation, int aliveCells, int isGameOver) {
        this.cells = cells;
        this.generation = generation;
        this.aliveCells = aliveCells;
        this.isGameOver = isGameOver;
    }

    public Object[] getCells() {
        return cells;
    }

    public int getGeneration() {
        return generation;
    }

    public int getAliveCells() {
        return aliveCells;
    }

    public int getIsGameOver() {
        return isGameOver;
    }
}