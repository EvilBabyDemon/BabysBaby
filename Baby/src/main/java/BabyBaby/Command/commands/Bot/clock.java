package BabyBaby.Command.commands.Bot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import BabyBaby.Command.commands.Owner.clockT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class clock {
    public static boolean clockused;
    public static File timenow;
	public static boolean timerchange;
	public static ScheduledExecutorService clock = Executors.newScheduledThreadPool(1);
    public static boolean openforcmds = true;
    public static boolean[][] grid = new boolean[10][10];
    public static int[][] rgbs;
    
    public static void clockTick(GuildMessageReceivedEvent event){
        
        event.getMessage().addReaction("🕰️").queue();
        LocalTime myObj = LocalTime.now();
        
        int clocktime = (15 - myObj.getMinute()%15)*60 + 60 - myObj.getSecond();

        if (clockused) {
            clock.shutdownNow();
            clock = Executors.newScheduledThreadPool(1);
        }
        String minuteString = ((15 + myObj.getMinute())%60 - myObj.getMinute()%15) == 0 ? ""  : "" + ((15 + myObj.getMinute())%60 - myObj.getMinute()%15);

        clockused = true;
        clock.schedule(new clockT(((myObj.getHour() + ((myObj.getMinute() > 44) ? 1 : 0) )%12) + minuteString, event.getGuild()), clocktime, TimeUnit.SECONDS);
        event.getMessage().addReaction(":checkmark:769279808244809798").queue();
        
    }

    public static void verify(GuildMessageReceivedEvent event){

        Message message = event.getMessage(); 
        String content = message.getContentRaw();
        String[] cmd = content.split(" ");
        
        int xver = Integer.parseInt(cmd[1]);
        int yver = Integer.parseInt(cmd[2]);

        if(xver%100==0 && yver%100==0 && grid[xver/100][yver/100]){
            
            
            message.addReaction(":checkmark:769279808244809798").queue();
            List<Attachment> test = message.getAttachments();
            Attachment test2 = test.get(0);

            try {
                
                BufferedImage img2 = ImageIO.read(new URL(test2.getUrl()));

                int[][] rgbs2 = new int [img2.getWidth()][img2.getHeight()];

                for (int i = 0; i < img2.getHeight(); i++) {
                    for (int j = 0; j < img2.getWidth(); j++) {
                        rgbs2[j][i] = img2.getRGB(j, i);
                    }
                }

                //PrintStream out = new PrintStream(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\checker" + tmp.getX() + tmp.getY() + ".txt"));
                MessageChannel channel = event.getGuild().getTextChannelById("819966095070330950");

                for (int i = 0; i < img2.getWidth(); i++) {
                    for (int j = 0; j < img2.getHeight(); j++) {
                        if(rgbs[i+xver][j+yver] != rgbs2[i][j] && rgbs[i+xver][j+yver] != 0){
                            String col = Integer.toHexString(rgbs[i+xver][j+yver]);
                            col = col.substring(2);
                            //String pixelset = (Math.random()<0.5) ? "setpIxel" : "setpixel";
                            channel.sendMessage(".place setpIxel " + (i+xver) + " " + (j+yver) + " " + "#" + col).complete();//String.format("#%02x%02x%02x", c1.getRed(),c1.getGreen(), c1.getBlue());
                        }	
                    }
                }
            

                channel.sendMessage("DONE WITH A GRID").queue(response -> {
                    openforcmds = true;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(timerchange && xver == 900 && yver == 720){
            timerchange = false;
            message.addReaction(":checkmark:769279808244809798").queue();
            List<Attachment> test = message.getAttachments();
            Attachment test2 = test.get(0);

            String timename = timenow.getName();

            try {
                
                BufferedImage timer = ImageIO.read(timenow);
                int[][] tim = new int [timer.getWidth()][timer.getHeight()];
                for (int i = 720; i < timer.getHeight(); i++) {
                    for (int j = 900; j < timer.getWidth(); j++) {
                        tim[j][i] = timer.getRGB(j, i);
                    }
                }

                BufferedImage img2 = ImageIO.read(new URL(test2.getUrl()));
                int[][] rgbs2 = new int [img2.getWidth()][img2.getHeight()];
                for (int i = 0; i < img2.getHeight(); i++) {
                    for (int j = 0; j < img2.getWidth(); j++) {
                        rgbs2[j][i] = img2.getRGB(j, i);
                    }
                }

                MessageChannel channel = event.getGuild().getTextChannelById("819966095070330950");
                for (int i = 0; i < img2.getWidth(); i++) {
                    for (int j = 0; j < img2.getHeight(); j++) {
                        if(tim[i+xver][j+yver] != rgbs2[i][j] && tim[i+xver][j+yver] != 0){
                            String col = Integer.toHexString(tim[i+xver][j+yver]);
                            col = col.substring(2);
                            channel.sendMessage(".place setpIxel " + (i+xver) + " " + (j+yver) + " " + "#" + col + " | " + timename).complete();
                        }	
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    


}
