package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PlaceDraw implements IOwnerCMD {
    public boolean on;
    ArrayList<String> printer;
    MessageChannel channel;
    int divider;

    @Override
    public String getName() {
        return "draw";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs(); 
        divider = Integer.parseInt(cmds.get(0));
        String file = cmds.get(1);
        printer = new ArrayList<>(); 
        boolean startAt = false;
        String x = "";
        String y = "";
        
        if(cmds.size()<4) {
            x = cmds.get(2);
            y = cmds.get(3);
            startAt = true;
        }
        
        try {
            Scanner s = new Scanner(new File(Data.PLACE + file + ".txt"));
            while(s.hasNextLine()){
                printer.add(s.nextLine());
            }
            s.close();
        } catch (Exception e){
            ctx.getMessage().addReaction(Data.xmark).queue();
            e.printStackTrace();
            return;
        }
        channel = ctx.getGuild().getTextChannelById("819966095070330950");
        ctx.getMessage().addReaction(Data.check).queue();

        for (int i = 0; i < divider; i++) {
            for (int j = i; j < printer.size(); j += divider) {
                if (startAt) {
                    if(printer.get(j).toLowerCase().startsWith(".place setpixel " + x + " " + y){
                        startAt = false;
                    }
                    continue;
                }
                try {
                    channel.sendMessage(printer.get(j)).complete();
                } catch (Exception e) {}
            }
        }
       
        channel.sendMessage("I am done Boss! Pls start the next else I am bored..... <@!223932775474921472>" ).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<Iterations> <FileName>", "Command to draw stuff on place");
    }
    
}
