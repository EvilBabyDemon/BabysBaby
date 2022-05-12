package BabyBaby.Command.commands.Owner;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class WhereamiCMD implements IOwnerCMD {

    @Override
    public String getName() {
        return "whereami";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> guildNames = ctx.getJDA().getGuilds().stream().map(guild -> guild.getName())
                .collect(Collectors.toList());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Guilds I am on.");
        eb.setColor(Color.GREEN);
        eb.setDescription(String.join(" ", guildNames));
        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Find out on which servers I am.");
    }

}
