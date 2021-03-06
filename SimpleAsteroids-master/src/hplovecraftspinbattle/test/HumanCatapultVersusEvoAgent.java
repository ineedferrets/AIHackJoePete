package hplovecraftspinbattle.test;

import ggi.agents.EvoAgentFactory;
import ggi.core.SimplePlayerInterface;
import hplovecraftspinbattle.actuator.SourceTargetActuator;
import hplovecraftspinbattle.core.SpinGameState;
import hplovecraftspinbattle.log.BasicLogger;
import hplovecraftspinbattle.params.Constants;
import hplovecraftspinbattle.params.SpinBattleParams;
import hplovecraftspinbattle.ui.CatapultController;
import hplovecraftspinbattle.ui.MouseSlingController;
import hplovecraftspinbattle.view.SpinBattleView;
import utilities.JEasyFrame;

import java.util.Random;

public class HumanCatapultVersusEvoAgent {

    public static void main(String[] args) throws Exception {
        // to always get the same initial game
        SpinBattleParams.random = new Random(8);
        SpinBattleParams params = new SpinBattleParams();

        params.maxTicks = 5000;
        params.gravitationalFieldConstant *= 1;
        params.transitSpeed *= 1;
        SpinGameState gameState = new SpinGameState().setParams(params).setPlanets();
        gameState.actuators[1] = new SourceTargetActuator().setPlayerId(1);

        BasicLogger basicLogger = new BasicLogger();
        // gameState.setLogger(new DefaultLogger());
        SpinBattleView view = new SpinBattleView().setParams(params).setGameState(gameState);
        String title = "Spin Battle Game" ;
        JEasyFrame frame = new JEasyFrame(view, title + ": Waiting for Graphics");
        frame.setLocation(400, 100);
        MouseSlingController mouseSlingController = new MouseSlingController();
        mouseSlingController.setGameState(gameState).setPlayerId(Constants.playerOne);

        CatapultController catapultController = new CatapultController();
        catapultController.setGameState(gameState).setPlayerId(Constants.playerOne);

        // CaveView.addMouseListener(mouseSlingController);
        view.addMouseListener(catapultController);
        waitUntilReady(view);

        SimplePlayerInterface evoAgent = new EvoAgentFactory().getAgent().setVisual();
        int[] actions = new int[2];


        for (int i=0; i<=5000 && !gameState.isTerminal(); i++) {
            actions[1] = evoAgent.getAction(gameState.copy(), 1);
            gameState.next(actions);
            // mouseSlingController.update();
            catapultController.update();
            // launcher.makeTransits(gameState, Constants.playerOne);
            view.setGameState((SpinGameState) gameState.copy());
            view.repaint();
            frame.setTitle(title + " : " + i); //  + " : " + CaveView.getTitle());
            Thread.sleep(40);
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
}
