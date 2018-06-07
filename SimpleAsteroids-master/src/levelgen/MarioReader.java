package levelgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import ntuple.LevelView;
import utilities.JEasyFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MarioReader {

    public static Map<Character, Integer> tiles = new HashMap();

    static {
        tiles.put('X', 0);
        tiles.put('S', 1);
        tiles.put('-', 2);
        tiles.put('?', 3);
        tiles.put('Q', 4);
        tiles.put('E', 5);
        tiles.put('<', 6);
        tiles.put('>', 7);
        tiles.put('[', 8);
        tiles.put(']', 9);
        tiles.put('o', 10);
    }

    public static HashMap<Integer, Color> tileColors = new HashMap();

    public static HashMap<Integer,BufferedImage> icons = new HashMap();
    static {
        readIcons();
    }

    public static int border = 11;
    static {
        tileColors.put(0, Color.darkGray);
        tileColors.put(1, Color.yellow);
        tileColors.put(2, new Color( 126,192,238));
        tileColors.put(3, Color.red);
        tileColors.put(4, Color.blue);
        tileColors.put(5, Color.magenta);
        tileColors.put(6, Color.green);
        tileColors.put(7, Color.black);
        tileColors.put(8, new Color(178,34,34));
        tileColors.put(9, new Color(165, 42, 42));
        tileColors.put(10, Color.pink);
        tileColors.put(border, Color.cyan);
    }

    static int targetWidth = 28;

    public static void main(String[] args) throws Exception {

        // showLevels();

        // createLevels();

        System.out.println("Icons: " + icons);

    }

    public static void readIcons() {

        for (int i=0; i<=10; i++) {
            try {
                String filename = String.format("sprites/mario/encoding_%d.png", i);
                // System.out.println("Reading: " + filename);
                BufferedImage img = ImageIO.read(new File(filename));
                icons.put(i, img);
            } catch (Exception ex) {

                // System.out.println(ex);
            }
        }

    }

    public static void showLevels() throws Exception {
        // String inputFile = "data/mario/example.txt";

        String inputDirectory = "data/mario/levels/";

        String outputFile = "data/mario/example.json";

        // need to iterate over all the files in a directory

        File file = new File(inputDirectory);
        String[] fileList = file.list();

        for (String inputFile : fileList) {
            try {
                System.out.println("Reading: " + inputFile);
                int[][] level = readLevel(new Scanner(new FileInputStream(inputDirectory + inputFile)));
                LevelView levelView = new LevelView(flip(level)).setColorMap(tileColors).setCellSize(10);
                new JEasyFrame(levelView, inputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void createLevels() throws Exception {
        // String inputFile = "data/mario/example.txt";

        String inputDirectory = "data/mario/levels/";

        String outputFile = "data/mario/example.json";

        // need to iterate over all the files in a directory

        ArrayList<int[][]> examples = new ArrayList<>();

        File file = new File(inputDirectory);
        String[] fileList = file.list();

        for (String inputFile : fileList) {
            try {
                System.out.println("Reading: " + inputFile);
                int[][] level = readLevel(new Scanner(new FileInputStream(inputDirectory + inputFile)));
                addData(examples, level);
                System.out.println(level);
                System.out.println("Read: " + inputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // System.out.println(examples);

        System.out.println("Processed examples");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String out = gson.toJson(examples);
        System.out.println("Created JSON String");

        // System.out.println(out);

        PrintWriter writer = new PrintWriter(outputFile);

        writer.print(out);
        writer.close();

        System.out.println("Wrote file with " + examples.size() + " examples");

    }

    public static int[][] flip(int[][] x) {
        int[][] y = new int[x[0].length][x.length];
        for (int i=0; i<x.length; i++) {
            for (int j=0; j<x[0].length; j++) {
                y[j][i] = x[i][j];
            }
        }
        return y;
    }

    static void addData(ArrayList<int[][]> examples, int[][] level) {
        int h = level.length;

        for (int offset = 0; offset < level[0].length - 1 - targetWidth; offset++) {
            int[][] example = new int[h][targetWidth];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    example[y][x] = level[y][x + offset];
                }
            }
            examples.add(example);
        }
    }

//    static int[][][][] makeExamples(int[][] level) {
//
//    }

    static int[] oneHot(int x) {
        // System.out.println("Tiles size: " + tiles.size());
        int[] vec = new int[tiles.size()];
        // System.out.println("Index = " + x);
        vec[x] = 1;
        return vec;
    }

    public static int[][] readLevel(Scanner scanner) throws Exception {
        String line;
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            width = line.length();
            lines.add(line);
            // System.out.println(line);
        }

        int[][] a = new int[lines.size()][width];
        System.out.println("Arrays length: " + a.length);
        for (int y = 0; y < lines.size(); y++) {
            System.out.println("Processing line: " + lines.get(y));
            for (int x = 0; x < width; x++) {
                a[y][x] = tiles.get(lines.get(y).charAt(x));
            }
        }

        return a;
    }

}
