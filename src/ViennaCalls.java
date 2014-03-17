import java.io.*;
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
            seq = seq.replace(".", "");
            seq = seq.concat("\n");
            out.write(seq);
            out.flush();
            String line;
            int i = 0;
            while(i < 2 && ( line = inp.readLine()) != null){
                if(i == 1){
                    int lastClosed = line.lastIndexOf(")");
                    int lastOpen = MainMethodClass.findClosestOpen(lastClosed, line);
                    String energy = line.substring(lastOpen);
                    line = line.substring(0, lastOpen);
                    cur.setFolding(line);
                    energy = energy.replace("(", "");
                    energy = energy.replace(")", "");
                    energy =  energy.trim();
                    cur.setEnergy(Double.parseDouble(energy));
                }
                i++;
            }
            System.out.println(cur.getFolding() + "   " + Double.toString(cur.getEnergy()));
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
                //System.out.println(line);
                line = line.substring(2);
                line = line.trim();
                cur.setDistanceFromConsensus(Integer.parseInt(line));
                i++;
            }
            System.out.println(cur.getDistanceFromConsensus());
        }
    }
}
