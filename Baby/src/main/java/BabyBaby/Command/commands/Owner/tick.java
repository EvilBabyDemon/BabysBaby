package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.commands.Bot.clock;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class tick implements OwnerCMD {

    @Override
    public String getName() {
        return "tick";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        LocalTime myObj = LocalTime.now();
        clock.clock = Executors.newScheduledThreadPool(1);
        String minuteString = (myObj.getMinute() - myObj.getMinute()%15) == 0 ? ""  : "" + (myObj.getMinute() - myObj.getMinute()%15);
        clock.clock.schedule(new clockTower(((myObj.getHour())%12) + minuteString, ctx.getGuild()), 0, TimeUnit.SECONDS);

        ctx.getMessage().addReaction(Data.check).queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

class clockTower implements Runnable {
    String time;
    Guild eth;

    public clockTower(String s, Guild g) {
        time = s;
        eth = g;
    }

    public void run() {
        clock.timenow  = new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\clock\\" + time + ".png");
        clock.timerchange = true;
        eth.getTextChannelById("819966095070330950").sendMessage(".place pixelverify 900 720").queue();
        eth.getTextChannelById("819966095070330950").sendMessage("+clock").queue();
    }
}
