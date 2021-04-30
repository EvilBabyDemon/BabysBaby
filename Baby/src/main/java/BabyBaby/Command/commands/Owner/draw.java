package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class draw implements OwnerCMD {
    public static boolean on;
    public static int stopped;
    static ArrayList<String> printer;
    static MessageChannel channel;
    static int x;
    static ArrayList<Emote> allemo;

    @Override
    public String getName() {
        return "draw";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs(); 
        x = Integer.parseInt(cmds.get(0));
        String file = cmds.get(1);
        printer = new ArrayList<>(); 

        allemo = new ArrayList<>(); 
        for (Emote var : ctx.getGuild().getEmotes()) {
            allemo.add(var);
        }
        

        try {
            Scanner s = new Scanner(new File(data.PLACE + file + ".txt"));
            while(s.hasNextLine()){
                printer.add(s.nextLine());
            }
        } catch (Exception e){
            ctx.getMessage().addReaction(data.xmark);
            e.printStackTrace();
            return;
        }
        channel = ctx.getGuild().getTextChannelById("819966095070330950");

        ctx.getMessage().addReaction(data.check).queue();
        on = false;
        drawing();

       
        channel.sendMessage("I am done Boss! Pls start the next else I am bored..... <@!223932775474921472>" ).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<Iterations> <FileName>", "Command to draw stuff on place");
    }
    
    public void drawing(){
        Random rand = new Random();
       
        for (int i = stopped; i < x; i++) {
            for (int j = i; j < printer.size();) {
                if(on){
                    stopped = i;
                    return;
                }
                String tmp = "";
                for (int k = 0; k < 10; k++) {
                    tmp += "\n " + allemo.get(rand.nextInt(allemo.size())).getAsMention();
                }
                channel.sendMessage(printer.get(j) + tmp).queue();
                j += x;
            }   
        }
    }
}
