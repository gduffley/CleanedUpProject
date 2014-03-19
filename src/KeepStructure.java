

import java.io.*;
import java.util.*;

/**
 * Created by Gordon on 3/17/14.
 */
public class KeepStructure {

    //option 1: Take sequences found from Sankoff, then at each step keep as many of the changes as possible,
    //but at the same time keeping the folding equal to the consensus sequence. --> assumes that all of the leaves
    //have folding = to the consensus sequence.

    //option 2: Take the sequences found at the Sankoff, then keep as many changes as possible but at the same time
    //conserve consensus, determine this before moving up to the next level in the Sankoff recursion

    //option 3: If you can write Sankoff to get a whole sequence at a time rather than a base at a time all the way up,
    //then just do sankoff and focus at maintaing folding at each level before moving up the next.
        //cons: if it is even possible to do Sankoff this way, you have to implement it

    //Best plausible option: Calculate Sankoff all the way through the way you already do. For each sequence find the
    //max number of the potential changes you can keep while at the same time keeping the sum of the distance from
    //each child <= the distance between the 2 children
        //cons: assumes that "integrity" of the Sankoff is the same even when we alter the children.

    //option 4: Do the sankoff but only change the base if it wont change pairing


    //Methods to conserve folding:
        //1)Insist that the folding of all nodes is the same as the consensus
        //2)Keep the max number of changes that you can, but at the same time make sure the sum of the differences of
          //the folding to both children <= the distance between the folding of the same children. This way if
          //both children have the same folding, the parent will have to keep this folding
        //3)Find a way ti minimize the differences in folding, ignoring what happens to base pairs
    //Ordering methods
        //1)Do the Sankoff and then adjust for these changes after
        //2)Find a way to do Sankoff one level at a time, conserve foldng, then move to the layer up
        //3)


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
            node.setPontSequence(node.getSequence());
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

    public static void keepStructureIterative(PhyloTree tree){
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
        /*Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
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
        }*/


    }
    public static void keepStructure(PhyloTree tree) throws IOException {
        keepStructureIterative(tree);
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        PhyloTreeNode cur;
        q.add(tree.getRoot());
        while(!q.isEmpty()){
            cur = q.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                q.add(cur.getChildren().get(i));
            }
            findSequence(cur);
        }
    }
    //take the potential sequence and detect the changes from
    private static void findSequence(PhyloTreeNode cur) throws IOException {
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
        String pont = cur.getPontSequence();
        String pontFolding = "";
        String pontEnergyString;
        double pontEnergy = 0;
        pont = pont.replace(".", "");
        int seqLength = pont.length();
        pont = pont.concat("\n");
        out.write(pont);
        out.flush();
        String line;
        int i = 0;
        while(i < 2 && ( line = inp.readLine()) != null){
            if(i == 1){
                int lastClosed = line.lastIndexOf(")");
                int lastOpen = MainMethodClass.findClosestOpen(lastClosed, line);
                String energy = line.substring(lastOpen);
                line = line.substring(0, lastOpen);
                pontFolding = line;
                pontEnergyString = energy.replace("(", "");
                pontEnergyString = energy.replace(")", "");
                pontEnergyString =  energy.trim();
                pontEnergy = Double.parseDouble(energy);
            }
            i++;
        }
        System.out.println(cur.getFolding() + "   " + Double.toString(cur.getEnergy()));
        p.destroy();
        boolean sameFolding = true;
        for(int j = 0; j < cur.getChildren().size(); j++){
            if(!pontFolding.equals(cur.getChildren().get(i).getFolding())) sameFolding = false;
        }
        if(sameFolding){
            cur.setSequence(cur.getPontSequence());
            cur.setEnergy(pontEnergy);
            return;
        }
        String seq = cur.getSequence();
        for(i = 0; i < seqLength; i++){
            if(pont.charAt(i) == (seq.charAt(i))) continue;
            else{
                String replacement =
            }
        }
    }



}
