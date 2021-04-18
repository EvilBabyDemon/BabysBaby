package BabyBaby.Command.commands.Bot;

import java.util.ArrayList;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class drawwithFerris {
    
    public void drawing(GuildMessageReceivedEvent event){
        ArrayList<String> printer = new ArrayList<>(); 
        String[] cmds = event.getMessage().getContentRaw().split("\n");
        MessageChannel channel = event.getJDA().getGuildById("747752542741725244").getTextChannelById("819966095070330950");

        for (int i = 1; i < cmds.length; i++) {
            printer.add(cmds[i]);
        }

        if(printer.size()==0)
            System.out.println("WTF");
        
        for (int i = 0; i < printer.size(); i++) {
            channel.sendMessage(printer.get(i)).queue();
        }
    }
}
