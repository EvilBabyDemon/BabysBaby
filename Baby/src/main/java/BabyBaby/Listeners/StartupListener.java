package BabyBaby.Listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import BabyBaby.Command.commands.Admin.*;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.GetUnmute;
import BabyBaby.data.data;

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
                    c = DriverManager.getConnection(data.db);
                    
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
                    c = DriverManager.getConnection(data.db);
                    
                    stmt = c.createStatement();

                    rs = stmt.executeQuery("SELECT * FROM USERS");            
                    while(rs.next()){
                        try{
                        Guild called = bot.getGuildById( rs.getString("GUILDUSER"));
                        User muteUser = called.getMemberById(rs.getString("ID")).getUser();
                        long time = Long.parseLong(rs.getString("MUTETIME"));
                        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
                        List<Role> tmp = called.getRolesByName("STFU", true);
                        Role muteR = tmp.get(0);
                        GetUnmute muteclass = new GetUnmute(muteUser, called, muteR);
                        mute.schedule(muteclass, (time-System.currentTimeMillis())/1000, TimeUnit.SECONDS);
                        MuteCMD.userMuted.put(called.getMember(muteUser), mute);
                        } catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Probably a User that left the server while being muted.");
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

        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES;");

                    while(rs.next()){
                        String id = rs.getString("ID");
                        data.emoteassign.put(rs.getString("EMOTE"), id);
                        data.roles.add(id);
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
                    c = DriverManager.getConnection(data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM MSGS;");
                    while(rs.next()){
                        String id = rs.getString("MSGID");
                        data.msgid.add(id);
                        String cat = rs.getString("CATEGORY");
                        ArrayList<String> temp = data.catToMsg.getOrDefault(cat, new ArrayList<String>());
                        temp.add(id);
                        data.catToMsg.put(cat, temp);
                        
                        data.msgToChan.put(id, rs.getString("CHANNELID"));
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
                List<Invite> inv = event.getJDA().getGuildById(data.ethid).retrieveInvites().complete();
                HashMap<String, Invite> urls = new HashMap<>();
                for (Invite var : inv) {
                    urls.put(var.getUrl(), var);
                }

                c = null;
                PreparedStatement pstmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(data.db);
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
                    c = DriverManager.getConnection(data.db);
                    for (String var : urls.keySet()) {
                        pstmt = c.prepareStatement("INSERT INTO INVITES (URL, AMOUNT) VALUES (?, ?);");
                        pstmt.setString(1, var);
                        pstmt.setInt(2, urls.get(var).getUses());
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
                AuditLogPaginationAction logs = event.getJDA().getGuildById(data.ethid).retrieveAuditLogs();
                for (AuditLogEntry entry : logs) {
                    if(entry.getType().equals(ActionType.KICK)){
                        data.kick = entry.getTimeCreated();
                        break;
                    }   
                }
                for (AuditLogEntry entry : logs) {
                    if(entry.getType().equals(ActionType.BAN)){
                        data.ban = entry.getTimeCreated();
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
                    c = DriverManager.getConnection(data.db);
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


        //Put admin mutes in cache
        threads.add(new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet rs;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM ADMINMUTE;");

                    while(rs.next()){
                        try{
                            Guild muteG = event.getJDA().getGuildById(rs.getString("GUILDID"));
                            User mutedPerson = event.getJDA().getUserById(rs.getString("USERID"));
                            int time = rs.getInt("TIME");
                            if(time == 0){
                                MutePersonCMD.userMuted.put(muteG.getMember(mutedPerson), null);
                            } else {
                                ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
                                mute.schedule(new GetUnmutePerson(mutedPerson, muteG), time , TimeUnit.SECONDS);
                                MutePersonCMD.userMuted.put(muteG.getMember(mutedPerson), mute);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Probably a User that left the server while being adminmuted.");
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
                    c = DriverManager.getConnection(data.db);
                    stmt = c.createStatement();
                    rs = stmt.executeQuery("SELECT MSGID FROM MSGS;");
        
                    while(rs.next()){
                        data.msgid.add(rs.getString("MSGID"));
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
                    c = DriverManager.getConnection(data.db);
                    
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
                        RemoveRoles.blind.put(called.getMember(blindUser), blindex);
                        RemoveRoles.blindexe.put(blindex, blindclass);
                        
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

        for (Thread var : threads) {
            try{
                var.join();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
        System.out.println("Started!" + (System.currentTimeMillis()-timestopper));


        try {
            Guild eth = bot.getGuildById(data.ethid);

            eth.updateCommands().complete();

            ArrayList<CommandData> slashcmds = new ArrayList<>();
            CommandData poll = new CommandData("poll", "A cmd to create a simple Poll.");
            poll.addOption(OptionType.STRING, "title", "This is the Title of your poll.", true);
            
            poll.addOption(OptionType.STRING, "option1", "This is Option " + 1, true);
            poll.addOption(OptionType.STRING, "option2", "This is Option " + 2, true);
            
            for (int i = 2; i < 10; i++) {
                poll.addOption(OptionType.STRING, "option" +(i+1), "This is Option " + (i+1));
            }
            slashcmds.add(poll);
            eth.upsertCommand(poll).complete();


            CommandData blind = new CommandData("blind", "A command to blind yourself. Do not use this cmd if you dont know what it does.");
                        
            blind.addOption(OptionType.INTEGER, "time", "Length of the blind.", true);
            blind.addOption(OptionType.STRING, "unit", "Default is minutes. Seconds, minutes, hours, days.");
            blind.addOption(OptionType.BOOLEAN, "force", "If forceblind or not. Default is false");
            slashcmds.add(blind);
            
            eth.upsertCommand(blind).complete();



            CommandData role = new CommandData("role", "A command to get/remove a role.");
                        
            role.addOption(OptionType.ROLE, "role", "The Role you want to have or get removed.", true);
            slashcmds.add(role);
            
            eth.upsertCommand(role).complete();

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
