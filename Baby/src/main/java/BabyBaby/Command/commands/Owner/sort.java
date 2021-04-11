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
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class sort implements OwnerCMD {

    @Override
    public String getName() {
        return "sort";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        channel.deleteMessageById(ctx.getMessage().getId()).queue();

        List<String> cmds = ctx.getArgs();
        LinkedList<String> sort = new LinkedList<>();
        try {
            Scanner s = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\" + cmds.get(0) + ".txt"));
            
            while(s.hasNext()){
                sort.add(s.nextLine());
            }
            s.close();
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HashMap<String, ArrayList<String>> adder = new HashMap<>();
        for (String hex : sort) {
            
            String hexString = hex.substring(hex.length()-6, hex.length());
            int[] color_array = {Integer.parseInt(hexString.substring(0,2), 16),Integer.parseInt(hexString.substring(2,4), 16),Integer.parseInt(hexString.substring(4,6), 16)};
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

        for (ArrayList<String> var : adder.values()) {
            
            ArrayList<String> tmp = new ArrayList<>();
            for (String str : var) {
                tmp.add(str);
            }


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Comparator<String> comp = new Comparator<String>(){
                        @Override
                        public int compare(String o1, String o2) {
                            return Integer.parseInt(o1.substring(o1.length()-6, o1.length()),16)-Integer.parseInt(o2.substring(o2.length()-6, o2.length()),16);
                        }
                    };
                    tmp.sort(comp);
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            copier.add(tmp);
        }


        try {
            PrintStream out = new PrintStream(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\sort" + cmds.get(0) + ".txt"));	
            
            for (ArrayList<String> var : copier) {
                for (String var2 : var) {
                    out.println(var2);
                }
            }

            out.flush();
            out.close();
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
