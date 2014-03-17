import java.util.*;

/**
 * Created by Gordon on 3/17/14.
 */
public class Sankoff {
    public static int sankoff(PhyloTree tree){
        int min = -MainMethodClass.INF;
        int cur = 0;
        int totalScore = 0;
        Collection<String> bases = new ArrayList<String>();
        bases.add("A");
        bases.add("C");
        bases.add("G");
        bases.add("U");
        bases.add(".");
        String bestBase = "";
        String curBase = "";
        PhyloTreeNode curNode = tree.getRoot();
        while(curNode.getChildren().size() == 2) curNode = curNode.getChildren().get(1);
        String seq = curNode.getSequence();
        seq = seq.replace(",","");
        int seqLength = seq.length();
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
            if(i == 0) tree.getRoot().setSequence(bestBase);
            else tree.getRoot().setSequence(tree.getRoot().getSequence().concat(bestBase));
            totalScore += min;
            min = -MainMethodClass.INF;
        }
        System.out.println(totalScore);
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(tree.getRoot());
        PhyloTreeNode curN;
        while(! q.isEmpty()){
            curN = q.poll();
            for(int i = 0; i < curN.getChildren().size(); i++){
                q.add(curN.getChildren().get(i));
            }
            String curSeq = curN.getSequence();
            curSeq = curSeq.replace(",","");
            curSeq = curSeq.replace("", ",");
            if(curSeq.startsWith(",")) curSeq = curSeq.replaceFirst(",", "");
            if(curSeq.endsWith(",")) curSeq = curSeq.substring(0,curSeq.length() -1);
            curN.setSequence(curSeq);
        }

        return totalScore;
    }

    private static int sankoffRecursion(PhyloTreeNode node, String base, int pos){
        int totalMax = -MainMethodClass.INF;
        int totalSum = 0;
        String bestBase = " ";
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
        if(node.getChildren().size() < 2){
            if(seqArray[pos+1].equals(base)){
                return 0;
            }
            else{
                return -MainMethodClass.INF;
            }
        }    //the score to go from our current pretend base to a new base
        //plus the sankoff of running the child
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

            //non-essential code to get sequences at each letter
            if(pos == 0 && node.getChildren().get(0).getChildren().size() > 1){
                node.getChildren().get(0).setSequence(bestL);
                node.getChildren().get(1).setSequence(bestR);
            }
            if(pos > 0 && node.getChildren().get(0).getChildren().size() > 1){
                PhyloTreeNode leftChild = node.getChildren().get(0);
                PhyloTreeNode rightChild = node.getChildren().get(1);
                if(!(pos < leftChild.getSequence().length())){
                    leftChild.setSequence(leftChild.getSequence().concat(bestL));
                    rightChild.setSequence(rightChild.getSequence().concat(bestR));
                }
                else{
                    String[] leftSeq = leftChild.getSequence().split("");
                    String[] rightSeq = rightChild.getSequence().split("");
                    leftSeq[pos + 1] = bestL;
                    rightSeq[pos + 1] = bestR;
                    String newL = "";
                    for(String str: leftSeq)newL += str;
                    String newR = "";
                    for(String str: rightSeq)newR += str;
                    leftChild.setSequence(newL);
                    rightChild.setSequence(newR);
                }
            }
            //end of non-essential code
            PhyloTreeNode current = node;
            while(current.getChildren().size() > 1){
                current = current.getChildren().get(0);
            }
            int seqLength = current.getSequence().length();
            ArrayList<Integer> differences = new ArrayList<Integer>();
            if(node.getPontSequence().length() == seqLength){
                String oldSeq = node.getSequence();
                String pontSeq = node.getPontSequence();
                for(int i = 0; i < seqLength; i++){
                    if(oldSeq.charAt(i) == pontSeq.charAt(i)) differences.add(i);
                }

            }

        }
        return totalMax;
    }
    //if we aren't at a leaf, for our pretend base
    //the score for our base is dependent on 2 criteria

}
