package BabyBaby.Command.commands.Owner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ConvertPlace implements IOwnerCMD {

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();

        boolean onpc = Boolean.parseBoolean(cmds.get(0));

        try {

            BufferedImage img;
            if (onpc) {
                img = ImageIO.read(new File(Data.PLACE + cmds.get(1) + ".png"));
            } else {
                img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            }

            int[][] rgbs = new int[img.getWidth()][img.getHeight()];

            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {
                    rgbs[j][i] = img.getRGB(j, i);
                }
            }
            PrintStream out = new PrintStream(new File(Data.PLACE + cmds.get(1) + ".txt"));

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    if (rgbs[i][j] != 0) {
                        String col = Integer.toHexString(rgbs[i][j]);
                        col = (col.length() == 7) ? col.substring(1) : col.substring(2);
                        out.println(".place setpIxel " + i + " " + j + " " + "#" + col);
                        // String.format("#%02x%02x%02x", c1.getRed(),c1.getGreen(), c1.getBlue());
                    }
                }
            }

            out.flush();
            out.close();

            ctx.getMessage().addReaction(ctx.getJDA().getEmojiById(Data.check)).queue();

        } catch (IOException e) {
            ctx.getMessage().addReaction(ctx.getJDA().getEmojiById(Data.xmark)).queue();
            e.printStackTrace();
        }

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<boolean on pc or not> <file/filename>",
                "Converts an image to txt place file");
    }

}
