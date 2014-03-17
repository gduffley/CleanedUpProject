import com.sun.javaws.Main;

import java.util.*;

/**
 * Created by Gordon on 3/17/14.
 */
public class KeepStructure {

    public static int keepStructureRecursive(PhyloTreeNode node, String base, int pos){
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
                sum += keepStructureRecursive(node.getChildren().get(0), curL, pos);
                if(sum > maxL){
                    maxL = sum;
                    bestL = curL;
                }
            }
            int maxR = -MainMethodClass.INF * 2;
            while(itR.hasNext()){
                curR = itR.next();
                sum = MainMethodClass.cost(base, curR);
                sum += keepStructureRecursive(node.getChildren().get(1), curR, pos);
                if(sum > maxR){
                    maxR = sum;
                    bestR = curR;
                }
            }
            totalMax = maxR + maxL;

            //non-essential code to get sequences at each letter
            if(pos == 0 && node.getChildren().get(0).getChildren().size() > 1){
                node.getChildren().get(0).setPontSequence(bestL);
                node.getChildren().get(1).setPontSequence(bestR);
            }
            if(pos > 0 && node.getChildren().get(0).getChildren().size() > 1){
                PhyloTreeNode leftChild = node.getChildren().get(0);
                PhyloTreeNode rightChild = node.getChildren().get(1);
                if(!(pos < leftChild.getPontSequence().length())){
                    leftChild.setPontSequence(leftChild.getPontSequence().concat(bestL));
                    rightChild.setPontSequence(rightChild.getPontSequence().concat(bestR));
                }
                else{
                    String[] leftSeq = leftChild.getPontSequence().split("");
                    String[] rightSeq = rightChild.getPontSequence().split("");
                    leftSeq[pos + 1] = bestL;
                    rightSeq[pos + 1] = bestR;
                    String newL = "";
                    for(String str: leftSeq)newL += str;
                    String newR = "";
                    for(String str: rightSeq)newR += str;
                    leftChild.setPontSequence(newL);
                    rightChild.setPontSequence(newR);
                }
            }

        }
        return totalMax;
    }

    public static void keepStructure(PhyloTree tree){
        PhyloTreeNode curNode;
        int seqLength;
        int totalScore = 0;
        Collection<String> bases = new ArrayList<String>();
        bases.add("A");
        bases.add("C");
        bases.add("G");
        bases.add("U");
        bases.add(".");
        curNode = tree.getRoot();
        while((curNode.getChildren().size()) > 0 ) curNode = curNode.getChildren().get(0);
        String seq = curNode.getSequence();
        seq = seq.replace(",","");
        seqLength = seq.length();
        for(int i = 0; i < seqLength; i++){
            Iterator<String> it = bases.iterator();
            int bestScore = -MainMethodClass.INF;
            String bestBase = "";
            while(it.hasNext()){
                String curBase = it.next();
                int curBaseScore = keepStructureRecursive(tree.getRoot(), curBase, i);
                if(curBaseScore > bestScore){
                    bestScore = curBaseScore;
                    bestBase = curBase;
                }
            }
            tree.getRoot().setPontSequence(tree.getRoot().getPontSequence().concat(bestBase));
            totalScore += bestScore;
        }
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(tree.getRoot());
        PhyloTreeNode curN;
        while(! q.isEmpty()){
            curN = q.poll();
            for(int i = 0; i < curN.getChildren().size(); i++){
                q.add(curN.getChildren().get(i));
            }
            String curSeq = curN.getPontSequence();
            curSeq = curSeq.replace(",","");
            curSeq = curSeq.replace("", ",");
            if(curSeq.startsWith(",")) curSeq = curSeq.replaceFirst(",", "");
            if(curSeq.endsWith(",")) curSeq = curSeq.substring(0,curSeq.length() -1);
            curN.setPontSequence(curSeq);
        }


    }


}