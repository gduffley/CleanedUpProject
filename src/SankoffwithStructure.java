import java.util.*;

/**
 * Created by Gordon on 4/8/14.
 */

/*

Notes:
-> So you start by assigning a single base to the index of the sequence
-> Recursively get deeper until you have reach a node that has a sequence already
-> If the base you are working with is part of a base pair, start the algorithm over, but pass in 2 inputs
and 2 spots in the sequence. For both the left and right children pass in all 25 combinations for the 2 spots and
save the score of the best pair. Do this all the way up and now you will have the sequence for 2 spots
    -> How does this help conserve base pairing?
    ->Maybe because
--> Start with a single base and an index
--> Take the consensus structure
    run the normal Sankoff for all of the indices that aren't involved in a base pair
    run the pairwise Sankoff for all of the indices the pairs of indices that can form a basepair. Out of
    those options select the one with the best score.


Steps:
1) Get the folding of the leafs
2) Extract out all of the pairs of bp that are base paired together
3) If in the leaf there is base pairing, then we will run sankoff on these 2 bases together, treating them as a
single pair of bases
4)



New Strategy
--> For every node, keep the base pairing of all of the leafs that are below it or keep what you're doing
--> Need to make it so that even if you have a base pair in one child, the other child still has access to all
bases. You could do this by imposing the consensus on all of the nodes except the leafs
 */

public class SankoffwithStructure {
    public static void sankoffwithStructure(PhyloTree tree){
        PhyloTreeNode curNode = tree.getRoot();
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        s.push(curNode);
        int seqLength = 0;
        while(!s.isEmpty()){
            curNode = s.pop();
            if(curNode.getChildren().size() == 2){
                s.push(curNode.getChildren().get(0));
                s.push(curNode.getChildren().get(1));
            }
            else seqLength = curNode.getSequence().length();
        }
        String conSeq = tree.getConsensusSequence();
        Collection<Integer> singles = new ArrayList<Integer>();
        Collection<Integer> firstOfPair = new ArrayList<Integer>();
        Collection<Integer> secondOfPair = new ArrayList<Integer>();
        for(int i = 0; i < conSeq.length(); i++){
            if(conSeq.charAt(i) == '.') singles.add(i);
            if(conSeq.charAt(i) == '<'){
                firstOfPair.add(i);
                int count = 1;
                for(int j = i + 1; j < conSeq.length(); j++){
                    if(conSeq.charAt(j) == '<') count++;
                    if(conSeq.charAt(j) == '>') count--;
                    if(count == 0){
                        secondOfPair.add(j);
                        break;
                    }
                }
            }
        }
        int sum = 0;
        for(Integer i : singles){
            sum += sankoffSingles(tree, i, seqLength);

        }
        Iterator<Integer> first = firstOfPair.iterator();
        Iterator<Integer> second = secondOfPair.iterator();
        while(first.hasNext()){
            sankoffPairs(tree, first.next(), second.next(), seqLength);
        }


    }

    private static void sankoffPairs(PhyloTree tree, Integer index1, Integer index2, int seqLength) {
        Collection<String> basePairs = new ArrayList<String>();
        basePairs.add("CG");
        basePairs.add("GC");
        basePairs.add("AU");
        basePairs.add("UA");
        basePairs.add("GU");
        basePairs.add("UG");
        Iterator<String> it = basePairs.iterator();
        String curBase = "";
        String bestBase = "";
        int maxScore = -MainMethodClass.INF;
        int curScore = 0;
        while(it.hasNext()){
            curBase = it.next();
            curScore = sankoffPairsRecursion(tree.getRoot(), index1, index2, curBase);
            if(curScore > maxScore){
                curScore = maxScore;
                bestBase = curBase;
            }
        }
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        PhyloTreeNode curNode = tree.getRoot();
        s.push(curNode);
        while(!s.empty()){
            curNode = s.pop();
            for(int i = 0; i < curNode.getChildren().size(); i++){
                s.push(curNode.getChildren().get(i));
            }
        }
    }

