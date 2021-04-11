package BabyBaby.Command.commands.Public;

import BabyBaby.Command.PublicCMD;
import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.ColouredStrings.ColouredStringDiff;
import BabyBaby.Command.CommandContext;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PingCMD implements PublicCMD {

    @Override
    public void handlePublic(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel().sendMessage(
                        EmbedUtils.getDefaultEmbed()
                                .setTitle(":regional_indicator_p::regional_indicator_o::regional_indicator_n::regional_indicator_g:")
                                .addField("Ping:"
                                        , new ColouredStringDiff()
                                                .addRed(ping + "ms", true)
                                                .build()
                                        , true)
                                .addField("WS ping:"
                                        , new ColouredStringDiff()
                                        .addRed(jda.getGatewayPing() + "ms",true)
                                        .build()
                                        , true)
                                .build()
                ).queue()
        );

        /*
        MessageChannel channel = event.getChannel();
        message.addReaction(check).queue();

        if (content.equals(prefix + "pong"))
            ping = "<:pinged:747783377322508290> Ping!";

        long time = System.currentTimeMillis();
        channel.sendMessage(ping).queue(response -> {
            response.editMessageFormat(ping + ": %d ms", System.currentTimeMillis() - time).queue();
        });
        */

    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        this.handlePublic(ctx);
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName()+"`");
        embed.setDescription("A really simple ping command.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + "ping")
                .build(), false);

        return embed.build();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        this.handlePublic(ctx);
    }
}