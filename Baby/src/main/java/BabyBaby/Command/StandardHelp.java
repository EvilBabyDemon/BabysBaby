package BabyBaby.Command;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class StandardHelp {
    public static MessageEmbed Help(String prefix, String name, String args, String description) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Help page of: `" + name + "`");
        embed.setDescription(description);

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addNormal(prefix + name + " " + args)
                .build(), false);

        return embed.build();
    }
}
