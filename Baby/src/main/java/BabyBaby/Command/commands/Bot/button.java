package BabyBaby.Command.commands.Bot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class button {
    public static int firstplace = 2100;
    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static boolean shut = false;
    static Guild eth;
    
    public static void tap(GuildMessageReceivedEvent event){
        
        if(!event.getGuild().getId().equals("747752542741725244"))  
            return;

        if(eth == null){
            eth = event.getGuild();
        }

        int time = Integer.parseInt(event.getMessage().getActionRows().get(0).getButtons().get(0).getLabel());

        if (time > firstplace) {
            time = 0;
        } else {
            time = firstplace - time;
            time *= 60;
        }

        if (shut) {
            scheduler.shutdownNow();
            scheduler = Executors.newScheduledThreadPool(1);
        }

        scheduler.schedule(new Later(), time, TimeUnit.SECONDS);
        
        System.out.println("In " + time/60 + " minutes");

        shut = true;
    }


}

class Later implements Runnable {

    public Later() {
    }

    public void run() {
        button.eth.getMemberById("223932775474921472").getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("Do it now!").queue();
        });
        
    }
}



