import org.omg.CosNaming._BindingIteratorImplBase;

import java.io.*;
import java.util.*;

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
 *Bugs:
 * -wont handle gaped sequences very well
 * Steps:
 * 1)Get all of the leafs, get string that represents their base pairing
 * 2)Set all of the basepairs array for the children
 * 3)Recursively set all of the basepair arrays for the nodes above them
 *
 */
public class SankoffwithStructure2 {

    public int SankoffwithStructure(PhyloTree tree) throws IOException {
        /**TODO: Update the data structure of the nodes to be a 2D array to store best values
         *just like what we have now, except make it 2D for every spot in the sequence
         */
        getFolding(tree);
        Collection<String> singleBases = new ArrayList<String>();
        singleBases.add("A");
        singleBases.add("C");
        singleBases.add("G");
        singleBases.add("U");
        singleBases.add(".");
        Collection<String> pairedBases = new ArrayList<String>();
        pairedBases.add("CG");
        pairedBases.add("GC");
        pairedBases.add("GU");
        pairedBases.add("UG");
        pairedBases.add("AU");
        pairedBases.add("UA");
        ArrayList<String> newSequence = new ArrayList<String>();
        int parsimonyScore = 0;
        PhyloTreeNode root = tree.getRoot();
        for(int i = 0; i < root.getSequence().length(); i++){
            if(root.getBasePairs().get(i) == -1){
                Iterator<String> it = singleBases.iterator();
                int curScore = -MainMethodClass.INF;
                int bestScore = -MainMethodClass.INF;
                String curBase = "";
                String bestBase = "";
                while(it.hasNext()){
                    curBase = it.next();
                    curScore = sankoffSingle(tree.getRoot(), curBase, i, singleBases);
                    if(curScore > bestScore){
                        bestBase = curBase;
                        bestScore = curScore;
                    }
                }
                newSequence.add(i, bestBase);
                parsimonyScore += bestScore;
            }
            else if(root.getBasePairs().get(i) > i){
                int j = root.getBasePairs().get(i);
                Iterator<String> it = pairedBases.iterator();
                String curBases = "";
                String bestBases = "";
                int bestScore = -MainMethodClass.INF;
                int curScore = -MainMethodClass.INF;
                while(it.hasNext()){
                    curBases = it.next();
                    curScore = sankoffPairs(tree.getRoot(), curBases, i, j, pairedBases);
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestBases = curBases;
                    }
                }
                newSequence.add(i, bestBases.substring(0,1));
                newSequence.add(j, bestBases.substring(1));
                parsimonyScore += bestScore;
            }
        }
        //TODO: Go through and get sequences from ifBaseIsParent 2D array
        return parsimonyScore;
    }

    //Case 1: Both of the parents have basepairing
        //TODO: Make it so that the basepairing with more children under it is the one chosen
        //Score is max
    private int sankoffPairs(PhyloTreeNode node, String curBases, int i, int j, Collection<String> pairedBases) {
        //Sample array {5,,-1,
        boolean LSinglei = false;
        boolean LSinglej = false;
        boolean RSinglei = false;
        boolean RSinglej = false;
        PhyloTreeNode childL = node.getChildren().get(0);
        PhyloTreeNode childR = node.getChildren().get(1);
        if(childL.getBasePairs().get(i) == -1) LSinglei = true;
        if(childR.getBasePairs().get(i) == -1) RSinglei = true;
        if(childL.getBasePairs().get(j) == -1) LSinglei = true;
        if(childR.getBasePairs().get(j) == -1) RSinglei = true;
        int scoreR;
        int scoreL;
        int transR;
        int transL;
        if(LSinglei && LSinglej && RSinglei && RSinglej){
            String curL;
            String curR;
            String bestR;
            String bestL;
            int curScore;
            int bestScore;
            Iterator<String> itL = pairedBases.iterator();
            while(itL.hasNext()){
                curL = itL.next();
                Iterator<String> itR = pairedBases.iterator();
                while(itR.hasNext()){
                    curR = itR.next();
                    try{
                        scoreR = childL.getSankoffScore()
                    }
                }
            }
        }




    }

    private int sankoffSingle(PhyloTreeNode node, String curBase, int index, Collection<String> singleBases) {
        int scoreL;
        int scoreR;
        int transitionR;
        int transitionL;
        int curScore;
        int bestScore = -MainMethodClass.INF;
        String curBaseR;
        String curBaseL;
        String bestBaseR = "";
        String bestBaseL = "";
        PhyloTreeNode childL;
        PhyloTreeNode childR;
        if(node.getChildren().size() < 2){
            if((node.getSequence().substring(index, index+1)).equals(curBase)) return 0;
            else return -MainMethodClass.INF;
        }
        else{
            childL = node.getChildren().get(0);
            childR = node.getChildren().get(1);
            Iterator<String> itL = singleBases.iterator();
            Iterator<String> itR = singleBases.iterator();
            while(itL.hasNext()){
                curBaseL = itL.next();
                while(itR.hasNext()){
                    curBaseR = itR.next();
                    try{
                        scoreL = childL.getSankoffScore(index, curBaseL);
                    }catch(IndexOutOfBoundsException e){
                        scoreL = sankoffSingle(childL, curBaseL, index, singleBases);
                        childL.addSankoffScore(index, curBaseL, scoreL);
                    }
                    try{
                        scoreR = childR.getSankoffScore(index, curBaseR);
                    }catch(IndexOutOfBoundsException e){
                        scoreR = sankoffSingle(childR, curBaseR, index, singleBases);
                        childR.addSankoffScore(index, curBaseR, scoreR);
                    }
                    transitionL = MainMethodClass.cost(curBase, curBaseL);
                    transitionR = MainMethodClass.cost(curBase, curBaseR);
                    curScore = scoreL + scoreR + transitionL + transitionR;
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestBaseL = curBaseL;
                        bestBaseR = curBaseR;
                    }
                }
            }
            childL.setBaseIfParent(index, curBase, bestBaseL);
            childR.setBaseIfParent(index, curBase, bestBaseR);
        }
        return bestScore;
    }


    private void getFolding(PhyloTree tree) throws IOException {
        PhyloTreeNode curNode = tree.getRoot();
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        s.push(curNode);
        while(!s.empty()){
            curNode = s.pop();
            if(curNode.getChildren().size() == 2){
                for(int i = 0; i < curNode.getChildren().size(); i++){
                    s.push(curNode.getChildren().get(i));
                }
            }
            else{
                foldingFromVienna(curNode);
                String sequence = curNode.getSequence();
                String folding = curNode.getFolding();
                for(int i = 0; i < curNode.getSequence().length(); i++){
                    if(folding.charAt(i) == '<'){
                        int brackets = 1;
                        int j;
                        for(j = i; j < folding.length(); j++){
                            if(folding.charAt(j) == '<') brackets++;
                            if(folding.charAt(j) == '>') brackets--;
                            if(brackets == 0) break;
                        }
                        curNode.setBasePair(i,j);
                    }
                    else curNode.setNoBP(i);
                }
                PhyloTreeNode curNodeUp = curNode;
                while(curNodeUp.getParent() != null){
                    curNodeUp = curNode.getParent();
                    for(int i = 0; i < curNode.getBasePairs().size(); i++){
                        if(curNode.getBasePairs().get(i) > i ){
                            curNodeUp.setBasePair(i,curNode.getBasePairs().get(i));
                        }
                    }
                }
            }
        }
    }

    private void foldingFromVienna(PhyloTreeNode curNode) throws IOException {
        String command = "C:\\Users\\Gordon\\Dropbox\\Winter2014\\Comp401\\ViennaRNAPackage\\rnaFold.exe";
        BufferedReader inp;
        BufferedWriter out;
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        InputStream ips = p.getInputStream();
        OutputStream ops = p.getOutputStream();
        inp = new BufferedReader(new InputStreamReader(ips));
        out = new BufferedWriter(new OutputStreamWriter(ops));
        String seq = curNode.getSequence();
        seq = seq.replace(",", "");
        seq = seq.replace(".", "");
        seq = seq.concat("\n");
        out.write(seq);
        out.flush();
        String line;
        int i = 0;
        while(i < 2 && ( line = inp.readLine()) != null){
            if(i == 1){
                int lastClosed = line.lastIndexOf(")");
                int lastOpen = MainMethodClass.findClosestOpen(lastClosed, line);
                String energy = line.substring(lastOpen);
                line = line.substring(0, lastOpen);
                curNode.setFolding(line);
                energy = energy.replace("(", "");
                energy = energy.replace(")", "");
                energy =  energy.trim();
                curNode.setEnergy(Double.parseDouble(energy));
            }
            i++;
        }
        p.destroy();

    }
}
