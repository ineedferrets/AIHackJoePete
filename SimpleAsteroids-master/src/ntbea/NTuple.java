package ntbea;

import evodef.SearchSpace;
import ntuple.TenSpace;
import ntuple.params.Param;
import utilities.StatSummary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by simonmarklucas on 13/11/2016.
 * <p>
 * Purpose is to define N-Tuples on a search space.
 * <p>
 * Each N-Tuple records the particular dimensions it samples.
 * <p>
 *     Then when a pattern is added, the N-Tuple looks up the values for
 *     its own dimensions and uses an IntArrayPattern as a hashkey
 *     using the particular values found in those dimensions
 *
 *     Each key is used to index to a StatSummary object in a HashMap.
 *     StatSummaries are created on demand
 */

public class NTuple {

    public static void main(String[] args) {
        // TenSpace is a nice easy one for testing
        SearchSpace searchSpace = new TenSpace(6);

        int[] tuple = new int[]{0, 1, 2};

        NTuple nt = new NTuple(searchSpace, tuple);

        int[] p = {1, 2, 3, 2, 1, 5};

        nt.add(p, 10);
        nt.add(p, 1);

        p[0] = 0;

        nt.add(p, -5);
        nt.printNonEmpty();
    }

    SearchSpace searchSpace;
    public int[] tuple;
    public HashMap<IntArrayPattern, StatSummary> ntMap;

    public int nSamples;
    int nEntries;


    public NTuple(SearchSpace searchSpace, int[] tuple) {
        this.searchSpace = searchSpace;
        this.tuple = tuple;
        reset();
    }

    public void reset() {
        nSamples = 0;
        nEntries = 0;
        ntMap = new HashMap<>();
    }

    public void add(int[] x, double v) {
        // for each address that occurs, we're going to store something
        StatSummary ss = getStatsForceCreate(x);
        ss.add(v);
        nSamples++;
    }

    public void add(int[] x, StatSummary ssIncoming) {
        // for each address that occurs, we're going to store something
        StatSummary ss = getStatsForceCreate(x);
        ss.add(ssIncoming);
        nSamples++;
    }

    public void printNonEmpty() {
        TreeSet<IntArrayPattern> orderedKeys = new TreeSet<>();
        orderedKeys.addAll(ntMap.keySet());
        for (IntArrayPattern key : orderedKeys) {
            StatSummary ss = ntMap.get(key);
            System.out.println(key + "\t " + ss.n() + "\t " + ss.mean() + "\t " + ss.sd());
            // System.out.println();
        }
    }

    public void printNonEmpty(Param[] params) {
        for (int i : tuple) {
            System.out.println(params[i].getName() + "\t ");
        }
        System.out.println();
        TreeSet<IntArrayPattern> orderedKeys = new TreeSet<>();
        orderedKeys.addAll(ntMap.keySet());
        for (IntArrayPattern key : orderedKeys) {
            StatSummary ss = ntMap.get(key);
            System.out.println(key + "\t " + ss.n() + "\t " + ss.mean());
        }
    }

    public String paramString(Param[] params, int[] ind) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ind.length; i++) {
            // sb.append(params[tuple[i]].getValue(ind[ind.length-1-i]) + "\t ");
            sb.append(params[tuple[i]].getValue(ind[i]) + "\t ");
        }
        return sb.toString();
    }

    /**
     * Get stats but force creation if it does not already exists
     *
     * @param x
     * @return
     */
    public StatSummary getStatsForceCreate(int[] x) {
        IntArrayPattern key = new IntArrayPattern().setPattern(x, tuple);
        StatSummary ss = ntMap.get(key);
        if (ss == null) {
            ss = new StatSummary();
            nEntries++;
            ntMap.put(key, ss);
        }
        return ss;
    }

    /**
     * For reporting we only want to know about the stats if they already exist.
     *
     * So this version provides that.
     *
     * @param x
     * @return
     */
    public StatSummary getStats(int[] x) {
        IntArrayPattern key = new IntArrayPattern().setPattern(x, tuple);
        return ntMap.get(key);
    }

    public int nSamples() {
        return nSamples;
    }

    public String toString() {
        return tuple.length + "\t " + Arrays.toString(tuple) + "\t " + nSamples  + "\t " + nEntries;
    }

}
