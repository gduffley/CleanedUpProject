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
                    curScore = sankoffPairs(tree.getRoot(), curBases, i, pairedBases, singleBases);
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
    //The sankoffPairs will conserve BP for a pair of bases only if both basepairing[i] = j && basepairing[j] = i
    //Any other cases are essentially legacy cases of other basepairing below in the array
    //Works this way because the
    /*
    SankoffScores works such that the score of sankoffScores[i] = sankoffScores[j] if i and j are a pair
        This implies that if you call Sankoff pairs on them

     */



    /*Case 1: Both of the parents have the same basepairing
        TODO: Make it so that the basepairing with more children under it is the one chosen
        Score = max over all 36 combinations of bases {lChild.sankoffDoubleScores(bases, index)
        + rChild.sankoffDoubleScores(bases, index) + the 4 transitions}.
    Case 2: One of (left in this case) has same base pairing, other has none
        Score = max over the 6 possible combinations of paired bases for the pairs and the 25 combinations for the
        other, so 150 combinations {lChild.sankoffDoubleScores(bases, index) + rChild.sankoffSingles(base, index)
        + sankoffSingle(base, second index) + 4 transitions}
    Case 3: One of (left in this case) has the same base pairing and the other has
    */

    private int sankoffPairs(PhyloTreeNode node, String bases, int index1, Collection<String> pairedBases,
                             Collection<String> singleBases) {
        int index2 = node.getBasePairs().get(index1);
        if(node.getChildren().size() == 0){
            if(node.getSequence().charAt(index1) == bases.charAt(0) &&
                    node.getSequence().charAt(index2) == bases.charAt(1)) return 0;
            else return -MainMethodClass.INF;
        }
        PhyloTreeNode LChild = node.getChildren().get(0);
        PhyloTreeNode RChild = node.getChildren().get(1);
        boolean LChildSame = false;
        boolean RChildSame = false;
        boolean Rindex1pairing = false;
        boolean Rindex2pairing = false;
        boolean Lindex1pairing = false;
        boolean Lindex2pairing = false;


        if(LChild.getBasePairs().get(index1) == index2) LChildSame = true;
        if(RChild.getBasePairs().get(index1) == index2) RChildSame = true;
        if(LChild.getBasePairs().get(index1) > -1) Lindex1pairing = true;
        if(LChild.getBasePairs().get(index2) > -1) Lindex2pairing = true;
        if(RChild.getBasePairs().get(index1) > -1) Rindex1pairing = true;
        if(RChild.getBasePairs().get(index2) > -1) Rindex2pairing = true;
        //case1, both of the children have the same pairing

        if(LChildSame && RChildSame) return bothSame(LChild, RChild, index1, index2, bases, pairedBases, singleBases);
        if(LChildSame && !Rindex1pairing && !Rindex2pairing) return oneSameOneNoPairing(index1, index2, LChild, RChild,
                bases, pairedBases, singleBases);
        if(RChildSame && !Lindex1pairing && !Lindex2pairing) return oneSameOneNoPairing(index1, index2, RChild, LChild,
                bases, pairedBases, singleBases);












    }
    private int bothSame(PhyloTreeNode LChild, PhyloTreeNode RChild, int index1, int index2, String bases,
                         Collection<String> pairedBases, Collection<String> singleBases){
        String LcurBases;
        String LbestBases = "";
        String RcurBases;
        String RbestBases = "";
        int scoreR;
        int scoreL;
        int bestScore = -MainMethodClass.INF;
        int curScore;
        Iterator<String> itL = pairedBases.iterator();
        while(itL.hasNext()){
            LcurBases = itL.next();
            Iterator<String> itR = pairedBases.iterator();
            try{
                scoreL = LChild.getSankoffPairsScore(index1, LcurBases);
            }
            catch(ArrayIndexOutOfBoundsException e){
                scoreL = sankoffPairs(LChild, LcurBases, index1, pairedBases, singleBases);
                LChild.setSankoffPairsScores(index1, index2, LcurBases, scoreL);
            }
            int Ltransitions = MainMethodClass.cost(bases.substring(0,1), LcurBases.substring(0,1));
            Ltransitions += MainMethodClass.cost(bases.substring(1), LcurBases.substring(1));
            while(itR.hasNext()){
                RcurBases = itR.next();
                try{
                    scoreR = RChild.getSankoffPairsScore(index1, RcurBases);
                }
                catch(ArrayIndexOutOfBoundsException e){
                    scoreR = sankoffPairs(RChild, RcurBases, index1, pairedBases, singleBases);
                    RChild.setSankoffPairsScores(index1, index2, RcurBases, scoreR);
                }
                int Rtransitions = MainMethodClass.cost(bases.substring(0,1), RcurBases.substring(0,1));
                Rtransitions += MainMethodClass.cost(bases.substring(1), RcurBases.substring(1));
                curScore = Ltransitions + Rtransitions + scoreR + scoreL;
                if(curScore > bestScore){
                    bestScore = curScore;
                    LbestBases = LcurBases;
                    RbestBases = RcurBases;
                }
            }
        }
        String index1Base = bases.substring(0,1);
        String index2Base = bases.substring(1);
        String Lindex1Base = LbestBases.substring(0,1);
        String Lindex2Base = LbestBases.substring(1);
        String Rindex1Base = RbestBases.substring(0,1);
        String Rindex2Base = RbestBases.substring(1);
        LChild.setBaseIfParent(index1, index1Base, Lindex1Base);
        LChild.setBaseIfParent(index2, index2Base, Lindex2Base);
        RChild.setBaseIfParent(index1, index1Base, Rindex1Base);
        RChild.setBaseIfParent(index2, index2Base, Rindex2Base);
        return bestScore;
    }
    private int oneSameOneNoPairing(int index1, int index2, PhyloTreeNode childSamePairs, PhyloTreeNode childSingle,
                                    String bases, Collection<String> pairedBases, Collection<String> singleBases) {
        int scoreChildSamePairs;
        int score1Single;
        int score2Single;
        int bestScore = -MainMethodClass.INF;
        int curScore;
        String curBasesChildSamePairs;
        String base1Single;
        String base2Single;
        String bestBasesPairs = "";
        String bestBase1Single = "";
        String bestBase2Single = "";
        singleBases.add("A");
        singleBases.add("C");
        singleBases.add("G");
        singleBases.add("U");
        singleBases.add(".");
        Iterator<String> same = pairedBases.iterator();
        while(same.hasNext()){
            curBasesChildSamePairs = same.next();
            try{
                scoreChildSamePairs = childSamePairs.getSankoffPairsScore(index1, curBasesChildSamePairs);
            }
            catch(ArrayIndexOutOfBoundsException e){
                scoreChildSamePairs = sankoffPairs(childSamePairs, curBasesChildSamePairs, index1, pairedBases,
                        singleBases);
                childSamePairs.setSankoffPairsScores(index1, index2, curBasesChildSamePairs, scoreChildSamePairs);
            }
            int transitionPaired = MainMethodClass.cost(bases.substring(0,1),
                    curBasesChildSamePairs.substring(0,1));
            transitionPaired += MainMethodClass.cost(bases.substring(1), curBasesChildSamePairs.substring(1));
            Iterator<String> it1 = singleBases.iterator();
            while(it1.hasNext()){
                base1Single = it1.next();
                try{
                    score1Single = childSingle.getSankoffScore(index1, base1Single);
                }
                catch(ArrayIndexOutOfBoundsException e){
                    score1Single = sankoffSingle(childSingle, base1Single, index1,singleBases);
                    childSingle.addSankoffScore(index1, base1Single, score1Single);
                }
                int transition1 = MainMethodClass.cost(bases.substring(0,1), base1Single);
                Iterator<String> it2 = singleBases.iterator();
                while(it2.hasNext()){
                    base2Single = it2.next();
                    try{
                        score2Single = childSingle.getSankoffPairsScore(index2, base2Single);
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                        score2Single = sankoffSingle(childSingle, base2Single, index2, singleBases);
                        childSingle.addSankoffScore(index2, base2Single, score2Single);
                    }
                    int transition2 = MainMethodClass.cost(bases.substring(1), base2Single);
                    curScore = transitionPaired + transition1 + transition2 + scoreChildSamePairs + score1Single
                            +score2Single;
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestBasesPairs = curBasesChildSamePairs;
                        bestBase1Single = base1Single;
                        bestBase2Single = base2Single;
                    }
                }
            }
        }
        String bases1 = bases.substring(0,1);
        String bases2 = bases.substring(1);
        String pairs1 = bestBasesPairs.substring(0,1);
        String pairs2 = bestBasesPairs.substring(1);
        childSamePairs.setBaseIfParent(index1, bases1, pairs1);
        childSamePairs.setBaseIfParent(index2, bases2, pairs2);
        childSingle.setBaseIfParent(index1, bases1, bestBase1Single);
        childSingle.setBaseIfParent(index2, bases2, bestBase2Single);
        return bestScore;
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

    //TODO: edit to make the folding of a node more accurately represent the general folding of the children
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
