package BabyBaby.Command.commands.Owner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TxtToGifCMD implements OwnerCMD {


    @Override
    public String getName() {
        return "togif";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        if(ctx.getArgs().size()==0){
            ctx.getChannel().sendMessage("Filename mising").queue();
            return;
        }


        try {
            Scanner scanner = new Scanner(new File(data.PLACE + ctx.getArgs().get(0) + ".txt"));
            
            BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
            int lineCnt = 0, imgCnt = 1;

            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {
                    img.setRGB(i, j, new Color(54, 57, 63).getRGB());
                }
            }
            
            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().split(" ");
                img.setRGB(Integer.parseInt(s[2]), Integer.parseInt(s[3]), Color.decode(s[4]).getRGB());
                lineCnt++;
                if (lineCnt % 500 == 0) {
                    try {
                        ImageIO.write(img, "png", new File(String.format(data.PLACE + "img%06d.png", imgCnt++)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            
            
            try {
                ImageIO.write(img, "png", new File(String.format(data.PLACE + "img%06d.png", imgCnt++)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<filename>", "Makes a gif out of a place txt file.");
    }
}