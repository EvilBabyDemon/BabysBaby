package BabyBaby.Listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.commands.Admin.AprilFools;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
//import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
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
    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
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

        if (event.getSelectMenu().getId().equals("betray")) {
            if (AprilFools.turnOff) {
                return;
            }
    
            User user = event.getUser();
            if (System.currentTimeMillis() - AprilFools.cooldownUser.getOrDefault(user.getId(), 0L) > AprilFools.cooldown) {
                String[] test = { "1089996311522201715", "1089996425091371128", "1089996512701984789",
                        "1089996620625612921", "1089996706009063424", "1089996740654006412", "1089996797780447282"};
                List<String> ids = Arrays.asList(test);

                Role roleObj = event.getGuild().getRoleById(event.getValues().get(0).getId());
                if (!ids.contains(roleObj.getId())) {
                    Helper.unhook("Select one of the colour roles!", failed, hook, user);
                    return;
                }
                Optional<Role> role = event.getMember().getRoles().stream().filter(t -> ids.contains(t.getId()))
                        .findFirst();
                if (role.isPresent()) {

                    if(role.get().getId().equals(roleObj.getId())) {
                        Helper.unhook("You have this role already!", failed, hook, user);
                        return;
                    }

                    event.getGuild().removeRoleFromMember(event.getMember(), role.get()).complete();
    
                    if (AprilFools.roleChannel.get(role.get().getId()) != null) {
                        TextChannel tc = event.getGuild().getTextChannelById(AprilFools.roleChannel.get(role.get().getId()));
                        tc.sendMessage(event.getUser().getAsMention() + " **has betrayed you!**").queue();
                    } else {
                        System.out.println(role.get().getId());
                    }
                }

                if (AprilFools.roleChannel.get(roleObj.getId()) != null) {
                    TextChannel tc = event.getGuild().getTextChannelById(AprilFools.roleChannel.get(roleObj.getId()));
                    tc.sendMessage(event.getUser().getAsMention() + " **has joined you!**").queue();
                } else {
                    System.out.println(role.get().getId());
                }
                event.getGuild().addRoleToMember(event.getMember(), roleObj).complete();
                Helper.unhook("You have betrayed your team!", failed, hook, user);
                AprilFools.cooldownUser.put(user.getId(), System.currentTimeMillis());
    
            } else {
                Helper.unhook("You can't betray your team yet as you did it too recently! Actions have Consequences.",
                        failed, hook, user);
            }
        }
    }

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
