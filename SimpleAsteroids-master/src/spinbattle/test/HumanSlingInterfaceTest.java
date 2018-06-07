package spinbattle.test;

import agents.dummy.RandomAgent;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import core.player.AbstractMultiPlayer;
import ggi.agents.EvoAgentFactory;
import ggi.core.SimplePlayerInterface;
import gvglink.SpinBattleLinkState;
import logger.sample.DefaultLogger;
import spinbattle.actuator.SourceTargetActuator;
import spinbattle.core.FalseModelAdapter;
import spinbattle.core.SpinGameState;
import spinbattle.log.BasicLogger;
import spinbattle.params.Constants;
import spinbattle.params.SpinBattleParams;
import spinbattle.players.HeuristicLauncher;
import spinbattle.ui.MouseSlingController;
import spinbattle.ui.SourceTargetMouseController;
import spinbattle.view.SpinBattleView;
import sun.java2d.pipe.SpanShapeRenderer;
import tools.ElapsedCpuTimer;
import utilities.JEasyFrame;
import spinbattle.discountOLMCTS.Agent;
import planetwar.*;


import java.util.Random;

public class HumanSlingInterfaceTest {

    public static int ROLLOUT_DEPTH = 200;
    public static double DISCOUNT_FACTOR = 1.20;
    public static double K = Math.sqrt(2);

    public static void main(String[] args) throws Exception {
        // to always get the same initial game
        long seed = 10;
        seed = new Random().nextLong();
        SpinBattleParams.random = new Random(seed);
        SpinBattleParams params = new SpinBattleParams();

        params.maxTicks = 5000;
        params.gravitationalFieldConstant *= 1;
        params.transitSpeed *= 1;
        params.width = 600;
        params.height = 450;
        params.nPlanets = 40; // 10 + new Random().nextInt(50);

        System.out.println("Selected n planets: " + params.nPlanets);

        SpinGameState gameState = new SpinGameState().setParams(params).setPlanets();
        gameState.actuators[Constants.playerOne] = new SourceTargetActuator().setPlayerId(Constants.playerOne);
        gameState.actuators[Constants.playerTwo] = new SourceTargetActuator().setPlayerId(Constants.playerTwo);
        System.out.println("nPlanets made = " + gameState.planets.size());
        // BasicLogger basicLogger = new BasicLogger();
        // gameState.setLogger(new DefaultLogger());
        SpinBattleView view = new SpinBattleView().setParams(params).setGameState(gameState);
        HeuristicLauncher launcher = new HeuristicLauncher();
        String title = "Spin Battle Game" ;
        JEasyFrame frame = new JEasyFrame(view, title + ": Waiting for Graphics");
        frame.setLocation(400, 100);

        //MouseSlingController mouseSlingController = new MouseSlingController();
        //mouseSlingController.setGameState(gameState).setPlayerId(Constants.playerOne);
        //view.addMouseListener(mouseSlingController);

        //int launchPeriod = 400; // params.releasePeriod;
        waitUntilReady(view);

        //SimplePlayerInterface mctsAgent = new GVGAIWrapper().setAgent(new Agent(gameState,null, Constants.playerTwo));

        //SimplePlayerInterface mctsAgent1 = getMCTSAgent(gameState, Constants.playerOne);

        SimplePlayerInterface mctsAgent2 = getMCTSAgent(gameState, Constants.playerTwo);

        SimplePlayerInterface randomAgent = new RandomAgent();

        SimplePlayerInterface evoAgent = new EvoAgentFactory().getAgent().setVisual();
        evoAgent = new FalseModelAdapter().setParams(params).setPlayer(evoAgent);
        int[] actions = new int[2];

        for (int i=0; i<=5000 && !gameState.isTerminal(); i++) {
            actions[Constants.playerOne] = randomAgent.getAction(gameState.copy(), Constants.playerOne);
            //actions[Constants.playerOne] = evoAgent.getAction(gameState.copy(), Constants.playerOne);
            actions[Constants.playerTwo] = mctsAgent2.getAction(gameState.copy(), Constants.playerTwo);
            //System.out.println(actions[Constants.playerTwo]);
            gameState.next(actions);
            //mouseSlingController.update();
            //launcher.makeTransits(gameState, Constants.playerOne);
            view.setGameState((SpinGameState) gameState.copy());
            view.repaint();
            frame.setTitle(title + " : " + i); //  + " : " + CaveView.getTitle());
            Thread.sleep(20);
        }
        System.out.println(gameState.isTerminal());
    }

    static void waitUntilReady(SpinBattleView view) throws Exception {
        int i = 0;
        while (view.nPaints == 0) {
            // System.out.println(i++ + " : " + CaveView.nPaints);
            Thread.sleep(50);
        }
    }

    static GVGAIWrapper getMCTSAgent(SpinGameState gameState, int playerId) {
        ElapsedCpuTimer timer = new ElapsedCpuTimer();
        SpinBattleLinkState linkState = new SpinBattleLinkState(gameState);
        AbstractMultiPlayer agent =
                new Agent(linkState.copy(), timer, playerId, ROLLOUT_DEPTH, DISCOUNT_FACTOR, K);

        GVGAIWrapper wrapper = new GVGAIWrapper().setAgent(agent);
        return wrapper;

    }
}
