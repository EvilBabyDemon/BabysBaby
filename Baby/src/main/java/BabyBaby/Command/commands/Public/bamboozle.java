package BabyBaby.Command.commands.Public;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class bamboozle implements PublicCMD {

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static boolean done = false;

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "justforgeorg";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        data.antibamboozle = false;
        
        if (done) {
            scheduler.shutdownNow();
            scheduler = Executors.newScheduledThreadPool(1);
        }
        done = true;
        scheduler.schedule(new Bambi(), 5, TimeUnit.SECONDS);
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return null;
    }
    
}
class Bambi implements Runnable {

    public Bambi() {
    }

    public void run() {
        data.antibamboozle = true;
    }
}