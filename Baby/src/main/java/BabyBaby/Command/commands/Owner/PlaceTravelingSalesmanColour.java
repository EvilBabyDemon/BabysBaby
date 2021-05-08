package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PlaceTravelingSalesmanColour implements OwnerCMD {

    @Override
    public String getName() {
        return "tsp";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        
        MessageChannel channel = ctx.getChannel();
        channel.deleteMessageById(ctx.getMessage().getId()).queue();

        //Get everything
        List<String> cmds = ctx.getArgs();
        ArrayList<String> sort = new ArrayList<>();
        try {
            Scanner s = new Scanner(new File(data.PLACE + cmds.get(0) + ".txt"));
            
            while(s.hasNext()){
                sort.add(s.nextLine());
            }
            s.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> sorted = new ArrayList<>();

        //traveling salesman problem but actually not just get nearest from current (colour)point
        int iter = (int) Math.random()*sort.size();
        while(sort.size()> 1){
            String spawn = sort.remove(iter);
            sorted.add(spawn);
            int dist = Integer.MAX_VALUE;
            for (int i = 0; i < sort.size(); i++) {
                int x = compcol(spawn, sort.get(i));
                if(dist > x){
                    dist = x;
                    iter=i;
                    if(spawn.split(" ")[4].equals(sort.get(iter).split(" ")[4])){
                        break;
                    }
                }
            }
        }
        sorted.add(sort.remove(0));

        

        //print everything in a file again
        try {
            PrintStream out = new PrintStream(new File(data.PLACE + "tsp" + cmds.get(0) + ".txt"));	

            for (String var : sorted) {
                out.println(var);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.sendMessage("Done").queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<boolean if on pc or not> <filename> [Attachment]", "A colour sorter based on the Traveling SalesmanProblem but actually not really tsp.");
    }
    //eculidian distance on colour
    private int compcol(String o1, String o2) {
        int[] oxy1 = hextonum(o1);
        int[] oxy2 = hextonum(o2);

        return (int) (Math.pow(oxy1[0]-oxy2[0],2) + Math.pow(oxy1[1]-oxy2[1],2) + Math.pow(oxy1[2]-oxy2[2],2));
    }
    private int[] hextonum (String s){
        String col = s.split(" ")[4].substring(1);
        return new int[] {Integer.parseInt(col.substring(0,2), 16), Integer.parseInt(col.substring(2,4), 16), Integer.parseInt(col.substring(4,6), 16)};
    }

    private int comparedist(String o1, String o2, String spawn) {
        int[] oxy1 = tonum(o1);
        int[] oxy2 = tonum(o2);
        String[] koor = spawn.split(" ");
        int xavg = Integer.parseInt(koor[2]);
        int yavg = Integer.parseInt(koor[3]);
        return (int) (Math.pow(xavg - oxy1[0],2) + Math.pow(yavg - oxy1[1],2)-(Math.pow(xavg - oxy2[0],2) + Math.pow(yavg - oxy2[1],2)));
    }
    private int[] tonum (String s){
        String[] cmds = s.split(" ");
        return new int[] {Integer.parseInt(cmds[2]), Integer.parseInt(cmds[3])};
    }

}
