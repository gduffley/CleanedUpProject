/**
 * Created by Gordon on 2/11/14.
 */
import java.util.ArrayList;
//mic check
public class PhyloTreeNode {
    private String sequence;
    private String name;
    private PhyloTreeNode parent;
    private ArrayList<PhyloTreeNode> children = new ArrayList<PhyloTreeNode>();
    private String folding;
    private double energy;
    private int distanceFromConsensus;
    private String ifParentisA = "";
    private String ifParentisC = "";
    private String ifParentisG = "";

    public String getIfParentisU() {
        return ifParentisU;
    }

    public void setIfParentisU(String ifParentisU) {
        this.ifParentisU = ifParentisU;
    }

    private String ifParentisU = "";
    private String ifParentisGap = "";

    public String getIfParentisA() {
        return ifParentisA;
    }

    public String getIfParentisC() {
        return ifParentisC;
    }

    public String getIfParentisG() {
        return ifParentisG;
    }

    public String getIfParentisGap() {
        return ifParentisGap;
    }

    public void setIfParentisA(String ifParentisA) {
        this.ifParentisA = ifParentisA;
    }

    public void setIfParentisC(String ifParentisC) {
        this.ifParentisC = ifParentisC;
    }

    public void setIfParentisG(String ifParentisG) {
        this.ifParentisG = ifParentisG;
    }

    public void setIfParentisGap(String ifParentisGap) {
        this.ifParentisGap = ifParentisGap;
    }

    public PhyloTreeNode(String name, String sequence){
        this.name = name;
        this.sequence = sequence;
        this.parent = null;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setParent(PhyloTreeNode parent){
        this.parent = parent;
    }

    public void addChild(PhyloTreeNode a){
        children.add(a);
        a.setParent(this);
    }

    public void removeChild(PhyloTreeNode a){
        this.children.remove(a);
    }

    public void setSequence(String sequence){
        this.sequence = sequence;
    }

    public String getName(){
        return this.name;
    }

    public String getSequence(){
        return this.sequence;
    }

    public PhyloTreeNode getParent() {return this.parent;}

    public ArrayList<PhyloTreeNode> getChildren() {return this.children;}

    public void setFolding(String folding){this.folding = folding;}
    public String getFolding() {return this.folding;}
    public void setEnergy(double energy){this.energy = energy;}
    public double getEnergy(){return this.energy;}
    public void setDistanceFromConsensus(int a){this.distanceFromConsensus = a;}
    public int getDistanceFromConsensus(){ return this.distanceFromConsensus;}

}

