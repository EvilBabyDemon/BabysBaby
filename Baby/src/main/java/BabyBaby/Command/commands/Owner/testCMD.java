package BabyBaby.Command.commands.Owner;
/*
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
*/
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Member;
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
        Member test = ctx.getGuild().getMemberById(ctx.getArgs().get(0));
        
        ctx.getChannel().sendMessage("" + test.getOnlineStatus()).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "(whatever it is atm)", "A cmd to test things out.");
    }
    /*
    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(),                 c.getGreen(), c.getBlue());
    } 
    */
    
}
