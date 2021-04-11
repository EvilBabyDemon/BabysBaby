package BabyBaby.ColouredStrings;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class StandardHelpEmbed {
    public MessageEmbed StandardHelp (String prefix, String name, String description, String cmd){
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + name +"`");
        embed.setDescription(description);

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + cmd)
                .build(), false);

        return embed.build();
    }
}