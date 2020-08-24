package ua.step.lifegame.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LifeLogic {

    private Set<Cell> cells;
    private int[] cellsCount;
    private int genNo = 0;
    private final int genNum = 50;
    private final int[][] directions = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 },
            { 1, 1 } };

    public LifeLogic() {
        cells = new HashSet<>();
        cellsCount = new int[genNum];
    }

    public Set<Cell> getCells() {
        return cells;
    }

    public void setCell(int x, int y) {
        if (cells.isEmpty()) {
            genNo = 0;
            cellsCount = new int[genNum];
        }
        cells.add(new Cell(x, y));
    }

    public void setCell(Cell cell) {
        if (cells.isEmpty()) {
            genNo = 0;
            cellsCount = new int[genNum];
        }
        cells.add(cell);
        cellsCount[genNo % genNum] = cells.size();
    }

    public int getCell(int x, int y) {
        return cells.contains(new Cell(x, y)) ? 1 : 0;
    }

    public int getCell(Cell cell) {
        return cells.contains(cell) ? 1 : 0;
    }

    public void removeCell(Cell cell) {
        cells.remove(cell);
        if (cells.isEmpty()) {
            genNo = 0;
            cellsCount = new int[genNum];
        }
        cellsCount[genNo % genNum] = cells.size();
    }

    public void clearField() {
        cells = new HashSet<>();
        cellsCount = new int[genNum];
        genNo = 0;
    }

    public int getCountPopulation() {
        return genNo;
    }

    public int getLiveCells() {
        return cells.size();
    }

    public boolean isGameOver() {
        if (genNo > 0 && cells.isEmpty()) {
            return true;
        }
        if (genNo > 0) {
            for (int i = 1; i < cellsCount.length; i++) {
                if (cellsCount[i] != cellsCount[i - 1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void next() {
        Set<Cell> newGen = new HashSet<>();
        cellsCount[genNo % genNum] = cells.size();
        for (Cell cell : cells) {
            int x = cell.getX();
            int y = cell.getY();
            if (getNeighbors(x, y) == 3 || getNeighbors(x, y) == 2) {
                newGen.add(new Cell(x, y));
            }
            HashSet<Cell> emptyCells = getEmptyCellsAround(x, y);
            for (Cell cell2 : emptyCells) {
                if (getNeighbors(cell2.getX(), cell2.getY()) == 3) {
                    newGen.add(cell2);
                }
            }
        }
        cells = newGen;
        genNo++;
    }

    public int getNeighbors(int x, int y) {
        int a = 0;
        for (int[] offset : directions) {
            int offsetX = offset[0];
            int offsetY = offset[1];
            a += getCell(x + offsetX, y + offsetY);
        }
        return a;
    }

    public void moveAll(int x, int y) {
        if (x == 0 && y == 0) {
            return;
        }
        Set<Cell> newCells = new HashSet<>();
        for (Cell cell : cells) {
            newCells.add(cell.updatePosition(x, y));
        }
        cells = newCells;
    }

    public String getPattern() {

        StringBuilder sb = new StringBuilder("#Life 1.05");
        List<Deque<Cell>> cellsLists = getCellsLists();
        for (Deque<Cell> list : cellsLists) {
            Cell ind = list.pollFirst();
            assert ind != null;
            int startLine = ind.getY();
            int leftDot = ind.getX();
            sb.append("\n#P ").append(leftDot).append(" ").append(startLine).append("\n");
            for (Cell cell : list) {
                int lineOffset = cell.getY() - startLine;
                while (lineOffset != 0) {
                    if (sb.charAt(sb.length() - 1) == '*') {
                        sb.append("\n");
                    } else {
                        sb.append(".\n");
                    }
                    leftDot = ind.getX();
                    startLine++;
                    lineOffset = cell.getY() - startLine;
                }
                int dotOffset = cell.getX() - leftDot;
                while (dotOffset != 0) {
                    sb.append(".");
                    leftDot++;
                    dotOffset = cell.getX() - leftDot;
                }
                sb.append("*");
                leftDot++;
            }
        }
        return sb.toString();
    }

    private List<Deque<Cell>> getCellsLists() {

        TreeMap<Integer, TreeSet<Integer>> cellsMap = new TreeMap<>();
        for (Cell cell : cells) {
            Integer key = cell.getY();
            if (cellsMap.containsKey(key)) {
                cellsMap.get(key).add(cell.getX());
            } else {
                cellsMap.computeIfAbsent(key, v -> new TreeSet<>()).add(cell.getX());
            }
        }
        List<Deque<Cell>> sellsLists = new ArrayList<>();
        makeSellsLists(cellsMap, sellsLists);
        return sellsLists;

    }

    private void makeSellsLists(TreeMap<Integer, TreeSet<Integer>> cellsMap, List<Deque<Cell>> cellsLists) {
        if (cellsMap.isEmpty()) {
            return;
        }
        int maxLineWidth = 30;
        int maxEmptyLines = 5;
        int lastLine = cellsMap.firstKey();
        int xMin = cellsMap.get(cellsMap.firstKey()).first();
        int xMax = xMin;
        Deque<Cell> currentBlock = new LinkedList<>();
        currentBlock.add(new Cell(xMin, lastLine));
        cellsLists.add(currentBlock);
        Iterator<Integer> mapIter = cellsMap.keySet().iterator();
        while (mapIter.hasNext()) {
            int k = mapIter.next();
            if (k - lastLine > maxEmptyLines) {
                break;
            }
            Iterator<Integer> iter = cellsMap.get(k).iterator();
            while (iter.hasNext()) {
                int i = iter.next();
                if (xMax - i > maxLineWidth) {
                    continue;
                } else if (i - xMin > maxLineWidth) {
                    break;
                }
                xMin = Math.min(i, xMin);
                xMax = Math.max(i, xMax);
                currentBlock.getFirst().setX(xMin);
                currentBlock.add(new Cell(i, k));
                iter.remove();
                lastLine = k;
            }
            if (cellsMap.get(k).isEmpty()) {
                mapIter.remove();
            }
        }
        makeSellsLists(cellsMap, cellsLists);
    }

    private HashSet<Cell> getEmptyCellsAround(int x, int y) {
        HashSet<Cell> emptyCells = new HashSet<>();
        for (int[] offset : directions) {
            int offsetX = offset[0];
            int offsetY = offset[1];
            if (!cells.contains(new Cell(x + offsetX, y + offsetY))) {
                emptyCells.add(new Cell(x + offsetX, y + offsetY));
            }
        }
        return emptyCells;

    }
    
    public void loadFile (MultipartFile file) {
    	try (InputStream is = new ByteArrayInputStream(file.getBytes());
                LineNumberReader fileReader = new LineNumberReader(new InputStreamReader(is))){
               String str;
               int startX = 0;
               int startY = 0;
               int lineNumber = -1;
               while ((str = fileReader.readLine()) != null) {
                   lineNumber++;
                   if (str.startsWith("#P")) {
                       String[] coordinates = str.split(" ");
                       startX = Integer.parseInt(coordinates[1]);
                       startY = Integer.parseInt(coordinates[2]);
                       lineNumber = -1;
                   } else if (!str.startsWith("#")) {
                       for (int i = 0; i < str.length(); i++) {
                           if (str.charAt(i) == '*') {
                               this.setCell(startX + i, startY + lineNumber);
                           }
                       }
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
    }


}