import java.util.Stack;

/**
 * Created by Gordon on 4/12/14.
 *
 * plan:
 * Check if the matrix if the children have a basepair for our index. IF they do, then the current node also has
 * a base pair. That means for the current node has the options of all the bases that pair, and the the children
 * have all of the pairs if the pair comes from below it, or all the bases if the pair doesn't. The score for a node
 * is the transitions + sankoff pairwise if has a basepair, or the sum of the max of the 2 individual ow.
 * Run single sankoff on all of the for sure single bases
 * run pair sankoff, which at the root iterates over all the possible pairs, choosing the one with the best sankoff
 * Normally you call Sankoff, and it theoretically makes each node belw
 *
 *
 * When running Sankoff pair, youre options for a node are only going to be the 6 pairs
 * For the children with pairs, then you run Sankoff pairs on that child, and return the best
 * score for you're given pair
 * For the child without a pair, you are going to run
 * SankoffPairRecursion(index1, index2, base1, base2) returns the score for the two bases. The score is the sum of
 * max( the transitions + the score of sankoffPairRecursion + the score of the 2 sankoff individuals)
 *
 * SankoffSingleRecursion(node, index, base)
 *      return max{transiton of base to basec0 + transition of base to baseC1
 *      + sankoffSingleRecursion(c0, index, baseC0) + sankoffSingleRecursion(c1, index, baseC1} --> try all the
 *      combinations for Basec0 and Basec1
 *
 * SankoffPairwiseRecursion(node, i0, i1, b0, b1)
 *      if c0 and c1 both contain the basepair{
 *          return max (transition b0 to c0b0 + transition b1 to c0b1 + transition b0 to c1b0 + transition b1 to c1b1 +
 *          SankoffPairwiseRecursion(c0, i0, i0, c0b0, c0b1) + SankoffPairwiseRecursion(c1, i0, i1, c1b0, c1b1) -->
 *          try all the possible combinations of c0b0 and c0b1 that form pairs and c1b0 and c1b1 that form pairs
*       }
*       if c0 contains the basepair and c1 doesn't{
 *          return max(transition b0 to c0b0 + transition b1 to c0b1 + transition b0 to c1b0 + transition b1 to c1b1 +
 *          SankoffSingleRecursion(c1, i0, c1b0) + SankoffSingleRecursion(c1, i1, c1b1) +
 *          SankoffPairwiseRecursion(c0, i0, i0, c0b0, c0b1) --> try all the combinations of c0b0 and c1b1 that form
 *          pairs, and all 16 combinations for c1b0 and c1b1
 *      }
 *
 */
public class SankoffwithStructure2 {
    public static int SankoffwithStructure(PhyloTree tree){
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        PhyloTreeNode curNode = tree.getRoot();
        s.push(curNode);
        while(!s.empty()){
            for(int i = 0; i < curNode.getChildren().size(); i++){
                s.push(curNode.getChildren().get(i));
            }
            PhyloTreeNode curNodeUp;
            if(curNode.getChildren().size() < 2){
                PhyloTreeNode curNodeParent = curNode.getParent();
                String folding = getFolding(curNode);
                for(int i = 0; i < curNode.getSequence().length(); i++){
                    if(curNode.getFolding().charAt(i) == '<' || curNode.getFolding().charAt(i) == '>'){
                        curNode.setLChildBPs(i, true);
                        curNode.setRChildBPs(i, true);
                    }
                    else{
                        curNode.setLChildBPs(i, false);
                        curNode.setRChildBPs(i, false);
                    }
                 }
                while(curNode != tree.getRoot()){
                    for(int i = 0; i < curNode.getSequence().length(); i++){
                        if( curNodeParent.getChildren().get(0) == curNode){
                            if(curNode.getRChildBPs(i) || curNode.getLChildBPs(i))
                        }
                    }
                    curNodeUp = curNodeUp.getParent();
                    curNode = cur
                }
            }
        }











        return 0;
    }

    private static String getFolding(PhyloTreeNode curNode) {
        return null;
    }
}
