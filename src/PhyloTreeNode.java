/**
 * Created by Gordon on 2/11/14.
 */
import com.sun.java.swing.plaf.motif.resources.motif_zh_TW;

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
    private String ifParentisU = "";
    private String ifParentisGap = "";
    private ArrayList<Integer> basePairs = new ArrayList<Integer>();
    private int[] scoreForSinglesACGUGap = new int[] {1,1,1,1,1};
    private String[] baseIfParentAGCUGap = new String[] {"","","","",""};

    public void setBaseIfParentAGCUGap(String nodeBase, String parentBase){
        switch(parentBase.charAt(0)){
            case 'A': baseIfParentAGCUGap[0] = nodeBase;
                break;
            case 'C': baseIfParentAGCUGap[1] = nodeBase;
                break;
            case 'G': baseIfParentAGCUGap[2] = nodeBase;
                break;
            case 'U': baseIfParentAGCUGap[3] = nodeBase;
                break;
            case '.': baseIfParentAGCUGap[4] = nodeBase;
        }
    }

    public String getBaseIfParentACGUGap(String parentBase){
        switch(parentBase.charAt(0)){
            case 'A': return baseIfParentAGCUGap[0];
            case 'C': return baseIfParentAGCUGap[1];
            case 'G': return baseIfParentAGCUGap[2];
            case 'U': return baseIfParentAGCUGap[3];
            case '.': return baseIfParentAGCUGap[4];
        }
        return "NOTABASE";
    }

    public void setScoreForSinglesACGT(String base, int score){
        switch(base.charAt(0)){
            case 'A': scoreForSinglesACGUGap[0] = score;
                break;
            case 'C': scoreForSinglesACGUGap[1] = score;
                break;
            case 'G': scoreForSinglesACGUGap[2] = score;
                break;
            case 'U': scoreForSinglesACGUGap[3] = score;
                break;
            case '.': scoreForSinglesACGUGap[4] = score;
        }
    }

    public int getScoreForSinglesACGT(String base){
        switch(base.charAt(0)){
            case 'A': return scoreForSinglesACGUGap[0];
            case 'C': return scoreForSinglesACGUGap[1];
            case 'G': return scoreForSinglesACGUGap[2];
            case 'U': return scoreForSinglesACGUGap[3];
            case '.': return scoreForSinglesACGUGap[4];
        }
        return 1;
    }


    public void setBasePair(int i, int j){
        basePairs.set(i, j);
        basePairs.set(j, i);
    }

    public ArrayList<Integer> getBasePairs(){
        return basePairs;
    }

    public String getIfParentisU() {
        return ifParentisU;
    }

    public void setIfParentisU(String ifParentisU) {
        this.ifParentisU = ifParentisU;
    }

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

