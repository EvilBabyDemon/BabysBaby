package BabyBaby.Command.commands.Owner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.commands.Bot.clock;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ucheck implements OwnerCMD{

    @Override
    public String getName() {
        return "ucheck";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
       
        File filecheck = new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\checker.png");


        try {
            BufferedImage img = ImageIO.read(filecheck);

            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {
                    clock.rgbs[j][i] = img.getRGB(j, i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                if(clock.rgbs[i][j] != 0 || clock.grid[i/100][j/100]){
                    clock.grid[i/100][j/100] = true;
                    j += -j%100 + 100;
                }
            }
        }
        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
