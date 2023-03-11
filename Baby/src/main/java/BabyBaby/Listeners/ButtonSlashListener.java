package BabyBaby.Listeners;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Role;
//import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ButtonSlashListener extends ListenerAdapter {

    // ButtonEvent
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        InteractionHook msgHook = null;
        boolean failed = false;
        try {
            msgHook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }

        // tracking usage
        Data.slashAndButton++;
        Data.users.add(event.getUser().getId());

        if (Data.emoteassign.containsKey(event.getComponentId())) {
            Role role = event.getGuild().getRoleById(Data.emoteassign.get(event.getComponentId()));
            Helper.roleGiving(event.getMember(), event.getGuild(), failed, role, msgHook);

            Data.cmdUses.putIfAbsent(role.getName(), 0);
            Data.cmdUses.computeIfPresent(role.getName(), (name, x) -> ++x);
        }
    }

    // SelectionMenu


    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        InteractionHook msgHook = null;
        boolean failed = false;
        try {
            msgHook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        if (event.getUser().isBot())
            return;

        if (event.getSelectMenu().getId().equals("menu:class")) {
            if (!failed) {
                msgHook.editOriginal("You have selected " + event.getValues().size()).queue();
            }
        }
    }

    // Slash Commands
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        InteractionHook hook = null;
        boolean failed = false;
        try {
            hook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        if (event.getUser().isBot())
            return;

        // check if blinded and then just ignore cmd
        if (event.getGuild().getId().equals(Data.ETH_ID)
                && event.getMember().getRoles().contains(event.getGuild().getRoleById(Data.BLIND_ID))) {
            String cheater = "Unblind yourself and don't try to cheat!";
            if (failed) {
                event.getUser().openPrivateChannel().complete().sendMessage(cheater).complete();
            } else {
                hook.editOriginal(cheater).queue();
            }
            return;
        }

        // tracking usage
        Data.slashAndButton++;
        Data.users.add(event.getUser().getId());

        String cmd = event.getName();
        ISlashCMD cmdClass = null;

        for (ISlashCMD cmdSlash : Data.slashcmds) {
            if (cmd.equals(cmdSlash.getName())) {
                cmdClass = cmdSlash;
                break;
            }
        }
        if (cmdClass == null) {
            Helper.unhook("Uhhh what? Please send a screenshot of this to my owner.", failed, hook, event.getUser());
            return;
        }

        cmdClass.handle(event, hook, failed);
        Data.cmdUses.putIfAbsent(cmdClass.getName(), 0);
        Data.cmdUses.computeIfPresent(cmdClass.getName(), (name, x) -> ++x);
    }

}
