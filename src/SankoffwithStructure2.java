import com.sun.javaws.Main;
import org.omg.CosNaming._BindingIteratorImplBase;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Gordon on 4/12/14.

 */
public class SankoffwithStructure2 {


    //Public method to be called
    public static int sankoff(PhyloTree tree) throws IOException{
        Collection<String> singleBases = new ArrayList<String>();
        singleBases.add("A");
        singleBases.add("C");
        singleBases.add("G");
        singleBases.add("U");
        singleBases.add(".");
        String newSequence = "";
        int parsimonyScore = 0;
        String curBase;
        String bestBase = "";
        int curScore;
        int bestScore;
        PhyloTreeNode root = tree.getRoot();
        PhyloTreeNode curNode = root;
        while(curNode.getChildren().size() > 0) curNode = curNode.getChildren().get(0);
        String sequence = curNode.getSequence();
        for(int i = 0; i < sequence.length(); i++){
            bestScore = -MainMethodClass.INF;
            Iterator<String> it = singleBases.iterator();
            while(it.hasNext()){
                curBase = it.next();
                curScore = sankoffSingle(root, curBase, i, singleBases);
                if(curScore > bestScore){
                    bestBase = curBase;
                    bestScore = curScore;
                }
            }
            newSequence = newSequence.concat(bestBase);
            root.setSequence(newSequence);
            curNode = tree.getRoot();
            Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
            q.add(curNode);
            while(!q.isEmpty()){
                curNode = q.poll();
                for(int j = 0; j < curNode.getChildren().size(); j++){
                    if(curNode.getChildren().size() > 0 && curNode.getChildren().get(j).getChildren().size() > 0){
                        PhyloTreeNode curChild = curNode.getChildren().get(j);
                        curBase = curNode.getSequence().substring(i,i+1);
                        q.add(curChild);
                        if(i == 0) {
                            curChild.setSequence(curChild.getBaseIfParent(i,curBase));
                        }
                        else{
                            String curSequence = curChild.getSequence();
                            curSequence = curSequence.concat(curChild.getBaseIfParent(i, curBase));
                            curChild.setSequence(curSequence);
                        }
                    }
                }
            }
            parsimonyScore += parsimonyScore;
        }
        root.setSequence(newSequence);
        return parsimonyScore;
    }
    public static int sankoffWithStructure(PhyloTree tree) throws IOException {
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
        String sequence = root.getSequence();
        sequence = sequence.replace(",","");
        for(int i = 0; i < sequence.length(); i++){
            int j = root.getBasePairs().get(i);
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
            else if(root.getBasePairs().get(i) > i && root.getBasePairs().get(i) == j
                    && root.getBasePairs().get(j) == i ){
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

   //Method to get called on all pairs of basepairs
    private static int sankoffPairs(PhyloTreeNode node, String bases, int index1, Collection<String> pairedBases,
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
        if(LChildSame && !Rindex1pairing && !Rindex2pairing) return oneNoPairing(index1, index2, LChild, RChild,
                bases, pairedBases, singleBases);
        if(RChildSame && !Lindex1pairing && !Lindex2pairing) return oneNoPairing(index1, index2, RChild, LChild,
                bases, pairedBases, singleBases);
        if(RChildSame && Lindex1pairing && Lindex2pairing) return oneWith2DifferentPairings(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        if(LChildSame && Rindex1pairing && Rindex2pairing) return oneWith2DifferentPairings(LChild, RChild, index1,
                index2, bases, singleBases, pairedBases);
        if(LChildSame && Rindex1pairing && !Rindex2pairing) return differentPairingAndSingle(LChild, RChild, index2,
                index1, bases, singleBases, pairedBases);
        if(LChildSame && !Rindex1pairing && Rindex2pairing) return differentPairingAndSingle(LChild, RChild, index1,
                index2, bases,singleBases, pairedBases);
        if(RChildSame && Lindex1pairing && !Lindex2pairing) return differentPairingAndSingle(RChild, LChild, index2,
                index1, bases, singleBases, pairedBases);
        if(RChildSame && !Lindex1pairing && Lindex2pairing) return differentPairingAndSingle(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        return 1;
    }


    private static int bothSame(PhyloTreeNode LChild, PhyloTreeNode RChild, int index1, int index2, String bases,
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
            scoreL = transitionAndScoreSame(LChild, index1, index2, bases, LcurBases, pairedBases, singleBases);
            Iterator<String> itR = pairedBases.iterator();
            while(itR.hasNext()){
                RcurBases = itR.next();
                scoreR = transitionAndScoreSame(RChild, index1, index2, bases, RcurBases, pairedBases, singleBases);
                curScore = scoreR + scoreL;
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


    private static int oneNoPairing(int index1, int index2, PhyloTreeNode same, PhyloTreeNode childSingle,
                                    String bases, Collection<String> pairedBases, Collection<String> singleBases) {
        int scoreSame;
        int score1Single;
        int score2Single;
        int bestScore = -MainMethodClass.INF;
        int curScore;
        String curBasesSame;
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
        Iterator<String> sameIt = pairedBases.iterator();
        while(sameIt.hasNext()){
            curBasesSame = sameIt.next();
            scoreSame = transitionAndScoreSame(same, index1, index2, bases, curBasesSame, pairedBases, singleBases);
            Iterator<String> it1 = singleBases.iterator();
            while(it1.hasNext()){
                base1Single = it1.next();
                score1Single = transitionAndScoreSingle(childSingle, index1, base1Single, bases,singleBases);
                Iterator<String> it2 = singleBases.iterator();
                while(it2.hasNext()){
                    base2Single = it2.next();
                    score2Single = transitionAndScoreSingle(childSingle, index2, base2Single, bases, singleBases);
                    curScore = scoreSame+ + score1Single +score2Single;
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestBasesPairs = curBasesSame;
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
        same.setBaseIfParent(index1, bases1, pairs1);
        same.setBaseIfParent(index2, bases2, pairs2);
        childSingle.setBaseIfParent(index1, bases1, bestBase1Single);
        childSingle.setBaseIfParent(index2, bases2, bestBase2Single);
        return bestScore;
    }

    //This is the potential problem, weighting the base that is the same too much
    private static int oneWith2DifferentPairings(PhyloTreeNode same, PhyloTreeNode different, int index1, int index2,
                                 String bases, Collection<String> singleBases, Collection<String> pairedBases){
        int singleIndex;
        int index1Pair = same.getBasePairs().get(index1);
        int index2Pair = same.getBasePairs().get(index2);
        String curBaseSame;
        String curBaseDif1;
        String curBaseDif2;
        int scoreSame;
        int scoreDif1;
        int scoreDif2;
        int transitionSame;
        int transitionDif1;
        int transitionDif2;
        int curScore;
        int bestScore = -MainMethodClass.INF;
        String bestBaseSame = "";
        String bestBaseDif1 = "";
        String bestBaseDif2 = "";
        Iterator<String> sameIt = pairedBases.iterator();
        while(sameIt.hasNext()){
            curBaseSame = sameIt.next();
            scoreSame = transitionAndScoreSame(same, index1, index2, bases, curBaseSame, pairedBases, singleBases);
            Iterator<String> dif1It = pairedBases.iterator();
            //The big issue is that we are calling SankoffPairs on a different indices but assuming that the parents
            //are the same. This might work, because the string that we are assuming are the parents of the node,
            //will itereate through all of them
            while(dif1It.hasNext()){
                curBaseDif1 = dif1It.next();
                scoreDif1 = transitionAndScoreDifferent(different, index1, index1Pair, bases, curBaseDif1, pairedBases,
                        singleBases);
                //SHOULD WE INCLUDE THE OTHER TRANSITION??????????????????????
                Iterator<String> dif2It = pairedBases.iterator();
                while(dif2It.hasNext()){
                    curBaseDif2 = dif2It.next();
                    scoreDif2 = transitionAndScoreDifferent(different, index2, index2Pair, bases, curBaseDif2,
                            pairedBases, singleBases);
                    curScore = scoreDif1 + scoreDif2 + scoreSame;
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestBaseDif1 = curBaseDif1;
                        bestBaseDif2 = curBaseDif2;
                        bestBaseSame = curBaseSame;
                    }
                }
            }
        }
        //CAN'T SET PARENT IFS FOR THE SECOND HALF OF THE DIFFERENT PAIRS BECAUSE WE GENERATE THE PAIRS WITHOUT
        //ANY KNOWLEDGE OF THE PARENT --> PROBABLY NOT GOOD
        String base1 = bases.substring(0,1);
        String base2 = bases.substring(1);
        String same1 = bestBaseSame.substring(0,1);
        String same2 = bestBaseSame.substring(1);
        same.setBaseIfParent(index1, base1, same1);
        same.setBaseIfParent(index2, base2, same2);
        String different1 = bestBaseDif1.substring(0,1);
        String different2 = bestBaseDif2.substring(0,1);
        different.setBaseIfParent(index1, base1, different1);
        different.setBaseIfParent(index2, base2, different2);
        return bestScore;
    }

    private static int differentPairingAndSingle(PhyloTreeNode same, PhyloTreeNode different, int indexSingle, int indexDif,
                                          String bases, Collection<String> singleBases, Collection<String> pairedBases){
        //boolean forward is true if indexSingle < indexDif
        if(indexSingle > indexDif){
            String temp = bases.substring(1);
            temp = temp.concat(bases.substring(0,1));
            bases = temp;
        }
        int scoreSame;
        int scoreDif;
        int scoreSingle;
        int curScore;
        int indexDifPair = same.getBasePairs().get(indexDif);
        int bestScore = -MainMethodClass.INF;
        String curSame;
        String curSingle;
        String curDif;
        String bestSame = "";
        String bestSingle = "";
        String bestDif = "";
        Iterator<String> itSame = pairedBases.iterator();
        while(itSame.hasNext()){
            curSame = itSame.next();
            scoreSame = transitionAndScoreSame(same, indexSingle, indexDif, bases, curSame, pairedBases, singleBases);
            Iterator<String> itDif = pairedBases.iterator();
            while(itDif.hasNext()){
                curDif = itDif.next();
                scoreDif = transitionAndScoreDifferent(different, indexDif, indexDifPair, bases, curDif, pairedBases,
                        singleBases);
                Iterator<String> itSingle = singleBases.iterator();
                while(itSingle.hasNext()){
                    curSingle = itSingle.next();
                    scoreSingle = transitionAndScoreSingle(different, indexSingle, curSingle, bases.substring(1),
                            singleBases);
                    curScore = scoreSame + scoreDif + scoreSingle;
                    if(curScore > bestScore){
                        bestScore = curScore;
                        bestDif = curDif;
                        bestSame = curSame;
                        bestSingle = curSingle;
                    }
                }
            }
        }
        //TODO: Look at this closely if you should be assigning more parents then you currently do
        same.setBaseIfParent(indexSingle, bases.substring(0,1), bestSame.substring(0,1));
        same.setBaseIfParent(indexDif, bases.substring(1), bestSame.substring(1));
        different.setBaseIfParent(indexSingle, bases.substring(1), bestSingle);
        //THIS line could very well be wrong
        different.setBaseIfParent(indexDif, bases.substring(0,1), bestDif.substring(0,1));
        return bestScore;
    }

    private static int transitionAndScoreSame(PhyloTreeNode same, int index1, int index2, String parentBase, String curBase,
                                       Collection<String> pairedBases, Collection<String> singleBases){
        int scoreSame;
        try{
            scoreSame = same.getSankoffPairsScore(index1, curBase);
        }
        catch(IndexOutOfBoundsException e){
            scoreSame = sankoffPairs(same, curBase, index1, pairedBases, singleBases);
            same.setSankoffPairsScores(index1, index2, curBase, scoreSame);
        }
        scoreSame += MainMethodClass.cost(parentBase.substring(0,1), curBase.substring(0, 1));
        scoreSame += MainMethodClass.cost(parentBase.substring(1), curBase.substring(1));
        return scoreSame;
    }


    private static int transitionAndScoreDifferent(PhyloTreeNode different, int index, int indexPair, String parent,
                                            String curBase, Collection<String> pairedBases,
                                            Collection<String> singleBases){
        int scoreDif;
        try{
            scoreDif = different.getSankoffPairsScore(index, curBase);
        }
        catch(ArrayIndexOutOfBoundsException e){
            scoreDif = sankoffPairs(different, parent, index, pairedBases, singleBases);
            different.setSankoffPairsScores(index, indexPair, curBase, scoreDif);
        }
        scoreDif += MainMethodClass.cost(parent.substring(0,1), curBase.substring(0,1));
        return scoreDif;
    }


    //Assumes that the parent of the SingleBase os the first one from bases. --> Just pass it a single
    //base as the parent is probably the safe way to go
    private static int transitionAndScoreSingle(PhyloTreeNode child, int index, String curBase, String parent,
                                         Collection<String> singleBases){
        int score;
        try{
            score = child.getSankoffScore(index, curBase);
        }
        catch(ArrayIndexOutOfBoundsException e){
            score = sankoffSingle(child, curBase, index,singleBases);
            child.addSankoffScore(index, curBase, score);
        }
        score += MainMethodClass.cost(parent.substring(0,1), curBase);
        return score;
    }


    //method called on all of the single bases
    private static int sankoffSingle(PhyloTreeNode node, String curBase, int index, Collection<String> singleBases) {
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
            String curBaseFromSequence = node.getSequence().substring(index, index+1);
            if(curBaseFromSequence.equals(curBase)) return 0;
            else return -MainMethodClass.INF;
        }
        else{
            childL = node.getChildren().get(0);
            childR = node.getChildren().get(1);
            Iterator<String> itL = singleBases.iterator();
            while(itL.hasNext()){
                curBaseL = itL.next();
                Iterator<String> itR = singleBases.iterator();
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
    private static void getFolding(PhyloTree tree) throws IOException {
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
                for(int i = 0; i < sequence.length(); i++){
                    if(folding.charAt(i) == '('){
                        int brackets = 1;
                        int j;
                        for(j = i; j < folding.length(); j++){
                            if(folding.charAt(j) == '(') brackets++;
                            if(folding.charAt(j) == ')') brackets--;
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

    private static void foldingFromVienna(PhyloTreeNode curNode) throws IOException {
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
