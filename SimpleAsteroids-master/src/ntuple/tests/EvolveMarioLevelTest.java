package ntuple.tests;

import evodef.EvoAlg;
import evodef.EvolutionListener;
import evodef.EvolutionLogger;
import evodef.SolutionEvaluator;
import evodef.DefaultMutator;
import ga.SimpleRMHC;
import levelgen.MarioReader;
import ntuple.ConvNTuple;
import ntuple.LevelView;
import ntuple.operator.ConvMutator;
import plot.LineChart;
import plot.LineChartAxis;
import utilities.ElapsedTimer;
import utilities.JEasyFrame;
import utilities.StatSummary;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static levelgen.MarioReader.*;

public class EvolveMarioLevelTest implements EvolutionListener {

    static int imageWidth = 50, imageHeight = 16;
    static int filterWidth = 4, filterHeight = 4;
    static int stride = 1;

    static boolean useInitialSeed = false;


    public static void main(String[] args) throws Exception {

        int[][] level = getAndShowLevel(true);

        int nTrials = 1;

        SimpleRMHC simpleRMHC = new SimpleRMHC();
        DefaultMutator mutator = new DefaultMutator(null);
        mutator.flipAtLeastOneValue = true;
        mutator.pointProb = 2;

        mutator.setSwap(true);

        simpleRMHC.setMutator(mutator);

        // EvoAlg evoAlg = simpleRMHC;
        // evoAlg = new SlidingMeanEDA().setHistoryLength(30);
        // evoAlg = new CompactSlidingGA();

        int nEvals = 20000;
        StatSummary results = new StatSummary();
        EvolveMarioLevelTest evolver = new EvolveMarioLevelTest();
        for (int i = 0; i < nTrials; i++) {
            ElapsedTimer timer = new ElapsedTimer();
            results.add(evolver.runTrial(simpleRMHC, nEvals, level));
            System.out.println(timer);
        }
    }

    // this should really take any type of EA, but at the moment it
    // is restricted to the SimpleRMHC to allow a bespoke mutation operator
    // to be plugged in
    public double runTrial(SimpleRMHC ea, int nEvals, int[][] sample) {
        int nDims = imageWidth * imageHeight;
        int mValues = distinctValues(sample);
        System.out.println("Distinct values = " + mValues);
        ConvNTuple convNTuple = getTrainedConvNTuple(sample, mValues);
        System.out.println("nSamples: " + convNTuple.nSamples());

        // set the "clever" mutation operator
        ea.setMutator(new ConvMutator().setConvNTuple(convNTuple).setForceBorder(true));

        if (useInitialSeed) {
            ea.setInitialSeed(generateSeed(sample));
        }
        SolutionEvaluator evaluator = new EvalConvNTuple(nDims, mValues).setConvNTuple(convNTuple);


        SolutionEvaluator trainingEvaluator = new EvalConvNTuple(nDims, mValues).setConvNTuple(convNTuple);


        double fitnessFull = trainingEvaluator.evaluate(flatten(sample));
        String labelFull = String.format("Full Width Training Sample: %.6f", fitnessFull);

        LevelView.showMaze(flatten(sample), sample.length, sample[0].length, labelFull, tileColors);
        showSamples(sample, trainingEvaluator);

        evaluator.logger().setListener(this);
        int[] solution = ea.runTrial(evaluator, nEvals);

        // can set entire solution to the most likely individual
        // solution = setAll(solution, 2);

        double fitness = evaluator.evaluate(solution);
        String label = String.format("Fitness: %.6f", fitness);

        // solution = flatten(toRect())
        plotData(evaluator.logger().fa);
        LevelView.showMaze(solution, imageWidth, imageHeight, label, tileColors);

        // showSamples(sample, evaluator);

        return fitness;
    }

    public static int[][] getAndShowLevel(boolean show) throws Exception {
        String inputFile = "data/mario/levels/mario-2-1.txt";

        System.out.println("Reading: " + inputFile);
        int[][] level = flip(readLevel(new Scanner(new FileInputStream(inputFile))));
        level = border(level);
        if (show) {
            LevelView levelView = new LevelView(level).setColorMap(tileColors).setCellSize(10);
            new JEasyFrame(levelView, inputFile);
        }
        return level;
    }

