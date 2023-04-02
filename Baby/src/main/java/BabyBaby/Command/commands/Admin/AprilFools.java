package BabyBaby.Command.commands.Admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class AprilFools implements IPublicCMD, ISlashCMD {

    public static HashMap<String, Long> cooldownUser = new HashMap<>();
    public static long cooldown = 120 * 60 * 1000;
    public static boolean turnOff = true;
    public static HashMap<String, Integer> weights = new HashMap<>(); 
    public static Map<String, String> roleChannel = Map.of(
        "1089996311522201715", "1090241101128020058",
        "1089996620625612921", "1090241142613872701",
        "1089996512701984789", "1090241190814810122",
        "1089996706009063424", "1090241270330433547",
        "1089996740654006412", "1090241320284598313",
        "1089996797780447282", "1090241453973843968",
        "1089996425091371128", "1090241560702091334"
    );
    @Override
    public String getName() {
        return "april";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        // Change users to add colour
        String[] ids = { "1089996311522201715", "1089996425091371128", "1089996512701984789",
                        "1089996620625612921", "1089996706009063424", "1089996740654006412", "1089996797780447282"};
        Guild guild = ctx.getGuild();
        List<Member> members = guild.getMembers();
        switch (ctx.getArgs().get(0)) {
            case "start":
                outer: for (Member mem : members) {
                    String roleId = "";
                    switch ((int) (mem.getIdLong() % 7)) {
                        case 0:
                            roleId = "1089996311522201715";
                            break;
                        case 1:
                            roleId = "1089996425091371128";
                            break;
                        case 2:
                            roleId = "1089996512701984789";
                            break;
                        case 3:
                            roleId = "1089996620625612921";
                            break;
                        case 4:
                            roleId = "1089996706009063424";
                            break;
                        case 5:
                            roleId = "1089996740654006412";
                            break;
                        case 6:
                            roleId = "1089996797780447282";
                            break;
                        default:
                            System.out.println("Didnt work for" + mem.getId());
                            continue;
                    }
                    
                    for (String id : ids) {
                        Role r = guild.getRoleById(id);
                        if (mem.getRoles().contains(r)) {
                            continue outer;
                        }
                    }
                    
                    Role role = guild.getRoleById(roleId);
                    guild.addRoleToMember(mem, role).queue();
                }
                break;
            case "stats":
                
                String text = "";
                for (String id : ids) {
                    Role role = guild.getRoleById(id);
                    text += "<@&" + id + ">: " + guild.getMembersWithRoles(role).size() + "\n";
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Stats of each colour:");
                eb.setDescription(text);

                ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
                break;
            case "set":
                cooldown = Long.valueOf(ctx.getArgs().get(1)) * 60 * 1000;
                ctx.getChannel().sendMessage("Set cooldown to " + cooldown + "ms").complete();
                break;
            case "toggle":
                turnOff = !turnOff;
                ctx.getChannel().sendMessage("Set turnOff to " + turnOff).complete();
                break;
            case "weight":
                weights.put(ctx.getArgs().get(1), Integer.parseInt(ctx.getArgs().get(2)));
                break;
            default:
                ctx.getChannel().sendMessage("Check help.").complete();
                break;

            case "dropdown":
                EntitySelectMenu menu = EntitySelectMenu.create("betray", SelectTarget.ROLE)
                    .setPlaceholder("Betray your colour!") // shows the placeholder indicating what this menu is for
                    .setRequiredRange(1, 1) // must select exactly one
                    .build();
            
                ctx.getChannel().sendMessage("Betray your colour:")
                    .addActionRow(menu)
                    .queue();
                break;
            case "shuffle":
                Role rmax = null;
                int maxsize = 0;
                Role rmin = null;
                int minsize = 3000;
                for (String id : ids) {
                    Role r = guild.getRoleById(id);
                    if (maxsize < guild.getMembersWithRoles(r).size()) {
                        maxsize = guild.getMembersWithRoles(r).size();
                        rmax = r;
                    }
                    if (minsize > guild.getMembersWithRoles(r).size()) {
                        minsize = guild.getMembersWithRoles(r).size();
                        rmin = r;
                    }
                }

                ArrayList<Member> maxMembers = new ArrayList<>(guild.getMembersWithRoles(rmax));
                Collections.shuffle(maxMembers);
                
                for (int i = 0; i < (maxsize - minsize) / 2; i++) {
                    Member change = maxMembers.get(i);
                    System.out.println(i+ ": " + change.getEffectiveName());
                    guild.removeRoleFromMember(change, rmax).complete();
                    guild.addRoleToMember(change, rmin).complete();
                }
                break;
        }
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "start|stats|toggle|set <minute>|weight <id> <int>", "Start, toggle on/off, get stats or set the cooldown of selecting colour");
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        if (turnOff) {
            return;
        }

        User user = event.getUser();
        if (System.currentTimeMillis() - cooldownUser.getOrDefault(user.getId(), 0L) > cooldown) {
            String[] test = { "1089996311522201715", "1089996425091371128", "1089996512701984789",
                    "1089996620625612921", "1089996706009063424", "1089996740654006412", "1089996797780447282"};
            List<String> ids = Arrays.asList(test);
            if (!ids.contains(event.getOption("join").getAsRole().getId())) {
                Helper.unhook("Select one of the colour roles!", failed, hook, user);
                return;
            }
            Optional<Role> role = event.getMember().getRoles().stream().filter(t -> ids.contains(t.getId()))
                    .findFirst();
            if (role.isPresent()) {
                event.getGuild().removeRoleFromMember(event.getMember(), role.get()).complete();

                if (roleChannel.get(role.get().getId()) != null) {
                    TextChannel tc = event.getGuild().getTextChannelById(roleChannel.get(role.get().getId()));
                    tc.sendMessage(event.getUser().getAsMention() + " **has betrayed you!**").queue();
                } else {
                    System.out.println(role.get().getId());
                }
            }
            if (roleChannel.get(event.getOption("join").getAsRole().getId()) != null) {
                TextChannel tc = event.getGuild().getTextChannelById(roleChannel.get(event.getOption("join").getAsRole().getId()));
                tc.sendMessage(event.getUser().getAsMention() + " **has joined you!**").queue();
            } else {
                System.out.println(role.get().getId());
            }
            event.getGuild().addRoleToMember(event.getMember(), event.getOption("join").getAsRole()).complete();
            Helper.unhook("You have betrayed your team!", failed, hook, user);
            cooldownUser.put(user.getId(), System.currentTimeMillis());

        } else {
            Helper.unhook("You can't betray your team yet as you did it too recently! Actions have Consequences.",
                    failed, hook, user);
        }
    }


    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl role = new CommandDataImpl(getName(),
                "Use this to switch to a different colour and betray your team. There is a cooldown!");
        role.addOption(OptionType.ROLE, "join", "The colour you want to join.", true);
        return role;
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        String[] ids = { "1089996311522201715", "1089996425091371128", "1089996512701984789",
                        "1089996620625612921", "1089996706009063424", "1089996740654006412", "1089996797780447282"};
        Guild guild = ctx.getGuild();
        String text = "";
        for (String id : ids) {
            Role role = guild.getRoleById(id);
            text += "<@&" + id + ">: " + guild.getMembersWithRoles(role).size() + "\n";
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Stats of each colour:");
        eb.setDescription(text);

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Send stats of colours.");
    }
}
