/**
 * Created by Gordon on 2/11/14.
 */
import com.sun.java.swing.plaf.motif.resources.motif_zh_TW;
import com.sun.swing.internal.plaf.basic.resources.basic_es;

import java.lang.reflect.Array;
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
    private ArrayList<Integer> basePairs = new ArrayList<Integer>();
    private ArrayList<int[]> sankoffSingleScores = new ArrayList<int[]>();
    private ArrayList<int[]> sankoffPairsScores = new ArrayList<int[]>();
    private ArrayList<String[]> baseIfParent = new ArrayList<String[]>();

    //Method works because using add doesn't override values already there
    public void setNoBP(int i){
        try{
            basePairs.set(i,-1);
        }catch(IndexOutOfBoundsException e){
            for(int j = basePairs.size(); j <= i; j++) basePairs.add(-1);
        }
    }

    public int getBasePair(int i) throws IndexOutOfBoundsException{
        try{
            return basePairs.get(i);
        }catch(IndexOutOfBoundsException e){
            throw e;
        }
    }

    //index1 corresponds with the first base in bases
    //AU,UA,CG,GC,GU,UG
    public void setSankoffPairsScores(int index1, int index2, String bases, int score){
        try{
            int[] temp = sankoffPairsScores.get(index1);
        }
        catch(ArrayIndexOutOfBoundsException e){
            int[] temp = new int[] {1,1,1,1,1,1};
            sankoffPairsScores.set(index1, temp);
        }
        try{
            int[] temp = sankoffPairsScores.get(index2);
        }
        catch(ArrayIndexOutOfBoundsException e){
            int[] temp = new int[] {1,1,1,1,1};
            sankoffPairsScores.set(index2, temp);
        }
        int[] temp1 = sankoffPairsScores.get(index1);
        int[] temp2 = sankoffPairsScores.get(index2);
        switch(bases.charAt(0)){
            case 'A':
                temp1[0] = score;
                temp2[1] = score;
                break;
            case 'U':
                if(bases.charAt(1) == 'A'){
                    temp1[1] = score;
                    temp2[0] = score;
                }
                else{
                    temp1[5] = score;
                    temp2[4] = score;
                }
                break;
            case 'C':
                temp1[2] = score;
                temp2[3] = score;
                break;
            case 'G':
                if(bases.charAt(1) == 'C'){
                    temp1[3] = score;
                    temp2[2] = score;
                }
                else{
                    temp1[4] = score;
                    temp2[5] = score;
                }
                break;
        }
    }

    //AU,UA,CG,GC,GU,UG
    public int getSankoffPairsScore(int index1, String bases){
        int[] temp = sankoffPairsScores.get(index1);
        switch(bases.charAt(0)){
            case 'A':
                if(temp[0] == 1) throw new IndexOutOfBoundsException();
                return temp[0];
            case 'C': if(temp[2] == 1) throw new IndexOutOfBoundsException();
                return temp[2];
            case 'G':
                if(bases.charAt(1) == 'C'){
                    if(temp[3] == 1) throw new ArrayIndexOutOfBoundsException();
                    return temp[3];
                }
                else{
                    if(temp[4] == 1) throw new ArrayIndexOutOfBoundsException();
                    return temp[4];
                }
            case 'U':
                if(bases.charAt(1) == 'G'){
                    if(temp[5] == 1) throw new ArrayIndexOutOfBoundsException();
                    return temp[5];
                }
                else{
                    if(temp[1] == 1) throw new ArrayIndexOutOfBoundsException();
                    return temp[1];
                }
        }
        return MainMethodClass.INF;
    }

    public void setBaseIfParent(int index, String parent, String base){
        try{
            baseIfParent.get(index);
        }catch(IndexOutOfBoundsException e){
            String[] temp = new String[5];
            baseIfParent.add(temp);
        }
        String[] temp;
        switch(parent.charAt(0)){
            case 'A':
                temp = baseIfParent.get(index);
                temp[0] = base;
                baseIfParent.set(index, temp);
                break;
            case 'C':
                temp = baseIfParent.get(index);
                temp[1] = base;
                baseIfParent.set(index, temp);
                break;
            case 'G':
                temp = baseIfParent.get(index);
                temp[2] = base;
                baseIfParent.set(index, temp);
                break;
            case 'U':
                temp = baseIfParent.get(index);
                temp[3] = base;
                baseIfParent.set(index, temp);
                break;
            case '.':
                temp = baseIfParent.get(index);
                temp[4] = base;
                baseIfParent.set(index, temp);
                break;
        }
    }
    //TODO: should probably add try catch
    public String getBaseIfParent(int index, String parent){
        String[] temp = baseIfParent.get(index);
        switch(parent.charAt(0)){
            case 'A': return temp[0];
            case 'C': return temp[1];
            case 'G': return temp[2];
            case 'U': return temp[3];
            case '.': return temp[4];
        }
        return "NO BASE. SOMETHING IS VERY WRONG";
    }

    public void addSankoffScore(int index, String base, int score){
        try {
            sankoffSingleScores.get(index);
        } catch (IndexOutOfBoundsException e) {
            int[] temp = new int[] {1,1,1,1,1};
            sankoffSingleScores.add(index, temp);
        }
        int[] temp;
        switch(base.charAt(0)){
            case 'A' :
                temp = sankoffSingleScores.get(index);
                temp[0] = score;
                sankoffSingleScores.set(index, temp);
                break;
            case 'C' :
                temp = sankoffSingleScores.get(index);
                temp[1] = score;
                sankoffSingleScores.set(index, temp);
                break;
            case 'G' :
                temp = sankoffSingleScores.get(index);
                temp[2] = score;
                sankoffSingleScores.set(index, temp);
                break;
            case 'U' :
                temp = sankoffSingleScores.get(index);
                temp[3] = score;
                sankoffSingleScores.set(index, temp);
                break;
            case '.' :
                temp = sankoffSingleScores.get(index);
                temp[4] = score;
                sankoffSingleScores.set(index, temp);
                break;

        }
    }

    public int getSankoffScore(int index, String base) throws IndexOutOfBoundsException{
        int [] temp = sankoffSingleScores.get(index);
        switch(base.charAt(0)){
            case 'A': if (temp[0] == 1) throw new IndexOutOfBoundsException();
                else return temp[0];
            case 'C': if (temp[1] == 1) throw new IndexOutOfBoundsException();
                else return temp[1];
            case 'G': if (temp[2] == 1) throw new IndexOutOfBoundsException();
                else return temp[2];
            case 'U': if (temp[3] == 1) throw new IndexOutOfBoundsException();
                else return temp[3];
            case '.': if (temp[4] == 1) throw  new IndexOutOfBoundsException();
                else return temp[4];
        }
        return MainMethodClass.INF;
    }

    public void setBasePair(Integer i, Integer j){
        try{
            basePairs.set(j,i);
        }catch(IndexOutOfBoundsException e){
            for(int k = basePairs.size(); k <= j; k++) basePairs.add(-1);
            basePairs.set(j, i);
        }
        basePairs.set(i,j);
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

