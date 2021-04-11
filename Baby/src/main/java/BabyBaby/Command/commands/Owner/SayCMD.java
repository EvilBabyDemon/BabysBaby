package BabyBaby.Command.commands.Owner;

import java.util.List;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SayCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public void handleOwner(CommandContext ctx) {

        List<String> contentList = ctx.getArgs();


        if(contentList == null || contentList.size()==0)
            return;
        
        String content = ctx.getMessage().getContentRaw().substring(4);

        ctx.getChannel().deleteMessageById(ctx.getMessage().getId()).queue();
        ctx.getChannel().sendMessage(content).queue();

    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName()+"`");
        embed.setDescription("Bot repeats you.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + getName() + " <text>")
                .build(), false);

        return embed.build();
    }
    
}
