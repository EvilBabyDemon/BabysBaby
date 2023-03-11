package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class PollSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        if (!event.getMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND)) {
            Helper.unhook("You need write permissions here!", failed, hook, event.getUser());
        }
        String topic = event.getOption("title").getAsString();

        String[] emot = { "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟" };
        
        if (topic.length() > 256) {
            event.getChannel().sendMessage("Your Title can't be longer than 256 chars.").queue();
            return;
        }

        String options = "";
        int amount = 0;
        for (int i = 0; i < 10; i++) {
            if (event.getOption("option" + (i + 1)) == null)
                continue;
            options += emot[amount] + " : " + event.getOption("option" + (i + 1)).getAsString() + "\n";
            amount++;
        }

        if (options.length() > 2000) {
            event.getChannel()
                    .sendMessage("All your options together can't be more than 2000 chars, so keep it simpler!")
                    .queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(topic);
        eb.setColor(0);
        eb.setDescription(options);
        String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
                : event.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, event.getMember().getUser().getAvatarUrl());

        Message built = event.getChannel().sendMessageEmbeds(eb.build()).complete();
        for (int i = 0; i < amount; i++) {
            built.addReaction(Emoji.fromUnicode(emot[i])).queue();
        }

        Helper.unhook("Done", failed, hook, event.getUser());

    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl poll = new CommandDataImpl(getName(), "A cmd to create a simple Poll.");
        poll.addOption(OptionType.STRING, "title", "This is the Title of your poll.", true);

        poll.addOption(OptionType.STRING, "option1", "This is Option " + 1, true);
        poll.addOption(OptionType.STRING, "option2", "This is Option " + 2, true);

        for (int i = 2; i < 10; i++) {
            poll.addOption(OptionType.STRING, "option" + (i + 1), "This is Option " + (i + 1));
        }

        return poll;
    }

}
