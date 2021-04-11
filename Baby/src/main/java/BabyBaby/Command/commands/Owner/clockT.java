package BabyBaby.Command.commands.Owner;

import java.io.File;

import BabyBaby.Command.commands.Bot.clock;
import net.dv8tion.jda.api.entities.Guild;

public class clockT implements Runnable {
    String time;
    Guild eth;

    public clockT(String s, Guild g) {
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