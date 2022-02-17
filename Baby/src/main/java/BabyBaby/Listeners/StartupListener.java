package BabyBaby.Listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import BabyBaby.Command.commands.Admin.*;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.Command.commands.Slash.PollSlashCMD;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Data;

import javax.annotation.Nonnull;


import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class StartupListener extends ListenerAdapter{
    
    public final JDA bot;

    public StartupListener(JDA bot) throws IOException {
        this.bot = bot;
    }


    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        long timestopper = System.currentTimeMillis();


        LinkedList<Thread> threads = new LinkedList<>();
        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                Connection c = null;
                Statement stmt = null;

                ResultSet rs;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    
                    stmt = c.createStatement();

                    rs = stmt.executeQuery("SELECT * FROM GUILD;");
                    
                    while (rs.next()) {
                        String id = rs.getString("ID");
                        String prefixstr = rs.getString("PREFIX");
                        BabyListener.prefix.put(id, prefixstr);
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }));
        threads.getLast().start();

        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES;");

                    while(rs.next()){
                        String id = rs.getString("ID");
                        Data.emoteassign.put(rs.getString("EMOTE"), id);
                        Data.roles.add(id);
                    }

                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                }
            }
        }));
        threads.getLast().start();


        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM MSGS;");
                    while(rs.next()){
                        String id = rs.getString("MSGID");
                        Data.msgid.add(id);
                        String cat = rs.getString("CATEGORY");
                        ArrayList<String> temp = Data.catToMsg.getOrDefault(cat, new ArrayList<String>());
                        temp.add(id);
                        Data.catToMsg.put(cat, temp);
                        
                        Data.msgToChan.put(id, rs.getString("CHANNELID"));
                    }
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                }
            }
        }));
        threads.getLast().start();

        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                Connection c = null;
                List<Invite> inv = event.getJDA().getGuildById(Data.ETH_ID).retrieveInvites().complete();
                

                //urls.put(vanity.getUrl(), value)
                
                PreparedStatement pstmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    pstmt = c.prepareStatement("DELETE FROM INVITES;");
                    pstmt.execute();
                    pstmt.close();
                    c.close();
                } catch ( Exception e ) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                }

                c = null;
                pstmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    for (Invite invite : inv) {
                        pstmt = c.prepareStatement("INSERT INTO INVITES (URL, AMOUNT) VALUES (?, ?);");
                        pstmt.setString(1, invite.getUrl());
                        pstmt.setInt(2, invite.getUses());
                        pstmt.executeUpdate();
                        pstmt.close();
                    }
                    c.close();
                } catch ( Exception e ) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                } 
            }
        }));
        threads.getLast().start();


        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                AuditLogPaginationAction logs = event.getJDA().getGuildById(Data.ETH_ID).retrieveAuditLogs();
                for (AuditLogEntry entry : logs) {
                    if(entry.getType().equals(ActionType.KICK)){
                        Data.kick = entry.getTimeCreated();
                        break;
                    }   
                }
                for (AuditLogEntry entry : logs) {
                    if(entry.getType().equals(ActionType.BAN)){
                        Data.ban = entry.getTimeCreated();
                        break;
                    }   
                }
            }
        }));
        threads.getLast().start();


        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM REMINDERS;");

                    while(rs.next()){
                        try {
                        ScheduledExecutorService remind = Executors.newScheduledThreadPool(1);
                        remind.schedule(new GetReminder(event.getJDA().getUserById(rs.getString("USERID")), event.getJDA().getGuildById(rs.getString("GUILDID")), rs.getString("TEXTS"), rs.getString("CHANNELID"), rs.getString("pk")), (rs.getLong("TIME")-System.currentTimeMillis())/1000 , TimeUnit.SECONDS);
                        } catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Probably a User that left the server while being reminded.");
                        }
                    }
                    
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                }
            }
        }));
        threads.getLast().start();

        //put assign message ids in cache
        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT MSGID FROM MSGS;");
        
                    while(rs.next()){
                        Data.msgid.add(rs.getString("MSGID"));
                    }
                    
                    stmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                }
            }
        }));
        threads.getLast().start();

        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;

                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    
                    stmt = c.createStatement();

                    rs = stmt.executeQuery("SELECT * FROM ROLEREMOVAL");            
                    while(rs.next()){
                        try{
                        Guild called = bot.getGuildById( rs.getString("GUILDID"));
                        User blindUser = called.getMemberById(rs.getString("USERID")).getUser();
                        long time = Long.parseLong(rs.getString("MUTETIME"));
                        ScheduledExecutorService blindex = Executors.newScheduledThreadPool(1);
                        
                        GetRolesBack blindclass = new GetRolesBack(blindUser, called, rs.getString("ROLES"));
                        blindex.schedule(blindclass, (time-System.currentTimeMillis())/1000, TimeUnit.SECONDS);
                        BlindCMD.blind.put(called.getMember(blindUser), blindex);
                        BlindCMD.blindexe.put(blindex, blindclass);
                        
                        if(rs.getBoolean("ADMINMUTE")){
                            AdminMuteBlindCMD.userBlinded.add(called.getMember(blindUser));
                        }
                        
                        } catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Probably a User that left the server while being blinded.");
                        }

                    }
                    stmt.close();
                    c.close();

                } catch ( Exception e ) {
                    e.printStackTrace(); 
                }
            }
        }));
        threads.getLast().start();

        for (Thread thread : threads) {
            try{
                thread.join();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
        System.out.println("Started!" + (System.currentTimeMillis()-timestopper));


        try {
            Guild eth = bot.getGuildById(Data.ETH_ID);
            eth.updateCommands().complete();
            ArrayList<CommandData> slashcmds = new ArrayList<>();

            PollSlashCMD test = new PollSlashCMD();
            test.load(test.initialise(eth), eth);

            //blind slash cmd
            CommandDataImpl blind = new CommandDataImpl("blind", "A command to blind yourself. You won't see any channels for this time.");
                        
            blind.addOption(OptionType.NUMBER, "time", "Length of the blind.", true);
            blind.addOption(OptionType.STRING, "unit", "Default is minutes. Seconds, minutes, hours, days.");
            blind.addOption(OptionType.BOOLEAN, "force", "If forceblind or not. Default is false.");
            blind.addOption(OptionType.BOOLEAN, "semester", "You will keep your Subject Channels. Default is false.");
            
            slashcmds.add(blind);
            eth.upsertCommand(blind).complete();

            
            //remind
            CommandDataImpl remind = new CommandDataImpl("remind", "A command to remind yourself.");
                        
            remind.addOption(OptionType.NUMBER, "time", "In how many Time units do you want to get reminded?", true);
            remind.addOption(OptionType.STRING, "unit", "Default is minutes. Others are seconds, minutes, hours, days.");
            
            slashcmds.add(remind);
            eth.upsertCommand(remind).complete();


            //role slash cmd
            CommandDataImpl role = new CommandDataImpl("role", "A command to get/remove a role.");
                        
            role.addOption(OptionType.ROLE, "role", "The Role you want to have or get removed.", true);
            
            slashcmds.add(role);
            eth.upsertCommand(role).complete();


            //report slash cmd
            CommandDataImpl report = new CommandDataImpl("report", "A command to report a incident to Staff anonymously.");
            
            report.addOption(OptionType.STRING, "issue", "The isssue you have or the incident that occured.", true);
            report.addOption(OptionType.USER, "user", "If you want to report a User. This can be left empty.");
            
            slashcmds.add(report);
            eth.upsertCommand(report).complete();


            //rolesleft slash cmd
            CommandDataImpl rolesleft = new CommandDataImpl("roles", "A command to see which roles you still could get.");
            slashcmds.add(rolesleft);
            eth.upsertCommand(rolesleft).complete();


            //admin slash Cmds
            CommandDataImpl admin = new CommandDataImpl("admin", "All admin commands.");
            LinkedList<SubcommandData> subc = new LinkedList<>();
            
            //timeout
            SubcommandData timeout = new SubcommandData("timeout", "Cmd to timeout a user.");
            timeout.addOption(OptionType.USER, "user", "The user to timeout.", true);
            timeout.addOption(OptionType.NUMBER, "time", "The duration of the time out", true);
            timeout.addOption(OptionType.STRING, "unit", "Seconds, minutes, hours, days, years", true);
            timeout.addOption(OptionType.STRING, "reason", "Reason why user got a time out. User doesn't see that.", false);   
            subc.add(timeout);

            //ban
            SubcommandData ban = new SubcommandData("ban", "Cmd to ban a user.");
            ban.addOption(OptionType.USER, "user", "The user to ban.", true);
            ban.addOption(OptionType.STRING, "reason", "Reason why user got a ban. User doesn't see that.", false);   
            subc.add(ban);

            //kick
            SubcommandData kick = new SubcommandData("kick", "Cmd to kick a user.");
            kick.addOption(OptionType.USER, "user", "The user to kick.", true);
            kick.addOption(OptionType.STRING, "reason", "Reason why user got a kick. User doesn't see that.", false);   
            subc.add(kick);

            //warn
            SubcommandData warn = new SubcommandData("warn", "Cmd to warn a user.");
            warn.addOption(OptionType.USER, "user", "The user to warn.", true);
            warn.addOption(OptionType.STRING, "reason", "Reason why user got a warning. User gets this message dmed.", true);  
            subc.add(warn);

            //warnings
            SubcommandData warnings = new SubcommandData("warnings", "Cmd to see warnings of users. If no user is provided all users with warnings are shown");
            warnings.addOption(OptionType.USER, "user", "Warnings of user.", false);
            warnings.addOption(OptionType.STRING, "userid", "Id of user for the case they left the server.", false);
            subc.add(warnings);

            //whois
            SubcommandData whois = new SubcommandData("whois", "Cmd to see warnings of users. If no user is provided all users with warnings are shown");
            whois.addOption(OptionType.USER, "user", "Warnings of user.", false);
            whois.addOption(OptionType.STRING, "userid", "Id of user for the case you can't type their username.", false);
            whois.addOption(OptionType.BOOLEAN, "ephemeral", "True if message should be ephemeral. Default is false", false);
            subc.add(whois);

            //rolebutton
            SubcommandData rolebutton = new SubcommandData("rolebutton", "Cmd to send a button for a role");
            rolebutton.addOption(OptionType.ROLE, "role", "Select assignable Role.", true);
            subc.add(rolebutton);

            //addrole 
            SubcommandData addrole = new SubcommandData("addrole", "Command to add a selfassignable role.");
            addrole.addOption(OptionType.ROLE, "role", "Select assignable Role.", true);
            addrole.addOption(OptionType.STRING, "emote", "Connect emote", true); //not sure if that works with emotes
            addrole.addOption(OptionType.STRING, "category", "Add role to a category", false);
            subc.add(addrole);

            //assign
            SubcommandData assign = new SubcommandData("assign", "Command to send message for roleassignment channel.");
            subc.add(assign);
            
            //editassign
            SubcommandData editassign = new SubcommandData("editassign", "Command to update role messages.");
            subc.add(editassign);

            //delrole
            SubcommandData delrole = new SubcommandData("delrole", "Command to remove a selfassignable role.");
            delrole.addOption(OptionType.ROLE, "role", "Select Role to delete from Bot.", true);
            subc.add(delrole);

            //roleid
            SubcommandData roleid = new SubcommandData("roleid", "Command to get all ID's of selfassignable role.");
            subc.add(roleid);

            //updaterole  
            SubcommandData updaterole = new SubcommandData("updaterole", "Command to update a selfassignable role. If optional field is left empty, it doesn't change.");
            updaterole.addOption(OptionType.STRING, "roleid", "Role id at the moment.", true);
            updaterole.addOption(OptionType.ROLE, "newrole", "New ID/role", false);
            updaterole.addOption(OptionType.STRING, "emote", "New emote", false); //not sure if that works with emotes
            updaterole.addOption(OptionType.STRING, "category", "New category", false);
            subc.add(updaterole);
            
            admin.addSubcommands(subc); //Not sure if that works tbh and documentation is sparse
            admin.setDefaultEnabled(false);
            
            slashcmds.add(admin);

            String adminID = eth.upsertCommand(admin).complete().getId();
            Role adminrole = eth.getRoleById(Data.ADMIN_ID);
            Role modrole = eth.getRoleById(Data.MODERATOR_ID);
            
            eth.updateCommandPrivilegesById(adminID, Arrays.asList(CommandPrivilege.enable(adminrole), CommandPrivilege.enable(modrole))).complete();
            System.out.println("Completely done.");

            eth.updateCommands().complete();

            /*
            try {
                CommandData test = new CommandData("test", "A command to test stuff");
                
                
                test.setDefaultEnabled(false);
                //SubcommandGroupData sgd = new SubcommandGroupData("testing", "test2");
                SubcommandData testing = new SubcommandData("testingdata", "test3");
                SubcommandData testing2 = new SubcommandData("test2", "But actually test4");
                
                testing.addOption(OptionType.ROLE, "options", "Mentionable stuff", true);
                test.addSubcommands(testing);
                test.addSubcommands(testing2);

                
                

                /*
                sgd.addSubcommands(testing);
                sgd.addSubcommands(testing2);
                
                SubcommandGroupData sgd2 = new SubcommandGroupData("2ndsgd", "Second subcommandgroup");
                SubcommandData tmptesting = new SubcommandData("subcmd", "subcommand 1");
                SubcommandData tmptesting2 = new SubcommandData("subcmd2", "subcmd 2 in group2");
                sgd2.addSubcommands(tmptesting);
                sgd2.addSubcommands(tmptesting2);

                test.addSubcommandGroups(sgd2);
                test.addSubcommandGroups(sgd);
                


                
                var cmd = eth.upsertCommand(test).complete();
                
                long id = cmd.getIdLong();
                slashcmds.add(test);
                
                CommandPrivilege me = new CommandPrivilege(Type.USER, true, 223932775474921472L);
                
                eth.updateCommandPrivilegesById(id, me).complete();
                
            } catch (Exception e) {
                System.out.println("Didn't work");
                e.printStackTrace();
            }   
            */

            for (CommandData cmd : slashcmds) {
                BabyListener.slash.add(cmd.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }



}
