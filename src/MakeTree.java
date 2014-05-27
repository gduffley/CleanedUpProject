import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Gordon on 3/17/14.
 */
//test
public class MakeTree {
    private static ArrayList<PhyloTreeNode> sequenceVariations; //ArrayList PhyloTreeNodes that represent a gene variation in the family
    private static String ss_Cons = ""; //consensus secondary structure
    private static String rf = ""; //sequence alignment???
    private static PhyloTree tree; //phylogenetic tree of the gene family
    private static String name; //name of the family we are working on
    private static int alphCounter = 0; //counter so that each new node gets a unique name

    private static void stockholmParse(String stockholmFile) throws IOException {
        sequenceVariations = new ArrayList<PhyloTreeNode>();
        String line = null;
        int sequenceCounter = 0; //counter to align the sequences to the PhyloTreeNodes with the correct names
        name = ""; //name of the family we are working on
        try{
            FileReader fileReader = new FileReader(stockholmFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null){
                if(line.contains("#=GF DE")){
                    String[] parts = line.split("\\s{1,}");
                    for(int i = 2; i < parts.length; i++){
                        name = name.concat(parts[i]); //line with the
                        name = name.concat(" ");
                    }
                    name = name.substring(0, name.length() - 1);
                }
                else if(line.contains("#=GC SS_cons")){
                    String[] parts = line.split("\\s{1,}");
                    ss_Cons = ss_Cons.concat(parts[2]);
                }
                else if(line.contains("#=GC RF")){
                    String[] parts = line.split("\\s{1,}");
                    rf = rf.concat(parts[2]);
                }
                else if(line.contains("#=GS")){
                    String[] parts = line.split("\\s{1,}");
                    PhyloTreeNode current = new PhyloTreeNode(parts[3], "");
                    sequenceVariations.add(current);
                }
                else if(!line.startsWith("#") && !line.equals("//") && !line.equals("")){
                    String[] parts = line.split("\\s{1,}");
                    if(sequenceCounter == sequenceVariations.size()) sequenceCounter = 0;
                    PhyloTreeNode curNode = sequenceVariations.get(sequenceCounter);
                    curNode.setSequence(curNode.getSequence().concat(parts[1]));
                    sequenceCounter ++;
                }
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("no file homie");
        }
        catch(IOException ex){
            System.out.println("error reading the file");
        }
    }

    //identify the most inner bracket --> find the first )
    //find the ( that goes with it
    //combine the 2 of them to create a single node, the root, labeled A, will be their parents
    //recursively move to the next layer, where you combine the the current tree and the new sequence, by creating a new root, with its
    //children being the old root, and the new sequence
    //if there are 2 genes in a single layer at any point,
    //create a parent for them, treat the parent node like a single node
    //testd
    private static void phyloTreeCreator(String treeFile) throws IOException{
        String line = null;
        int curOpen;
        int curClosed;
        tree = new PhyloTree(Integer.toString(alphCounter), "tbd"); //creates root with name A
        alphCounter++;
        tree.setConsensusSequence(ss_Cons);
        try{
            FileReader fileReader = new FileReader(treeFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null){

                //Next step is to figure out how many genes are in-between
                //find out which genes are in between
                //if 2, add them together and treat parent as if there was a single gene
                //create a new root, make the previous root and the gene the children of the new root
                while(line.contains("(") || line.contains(")")){ //after dealing with a layer, swap out () so when done there are none left
                    curClosed = line.indexOf(")"); //finds first instance of ) in the string
                    curOpen = MainMethodClass.findClosestOpen(curClosed, line); //method returns closest ( bracket in the string
                    String layer = line.substring(curOpen, curClosed + 1); //creates layer, which is the most interior bracket set
                    int numberofGenesInLayer = numberOfSequences(layer); //finds if there is 1 or 2 genes in the layer
                    PhyloTreeNode newNode = null;

                    //if there are 2 nodes in the layer, we are going to combine them and create a parent node
                    //if the root has < 2 children, just attach this new node
                    //if the root has 2 children, then we need to create a new root node which will have children
                    //that are our combined nodes and the old root
                    if(numberofGenesInLayer == 2){
                        newNode = twoLayer(layer);
                        if(tree.getRoot().getChildren().size() == 2){
                            PhyloTreeNode newRoot = new PhyloTreeNode(Integer.toString(alphCounter), "tbd");
                            newRoot.addChild(newNode);
                            tree.addNode(newRoot);
                            alphCounter++;
                        }
                        else tree.getRoot().addChild(newNode);
                    }
                    //if the layer only has 1 gene
                    //if the root has < 2 children, just add the node from the layer as a child to the root
                    //if the root has 2 children, create a new root with children that are our new node, and the old root
                    if(numberofGenesInLayer == 1){
                        String curName;
                        for(int i = 0; i < sequenceVariations.size(); i++){
                            curName = sequenceVariations.get(i).getName();
                            if(layer.indexOf(curName) > 0){
                                newNode = sequenceVariations.get(i);
                            }
                        }
                        if(tree.getRoot().getChildren().size() == 2){
                            PhyloTreeNode newRoot = new PhyloTreeNode(Integer.toString(alphCounter), "tbd");
                            newRoot.addChild(newNode);
                            tree.addNode(newRoot);
                            alphCounter++;
                        }
                        else tree.getRoot().addChild(newNode);
                    }
                    //method to cut out the old layer
                    if(curOpen != 0){
                        String lineFirstHalf = line.substring(0, curOpen);
                        String lineSecondHalf = line.substring(curClosed + 1);
                        line = lineFirstHalf.concat(lineSecondHalf);
                    }
                    else break;
                }
            }
            tree.setNumberOfLeafs(sequenceVariations.size());
        }
        catch(FileNotFoundException ex) {
            System.out.println("no file homie1");
        }
        catch(IOException ex){
            System.out.println("error reading the file");
        }
        tree.setName(name);
    }
    //method that takes a layer of 2 and combines the 2 nodes into a new parent node
    private static PhyloTreeNode twoLayer(String layer) {
        String curName;
        PhyloTreeNode parent = new PhyloTreeNode(Integer.toString(alphCounter), "tbd");
        alphCounter++;
        for(int i = 0; i < sequenceVariations.size(); i++){
            curName = sequenceVariations.get(i).getName();
            if(layer.indexOf(curName) > 0){
                parent.addChild(sequenceVariations.get(i));
            }
        }
        return parent;
    }
    //figures out if the layer is a one or 2 layer
    private static int numberOfSequences(String layer){
        int counter = 0;
        String curName;
        for(int i = 0; i < sequenceVariations.size(); i++){
            curName = sequenceVariations.get(i).getName();
            if(layer.indexOf(curName) > 0) counter++;
        }
        return counter;
    }

    //Finds the closest open bracket to our current closed bracker
    //BFS print out of the tree
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


    private static void sequenceMod(){
        for(int i = 0; i < sequenceVariations.size(); i++){
            String seq = sequenceVariations.get(i).getSequence();
            seq = seq.replace("", ",");
            seq = seq.substring(1, seq.lastIndexOf(","));
            sequenceVariations.get(i).setSequence(seq);
        }
    }
    public static PhyloTree makeTree(String args0, String args1) throws IOException {
        if(alphCounter == 0){
            int dfdf = 3334343;
        }
        stockholmParse(args0);
        phyloTreeCreator(args1);
        //sequenceMod();
        return tree;
    }
}
