import com.sun.javaws.Main;
import org.omg.CosNaming._BindingIteratorImplBase;

import java.beans.IndexedPropertyDescriptor;
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
        int parsimonyScore = 0;
        getFolding(tree);
        String sequence = "";
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        PhyloTreeNode printNode = tree.getRoot();
        q.add(printNode);
        while(!q.isEmpty()){
            printNode = q.poll();
            if(printNode.getChildren().size() > 0){
                for(int i = 0; i < printNode.getChildren().size(); i++){
                    q.add(printNode.getChildren().get(i));
                }
            }
            else{
                sequence = printNode.getSequence();
            }
        }
        PhyloTreeNode root = tree.getRoot();
        ArrayList<String> newSequence = new ArrayList<String>(sequence.length());
        for(int i = 0; i < sequence.length(); i++){ //This is the line to change to change what index
            int j = root.getBasePair(i);
            if(root.getBasePair(i) == -1){
                Iterator<String> it = singleBases.iterator();
                int curScore;
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
                try{
                    newSequence.set(i, bestBase);
                }catch(IndexOutOfBoundsException e){
                    for(int k = newSequence.size(); k <= i; k++) newSequence.add(k, "");
                    newSequence.set(i, bestBase);
                }
                parsimonyScore += bestScore;
            }
            else if(root.getBasePair(i) > i && root.getBasePair(i) == j
                    && root.getBasePair(j) == i ){
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
                try{
                    newSequence.set(j, bestBases.substring(1));
                    newSequence.set(i,bestBases.substring(0,1));
                }catch(IndexOutOfBoundsException e){
                    for(int k = newSequence.size(); k <= Math.max(i,j); k++) newSequence.add(k, "");
                    newSequence.set(i, bestBases.substring(1));
                    newSequence.set(j, bestBases.substring(0,1));
                }
                parsimonyScore += bestScore;
            }
            else{
                Iterator<String> it = singleBases.iterator();
                int curScore;
                int bestScore = -MainMethodClass.INF;
                String curBase = "";
                String bestBase = "";
                while(it.hasNext()){
                    curBase = it.next();
                    curScore = sankoffPseudoPair(tree.getRoot(), curBase, i, singleBases, pairedBases);
                    if(curScore > bestScore){
                        bestBase = curBase;
                        bestScore = curScore;
                    }
                }
                try{
                    newSequence.set(i, bestBase);
                }catch(IndexOutOfBoundsException e){
                    for(int k = newSequence.size(); k <= i; k++) newSequence.add(k, "");
                    newSequence.set(i, bestBase);
                }
                parsimonyScore += bestScore;
            }
        }
        String newSequenceString = "";
        for(int i = 0; i < sequence.length(); i++){
            newSequenceString = newSequenceString.concat(newSequence.get(i));
        }
        tree.getRoot().setSequence(newSequenceString);
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        for(int i = 0; i < tree.getRoot().getChildren().size(); i++){
            s.add(tree.getRoot().getChildren().get(i));
        }
        PhyloTreeNode curNode;
        while(!s.empty()){
            curNode = s.pop();
            for(int i = 0; i < curNode.getChildren().size(); i++){
                s.add(curNode.getChildren().get(i));
            }
            if(curNode.getChildren().size() > 0){
                newSequenceString = "";
                String parentSequence = curNode.getParent().getSequence();
                for(int i = 0; i < sequence.length(); i++){
                    if(i < sequence.length() - 1){
                    newSequenceString = newSequenceString.concat(curNode.getBaseIfParent(i, parentSequence.substring(i,i+1)));
                    }
                    else newSequenceString = newSequenceString.concat((
                            curNode.getBaseIfParent(i, parentSequence.substring(i))));
                }
            }
            curNode.setSequence(newSequenceString);
        }
        return parsimonyScore;
    }
    // Cases:
   /*
        1) Both the children have pseudoPairing --> score if pseudoP  airing called on both nodes
        2) One child with pseudoPairing and the other child with no pairing --> pseudoPairing and single
        3) one child with pseudoPairing and the other with real pairing for that base --> pseudoPairing and sankoffPair
        4) The children have different pairing

        Base case of single, with scoring being different depending on the condition of the children
        Going to

    */
    private static int sankoffPseudoPair(PhyloTreeNode node, String curBase, int index,
                                         Collection<String> singleBases, Collection<String> pairedBases) {
        PhyloTreeNode childL = node.getChildren().get(0);
        PhyloTreeNode childR = node.getChildren().get(1);
        ArrayList<Integer> basePairL = childL.getBasePairs();
        ArrayList<Integer> basePairR = childR.getBasePairs();
        int curScoreL;
        int curScoreR;
        int bestScoreL = -MainMethodClass.INF;
        int bestScoreR = -MainMethodClass.INF;
        int pairIndexL = basePairL.get(index);
        int pairIndexR = basePairR.get(index);
        String curBaseL;
        String curBaseR;
        String bestBaseL = "";
        String bestBaseR = "";
        if(pairIndexL > 0 && basePairL.get(pairIndexL) != index){
           Iterator<String> itL = singleBases.iterator();
           while(itL.hasNext()){
               curBaseL = itL.next();
               try{
                   curScoreL = childL.getSankoffPseudoScore(index, curBaseL);
               }catch(ArrayIndexOutOfBoundsException e){
                   curScoreL = sankoffPseudoPair(childL, curBaseL, index, singleBases, pairedBases);
                   childL.addSankoffPseudoScore(index, curBaseL, curScoreL);
               }
               curScoreL += MainMethodClass.cost(curBase, curBaseL);
               if(curScoreL > bestScoreL){
                   bestBaseL = curBaseL;
                   bestScoreL = curScoreL;
               }

           }
        }
        else if(pairIndexL > 0 && basePairL.get(pairIndexL) == index){
            Iterator<String> itL = pairedBases.iterator();
            String curBasePairL;
            while(itL.hasNext()){
                curBaseL = itL.next();
                curBasePairL = curBaseL.concat(node.getSequence().substring(pairIndexL, pairIndexL + 1));
                try{
                    curScoreL = childL.getSankoffPairsScore(index, curBaseL);
                }catch(IndexOutOfBoundsException e){
                    curScoreL = sankoffPairs(childL, curBasePairL, index, pairedBases, singleBases);
                    childL.setSankoffPairsScores(index, pairIndexL, curBaseL, curScoreL);
                }
                curScoreL += MainMethodClass.cost(curBase, curBaseL.substring(0,1));
                curScoreL += MainMethodClass.cost(curBasePairL.substring(1), curBaseL.substring(1));
                if(curScoreL > bestScoreL){
                    bestBaseL = curBaseL;
                    bestScoreL = curScoreL;
                }
            }
        }
        else{
            Iterator<String> itL = pairedBases.iterator();
            while(itL.hasNext()){
                curBaseL = itL.next();
                try{
                    curScoreL = childL.getSankoffScore(index, curBaseL);
                }catch(ArrayIndexOutOfBoundsException e){
                    curScoreL = sankoffSingle(childL, curBaseL, index, singleBases);
                    childL.addSankoffPseudoScore(index, curBaseL, curScoreL);
                }
                curScoreL += MainMethodClass.cost(curBase, curBaseL);
                if(curScoreL > bestScoreL){
                    bestBaseL = curBaseL;
                    bestScoreL = curScoreL;
                }
            }
        }
        if(pairIndexR > 0 && basePairR.get(pairIndexR) != index){
            Iterator<String> itR = singleBases.iterator();
            while(itR.hasNext()){
                curBaseR = itR.next();
                try{
                    curScoreR = childR.getSankoffPseudoScore(index, curBaseR);
                }catch(IndexOutOfBoundsException e){
                    curScoreR = sankoffPseudoPair(childR, curBaseR, index, singleBases, pairedBases);
                    childR.addSankoffPseudoScore(index, curBaseR, curScoreR);
                }
                curScoreR += MainMethodClass.cost(curBase, curBaseR);
                if(curScoreR > bestScoreR){
                    bestBaseR = curBaseR;
                    bestScoreR = curScoreR;
                }

            }
        }
        else if(pairIndexR > 0 && basePairR.get(pairIndexR) == index){
            Iterator<String> itR = pairedBases.iterator();
            String curBasePairR;
            while(itR.hasNext()){
                curBaseR = itR.next();
                curBasePairR = curBaseR.concat(node.getSequence().substring(pairIndexR, pairIndexR + 1));
                try{
                    curScoreR = childR.getSankoffPairsScore(index, curBaseR);
                }catch(IndexOutOfBoundsException e){
                    curScoreR = sankoffPairs(childR, curBasePairR, index, pairedBases, singleBases);
                    childR.setSankoffPairsScores(index, pairIndexR, curBaseR, curScoreR);
                }
                curScoreR += MainMethodClass.cost(curBase, curBaseR.substring(0,1));
                curScoreR += MainMethodClass.cost(curBasePairR.substring(1), curBaseR.substring(1));
                if(curScoreR > bestScoreR){
                    bestBaseR = curBaseR;
                    bestScoreR = curScoreR;
                }
            }
        }
        else{
            Iterator<String> itR = pairedBases.iterator();
            while(itR.hasNext()){
                curBaseR = itR.next();
                try{
                    curScoreR = childR.getSankoffScore(index, curBaseR);
                }catch(ArrayIndexOutOfBoundsException e){
                    curScoreR = sankoffSingle(childR, curBaseR, index, singleBases);
                    childR.addSankoffPseudoScore(index, curBaseR, curScoreR);
                }
                curScoreR += MainMethodClass.cost(curBase, curBaseR);
                if(curScoreR > bestScoreR){
                    bestBaseR = curBaseR;
                    bestScoreR = curScoreR;
                }
            }
        }
        if(bestBaseL.length() == 1) childL.setBaseIfParent(index, curBase, bestBaseL);
        if(bestBaseR.length() == 1) childR.setBaseIfParent(index, curBase, bestBaseR);
        if(bestBaseL.length() == 2){
            childL.setBaseIfParent(index, curBase, bestBaseL.substring(0,1));
            childL.setBaseIfParent(pairIndexL, node.getSequence().substring(pairIndexL, pairIndexL + 1), bestBaseL.substring(1));
        }
        if(bestBaseR.length() == 2){
            childR.setBaseIfParent(index, curBase, bestBaseR.substring(0,1));
            try{
                childR.setBaseIfParent(pairIndexR, node.getSequence().substring(pairIndexR, pairIndexR + 1), bestBaseR.substring(1));
            }catch(StringIndexOutOfBoundsException e){
                childR.setBaseIfParent(pairIndexR, node.getSequence().substring(pairIndexR), bestBaseR.substring(1));
            }
        }
        return bestScoreR + bestScoreL;
    }




    //Method to get called on all pairs of basepairs
    private static int sankoffPairs(PhyloTreeNode node, String bases, int index1, Collection<String> pairedBases,
                             Collection<String> singleBases) {
        int index2 = node.getBasePair(index1);
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


        if(LChild.getBasePair(index1) == index2 && LChild.getBasePair(index2) == index1) LChildSame = true;
        if(RChild.getBasePair(index1) == index2 && RChild.getBasePair(index2) == index1) RChildSame = true;
        if(LChild.getBasePair(index1) > -1) Lindex1pairing = true;
        if(LChild.getBasePair(index2) > -1) Lindex2pairing = true;
        if(RChild.getBasePair(index1) > -1) Rindex1pairing = true;
        if(RChild.getBasePair(index2) > -1) Rindex2pairing = true;
        //case1, both of the children have the same pairing
        /*
        cases:
        1)Both children with the same pairing
        2)One child with the same pairing, and the other with different pairing at one base, and single at the other
        3)One child with same pairing, and the other with 2 single
        4)One child with same, and the other with different pairing at both nodes
         */
        //Case 1
        if(LChildSame && RChildSame) return samePairing(LChild, RChild, index1, index2, bases, pairedBases, singleBases);

        //Case 3
        else if(LChildSame && !Rindex1pairing && !Rindex2pairing) return bothSingle(index1, index2, LChild, RChild,
                bases, pairedBases, singleBases);
        else if(RChildSame && !Lindex1pairing && !Lindex2pairing) return bothSingle(index1, index2, RChild, LChild,
                bases, pairedBases, singleBases);

        //Case 4
        else if(RChildSame && Lindex1pairing && Lindex2pairing) return bothDifferent(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        else if(LChildSame && Rindex1pairing && Rindex2pairing) return bothDifferent(LChild, RChild, index1,
                index2, bases, singleBases, pairedBases);

        //Case 2
        else if(LChildSame && Rindex1pairing && !Rindex2pairing) return differentAndSingle(LChild, RChild, index2,
                index1, bases, singleBases, pairedBases);
        else if(LChildSame && !Rindex1pairing && Rindex2pairing) return differentAndSingle(LChild, RChild, index1,
                index2, bases,singleBases, pairedBases);
        else if(RChildSame && Lindex1pairing && !Lindex2pairing) return differentAndSingle(RChild, LChild, index2,
                index1, bases, singleBases, pairedBases);
        else if(RChildSame && !Lindex1pairing && Lindex2pairing) return differentAndSingle(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        else return 1;
    }


    private static int samePairing(PhyloTreeNode LChild, PhyloTreeNode RChild, int index1, int index2, String bases,
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


    private static int bothSingle(int index1, int index2, PhyloTreeNode same, PhyloTreeNode childSingle,
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
    private static int bothDifferent(PhyloTreeNode same, PhyloTreeNode different, int index1, int index2,
                                 String bases, Collection<String> singleBases, Collection<String> pairedBases){
        int singleIndex;
        int index1Pair = same.getBasePair(index1);
        int index2Pair = same.getBasePair(index2);
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
    //Bug could be 2 issues. Either sankoffPair isn't being called with all combinations of bases, or the bases
    //actually aren't paired
    private static int differentAndSingle(PhyloTreeNode same, PhyloTreeNode different, int indexSingle, int indexDif,
                                          String bases, Collection<String> singleBases, Collection<String> pairedBases){
        //boolean forward is true if indexSingle < indexDif
        /*if(indexSingle > indexDif){
            String temp = bases.substring(1);
            temp = temp.concat(bases.substring(0,1));
            bases = temp;
        }*/
        int indexDifPair = different.getBasePair(indexDif);
        int bestScoreScore = -MainMethodClass.INF;
        int bestScoreSame = -MainMethodClass.INF;
        int bestScoreDif = -MainMethodClass.INF;
        int bestScoreSingle = -MainMethodClass.INF;
        String bestBaseSame = "";
        String bestBaseSingle = "";
        String bestBaseDif = "";
        Iterator<String> itSame = pairedBases.iterator();
        while(itSame.hasNext()){
            String curBaseSame = itSame.next();
            int curScoreSame = transitionAndScoreSame(same, indexSingle, indexDif, bases, curBaseSame, pairedBases, singleBases);
            Iterator<String> itDif = pairedBases.iterator();
            if(curScoreSame > bestScoreSame){
                bestScoreSame = curScoreSame;
                bestBaseSame = curBaseSame;
            }
        }
        Iterator<String> itDif = pairedBases.iterator();
        while(itDif.hasNext()){
            String curBaseDif = itDif.next();
            //System.out.println(different.getName() + " " + indexDif + " " + indexDifPair + " " + bases);
             int curScoreDif = transitionAndScoreDifferent(different, indexDif, indexDifPair, bases, curBaseDif, pairedBases,
                    singleBases);
            if(curScoreDif > bestScoreDif){
                bestBaseDif = curBaseDif;
                bestScoreDif = curScoreDif;
            }
        }
        Iterator<String> itSingle = singleBases.iterator();
        while(itSingle.hasNext()){
            String curBaseSingle = itSingle.next();
            int curScoreSingle = transitionAndScoreSingle(different, indexSingle, curBaseSingle, bases.substring(1),
                    singleBases);
            if(curScoreSingle > bestScoreSingle){
                bestScoreSingle = curScoreSingle;
                bestBaseSingle = curBaseSingle;
            }
        }
        //TODO: Look at this closely if you should be assigning more parents then you currently do
        same.setBaseIfParent(indexSingle, bases.substring(0,1), bestBaseSame.substring(0,1));
        same.setBaseIfParent(indexDif, bases.substring(1), bestBaseSame.substring(1));
        different.setBaseIfParent(indexSingle, bases.substring(1), bestBaseSingle);
        //THIS line could very well be wrong
        different.setBaseIfParent(indexDif, bases.substring(0,1), bestBaseDif.substring(0,1));
        return bestScoreSingle + bestScoreDif + bestScoreSame;
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
        catch(IndexOutOfBoundsException e){
            scoreDif = sankoffPairs(different, curBase, index, pairedBases, singleBases);
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
        catch(IndexOutOfBoundsException e){
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
        String sequence = "";
        String folding;
        while(!s.empty()){
            curNode = s.pop();
            if(curNode.getChildren().size() == 2){
                for(int i = 0; i < curNode.getChildren().size(); i++){
                    s.push(curNode.getChildren().get(i));
                }
            }
            else{
                foldingFromVienna(curNode);
                sequence = curNode.getSequence();
                folding = curNode.getFolding();
                for(int i = 0; i < sequence.length(); i++){
                    if(folding.charAt(i) == '('){
                        int brackets = 0;
                        int j;
                        for(j = i; j < folding.length(); j++){
                            if(folding.charAt(j) == '(') brackets++;
                            if(folding.charAt(j) == ')') brackets--;
                            if(brackets == 0) break;
                        }
                        curNode.setBasePair(i,j);
                    }
                    else if(folding.charAt(i) == ')');
                    else curNode.setNoBP(i);
                }
                    //pass up all folds, and -1 at no pairs if the parent doesn't have any folding
                    PhyloTreeNode curNodeUp = curNode;
                    while(curNodeUp.getParent() != null){
                        curNodeUp = curNodeUp.getParent();
                        for(int i = 0; i < sequence.length(); i++){
                            try{
                                if(curNodeUp.getBasePair(i) == -1){
                                    if(curNode.getBasePair(i) != -1) curNodeUp.setBasePair(i, curNode.getBasePair(i));
                                }
                            }catch(IndexOutOfBoundsException e){
                                if(curNode.getBasePair(i) == -1) curNodeUp.setNoBP(i);
                                else curNodeUp.setBasePair(i,curNode.getBasePair(i));
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
        ArrayList<Integer> gaps = new ArrayList<Integer>();
        for(int i = 0; i < seq.length() - 1; i++){
            if(seq.substring(i,i+1).equals(".")) gaps.add(i);
        }
        if(seq.substring(seq.length()-1) == ".") gaps.add(seq.length()-1);
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
                line = line.replace(" ", "");
                for(i = 0; i < gaps.size(); i++){
                    line = line.substring(0,gaps.get(i)).concat(".".concat(line.substring(gaps.get(i))));
                }
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
