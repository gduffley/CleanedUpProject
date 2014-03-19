import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Gordon on 3/19/14.
 */
public class KeepStructure2 {
    private static void keepStructure(PhyloTree tree){
        PhyloTreeNode cur = tree.getRoot();
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(cur);
        while(!q.isEmpty()){
            cur = q.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                q.add(cur.getChildren().get(i));
            }
            findSequence(cur);
        }
    }

    private static void findSequence(PhyloTreeNode cur) {
        //The sequence is final for the children
        //the folding is accurate for the sequence that comes from original sankoff
        //if both the parents have the same folding, we want to find the max nu
        //Case 1: Folding of both of the children and the parents is the same, then keep
        boolean foldingSame = true;
        for(int i = 0; i < cur.getChildren().size(); i++){
            if(!cur.getChildren().get(i).getFolding().equals(cur.getFolding())) foldingSame = false;
        }
        if(foldingSame) return;

        //Case 2: The folding is not all the same.
            //Step1: Find the threshold = sum of distances of folding between
    }
}
