/**
 * Created by Gordon on 2/11/14.
 */
//te
public class PhyloTree {
    private PhyloTreeNode root;
    private String consensusSequence;
    private String name;
    private int numberOfLeafs;

    public int getNumberOfLeafs() {
        return numberOfLeafs;
    }

    public void setNumberOfLeafs(int numberOfLeafs) {
        this.numberOfLeafs = numberOfLeafs;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhyloTree(String rootName, String sequence){
        root = new PhyloTreeNode(rootName, sequence);
        this.name = "You didnt pass it a name";
        this.numberOfLeafs = 0;
    }

    public void addNode(PhyloTreeNode newRoot){
        newRoot.addChild(this.root);
        root = newRoot;
    }
    public PhyloTreeNode getRoot(){
        return this.root;
    }
    public void setConsensusSequence(String a) {this.consensusSequence = a;}
    public String getConsensusSequence()  {return this.consensusSequence; }

}