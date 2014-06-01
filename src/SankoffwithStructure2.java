 import com.sun.javaws.Main;
 import com.sun.org.apache.regexp.internal.recompile;
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
    public PhyloTree sankoff(PhyloTree tree) throws IOException{
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
            bestBase = "";
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
            parsimonyScore += bestScore;
            if(bestBase.equals("")){
                int breaker = 4+4;
            }
            newSequence = newSequence.concat(bestBase);
            root.setSequence(newSequence);
            curNode = tree.getRoot();
            Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
            q.add(curNode);
            while(!q.isEmpty()){
                curNode = q.poll();
                for(int j = 0; j < curNode.getChildren().size(); j++){
                    if(curNode.getChildren().get(j).getChildren().size() > 0){
                        PhyloTreeNode curChild = curNode.getChildren().get(j);
                        curBase = curNode.getSequence().substring(i, i+1);
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
        }
        //System.out.println(parsimonyScore);
        layerAndParsimony(tree);
        return tree;
    }
    public PhyloTree sankoffWithStructure(PhyloTree tree) throws IOException {
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
        for(int i = 0; i < sequence.length(); i++){
            if(i == 15){
                int rakes = 44;
            }
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
                if(bestBase.equals("")){
                    int rest = 99;
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
                if(bestBases.equals("")){
                    int rest = 99;
                }
                parsimonyScore += bestScore;
            }
            else if(root.getBasePair(i) == -2){
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
                if(bestScore <= -MainMethodClass.INF){
                    int breaks = 9;
                }
                if(bestScore <= -MainMethodClass.INF){
                    int psueooscore = 99;
                }
                else{
                     int itworks = 33;
                }
                parsimonyScore += bestScore;
            }
        }
        String newSequenceString = "";
        for(int i = 0; i < sequence.length(); i++){
            newSequenceString = newSequenceString.concat(newSequence.get(i));
        }
        tree.getRoot().setSequence(newSequenceString);
        Queue<PhyloTreeNode> s = new LinkedList<PhyloTreeNode>();
        for(int i = 0; i < tree.getRoot().getChildren().size(); i++){
            s.add(tree.getRoot().getChildren().get(i));
        }
        PhyloTreeNode curNode;
        while(!s.isEmpty()){
            curNode = s.poll();
            for(int i = 0; i < curNode.getChildren().size(); i++){
                s.add(curNode.getChildren().get(i));
            }
            if(curNode.getChildren().size() > 0 && curNode != tree.getRoot()){
                newSequenceString = "";
                String parentSequence = curNode.getParent().getSequence();
                for(int i = 0; i < sequence.length(); i++){
                    if(false){
                        int pasue = 343;
                    }
                    try{
                        newSequenceString =
                                newSequenceString.concat(curNode.getBaseIfParent(i, parentSequence.substring(i,i+1)));
                    }catch(NullPointerException e){
                        int dfd = 4343;
                    }
                }
                curNode.setSequence(newSequenceString);
            }
        }
        layerAndParsimony(tree);
        return tree;
    }
    // Cases:
   /*
    Each of children fall into 1 of 3 cases
    1)The index passed has no pairing
    2)The index passed has real pairing
    3)The index passed has pseudopairing

    */
    private static int sankoffSingle(PhyloTreeNode node, String curBase, int index, Collection<String> singleBases) {
        int scoreL;
        int scoreR;
        int bestScoreL = -MainMethodClass.INF;
        int bestScoreR = -MainMethodClass.INF;
        int curScore;
        int bestScore = -MainMethodClass.INF;
        String curBaseR;
        String curBaseL;
        String bestBaseR = "";
        String bestBaseL = "";
        PhyloTreeNode childL;
        PhyloTreeNode childR;
        if(node.getName().equals("43") && index == 9){
            int ffff = 333;
        }
        if(node.getChildren().size() < 2){
            String curBaseFromSequence = node.getSequence().substring(index, index+1);
            if(!curBaseFromSequence.equals("G") && !curBaseFromSequence.equals("A") && !curBaseFromSequence.equals("C")
                    && !curBaseFromSequence.equals("U") && !curBaseFromSequence.equals(".")
                    && !curBaseFromSequence.equals("N") && !curBaseFromSequence.equals("R")
                    && !curBaseFromSequence.equals("Y") && !curBaseFromSequence.equals("W")
                    && !curBaseFromSequence.equals("M") && !curBaseFromSequence.equals("K")){
                int reqrqer = 83838;
            }
            if(curBaseFromSequence.equals(curBase)) return 0;
            if(curBaseFromSequence.equals("N")) return 0;
            if(curBaseFromSequence.equals("R")){
                if(curBase.equals("A") || curBase.equals("G")) return 0;
            }
            if(curBaseFromSequence.equals("Y")){
                if(curBase.equals("C") || curBase.equals("T")) return 0;
            }
            if(curBaseFromSequence.equals("W")){
                if(curBase.equals("A") || curBase.equals("T")) return 0;
            }
            if(curBaseFromSequence.equals("M")){
                if(curBase.equals("A") || curBase.equals("C")) return 0;
            }
            if(curBaseFromSequence.equals("K")){
                if(curBase.equals("G") || curBase.equals("T")) return 0;
            }
            if(curBaseFromSequence.equals("S")){
                if(curBase.equals("C") || curBase.equals("G")) return 0;
            }
            if(curBaseFromSequence.equals("B")){
                if(curBase.equals("C") || curBase.equals("G") || curBase.equals("T")) return 0;
            }
            if(curBaseFromSequence.equals("V")){
                if(curBase.equals("A") || curBase.equals("C") || curBase.equals("G")) return 0;
            }
            if(curBaseFromSequence.equals("H")){
                if(curBase.equals("A") || curBase.equals("C") || curBase.equals("T")) return 0;
            }
            if(curBaseFromSequence.equals("D")){
                if(curBase.equals("A") || curBase.equals("G") || curBase.equals("T")) return 0;
            }
            else return -MainMethodClass.INF;
        }
        else{
            childL = node.getChildren().get(0);
            childR = node.getChildren().get(1);
            Iterator<String> itL = singleBases.iterator();
            while(itL.hasNext()){
                curBaseL = itL.next();
                try{
                    scoreL = childL.getSankoffScore(index, curBaseL);
                }catch(IndexOutOfBoundsException e){
                    scoreL = sankoffSingle(childL, curBaseL, index, singleBases);
                    childL.addSankoffScore(index, curBaseL, scoreL);
                }
                scoreL += MainMethodClass.cost(curBase, curBaseL);
                if(scoreL > bestScoreL){
                    bestScoreL = scoreL;
                    bestBaseL = curBaseL;
                }
            }
            Iterator<String> itR = singleBases.iterator();
            while(itR.hasNext()){
                curBaseR = itR.next();
                try{
                    scoreR = childR.getSankoffScore(index, curBaseR);
                }catch(IndexOutOfBoundsException e){
                    scoreR = sankoffSingle(childR, curBaseR, index, singleBases);
                    childR.addSankoffScore(index, curBaseR, scoreR);
                }
                scoreR += MainMethodClass.cost(curBase, curBaseR);
                if(scoreR > bestScoreR){
                    bestScoreR = scoreR;
                    bestBaseR = curBaseR;
                }
            }
            if(childL.getChildren().size() < 2 && bestBaseL == ""){
                int breakdsa = 3243;
            }
            if(childR.getChildren().size() < 2 && bestBaseR == ""){
                int adfadf =343443;
            }
            childL.setBaseIfParent(index, curBase, bestBaseL);
            childR.setBaseIfParent(index, curBase, bestBaseR);
        }
        return bestScoreR + bestScoreL;
    }

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
        if(node.getName().equals("43") && index == 9){
           int ffff = 333;
        }
        if(pairIndexL == -2){
           Iterator<String> itL = singleBases.iterator();
           while(itL.hasNext()){
               curBaseL = itL.next();
               try{
                   curScoreL = childL.getSankoffPseudoScore(index, curBaseL);
               }catch(IndexOutOfBoundsException e){
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
        //works
        else if(pairIndexL >= 0 && basePairL.get(pairIndexL) == index){
            Iterator<String> itL = pairedBases.iterator();
            String curBasePairL;
            if(pairIndexL == 29){
                int afad = 234234;
            }
            while(itL.hasNext()){
                curBaseL = itL.next();
                try{
                    curScoreL = childL.getSankoffPairsScore(index, curBaseL);
                }catch(IndexOutOfBoundsException e){
                    curScoreL = sankoffPairs(childL, curBaseL, index, pairedBases, singleBases);
                    childL.setSankoffPairsScores(index, pairIndexL, curBaseL, curScoreL);
                }
                curScoreL += MainMethodClass.cost(curBase, curBaseL.substring(0,1));
                if(curScoreL > bestScoreL){
                    bestBaseL = curBaseL;
                    bestScoreL = curScoreL;
                }
            }
        }
        //This case at least works sometimes
        else if(basePairL.get(index) == -1){
            Iterator<String> itL = singleBases.iterator();
            while(itL.hasNext()){
                curBaseL = itL.next();
                try{
                    curScoreL = childL.getSankoffScore(index, curBaseL);
                }catch(IndexOutOfBoundsException e){
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
        else{
            int whatisgoingon = 100;
        }
        //this may not work
        if(pairIndexR == -2){
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
        else if(pairIndexR >= 0 && basePairR.get(pairIndexR) == index){
            Iterator<String> itR = pairedBases.iterator();
            String curBasePairR;
            while(itR.hasNext()){
                curBaseR = itR.next();
                try{
                    curScoreR = childR.getSankoffPairsScore(index, curBaseR);
                }catch(IndexOutOfBoundsException e){
                    curScoreR = sankoffPairs(childR, curBaseR, index, pairedBases, singleBases);
                    childR.setSankoffPairsScores(index, pairIndexR, curBaseR, curScoreR);
                }
                curScoreR += MainMethodClass.cost(curBase, curBaseR.substring(0,1));
                if(curScoreR > bestScoreR){
                    bestBaseR = curBaseR;
                    bestScoreR = curScoreR;
                }
            }
        }
        else if(basePairR.get(index) == -1){
            Iterator<String> itR = singleBases.iterator();
            while(itR.hasNext()){
                curBaseR = itR.next();
                try{
                    curScoreR = childR.getSankoffScore(index, curBaseR);
                }catch(IndexOutOfBoundsException e){
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
        else{
            int what = 333;
        }
        if(bestBaseL.equals("") || bestBaseR.equals("")){
            int adfas = 223432;
        }
        try{
            childL.setBaseIfParent(index, curBase, bestBaseL.substring(0,1));
        }catch(StringIndexOutOfBoundsException e){
            childL.setBaseIfParent(index, curBase, bestBaseL);
        }
        try{
            childR.setBaseIfParent(index, curBase, bestBaseR.substring(0,1));
        }catch(StringIndexOutOfBoundsException e){
            childR.setBaseIfParent(index, curBase, bestBaseR);
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
        if((index1 == 9 || index2 == 9) && (node.getName().equals("45") || node.getName().equals("44") ||
                node.getName().equals("42"))){
            int ffff = 333;
        }
        PhyloTreeNode LChild = node.getChildren().get(0);
        PhyloTreeNode RChild = node.getChildren().get(1);
        boolean LChildSame = false;
        boolean RChildSame = false;
        boolean Rindex1Single = false;
        boolean Rindex2Single = false;
        boolean Lindex1Single = false;
        boolean Lindex2Single = false;

        if(LChild.getBasePair(index1) == index2 && LChild.getBasePair(index2) == index1) LChildSame = true;
        if(RChild.getBasePair(index1) == index2 && RChild.getBasePair(index2) == index1) RChildSame = true;
        if(LChild.getBasePair(index1) == -1) Lindex1Single = true;
        if(LChild.getBasePair(index2) == -1) Lindex2Single = true;
        if(RChild.getBasePair(index1) == -1) Rindex1Single = true;
        if(RChild.getBasePair(index2) == -1) Rindex2Single = true;

        if(node.getName().equals("3") && index1 == 1){
            int sdfdf = 343;
        }
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
        else if(LChildSame && Rindex1Single && Rindex2Single) return bothSingle(index1, index2, LChild, RChild,
                bases, pairedBases, singleBases);
        else if(RChildSame && Lindex1Single && Lindex2Single) return bothSingle(index1, index2, RChild, LChild,
                bases, pairedBases, singleBases);

        //Case 4
        else if(RChildSame && !Lindex1Single && !Lindex2Single) return bothDifferent(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        else if(LChildSame && !Rindex1Single && !Rindex2Single) return bothDifferent(LChild, RChild, index1,
                index2, bases, singleBases, pairedBases);

        //Case 2
        else if(LChildSame && !Rindex1Single && Rindex2Single) return differentAndSingle(LChild, RChild, index2,
                index1, bases, singleBases, pairedBases);
        else if(LChildSame && Rindex1Single && !Rindex2Single) return differentAndSingle(LChild, RChild, index1,
                index2, bases, singleBases, pairedBases);
        else if(RChildSame && !Lindex1Single && Lindex2Single) return differentAndSingle(RChild, LChild, index2,
                index1, bases, singleBases, pairedBases);
        else if(RChildSame && Lindex1Single && !Lindex2Single) return differentAndSingle(RChild, LChild, index1,
                index2, bases, singleBases, pairedBases);
        else return 1;
    }


    private static int samePairing(PhyloTreeNode LChild, PhyloTreeNode RChild, int index1, int index2, String bases,
                         Collection<String> pairedBases, Collection<String> singleBases){
        String curBaseL;
        String curBaseR;
        String bestBaseL = "";
        String bestBaseR = "";
        int curScoreR;
        int curScoreL;
        int bestScoreL = -MainMethodClass.INF;
        int bestScoreR = -MainMethodClass.INF;
        Iterator<String> itL = pairedBases.iterator();
        if(LChild.getName().equals("Z00028.1/5121-5234") && index1 == 2 && index2 == 40){
            int rest = 222;
        }
        while(itL.hasNext()){
            curBaseL = itL.next();
            curScoreL = transitionAndScoreSame(LChild, index1, index2, bases, curBaseL, pairedBases, singleBases);
            if(curScoreL > bestScoreL){
                bestScoreL = curScoreL;
                bestBaseL = curBaseL;
            }
        }
        Iterator<String> itR = pairedBases.iterator();
        while(itR.hasNext()){
            curBaseR = itR.next();
            curScoreR = transitionAndScoreSame(RChild, index1, index2, bases, curBaseR, pairedBases, singleBases);
            if(curScoreR > bestScoreR){
                bestScoreR = curScoreR;
                bestBaseR = curBaseR;
            }
        }
        if(bestBaseR.length() < 2){
            int breaker = 50;
        }
        String index1Base = bases.substring(0,1);
        String index2Base = bases.substring(1);
        String Lindex1Base = bestBaseL.substring(0,1);
        String Lindex2Base = bestBaseL.substring(1);
        String Rindex1Base = bestBaseR.substring(0,1);
        String Rindex2Base = bestBaseR.substring(1);
        LChild.setBaseIfParent(index1, index1Base, Lindex1Base);
        LChild.setBaseIfParent(index2, index2Base, Lindex2Base);
        RChild.setBaseIfParent(index1, index1Base, Rindex1Base);
        RChild.setBaseIfParent(index2, index2Base, Rindex2Base);
        return bestScoreR + bestScoreL;
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

        int index1PairSame = same.getBasePair(index1);
        int index2PairSame = same.getBasePair(index2);
        int index1PairDif = different.getBasePair(index1);
        int index2PairDif = different.getBasePair(index2);
        String curBaseSame;
        String curBaseDif1;
        String curBaseDif2;
        int bestScoreSame = -MainMethodClass.INF;
        int bestScoreDif1 = -MainMethodClass.INF;
        int bestScoreDif2 = -MainMethodClass.INF;
        int curScoreSame;
        int curScoreDif1;
        int curScoreDif2;
        String bestBaseSame = "";
        String bestBaseDif1 = "";
        String bestBaseDif2 = "";
        if(index1 == 9 || index2 == 9){
            int fdfd = 33;
        }
        Iterator<String> sameIt = pairedBases.iterator();
        while(sameIt.hasNext()){
            curBaseSame = sameIt.next();
            curScoreSame = transitionAndScoreSame(same, index1, index2, bases, curBaseSame, pairedBases, singleBases);
            if(curScoreSame > bestScoreSame){
                bestScoreSame = curScoreSame;
                bestBaseSame = curBaseSame;
            }
        }

        //The big issue is that we are calling SankoffPairs on a different indices but assuming that the parents
        //are the same. This might work, because the string that we are assuming are the parents of the node,
        //will itereate through all of them
        if(index1PairDif == -2){
            Iterator<String> difL1It = singleBases.iterator();
            while(difL1It.hasNext()){
                curBaseDif1 = difL1It.next();
                curScoreDif1 = getPseudoScore(different, index1, curBaseDif1, bases.substring(0,1),
                        singleBases, pairedBases);
                if(curScoreDif1 > bestScoreDif1){
                    bestScoreDif1 = curScoreDif1;
                    bestBaseDif1 = curBaseDif1;
                }
            }
        }
        else{
            Iterator<String> dif1It = pairedBases.iterator();
            while(dif1It.hasNext()){
                curBaseDif1 = dif1It.next();
                curScoreDif1 = transitionAndScoreDifferent(different, index1, index1PairDif, bases, curBaseDif1, pairedBases, //changes this line
                        singleBases);
                if(curScoreDif1 > bestScoreDif1){
                    bestScoreDif1 = curScoreDif1;
                    bestBaseDif1 = curBaseDif1;
                }
            }
         }
        if(index2PairDif == -2){
            Iterator<String> difL2It = singleBases.iterator();
            while(difL2It.hasNext()){
                curBaseDif2 = difL2It.next();
                curScoreDif2 = getPseudoScore(different, index2, curBaseDif2, bases.substring(1),
                        singleBases, pairedBases);
                if(curScoreDif2 > bestScoreDif2){
                    bestScoreDif2 = curScoreDif2;
                    bestBaseDif2 = curBaseDif2;
                }
            }
        }
        else{
            Iterator<String> dif2It = pairedBases.iterator();
            while(dif2It.hasNext()){
                curBaseDif2 = dif2It.next();
                curScoreDif2 = transitionAndScoreDifferent(different, index2, index2PairDif, bases, curBaseDif2, //changed this line
                        pairedBases, singleBases);
                if(curScoreDif2 > bestScoreDif2){
                    bestScoreDif2 = curScoreDif2;
                    bestBaseDif2 = curBaseDif2;
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
        return bestScoreDif1 + bestScoreDif2 + bestScoreSame;
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
        //Assume indexSingle first and then indexDifferent
        int indexDifPair = different.getBasePair(indexDif);
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
        if(indexDifPair == -2){
            Iterator<String> itDif = singleBases.iterator();
            while(itDif.hasNext()){
                String curBaseDif = itDif.next();
                int curScoreDif = getPseudoScore(different, indexDif, curBaseDif, bases.substring(1),
                        singleBases, pairedBases);
                if(curScoreDif > bestScoreDif){
                    bestBaseDif = curBaseDif;
                    bestScoreDif = curScoreDif;
                }
            }
        }
        else{
            Iterator<String> itDif = pairedBases.iterator();
            while(itDif.hasNext()){
                String curBaseDif = itDif.next();
                 int curScoreDif = transitionAndScoreDifferent(different, indexDif, indexDifPair, bases, curBaseDif, pairedBases,
                        singleBases);
                if(curScoreDif > bestScoreDif){
                    bestBaseDif = curBaseDif;
                    bestScoreDif = curScoreDif;
                }
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
                    if(i == 117){
                        int restrer = 23423;
                    }
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
            }
        }
                    //pass up all folds, and -1 at no pairs if the parent doesn't have any folding
                    PhyloTreeNode curNodeUp = curNode;
                /*
                    while(curNodeUp.getParent() != null){
                        curNodeUp = curNodeUp.getParent();
                        if(curNodeUp.getName().equals("2")){
                            int sdfs =3434 ;
                        }
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
                    */
        Queue<PhyloTreeNode> leafs = new LinkedList<PhyloTreeNode>();
        Queue<PhyloTreeNode> all = new LinkedList<PhyloTreeNode>();
        curNode = tree.getRoot();
        all.add(curNode);
        while(!all.isEmpty()){
            curNode = all.poll();
            for(int i = 0; i < curNode.getChildren().size(); i++){
                all.add(curNode.getChildren().get(i));
            }
            if(curNode.getChildren().size() == 0) leafs.add(curNode);
        }
        while(!leafs.isEmpty()){
            curNode = leafs.poll();
            if(curNode.getParent() != null) leafs.add(curNode.getParent());
            if(curNode.getBasePairs().size() == 0 && curNode.getChildren().get(0).getBasePairs().size() > 0
                    && curNode.getChildren().get(1).getBasePairs().size() > 0){
                curNode.setBasePairs(mergeFold(curNode.getChildren().get(0),
                        curNode.getChildren().get(1)));

            }
        }
    }
    //Add all folding that is the same, belongs to the consensus sequence as long as there are no
    //pseudoknots, and also any that either child has that doesn't cause pseudoknots
    //Output will be the an ArrayList with -1 for bases that are single and all children below are single,
    //-2 for all bases that aren't paired in the new sequence but have a child paired in the nodes below,
    //and if there is a pair, that is represented in the ArrayList
    private static ArrayList<Integer> mergeFold(PhyloTreeNode node2, PhyloTreeNode node1) {
        ArrayList<Integer> bp1 = node1.getBasePairs();
        ArrayList<Integer> bp2 = node2.getBasePairs();
        ArrayList<Integer> bpComb = new ArrayList<Integer>(bp1.size());
        for(int i = 0; i < bp1.size(); i++){
            bpComb.add(-3);
        }
        //For loop adds all of the basepairs that are shared by both nodes to the new ArrayList<Integer>
        for(int i = 0; i < bp1.size(); i++){
            if(bp1.get(i) >= 0 && bp2.get(i) >= 0 && bp1.get(i) == bp2.get(i) && bp1.get(bp1.get(i)) == bp2.get(bp2.get(i))){
                bpComb.set(i, bp1.get(i));
                bpComb.set(bp1.get(i), i);
            }
        }
        //all add all basepairs that one of them have and don't cause a pseudoknot
        for(int i = 0; i < bp1.size(); i++){
            if(bp1.get(i) >= 0 && i > bp1.get(i) && bp1.get(bp1.get(i)) == i && pseudoknotCheck(bpComb, i, bp1.get(i))){
                bpComb.set(i, bp1.get(i));
                bpComb.set(bp1.get(i), i);
            }
            if(bp2.get(i) >= 0 && i > bp2.get(i) && bp2.get(bp2.get(i)) == i && pseudoknotCheck(bpComb, i, bp2.get(i))){
                bpComb.set(bp2.get(i), i);
                bpComb.set(i, bp2.get(i));
            }
        }
        for(int i = 0; i < bp1.size(); i++){
            if((bp1.get(i) == -2 || bp1.get(i) >= 0) && bpComb.get(i) == -3) bpComb.set(i, -2);
            if((bp2.get(i) == -2 || bp2.get(i) >= 0) && bpComb.get(i) == -3) bpComb.set(i, -2);
            if(bpComb.get(i) == -3) bpComb.set(i, -1);
        }
        return bpComb;
    }
    //Retruns true if adding the bases to the ArrayList would NOT create a pseudoknot
    private static boolean pseudoknotCheck(ArrayList<Integer> bpComb, int i1, int i2) {
        boolean knot = true;
        if(bpComb.get(i1) >= 0 || bpComb.get(i2) >= 0) return false;
        for(int i = 0; i< bpComb.size(); i++){
            if(bpComb.get(i) >= 0){
                if(!((i1 > i && i2 < bpComb.get(i)) || (i1 < i && i2 > bpComb.get(i)))) knot = false;
            }
        }
        return knot;
    }

    private static int getPseudoScore(PhyloTreeNode node, int index, String curBase, String parent,
                                      Collection<String> singleBases, Collection<String> pairedBases){
        int score = 0;
        try{
            score = node.getSankoffPseudoScore(index, curBase);
        }catch(IndexOutOfBoundsException e){
            score = sankoffPseudoPair(node, curBase, index, singleBases, pairedBases);
            node.addSankoffPseudoScore(index, curBase, score);
        }
        score += MainMethodClass.cost(parent, curBase);
        return score;
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
        if(seq.substring(seq.length()-1).equals(".")) gaps.add(seq.length()-1);
        seq = seq.replace(".", "");
        seq = seq.concat("\n");
        out.write(seq);
        out.flush();
        String line;
        int j = 0;
        while(j < 2 && ( line = inp.readLine()) != null){
            if(j == 1){
                int lastClosed = line.lastIndexOf(")");
                int lastOpen = MainMethodClass.findClosestOpen(lastClosed, line);
                String energy = line.substring(lastOpen);
                line = line.substring(0, lastOpen);
                line = line.replace(" ", "");
                for(int i = 0; i < gaps.size(); i++){
                    line = line.substring(0,gaps.get(i)).concat(".".concat(line.substring(gaps.get(i))));
                }
                curNode.setFolding(line);
                energy = energy.replace("(", "");
                energy = energy.replace(")", "");
                energy =  energy.trim();
                curNode.setEnergy(Double.parseDouble(energy));
            }
            j++;
        }
        p.destroy();

    }
    public static void layerAndParsimony(PhyloTree tree){
        int parsimonyScore;
        PhyloTreeNode root = tree.getRoot();
        Queue<PhyloTreeNode> all = new LinkedList<PhyloTreeNode>();
        Queue<PhyloTreeNode> leafs = new LinkedList<PhyloTreeNode>();
        Queue<PhyloTreeNode> bottomUp = new LinkedList<PhyloTreeNode>();
        all.add(root);
        PhyloTreeNode cur;
        while(!all.isEmpty()){
            cur = all.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                all.add(cur.getChildren().get(i));
                cur.getChildren().get(i).setLayer(cur.getLayer() + 1);
            }
            if(cur.getChildren().size() < 2) leafs.add(cur);
        }
        while(!leafs.isEmpty()){
            cur = leafs.poll();
            bottomUp.add(cur.getParent());
        }
        while(!bottomUp.isEmpty()){
            cur = bottomUp.poll();
            if(cur.getParsimonyScore() == 0){
                if(cur.getParent() != null) bottomUp.add(cur.getParent());
                int newScore = 0;
                String childLSeq = cur.getChildren().get(0).getSequence();
                String childRSeq = cur.getChildren().get(1).getSequence();
                String curSeq = cur.getSequence();
                for(int i = 0; i < cur.getSequence().length(); i++){
                    if(childLSeq.length() != childRSeq.length()){
                        int fdfd = 33;
                    }
                    if(curSeq.charAt(i) != childLSeq.charAt(i))
                        newScore += MainMethodClass.cost(curSeq.substring(i, i+1), childLSeq.substring(i, i+1));
                    if(curSeq.charAt(i) != childRSeq.charAt(i))
                        newScore += MainMethodClass.cost(curSeq.substring(i, i+1), childRSeq.substring(i, i+1));
                }
                newScore = newScore + cur.getChildren().get(0).getParsimonyScore()
                         + cur.getChildren().get(1).getParsimonyScore();
                cur.setParsimonyScore(newScore);
                cur.setLayer(cur.getChildren().get(0).getLayer() + 1);
            }
        }
        recursiveParsimony(root);
    }

    private static int recursiveParsimony(PhyloTreeNode node) {
        if(node.getChildren().size() == 0){
            node.setParsimonyScore(0);
            return 0;
        }
        else{
            String childLSeq = node.getChildren().get(0).getSequence();
            String childRSeq = node.getChildren().get(1).getSequence();
            String curSeq = node.getSequence();
            int newScore = 0;
            for(int i = 0; i < node.getSequence().length(); i++){
                if(curSeq.charAt(i) != childLSeq.charAt(i))
                    newScore += MainMethodClass.cost(curSeq.substring(i, i+1), childLSeq.substring(i, i+1));
                if(curSeq.charAt(i) != childRSeq.charAt(i))
                    newScore += MainMethodClass.cost(curSeq.substring(i, i+1), childRSeq.substring(i, i+1));
            }
            newScore = newScore + recursiveParsimony(node.getChildren().get(0))
                    + recursiveParsimony(node.getChildren().get(1));
            node.setParsimonyScore(newScore);
            return newScore;
        }
    }
}
