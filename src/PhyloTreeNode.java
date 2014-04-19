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
    private ArrayList<int[]> sankoffScores = new ArrayList<int[]>();
    private ArrayList<String[]> baseIfParentSingle = new ArrayList<String[]>();

    public void addSankoffScore(int index, String base, int score){
        try {
            sankoffScores.get(index);
        } catch (IndexOutOfBoundsException e) {
            int[] temp = new int[] {1,1,1,1,1};
            sankoffScores.add(index, temp);
        }
        int[] temp;
        switch(base.charAt(0)){
            case 'A' :
                temp = sankoffScores.get(index);
                temp[0] = score;
                sankoffScores.set(index, temp);
                break;
            case 'C' :
                temp = sankoffScores.get(index);
                temp[1] = score;
                sankoffScores.set(index, temp);
                break;
            case 'G' :
                temp = sankoffScores.get(index);
                temp[2] = score;
                sankoffScores.set(index, temp);
                break;
            case 'U' :
                temp = sankoffScores.get(index);
                temp[3] = score;
                sankoffScores.set(index, temp);
                break;
            case '.' :
                temp = sankoffScores.get(index);
                temp[4] = score;
                sankoffScores.set(index, temp);
                break;

        }
    }

    public int getSankoffScore(int index, String base){
        int [] temp = sankoffScores.get(index);
        switch(base.charAt(0)){
            case 'A': return temp[0];
            case 'C': return temp[1];
            case 'G': return temp[2];
            case 'U': return temp[3];
            case '.': return temp[4];
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

