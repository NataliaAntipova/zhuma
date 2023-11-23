package com.violet.saper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Field {
    int[][] magic = {{-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}};
    int[][] matrix;
    int[][] minetrix; //по индексу
    int width, height;
    int mines;

    Field(int width, int height) {
        this.width = width;
        this.height = height;
        clear();
    }

    void clear() {
        matrix = new int[width][height];
    }

    void randomMines(int mines) {
        this.mines = mines;
        minetrix = new int[mines][2];
        Random rand = new Random();
        for (int i = 0; i < mines; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            boolean can = true;
            for (int[] mine : minetrix) {
                if (mine[0] == x && mine[1] == y) {
                    i--;
                    can = false;
                    break;
                }
            }
            if (can) {
                minetrix[i][0] = x;
                minetrix[i][1] = y;
                matrix[x][y] = -1;
            }
        }
    }

    void count() {
        for (int i = 0; i < mines; i++) {
            int x = minetrix[i][0];
            int y = minetrix[i][1];
            for (int j = 0; j < 8; j++) {
                int xNew = x + magic[j][0];
                int yNew = y + magic[j][1];
                addMine(xNew, yNew);
            }
        }
    }

    List<int[]> getNullsWithOthers(int[][] matrix, int x, int y) {
        List<int[]> nullsList = getNulls(matrix, x, y);
        List<int[]> result = new ArrayList<>();
        for (int[] nulls : nullsList) {
            for (int i = 0; i < 8; i++) {
                int xPos = nulls[0] + magic[i][0];
                int yPos = nulls[1] + magic[i][1];
                if (safeCheck(xPos, yPos) && matrix[xPos][yPos] > 0) {
                    boolean isNew = true;
                    for (int[] one : result) {
                        if (one[0] == xPos && one[1] == yPos) {
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) result.add(new int[]{xPos, yPos});
                }
            }
        }
        nullsList.addAll(result);
        return nullsList;
    }

    List<int[]> getNulls(int[][] matrix, int x, int y) {
        List<int[]> result = new ArrayList<>();
        if (x < 0 || x >= matrix.length || y < 0 || y >= matrix[0].length || matrix[x][y] != 0) {
            return result;
        }
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        fill(x, y, visited, matrix, result);
        return result;
    }

    private void fill(int x, int y, boolean[][] visited, int[][] matrix, List<int[]> result) {
        if (x < 0 || x >= matrix.length || y < 0 || y >= matrix[0].length || visited[x][y] || matrix[x][y] != 0) {
            return;
        }
        result.add(new int[]{x, y});
        visited[x][y] = true;
        fill(x - 1, y, visited, matrix, result);
        fill(x + 1, y, visited, matrix, result);
        fill(x, y - 1, visited, matrix, result);
        fill(x, y + 1, visited, matrix, result);
    } //fill рекурсия - вызывает саму себя (fload fill - принцип работы)

    boolean safeCheck(int x, int y) {
        return (x >= 0) && (x < width)
                && (y >= 0) && (y < height);
    } //границы

    void addMine(int x, int y) {
        if (safeCheck(x, y) && (matrix[x][y] != -1))
            matrix[x][y]++; //безопасно прибавить значение
    }
}