    public int[][] subSample(int[][] a, int x, int y, int w, int h) {
        int[][] b = new int[w][h];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                b[i][j] = a[x+i][y+j];
            }
        }
        return b;
    }

    public static int[][] border(int[][] a) {
        int[][] b = new int[a.length + 2][a[0].length + 2];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                if (i == 0 || j == 0 || i == b.length - 1 || j == b[0].length - 1) {
                    b[i][j] = MarioReader.border;
                } else {
                    b[i][j] = a[i - 1][j - 1];
                }
            }
        }
        // return a;
        return b;
    }

    public static int[][] setAll(int[][] a, int value) {
        int[][] b = new int[a.length][a[0].length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] = value;
            }
        }
        return b;
    }

    public static int[] setAll(int[] a, int value) {
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = value;
        }
        return b;
    }

    public static void printLevel(int[][] level) {
        for (int[] a : level) {
            System.out.println(Arrays.toString(a));
        }
    }

    private void showSamples(int[][] sample, SolutionEvaluator solutionEvaluator) {

        int gap=20;
        System.out.println("Sample Width: " + sample.length);


        for (int x=0; x<sample.length - imageWidth; x+=gap) {
            int[] sub = flatten(subSample(sample, x, 0, imageWidth, imageHeight));
            double fitness = solutionEvaluator.evaluate(sub);
            String label = String.format("Sample Slice Fitness: %.6f", fitness);
            LevelView.showMaze(sub, imageWidth, imageHeight, label, tileColors);
        }

        // also show a full sample

        // double fullFit = s

    }

    static Random random = new Random();

    private int[] generateSeed(int[][] sample) {
        int[] flatSample = flatten(sample);

        // now generate the initial seed by sampling directly from
        // the training sample

        int[] seed = new int[imageWidth * imageHeight];
        for (int i = 0; i < seed.length; i++) {
            int index = random.nextInt(flatSample.length);
            seed[i] = flatSample[index];
        }
        return seed;
    }

    private void plotData(ArrayList<Double> data) {
        LineChart lineChart = LineChart.easyPlot(data);
        int mid = (data.size() - 1) / 2;
        int end = data.size() - 2;
        System.out.println("Endpoint: " + end);
        lineChart.xAxis = new LineChartAxis(new double[]{0, mid, end});
        StatSummary ss = new StatSummary().add(data);
        lineChart.yAxis = new LineChartAxis(new double[]{ss.min(), ss.max()});
        lineChart.title = "Evolution of Fitness";
        lineChart.setXLabel("Iterations").setYLabel("Fitness");
        new JEasyFrame(lineChart, "KL-Based PCG");
    }

    private int distinctValues(int[][] a) {
        StatSummary ss = new StatSummary();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                ss.add(a[i][j]);
            }
        }
        return 1 + (int) ss.max();
    }

    static boolean wrapAround = false;

    public static ConvNTuple getTrainedConvNTuple(int[][] sample, int mValues) {
        int w = sample.length;
        int h = sample[0].length;

        System.out.println(w + " : " + h);

        ConvNTuple convNTuple = new ConvNTuple().setImageDimensions(w, h);
        convNTuple.setFilterDimensions(filterWidth, filterHeight);
        convNTuple.setMValues(mValues).setStride(stride);

        if (wrapAround)
            convNTuple.makeWrapAroundIndices();
        else
            convNTuple.makeIndices();
        convNTuple.reset();

        System.out.println("Address space size: " + convNTuple.addressSpaceSize());
        // System.out.println("Mean of empty summary: " + new StatSummary().mean());

        // now put some random data in to it
        ElapsedTimer timer = new ElapsedTimer();

        // 'x' is the Red Maze example explained here:
        // https://adamsmith.as/papers/wfc_is_constraint_solving_in_the_wild.pdf

        int[] x = flatten(sample);

        convNTuple.addPoint(x, 1);

        // now iterate over all the values in there
        System.out.println("Training finished: ");
        System.out.println(timer);

        // now check the klDiv

        System.out.println("Divergence = " + convNTuple.getKLDivergence(x, 1e-20));

//        // now add some random noise
//
//        for (int i=0; i<x.length; i++) {
//            if (random.nextDouble() < 0.01) {
//                x[i] = 0;
//            }
//        }
//
//        System.out.println("Divergence = " + convNTuple.getKLDivergence(x, 1e-20));
//


        // convNTuple.report();
        // System.out.println(timer);

        // now reset the indices to the true image size, but do not use wrap around
        convNTuple.setImageDimensions(imageWidth, imageHeight);

        if (wrapAround)
            convNTuple.makeWrapAroundIndices();
        else
            convNTuple.makeIndices();
        // convNTuple.report();

        return convNTuple;
    }

    static int[] flatten(int[][] a) {
        int n = a.length * a[0].length;
        int w = a.length;
        int[] x = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = a[i % w][i / w];
        }
        return x;
    }

    // the level CaveView update frequency should be tuned in order
    // to provide an animated CaveView of the evolutionary process
    // without slowing down the evolution too much

    // a figure of 5,000 may be reasonable
    static int freq = 1000;
    LevelView levelView;
    JEasyFrame levelFrame;

    @Override
    public void update(EvolutionLogger logger, int[] solution, double fitness) {
        if (logger.nEvals() % freq != 0) return;
        if (levelFrame == null) {
            // set them both up
            levelView = new LevelView(LevelView.toRect(solution, imageWidth, imageHeight));
            levelView.setColorMap(MarioReader.tileColors);
            levelFrame = new JEasyFrame(levelView, "Title");
        }
        levelView.setTiles(solution);
        levelFrame.setTitle(String.format("Evals: %d;\t fitness: %.3f", logger.nEvals(), fitness));
    }
}
