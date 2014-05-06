import java.util.*;

/**
 * Created by Gordon on 3/17/14.
 */

/*
This example pretends that the sequences are only a single base long, just iterate over all the bases
Steps:
    1) We are going to run the algorithm on the 5 bases, called B, and select the base with the max score
    2) Score(B) = for each base E max{transition(B --> E) + ScoreOfLeftChild(E)}
        + max for each base E max {transition(B --> E) + ScoreOfRightChild(E)}
    3) base case is if we are at the leaf, where score(B) = 0 if the sequence at the leaf = B, and -INF o.w.

    Achieve step 1 by iterating through each of the 5 bases in Sankoff(PhyloTree tree)
    This calls sankoffRecursion() which goes through and tests each base

 */
public class Sankoff {/*
    public static int sankoff(PhyloTree tree){
        int min = -MainMethodClass.INF;
        int cur = 0;
        int totalScore = 0;
        Collection<String> bases = new ArrayList<String>(); //list with the 4 bases so we can iterate through them
        bases.add("A");
        bases.add("C");
        bases.add("G");
        bases.add("U");
        bases.add(".");
        String bestBase = "";
        String curBase = "";
        PhyloTreeNode curNode = tree.getRoot();
        //Move through the tree to find a leaf, and take the leaf to find the length of the sequences
        //so you know how many times you have to run the algorithm
        while(curNode.getChildren().size() == 2) curNode = curNode.getChildren().get(1);
        String seq = curNode.getSequence();
        seq = seq.replace(",","");
        int seqLength = seq.length();
        //for each place in the sequence, we are going to test all for bases, and select the base that
        //has the highest score. Get the scores by calling sankoffRecursion(root, current Base from the list,
        //the position in the sequence we are working on)
        for(int i = 0; i < seqLength; i++){
            Iterator<String> it = bases.iterator();
            while(it.hasNext()){
                curBase = it.next();
                cur = sankoffRecursion(tree.getRoot(), curBase, i);
                if(cur > min){
                    min = cur;
                    bestBase = curBase;
                }
            }
            //if we are on the first letter, we need to erase "tbd" and add the base to the sequence
            //else we are just adding the base to the sequence
            if(i == 0) tree.getRoot().setSequence(bestBase);
            else tree.getRoot().setSequence(tree.getRoot().getSequence().concat(bestBase));
            Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
            q.add(tree.getRoot());
            PhyloTreeNode curN;
            while(! q.isEmpty()){
                curN = q.poll();
                for(int j = 0; j < curN.getChildren().size(); j++){
                    q.add(curN.getChildren().get(j));
                }
                if(curN.getChildren().size() == 2){
                    PhyloTreeNode lChild = curN.getChildren().get(0);
                    PhyloTreeNode rChild = curN.getChildren().get(1);
                    if(lChild.getChildren().size() == 2){
                        if(i == 0){
                            lChild.setSequence("");
                        }
                        char curB = curN.getSequence().charAt(i);
                        if(curB == 'A'){
                            lChild.setSequence(lChild.getSequence().concat(lChild.getIfParentisA()));
                        }
                        if(curB == 'C'){
                            lChild.setSequence(lChild.getSequence().concat(lChild.getIfParentisC()));
                        }
                        if(curB == 'G'){
                            lChild.setSequence(lChild.getSequence().concat(lChild.getIfParentisG()));
                        }
                        if(curB == 'U'){
                            lChild.setSequence(lChild.getSequence().concat(lChild.getIfParentisU()));
                        }
                        if(curB == '.'){
                            lChild.setSequence(lChild.getSequence().concat(lChild.getIfParentisGap()));
                        }
                    }
                    if(rChild.getChildren().size() > 0){
                        if(i == 0){
                            rChild.setSequence("");
                        }
                        char curB = curN.getSequence().charAt(i);
                        if(curB == 'A'){
                            rChild.setSequence(rChild.getSequence().concat(rChild.getIfParentisA()));
                        }
                        if(curB == 'C'){
                            rChild.setSequence(rChild.getSequence().concat(rChild.getIfParentisC()));
                        }
                        if(curB == 'G'){
                            rChild.setSequence(rChild.getSequence().concat(rChild.getIfParentisG()));
                        }
                        if(curB == 'U'){
                            rChild.setSequence(rChild.getSequence().concat(rChild.getIfParentisU()));
                        }
                        if(curB == '.'){
                            rChild.setSequence(rChild.getSequence().concat(rChild.getIfParentisGap()));
                        }
                    }
                }
            }
            totalScore += min;
            min = -MainMethodClass.INF;

        }

        System.out.println(totalScore);
        return totalScore;
    }

    public static int sankoffRecursion(PhyloTreeNode node, String base, int pos){
        int totalMax = -MainMethodClass.INF;
        int totalSum = 0;
        String bestBase = " ";
        //array with the bases in it
        Collection<String> bases = new ArrayList<String>();
        bases.add("A");
        bases.add("C");
        bases.add("G");
        bases.add("U");
        bases.add(".");
        //lets pretend that the base in the current position is the base passed
        Iterator<String> itL = bases.iterator();
        Iterator<String> itR = bases.iterator();
        String curL;
        String curR;
        String seqMod = node.getSequence().replace(",", "");
        String[] seqArray = seqMod.split("");
        String bestL = "";
        String bestR = "";
        //if we are at a leaf
        //if our pretend base matches the actual base that is there
        //score of 0, ow score of -MainMethodClass.INF
        //this is the base case of our recursion
        //it represents us trying all 4 bases for the spot in our leafs
        //because we already know what the base is going to be, we should assign a score of 0 for it
        //and a score of -INF for all other bases
        if(node.getChildren().size() < 2){
            int test = 12;
            if(seqArray[pos+1].equals(base)){
                return 0;
            }
            else{
                return -MainMethodClass.INF;
            }
        }
        //the score to go from our current pretend base to a new base
        //plus the sankoff of running the child
        //if we aren't at a leaf, our recursion formula is score of sankoff on the left leaf with our given base
        //+ score of sankoff when assigning this base to the right + the cost of going from our current base to
        //the new base in the left and the new base in the right
        //Easiest to explain at root
            //So we get Sankoff called on the root and we have a base given from the iterative function, e.g. "A"
            //The score will be the sum of running Sankoff on the left and right children giving it the base "A"
            //Their is no transition cost from going to "A" to "A", so that part is 0, and then we add the scores
            //of running sankoff on the right and left giving it "A" like the iterative gave the root "A".
            //the base case that kicks it all back up is when we get to the root, in which the score of sankoff
            //on it is either 0 if it's the base that is in the sequence, or its -INF if any other base
        //The code below calls Sankoff recursion on the children for each of the possible bases
        //remembers which bases for the children have the maximum score
        //the total score is the max score of running the sankoff on the left and right + the transition of the
        //current base to the bases in the sankoff.
        else{
            int maxL = -MainMethodClass.INF * 2;
            int sum;
            while(itL.hasNext()){
                curL = itL.next();
                sum = MainMethodClass.cost(base, curL);
                sum += sankoffRecursion(node.getChildren().get(0), curL,pos);
                if(sum > maxL){
                    maxL = sum;
                    bestL = curL;
                }
            }
            int maxR = -MainMethodClass.INF * 2;
            while(itR.hasNext()){
                curR = itR.next();
                sum = MainMethodClass.cost(base, curR);
                sum += sankoffRecursion(node.getChildren().get(1), curR, pos);
                if(sum > maxR){
                    maxR = sum;
                    bestR = curR;
                }
            }
            totalMax = maxR + maxL;

            //Assigns the spot in the sequence for both the left and right children with the
            //This code is wrong because in order to decide what value the sequence will actually be, you need
            //to know what the parent will be, where this code will make the sequence what the best option
            //for the last base that is attempted for the parent

            PhyloTreeNode leftChild = node.getChildren().get(0);
            PhyloTreeNode rightChild = node.getChildren().get(1);

            if(base == "A"){
                leftChild.setIfParentisA(bestL);
                rightChild.setIfParentisA(bestR);
            }
            if(base == "C"){
                leftChild.setIfParentisC(bestL);
                rightChild.setIfParentisC(bestR);
            }
            if(base == "G"){
                leftChild.setIfParentisG(bestL);
                rightChild.setIfParentisG(bestR);
            }
            if(base == "U"){
                leftChild.setIfParentisU(bestL);
                rightChild.setIfParentisU(bestR);
            }
            if(base == "."){
                leftChild.setIfParentisGap(bestL);
                rightChild.setIfParentisGap(bestR);
            }

        }
        return totalMax;
    }
    //if we aren't at a leaf, for our pretend base
    //the score for our base is dependent on 2 criteria
*/
}
