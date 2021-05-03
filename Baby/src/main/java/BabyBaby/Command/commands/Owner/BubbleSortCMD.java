package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BubbleSortCMD implements OwnerCMD {
    
    public int xavg = 0;
    public int yavg = 0;
    
    @Override
    public String getName() {
        return "bubblesort";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        channel.deleteMessageById(ctx.getMessage().getId()).queue();

        //Comments just for Georg

        //Get everything
        List<String> cmds = ctx.getArgs();
        LinkedList<String> sort = new LinkedList<>();
        try {
            Scanner s = new Scanner(new File(data.PLACE + cmds.get(0) + ".txt"));
            
            while(s.hasNext()){
                sort.add(s.nextLine());
            }
            s.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        //with the HashMap we store all same entries together in one ArrayList
        HashMap<String, ArrayList<String>> adder = new HashMap<>();
        for (String hex : sort) {
            
            String hexString = hex.substring(hex.length()-6, hex.length());
            int[] color_array = {Integer.parseInt(hexString.substring(0,2), 16),Integer.parseInt(hexString.substring(2,4), 16),Integer.parseInt(hexString.substring(4,6), 16)};
            //This here to norm the colours to only 64 possibilities (our Buckets)
            int norm = Math.max(color_array[0]+color_array[1]+color_array[2],1);
            if(norm!=0){
                color_array[0] = (int)Math.round(4.0*color_array[0]/norm);
                color_array[1] = (int)Math.round(4.0*color_array[1]/norm);
                color_array[2] = (int)Math.round(4.0*color_array[2]/norm);
                
                hexString = ""+color_array[0]+color_array[1]+color_array[2];
            }
            //now you have 64 different colors to iterate through.

            ArrayList<String> tmp = adder.getOrDefault(hexString, new ArrayList<String>());
            tmp.add(hex);
            adder.put(hexString, tmp);
        }

        ArrayList<ArrayList<String>> copier = new ArrayList<>();

        //Now we iterate all Buckets from the HashMap
        HashMap<Thread, ArrayList<String>> threads = new HashMap<>();

        for (ArrayList<String> var : adder.values()) {
            
            ArrayList<String> tmp = new ArrayList<>();
            for (String str : var) {
                tmp.add(str);
            }

            //Multithreading to sort every bucket 
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {                   
                    //get avg x and y for this bucket
                    for (int i = 0; i < tmp.size(); i++) {
                        String[] parts = tmp.get(i).split(" ");
                        xavg += Integer.parseInt(parts[2]);
                        yavg += Integer.parseInt(parts[3]);
                    }
                    xavg = xavg/tmp.size();
                    yavg = yavg/tmp.size();

                    
                    //Comparator mit euklidischer distance on every Bucket 
                    Comparator<String> comp = new Comparator<String>(){
                        @Override
                        public int compare(String o1, String o2) {
                            int[] oxy1 = tonum(o1);
                            int[] oxy2 = tonum(o2);
                            return (int) (Math.pow(xavg - oxy1[0],2) + Math.pow(xavg - oxy1[1],2)-(Math.pow(xavg - oxy2[0],2) + Math.pow(xavg - oxy2[1],2)));
                        }
                        public int[] tonum (String s){
                            String[] cmds = s.split(" ");
                            return new int[] {Integer.parseInt(cmds[2]), Integer.parseInt(cmds[3])};
                        }
                        
                    };
                    tmp.sort(comp);
                }
            });
            t.start();
            //Storing threads and Sorted Lists in a HashMap
            threads.put(t, tmp);

            //copier.add(tmp);
        }
        //I probably have way too many threads and this doesnt give any speedup but whatever xD
        //joining all threads and adding them all together in one List

        for (Thread var : threads.keySet()) {
            try{
                var.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            copier.add(threads.get(var));
        }

        

        //print everything in a file again
        try {
            PrintStream out = new PrintStream(new File(data.PLACE + "bubblesortseq" + cmds.get(0) + ".txt"));	
            PrintStream paraout = new PrintStream(new File(data.PLACE + "bubblesort" + cmds.get(0) + ".txt"));	
            
            for (ArrayList<String> var : copier) {
                for (String var2 : var) {
                    out.println(var2);
                }
            }
            out.flush();
            out.close();

            int j = 0;
            boolean doing = true;
            while(doing){
                doing = false;
                for (int i = 0; i < copier.size(); i++) {
                    try {
                        paraout.println(copier.get(i).get(j));
                    } catch (Exception e) {
                        copier.remove(i--);
                    }
                }
                j++;
            }
            
            paraout.flush();
            paraout.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        channel.sendMessage("Done").queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<Filename>", "Command to sort a place file by colour.");
    }
}