    private static int sankoffPairsRecursion(PhyloTreeNode node, Integer index1, Integer index2, String curBase) {
        Collection<String> basePairs = new ArrayList<String>();
        basePairs.add("CG");
        basePairs.add("GC");
        basePairs.add("AU");
        basePairs.add("UA");
        basePairs.add("GU");
        basePairs.add("UG");
        int bestScore = -MainMethodClass.INF;
        int curScore = 0;
        if(node.getChildren().size() == 0){

        }
        Iterator<String> it = basePairs.iterator();
        while(it.hasNext()){
            score
        }









        return 0;
    }

    public static int sankoffSingles(PhyloTree tree, int index, int seqLength){
        Collection<String> singleBases = new ArrayList<String>();
        singleBases.add("A");
        singleBases.add("C");
        singleBases.add("G");
        singleBases.add("U");
        Iterator<String> it = singleBases.iterator();
        int max = -MainMethodClass.INF;
        int cur = -MainMethodClass.INF;
        String bestBase = "";
        String curBase = "";
        while(it.hasNext()){
            curBase = it.next();
            cur = sankoffSinglesRecursion(tree, curBase, index);
            if(cur > max){
                max = cur;
                bestBase = curBase;
            }
        }
        if(index == 0) tree.getRoot().setSequence(bestBase);
        PhyloTreeNode curNode = tree.getRoot();
        Stack<PhyloTreeNode> s = new Stack<PhyloTreeNode>();
        s.push(curNode);
        while(!s.empty()){
            curNode = s.pop();
            for(int i = 0; i < curNode.getChildren().size(); i++){
                s.push(curNode.getChildren().get(i));
            }
            if(curNode.getChildren().size() > 0 &&
                    curNode.getChildren().get(0).getChildren().size() > 0 &&
                    curNode.getChildren().get(1).getChildren().size() > 0){
                PhyloTreeNode c0 = curNode.getChildren().get(0);
                PhyloTreeNode c1 = curNode.getChildren().get(1);
                String c0Base = "";
                String c1Base = "";
                switch(curNode.getSequence().charAt(index)){
                    case 'A':
                        c0Base = c0.getIfParentisA();
                        c1Base = c1.getIfParentisA();
                        break;
                    case 'C':
                        c0Base = c0.getIfParentisC();
                        c1Base = c1.getIfParentisC();
                        break;
                    case 'G':
                        c0Base = c0.getIfParentisG();
                        c1Base = c1.getIfParentisG();
                        break;
                    case 'U':
                        c0Base = c0.getIfParentisU();
                        c1Base = c1.getIfParentisU();
                        break;
                    case '.':
                        c0Base = c0.getIfParentisGap();
                        c1Base = c1.getIfParentisGap();
                        break;
                }
                if(index == 1){
                    String c0Sequence = c0Base;
                    String c1Sequence = c1Base;
                    for(int i = 0; i < seqLength-1; i++){
                        c0Sequence = c0Sequence.concat("?");
                        c1Sequence = c1Sequence.concat("?");
                    }
                    c0.setSequence(c0Base);
                    c1.setSequence(c1Base);
                }
                else{
                    String c0Sequence = c0.getSequence();
                    String c1Sequence = c1.getSequence();
                    char[] c0Array = c0Sequence.toCharArray();
                    char[] c1Array = c1Sequence.toCharArray();
                    c0Array[index] = c0Base.charAt(0);
                    c1Array[index] = c1Base.charAt(1);
                    c0Sequence = "";
                    c1Sequence = "";
                    for(int i = 0; i < c0Array.length; i++){
                        c0Sequence = c0Sequence.concat(Character.toString(c0Array[i]));
                        c1Sequence = c1Sequence.concat(Character.toString(c1Array[i]));
                    }
                    c1.setSequence(c1Sequence);
                    c0.setSequence(c0Sequence);
                }
            }
        }
        return max;
    }

    private static int sankoffSinglesRecursion(PhyloTree tree, String curBase, int index) {
        return Sankoff.sankoffRecursion(tree.getRoot(), curBase, index);
    }
}
