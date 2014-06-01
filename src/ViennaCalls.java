import sun.tools.jar.resources.jar;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Gordon on 3/17/14.
 */
public class ViennaCalls {
    public static void rnaFold(PhyloTree tree) throws IOException {
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
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(tree.getRoot());
        PhyloTreeNode cur;
        while(! q.isEmpty()){
            cur = q.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                q.add(cur.getChildren().get(i));
            }
            String seq = cur.getSequence();
            seq = seq.replace(",", "");
            ArrayList<Integer> gaps = new ArrayList<Integer>();
            for(int i = 0; i < seq.length() - 1; i++){
                if(seq.substring(i,i+1).equals(".")) gaps.add(i);
            }
            if(seq.substring(seq.length()-1).equals(".")) gaps.add(seq.length()-1);
            seq = seq.replace(".", "");
            seq = seq.concat("\n");
            out.write(seq);
            out.flush();
            String line;
            int j = 0;
            while(j < 2 && ( line = inp.readLine()) != null){
                if(j == 1){
                    int lastClosed = line.lastIndexOf(")");
                    int lastOpen = MainMethodClass.findClosestOpen(lastClosed, line);
                    String energy = line.substring(lastOpen);
                    line = line.substring(0, lastOpen);
                    line.replace(" ", "");
                    for(int i = 0; i < gaps.size(); i++){
                        line = line.substring(0,gaps.get(i)).concat(".".concat(line.substring(gaps.get(i))));
                    }
                    line.replace(" ","");
                    cur.setFolding(line);
                    energy = energy.replace("(", "");
                    energy = energy.replace(")", "");
                    energy =  energy.trim();
                    cur.setEnergy(Double.parseDouble(energy));
                }
                j++;
            }
        }
        p.destroy();

    }
    public static void calcDistancesFromConsensus(PhyloTree tree) throws IOException {
        String command = "C:\\Users\\Gordon\\Dropbox\\Winter2014\\Comp401\\ViennaRNAPackage\\rnaDistance.exe";
        BufferedReader inp;
        BufferedWriter out;
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        InputStream ips = p.getInputStream();
        OutputStream ops = p.getOutputStream();
        inp = new BufferedReader(new InputStreamReader(ips));
        out = new BufferedWriter(new OutputStreamWriter(ops));
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(tree.getRoot());
        PhyloTreeNode cur;
        while(! q.isEmpty()){
            cur = q.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                q.add(cur.getChildren().get(i));
            }
            String con = tree.getConsensusSequence().concat("\n");
            con = con.replace("<", "(");
            con = con.replace(">", ")");
            String curFolding = cur.getFolding().concat("\n");
            out.write(con);
            out.flush();
            out.write(curFolding);
            out.flush();
            String line;
            int i = 0;
            while(i < 1 && ( line = inp.readLine()) != null){
                line = line.substring(2);
                line = line.trim();
                cur.setDistanceFromConsensus(Integer.parseInt(line));
                i++;
            }
        }
    }

    public static void calcDistanceFromImposed(PhyloTree tree) throws IOException{
        String command = "C:\\Users\\Gordon\\Dropbox\\Winter2014\\Comp401\\ViennaRNAPackage\\rnaDistance.exe";
        BufferedReader inp;
        BufferedWriter out;
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        InputStream ips = p.getInputStream();
        OutputStream ops = p.getOutputStream();
        inp = new BufferedReader(new InputStreamReader(ips));
        out = new BufferedWriter(new OutputStreamWriter(ops));
        Queue<PhyloTreeNode> q = new LinkedList<PhyloTreeNode>();
        q.add(tree.getRoot());
        PhyloTreeNode cur;
        while(! q.isEmpty()){
            cur = q.poll();
            for(int i = 0; i < cur.getChildren().size(); i++){
                q.add(cur.getChildren().get(i));
            }
            String con = makeFolding(cur.getBasePairs());
            con = con.concat(("\n"));
            String curFolding = cur.getFolding().concat("\n");
            out.write(con);
            out.flush();
            out.write(curFolding);
            out.flush();
            String line;
            int i = 0;
            while(i < 1 && ( line = inp.readLine()) != null){
                line = line.substring(2);
                line = line.trim();
                int dist = Integer.parseInt(line);
                cur.setDistFromImpossed(dist);
                int ddd = 55;
                i++;
            }
        }
        int dfd = 33;
    }

    private static String makeFolding(ArrayList<Integer> basePairs) {
        char[] folding = new char[basePairs.size()];
        for(int i = 0; i < basePairs.size(); i++){
            if(basePairs.get(i) >= 0 && basePairs.get(i) > i){
                folding[i] = '(';
                folding[basePairs.get(i)] = ')';
            }
            if(basePairs.get(i) == -1 || basePairs.get(i) == -2) folding[i] = '.';
        }
        String foldingString = "";
        for(int i = 0; i < basePairs.size(); i++){
            foldingString = foldingString.concat(String.valueOf(folding[i]));
        }
        return foldingString;
    }
}
