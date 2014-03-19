import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Gordon on 3/17/14.
 */
public class MainMethodClass {
    public static final int INF = 10000;
    public static int[][] cost;
    public static int cost(String b1, String b2){
        int b1Int = 0;
        int b2Int = 0;
        switch (b1.charAt(0)){
            case 'A':
                b1Int = 0;
                break;
            case 'C':
                b1Int = 1;
                break;
            case 'G':
                b1Int = 2;
                break;
            case 'U':
                b1Int = 3;
                break;
            case '.':
                b1Int = 4;
                break;
        }
        switch (b2.charAt(0)){
            case 'A':
                b2Int = 0;
                break;
            case 'C':
                b2Int = 1;
                break;
            case 'G':
                b2Int = 2;
                break;
            case 'U':
                b2Int = 3;
                break;
            case '.':
                b2Int = 4;
                break;
        }
        return cost[b1Int][b2Int];
    }
    public static int findClosestOpen(int closedLocation, String line){
        int curClosestOpen = 0;
        int nextOpen = 0;
        while(nextOpen < closedLocation && nextOpen != -1){
            curClosestOpen = nextOpen;
            nextOpen = line.indexOf("(", curClosestOpen + 1);
        }
        return curClosestOpen;
    }
    private static void printTree(PhyloTree tree){
        PhyloTreeNode current = null;
        Queue<PhyloTreeNode> s = new LinkedList<PhyloTreeNode>();
        s.add(tree.getRoot());
        while(!s.isEmpty()){
            current = s.poll();
            System.out.print(current.getName());
            if(current.getParent() != null) System.out.print("     " + current.getParent().getName());
            else System.out.print("       no parent");
            System.out.println("     " + current.getSequence());
            if(current.getChildren() != null){
                for(int i = 0; i < current.getChildren().size(); i++){
                    s.add(current.getChildren().get(i));
                }
            }
        }
    }


    public static void main(String Args[]){
        try {
            PhyloTree tree = MakeTree.makeTree(Args[0], Args[1]);
            //System.out.println(bottomUp(tree.getRoot()));
            //printTree(tree);
            //topDown(tree.getRoot());
            //printTree(tree);
            //rnaFold(tree);
            //calcDistancesFromConsensus(tree);
            cost = new int[5][5];
            for(int i = 0; i < 5; i++){
                if(i != 4){
                    cost[i][4] = -2;
                    cost[4][i] = -2;
                }
                cost[i][i] = 0;
            }
            //get across the diagonal correct
            cost[0][1] = -2;
            cost[0][2] = -1;
            cost[0][3] = -2;
            cost[1][2] = -2;
            cost[1][3] = -1;
            cost[2][3] = -2;
            cost[1][0] = -2;
            cost[2][0] = -1;
            cost[3][0] = -2;
            cost[2][1] = -2;
            cost[3][1] = -1;
            cost[3][2] = -2;
            int j = 0;
            Sankoff.sankoff(tree);
            printTree(tree);
            KeepStructure.keepStructure(tree);
            Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
            PhyloTreeNode curNode = tree.getRoot();
            q.add(curNode);
            while(!q.isEmpty()){
                curNode = q.poll();
                for(int k = 0; k < curNode.getChildren().size(); k++){
                    q.add(curNode.getChildren().get(k));
                }
                System.out.println(curNode.getPontSequence());
            }







        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

