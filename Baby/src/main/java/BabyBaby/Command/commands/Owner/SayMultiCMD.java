package BabyBaby.Command.commands.Owner;

import java.util.List;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SayMultiCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "for";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
       
        MessageChannel channel = ctx.getChannel();
        
        List<String> args = ctx.getArgs();


        int x = Integer.parseInt(args.remove(0));

        Message message = ctx.getMessage();
        
        String content = message.getContentRaw();

        content = content.substring(0, content.length());

        channel.deleteMessageById(message.getId()).queue();
        for (int i = 0; i < x; i++) {
            channel.sendMessage(content).queue();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName()+"`");
        embed.setDescription("Iterative spam function.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + getName() + " <int> <text to spam>")
                .build(), false);

        return embed.build();
    }
    

}
