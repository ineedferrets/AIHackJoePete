package spinbattle.mcts;

import controllers.multiPlayer.discountOLMCTS.TreeNode;
import ontology.Types;

import java.util.Random;

public class MCTSPlayer {

    public TreeNode m_root;

    int[] NUM_ACTIONS;
    Types.ACTIONS[][] actions;

    public Random m_rnd;
    public int id, oppID, n_players;
}
