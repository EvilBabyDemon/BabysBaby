package BabyBaby.Command.commands.Slash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.commands.Public.UnBlindCMD;
import BabyBaby.data.Data;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class BlindSlashCMD implements ISlashCMD {
    public static HashMap<Member, ScheduledExecutorService> blind = new HashMap<>();
    public static HashMap<ScheduledExecutorService, GetRolesBack> blindexe = new HashMap<>();
    public static HashSet<GetRolesBack> forceSet = new HashSet<>();

    @Override
    public String getName() {
        return "blind";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String unit = event.getOption("unit", OptionMapping::getAsString);
        boolean force = event.getOption("force", false, OptionMapping::getAsBoolean);
        boolean semester = event.getOption("semester", false, OptionMapping::getAsBoolean);
        String time = event.getOption("time").getAsString();
        roleRemoval(time, event.getMember(), event.getGuild(), unit, force, event.getChannel(), semester, event, hook,
                failed);
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl blind = new CommandDataImpl(getName(),
                "A command to blind yourself. You won't see any channels for this time.");

        blind.addOption(OptionType.NUMBER, "time", "Length of the blind.", true);
        blind.addOption(OptionType.STRING, "unit", "Default is minutes. Seconds, minutes, hours, days.");
        blind.addOption(OptionType.BOOLEAN, "force", "If forceblind or not. Default is false.");
        blind.addOption(OptionType.BOOLEAN, "semester", "You will keep your Subject Channels. Default is false.");

        eth.upsertCommand(blind).complete();
        return blind;
    }

    public void roleRemoval(String number, Member mem, Guild guild, String unit, boolean force, MessageChannel channel,
            Boolean semester, SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {

        LinkedList<GuildChannel> gchan = new LinkedList<>();
        Role everyone = guild.getRoleById(guild.getId());

        for (TextChannel textChannel : guild.getTextChannels()) {
            if (semester && textChannel.getParentCategory() != null) {
                String catName = textChannel.getParentCategory().getName().toLowerCase();
                if (catName.contains("gess")) {
                    continue;
                }
            }
            if (!everyone.hasAccess(textChannel)) {
                gchan.add(textChannel);
            }
        }

        Member silenced = mem;
        List<Role> begone = silenced.getRoles();
        LinkedList<Role> permrole = new LinkedList<>();

        double time;
        User blindUser = mem.getUser();
        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);

        try {
            if (number.length() > 18 || Double.parseDouble(number) > Integer.MAX_VALUE) {
                time = Integer.MAX_VALUE;
            } else {
                time = Double.parseDouble(number);
            }
        } catch (NumberFormatException e) {
            Helper.unhook("You probably forgot the space between the time and unit, if not use numbers pls!", failed,
                    hook, mem);
            return;
        }

        if (time <= 0) {
            Helper.unhook("Use positive numbers thx!", failed, hook, mem);
            return;
        }

        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = "" + retrieverObj[0];
        long rounder = (long) retrieverObj[1];

        if (rounder <= 29) {
            Helper.unhook("Use values of at least 30 seconds please!", failed, hook, mem);
            return;
        }

        long timesql = (System.currentTimeMillis() + rounder * 1000);

        Role highestbot = guild.getSelfMember().getRoles().get(0);

        // check if there is a role that is higher than a bot but also can see a channel
        for (Role role : begone) {
            String roleName = role.getName().toLowerCase();
            if (semester && (roleName.contains(". semester") || roleName.contains("all semesters"))) {
                continue;
            }
            for (GuildChannel guildChannel : gchan) {
                if (role.hasAccess(guildChannel)) {
                    if (role.getPosition() >= highestbot.getPosition()) {
                        Helper.unhook(
                                "Sry you have a higher Role than this bot with viewing permissions. Can't take your roles away",
                                failed, hook, mem);
                        return;
                    }
                    permrole.add(role);
                    break;
                }
            }
        }

        Connection c = null;
        PreparedStatement stmt = null;
        String role = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            for (Role roleToStr : permrole) {
                role += roleToStr.getId() + " ";
            }

            stmt = c.prepareStatement(
                    "INSERT INTO ROLEREMOVAL (USERID, GUILDID, MUTETIME, ROLES, ADMINMUTE) VALUES (?, ?, ?, ?, ?);");
            stmt.setString(1, mem.getId());
            stmt.setString(2, guild.getId());
            stmt.setString(3, timesql + "");
            stmt.setString(4, role);
            stmt.setString(5, "false");

            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch (Exception e) {
            Helper.unhook(e.getClass().getName() + ": " + e.getMessage(), failed, hook, mem);
            return;
        }

        GetRolesBack scheduledclass = new GetRolesBack(blindUser, guild, role);
        mute.schedule(scheduledclass, rounder, TimeUnit.SECONDS);

        blind.put(mem, mute);
        blindexe.put(mute, scheduledclass);

        String msg = " got blinded for ~" + time + " " + strUnit + ".";
        if (force) {
            forceSet.add(scheduledclass);
            msg += " **Wait out the timer!!!** And hopefully you are productive!";
        } else {
            msg += " Either wait out the timer or write me (<@781949572103536650>) in Private chat \"+"
                    + new UnBlindCMD().getName() + "\"";
        }
        Helper.unhook(mem.getAsMention() + msg, failed, hook, mem);

        LinkedList<Role> addrole = new LinkedList<>();
        try {
            addrole.add(guild.getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }

        guild.modifyMemberRoles(mem, addrole, permrole).complete();
        try {
            blindUser.openPrivateChannel().complete().sendMessage("You" + msg).queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }

    }

}
