package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class draw implements OwnerCMD {

    @Override
    public String getName() {
        return "draw";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel(); 
        List<String> cmds = ctx.getArgs(); 
        int x = Integer.parseInt(cmds.get(0));
        int start = Integer.parseInt(cmds.get(1));
        for(int i = start; i < x; i++){
            try {
                Scanner s = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\" + cmds.get(2) + ".txt"));
                for(int j = 0; j < i; j++){
                    if(s.hasNextLine()){
                        s.nextLine();
                    }
                }
                
                while(s.hasNextLine()){
                    channel.sendMessage(s.nextLine()).complete();
                    for(int j = 0; j < x-1; j++){
                        if(s.hasNextLine()){
                            s.nextLine();
                        }
                    }
                }
                s.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            channel.sendMessage("I did " + (i+1) +"/" + x + ". Lets go on! <@!223932775474921472>" ).queue();
        }
        channel.sendMessage("I am done Boss! Pls start the next else I am bored..... <@!223932775474921472>" ).queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<Iterations> <Start at index> <FileName>", "Command to draw stuff on place");
    }
    
}
