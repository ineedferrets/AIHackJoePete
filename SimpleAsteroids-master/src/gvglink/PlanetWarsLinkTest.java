package gvglink;

import controllers.multiPlayer.ea.Agent;
import core.player.AbstractMultiPlayer;
import evodef.EvoAlg;
import evodef.GameActionSpaceAdapterMulti;
import ga.SimpleRMHC;
import ontology.Types;
import planetwar.GameState;
import planetwar.PlanetWarView;
import tools.ElapsedCpuTimer;
import utilities.ElapsedTimer;
import utilities.JEasyFrame;
import utilities.StatSummary;

import java.util.Random;

public class PlanetWarsLinkTest {

    // todo: show a graphic of the rollout predictions
    // split this in to two parts
    // for each action, we need to collect the data
    // then we need to display the data
    // each time plot the fitness of each sample versus the length
    // of the rollout - this will give a good idea of what we need to plot...

    // todo: implement a collision mechanism

    // todo: implement a nice graphic that shows transfer from planet to buffer
    // this is very easy to do - but question of whether we need a timed or instant movement...

    public static void main(String[] args) throws Exception {
        int nTrials = 100;
        ElapsedTimer timer = new ElapsedTimer();
        StatSummary ss = new StatSummary();
        for (int i=0; i<nTrials; i++) {
            System.out.println("Game: " + i);
            ss.add(runTest());
            System.out.println();
        }
        System.out.println(ss);
        System.out.println(timer);
    }

    public static double runTest() throws Exception {

        PlanetWarsLinkState state = new PlanetWarsLinkState();
        // state.state.

        GameState.includeBuffersInScore = true;


        PlanetWarView view = null;
        view = new PlanetWarView((GameState) state.state);
        // JEasyFrame frame = new JEasyFrame(CaveView, "Simple Planet Wars");
//        KeyController controller = new KeyController();
//        frame.addKeyListener(controller);

        // now play
        Random random = new Random();


        AbstractMultiPlayer player1, player2;
        GameActionSpaceAdapterMulti.visual = false;

        // DefaultMutator.totalRandomChaosMutation = true;

//        controllers.singlePlayer.sampleOLMCTS.Agent olmcts =
//                new controllers.singlePlayer.sampleOLMCTS.Agent(linkState, timer);

        int idPlayer1 = 0;
        int idPlayer2 = 1;

        ElapsedCpuTimer timer = new ElapsedCpuTimer();

        player2 = new controllers.multiPlayer.discountOLMCTS.Agent(state.copy(), timer, idPlayer2);
        // player2 = new controllers.multiPlayer.sampleOLMCTS.Agent(state.copy(), timer, idPlayer2);

        // try the evolutionary players

        int nResamples = 1;
        EvoAlg evoAlg = new SimpleRMHC(nResamples);

        int nEvals = 133;
        // evoAlg = new SlidingMeanEDA().setHistoryLength(20);


        Agent evoAgent = new controllers.multiPlayer.ea.Agent(state.copy(), timer, evoAlg, idPlayer1, nEvals);
        evoAgent.sequenceLength = 15;
        evoAgent.setUseShiftBuffer(true);
        player1 = evoAgent;

        // player2 = new controllers.multiPlayer.ea.Agent(linkState, timer, evoAlg2, idPlayer2, nEvals);
        // player2 = new controllers.multiPlayer.ea.Agent(linkState, timer, new SimpleRMHC(nResamples), idPlayer2, nEvals);

        // player1 = new controllers.multiPlayer.smlrand.Agent();
        // player2 = new controllers.multiPlayer.smlrand.Agent();
        // player2 = new controllers.multiPlayer.doNothing.Agent(state, timer, 1);

        // EvoAlg evoAlg2 = new SimpleRMHC(2);

        // player1 = new controllers.multiPlayer.ea.Agent(linkState, timer, evoAlg2, idPlayer1, nEvals);


        // player1 =
        int thinkingTime = 50; // in milliseconds
        int delay = 200;

        // player = new controllers.singlePlayer.sampleRandom.Agent(stateObs, timer);

        // check that we can play the game

        int nSteps = 200;
        view = null;

        for (int i = 0; i < nSteps; i++) {

            timer = new ElapsedCpuTimer();
            timer.setMaxTimeMillis(thinkingTime);

            Types.ACTIONS action1 = player1.act(state.copy(), timer);

            timer = new ElapsedCpuTimer();
            timer.setMaxTimeMillis(thinkingTime);
            Types.ACTIONS action2 = player2.act(state.copy(), timer);
            // §action2 =

            state.advance(new Types.ACTIONS[]{action1, action2});

            if (view != null) {
                view.update((GameState) state.state);
                Thread.sleep(delay);
            }
            // System.out.println("Game tick: " + i);
        }
        System.out.println("Game Score: " + state.getGameScore());
        // System.out.println("MCTS Evals: " + TreeNode);
        return state.getGameScore() > 0 ? 1 : 0;
    }




}

