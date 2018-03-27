package com.even.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class BoundingBox {
    int left;
    int top;
    int right;
    int bottom;
}

public class Even {

    private static char[][] map;
    private static boolean[][] visited;

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            map = initalizeMap(args[0]);
            boundingBox(map);
        } else {
            System.exit(-1);
        }

    }

    private static void printBox(BoundingBox box) {
        if (box == null) {
            System.exit(-1);
        }
        System.out.print("(" + box.top + "," + box.left + ")");
        System.out.println("(" + box.bottom + "," + box.right + ")");
    }

    private static void boundingBox(char[][] map) {
        List<BoundingBox> result = new ArrayList<BoundingBox>();

        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                if (map[r][c] == '*' && !visited[r][c]) {
                    visited[r][c] = true;
                    BoundingBox box = new BoundingBox();
                    box.left = c;
                    box.right = c;
                    box.bottom = r;
                    box.top = r;
                    box = findMinBox(r, c, box);
                    box = findMinBox(r + 1, c, box);
                    box = findMinBox(r - 1, c, box);
                    box = findMinBox(r, c - 1, box);
                    box = findMinBox(r, c + 1, box);
                    box = adjustBox(box);
                    result.add(box);
                }
            }
        }

        result = deleteOverlaps(result);
        BoundingBox maxBox = returnMaxBox(result);
        printBox(maxBox);
    }

    private static List<BoundingBox> deleteOverlaps(List<BoundingBox> result) {
        for (int i = 0; i < result.size(); i++) {
            for (int j = i + 1; j < result.size(); j++) {
                try {
                    if (overLap(result.get(i), result.get(j))) {
                        result.remove(result.get(j));
                        result.remove(result.get(i));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("array out of bounds for: " + "i: " + i + " and j: " + j);
                }
            }
        }
        return result;
    }

    private static BoundingBox adjustBox(BoundingBox box) {
        box.left++;
        box.top++;
        box.right++;
        box.bottom++;
        return box;
    }

    private static BoundingBox returnMaxBox(List<BoundingBox> boxes) {
        int max = 0;
        BoundingBox largestBox = new BoundingBox();
        if (boxes.size() == 0) {
            System.exit(0);
        }
        for (BoundingBox b : boxes) {
            if (getArea(b) > max) {
                max = getArea(b);
                largestBox = b;
            }
        }

        return largestBox;
    }

    private static int getArea(BoundingBox box) {
        return (box.bottom - box.top + 1) * (box.right - box.left + 1);
    }

    private static boolean overLap(BoundingBox box1, BoundingBox box2) {
        boolean checkRight = box1.right > box2.left && box1.right < box2.right;
        boolean checkLeft = box1.left > box2.left && box1.left < box2.right;
        boolean checkTop = box1.top > box2.top && box1.top < box2.bottom;
        boolean checkBottom = box1.bottom > box2.top && box1.bottom < box2.bottom;

        return (checkTop && checkRight) || (checkTop && checkLeft) || (checkBottom && checkRight) || (
            checkBottom && checkLeft);
    }

    private static BoundingBox findMinBox(int r, int c, BoundingBox box) {
        if (r < 0 || r >= map.length || c < 0 || c >= map[0].length || visited[r][c]
            || map[r][c] == '-') {
            return box;
        }
        visited[r][c] = true;
        box.right = Math.max(c, box.right);
        box.left = Math.min(c, box.left);
        box.bottom = Math.max(r, box.bottom);
        box.top = Math.min(r, box.top);

        findMinBox(r + 1, c, box);
        findMinBox(r - 1, c, box);
        findMinBox(r, c - 1, box);
        findMinBox(r, c + 1, box);

        return box;
    }

    private static char[][] initalizeMap(String fileName) throws IOException {
        int[] dimensions = getLineCount(fileName);

        int rowCount = dimensions[0];
        int columnLength = dimensions[1];

        char[][] map = new char[rowCount][columnLength];

        InputStream stream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        String line;
        int r = 0;
        int c = 0;

        while ((line = br.readLine()) != null) {
            String[] letters = line.split("");
            for (String s : letters) {
                map[r][c % columnLength] = s.charAt(0);
                c++;
            }
            r++;
        }

        visited = new boolean[map.length][map[0].length];
        return map;
    }

    private static int[] getLineCount(String fileName) throws IOException {

        InputStream stream = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        int lineCount = 0;
        int lineWidth = 0;
        String line;

        while ((line = br.readLine()) != null) {
            lineWidth = line.length();
            lineCount++;
        }

        return new int[]{lineCount, lineWidth};
    }


}



