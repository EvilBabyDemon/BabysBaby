package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class testCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        /*
        BufferedImage img;
        PrintStream writer;
        try {
            img = ImageIO.read(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\" + ctx.getArgs().get(0) + ".png"));
            writer = new PrintStream("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\" + ctx.getArgs().get(0) + ".txt");
    
            boolean[][] usedPixels = new boolean[img.getWidth()][img.getHeight()];
            
            for (int i = 1250; i >= -10; i--) {
                for (double j = 0; j < 2 * Math.PI; j += 0.0001) {
                    int x = img.getWidth() / 2 + (int)(Math.sin(j) * i);
                    int y = img.getHeight() / 2 - (int)(Math.cos(j) * i);
                    
                    
                    if (x < img.getWidth() && x >= 0 && y < img.getHeight() && y >= 0 && !usedPixels[x][y]) {
                        Color color = new Color(img.getRGB(x, y), true);
                        if (color.getAlpha() > 230)
                            writer.println(".place setpixel " + (0 + x) + " " + (0 + y) + " " + rgbToHex(color));
                        usedPixels[x][y] = true;
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        MessageChannel channel = ctx.getChannel();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("title");
        eb.setColor(1);
        eb.setDescription("This is the first message.");
        
        eb.setFooter("A first test.");
        
        Message editor = channel.sendMessage(eb.build()).complete();

        eb.setTitle("Title 2");

        eb.setDescription("This should be a new message.");

        editor.editMessage(eb.build()).queueAfter(10, TimeUnit.SECONDS);

        ctx.getMessage().addReaction(data.check).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        // TODO Auto-generated method stub
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    /*
    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(),                 c.getGreen(), c.getBlue());
    } 
    */
    
}
