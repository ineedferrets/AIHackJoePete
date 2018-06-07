package gvglink;

import core.game.StateObservationMulti;
import ggi.core.AbstractGameState;
import ontology.Types;
import spinbattle.core.SpinGameState;
// import planetwar.GameState;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sml on 24/10/2016.
 */
public class SpinBattleLinkState
        extends StateObservationMulti {

    static int maxTick = 500;
    public static int nTicks = 0;

    // this class is already 2-player ready
    public AbstractGameState state;


    public static void main(String[] args) {
        System.out.println(Types.ACTIONS.values().length);
        for (Types.ACTIONS a : Types.ACTIONS.values()) {
            System.out.println(a + "\t " + a.ordinal());
        }
    }

    public SpinBattleLinkState() {
        // todo fix this to use a default state
        this(new SpinGameState());
    }

    public void setup(String actionFile, int randomSeed, boolean isHuman) {

        // this.state = state.defaultState();
    }

    static int nPlanets = 10;

    public SpinBattleLinkState(AbstractGameState state) {
        super(null);
        this.state = state;
        setGvgActions(null);
        // state.setNPlanets(nPlanets).setAlternateOwnerships().setRandomGrowthRates();
    }

    public StateObservationMulti copy() {

        return new SpinBattleLinkState(state.copy());
    }

    // static int[] scores = new int[Types.ACTIONS]


    public void advance(Types.ACTIONS action) {
        if (true) throw new RuntimeException("Not meant to call this!");
        // int[] a = new int[]{action.ordinal(), random.nextInt(actions.size())};
        // state.next(a);
    }


    ArrayList<Types.ACTIONS> gvgActions = null;

    public void setGvgActions(Types.ACTIONS[] actions) {
        gvgActions = new ArrayList<>();

        int actionLimit = state.nActions();

        // static int actionLimit = new SpinGameState().nActions();

        for (Types.ACTIONS a : Types.ACTIONS.values()) {
            if (gvgActions.size() < actionLimit) {
                gvgActions.add(a);
            }
        }
        // System.out.println("nActions = " + actions.size());
    }

    public void advance(Types.ACTIONS[] actions) {

        // System.out.println(Arrays.toString(actions));

        setGvgActions(actions);

        int[] a = new int[]{actions[0].ordinal(), actions[1].ordinal()};
        state.next(a);
        nTicks++;
    }


    static Random random = new Random();

    /**
     * Sets a new seed for the forward model's random generator (creates a new object)
     *
     * @param seed the new seed.
     */
    public void setNewSeed(int seed) {

        // state.random.setSeed(seed);
        // and generate a new default state
        // state = state.defaultState(seed);
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar.
     *
     * @return the available actions.
     */
    public ArrayList<Types.ACTIONS> getAvailableActions() {
        return gvgActions;
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar. If the parameter 'includeNIL' is true, the array contains the (always available)
     * NIL action. If it is false, this is equivalent to calling getAvailableActions().
     *
     * @param includeNIL true to include Types.ACTIONS.ACTION_NIL in the array of actions.
     * @return the available actions.
     */
    public ArrayList<Types.ACTIONS> getAvailableActions(boolean includeNIL) {
        return gvgActions;
    }

    public ArrayList<Types.ACTIONS> getAvailableActions(int playerID) {
        return gvgActions;
    }


    public int getNoPlayers() {
        return 2;
    }

    public double getGameScore() {
        return state.getScore();
    }

    public double getGameScore(int playerId) {
        if (playerId == 0) {
            return getGameScore();
        } else {
            return -getGameScore();
        }
    }

    public double getHeuristicScore() {
        return state.getScore();
    }

    public int getGameTick() {

        return -1; // state.getGameTick();
    }

    /**
     * Indicates if there is a game winner in the current observation.
     * Possible values are Types.WINNER.PLAYER_WINS, Types.WINNER.PLAYER_LOSES and
     * Types.WINNER.NO_WINNER.
     *
     * @return the winner of the game.
     */
    public Types.WINNER getGameWinner() {
        return Types.WINNER.NO_WINNER;
    }


    static Types.WINNER[] noWinners = new Types.WINNER[]{Types.WINNER.NO_WINNER, Types.WINNER.NO_WINNER};

    public Types.WINNER[] getMultiGameWinner() {
        return noWinners;
    }

    /**
     * Indicates if the game is over or if it hasn't finished yet.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return getGameTick() >= maxTick;
    }
}