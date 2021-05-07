package BabyBaby.Command.commands.Public;

import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import BabyBaby.Command.CommandContext;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PingCMD implements PublicCMD {

    @Override
    public void handlePublic(CommandContext ctx) {
        /*
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
        */
        MessageChannel channel = ctx.getChannel();
		ctx.getMessage().addReaction(data.check).complete();
        String ping = "<:pinged:747783377322508290> Ping!";
        long time = System.currentTimeMillis();
        channel.sendMessage(ping).queue(response -> {
            response.editMessageFormat(ping + ": %d ms", System.currentTimeMillis() - time).queue();
        });

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
        return StandardHelp.Help(prefix, getName(), "", "A really simple ping command.");
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