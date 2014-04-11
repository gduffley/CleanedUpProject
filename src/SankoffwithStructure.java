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
    ->Maybe becasue
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
 */
public class SankoffwithStructure {
    public static void sankoffwithStructure(PhyloTree tree){
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
        for(Integer i : singles){
            sankoffSingles(tree, i);

        }
        Iterator<Integer> first = firstOfPair.iterator();
        Iterator<Integer> second = secondOfPair.iterator();
        while(first.hasNext()){
            sankoffDoubles(tree, first.next(), second.next());
        }


    }

    private static void sankoffDoubles(PhyloTree tree, Integer index1, Integer index2) {

    }

    public static void sankoffSingles(PhyloTree tree, int index){
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
                if(index == 0){
                    switch(curNode.getSequence().charAt(index)){
                        case 'A':
                            c0.setSequence(c0.getIfParentisA());
                            c1.setSequence(c1.getIfParentisA());
                            break;
                        case 'C':
                            c0.setSequence(c0.getIfParentisC());
                            c1.setSequence(c1.getIfParentisC());
                            break;
                        case 'G':
                            c0.setSequence(c0.getIfParentisG());
                            c1.setSequence(c1.getIfParentisG());
                            break;
                        case 'U':
                            c0.setSequence(c0.getIfParentisU());
                            c1.setSequence(c1.getIfParentisU());
                            break;
                        case '.':
                            c0.setSequence(c0.getIfParentisGap());
                            c1.setSequence(c1.getIfParentisGap());
                            break;

                    }
                }
                else{
                    switch(curNode.getSequence().charAt(index)){
                        case 'A':
                            c0.setSequence(c0.getSequence().concat(c0.getIfParentisA()));
                            c1.setSequence(c1.getSequence().concat(c1.getIfParentisA()));
                            break;
                        case 'C':
                            c0.setSequence(c0.getSequence().concat(c0.getIfParentisC()));
                            c1.setSequence(c1.getSequence().concat(c1.getIfParentisC()));
                            break;
                        case 'G':
                            c0.setSequence(c0.getSequence().concat(c0.getIfParentisG()));
                            c1.setSequence(c1.getSequence().concat(c1.getIfParentisG()));
                            break;
                        case 'U':
                            c0.setSequence(c0.getSequence().concat(c0.getIfParentisU()));
                            c1.setSequence(c1.getSequence().concat(c1.getIfParentisU()));
                            break;
                        case '.':
                            c0.setSequence(c0.getSequence().concat(c0.getIfParentisGap()));
                            c1.setSequence(c1.getSequence().concat(c1.getIfParentisGap()));
                            break;
                    }
                }
            }
        }
    }

    private static int sankoffSinglesRecursion(PhyloTree tree, String curBase, int index) {
        return Sankoff.sankoffRecursion(tree.getRoot(), curBase, index);
    }
}
