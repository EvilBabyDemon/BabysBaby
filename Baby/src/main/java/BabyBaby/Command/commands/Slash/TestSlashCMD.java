package BabyBaby.Command.commands.Slash;

import BabyBaby.Command.ISlashCMD;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
//import net.dv8tion.jda.api.interactions.components.Modal;
//import net.dv8tion.jda.api.interactions.components.text.TextInput;
//import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class TestSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        if(failed){
            return;
        }
        /*
        TextInput email = TextInput.create("email", "Email", TextInputStyle.SHORT)
                .setPlaceholder("Enter your E-mail")
                .setMinLength(10)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();

        TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your concerns go here")
                .setMinLength(30)
                .setMaxLength(1000)
                .build();
        
        Modal modal = Modal.create("support", "Support")
                .addActionRows(ActionRow.of(email), ActionRow.of(body))
                .build();

        event.replyModal(modal).queue();
        */
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl test = new CommandDataImpl(getName(), "Cmd to see stuff about a user.");
        test.addOption(OptionType.USER, "user", "User to stalk.", false);
        return test;
    }
    
}
