package BabyBaby.Command.commands.Public;

import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import BabyBaby.Command.CommandContext;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PingCMD implements IPublicCMD {

    @Override
    public void handlePublic(CommandContext ctx) {
        /*
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel().sendMessage(
                        EmbedUtils.getDefaultEmbed()
                                .setTitle(
                                        ":regional_indicator_p::regional_indicator_o::regional_indicator_n::regional_indicator_g:")
                                .addField("Ping:", new ColouredStringDiff()
                                        .addRed(ping + "ms", true)
                                        .build(), true)
                                .addField("WS ping:", new ColouredStringDiff()
                                        .addRed(jda.getGatewayPing() + "ms", true)
                                        .build(), true)
                                .build())
                        .queue());
        */
        MessageChannel channel = ctx.getChannel();
        ctx.getMessage().addReaction(Data.check).complete();
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
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "A really simple ping command.");
    }
}