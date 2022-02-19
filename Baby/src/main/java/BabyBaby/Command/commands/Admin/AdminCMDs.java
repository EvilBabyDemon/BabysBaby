package BabyBaby.Command.commands.Admin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class AdminCMDs {

    //ban
    public static void ban(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {

        MessageChannel channel = event.getChannel();

        
        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            Helper.unhook("Missing Permissions.", failed, hook, event.getUser());
            return;
        }

        Member bad = event.getOption("user").getAsMember();
        

        if(bad.getRoles().get(0).getPosition() >= event.getMember().getRoles().get(0).getPosition()){
            Helper.unhook("Can't ban someone with a higher or same role.", failed, hook, event.getUser());
            return;
        }
        String reason = event.getOption("reason") == null ? "" : event.getOption("reason").getAsString();

        if(reason==""){
            bad.ban(0).complete();
        } else {
            bad.ban(0, reason).complete();
        }

        User author = event.getUser();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(author.getAsTag() + " (" + author.getId() + ")", author.getAvatarUrl(), author.getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(author.getAvatarUrl());
        eb.setDescription(":warning: **Banned** " + bad.getAsMention() + "(" + bad.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);
        channel.sendMessageEmbeds(eb.build()).queue();
        Helper.unhook("Done", failed, hook, event.getUser());
    }

    //kick
    public static void kick(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        MessageChannel channel = event.getChannel();
        
        if(!event.getMember().hasPermission(Permission.KICK_MEMBERS)){
            Helper.unhook("Missing Permissions.", failed, hook, event.getUser());
            return;
        }

        Member bad = event.getOption("kick").getAsMember();

        String reason = event.getOption("reason") == null ? "" : event.getOption("reason").getAsString();

        if(bad.getRoles().get(0).getPosition() >= event.getMember().getRoles().get(0).getPosition()){
            Helper.unhook("Can't kick someone with a higher or same role.", failed, hook, event.getUser());
            return;
        }

        if(reason==""){
            bad.kick().complete();
        } else {
            bad.kick(reason).complete();
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(event.getUser().getAvatarUrl());
        
        eb.setDescription(":warning: **Kicked** " + bad.getAsMention() + "(" + bad.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);
        channel.sendMessageEmbeds(eb.build()).queue();

        Helper.unhook("Done.", failed, hook, event.getUser());
    }

    //warn
    public static void warn(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        MessageChannel channel = event.getChannel();
        String person = event.getOption("user").getAsString();
        String reason = event.getOption("reason").getAsString();
        
        LocalDate time = LocalDate.now();

        String date = time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear();

        Connection c = null;
        PreparedStatement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            
            String sql = "INSERT INTO WARNINGS (USER,REASON,DATE) " +
                            "VALUES (?,?,?);"; 
            stmt = c.prepareStatement(sql);
            
            stmt.setLong(1, Long.parseLong(person));
            stmt.setString(2, reason);
            stmt.setString(3, date);
            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace();
            return;
        }

        MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(event.getGuild().getMemberById(person).getUser().getAvatarUrl());
        Member warned = event.getGuild().getMemberById(person);

        eb.setDescription(":warning: **Warned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);

        log.sendMessageEmbeds(eb.build()).queue();

        event.getChannel().sendMessageEmbeds(eb.build()).queue();

        warned.getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();
        Helper.unhook("Done", failed, hook, event.getUser());   
    }

    //timeout
    public static void timeout(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Member bad = event.getOption("user").getAsMember();
        double time = event.getOption("time").getAsDouble();
        String unit = event.getOption("unit").getAsString();
        String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : ""; 
        
        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = ""+retrieverObj[0];
        long rounder = (long) retrieverObj[1];
        
        if(!event.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            Helper.unhook("Need moderate Members permissions!", failed, hook, event.getUser());
            return;
        }

        if(bad.hasPermission(Permission.ADMINISTRATOR)) {
            Helper.unhook("Can't timeout an Admin.", failed, hook, event.getUser());
            return;
        }

        try {
            bad.timeoutFor(rounder, TimeUnit.MINUTES).complete();
        } catch (Exception e) {
            Helper.unhook("Pretty sure the time was too long.", failed, hook, event.getUser());
        }
        
        Helper.unhook(bad.getAsMention() + "was timed out for " + time + " " + strUnit, failed, hook, event.getUser());
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(event.getUser().getAvatarUrl());
        
        eb.setDescription(":warning: **Timeout** " + bad.getAsMention() + "(" + bad.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);
        event.getChannel().sendMessageEmbeds(eb.build()).queue();

        Helper.unhook("Done.", failed, hook, event.getUser());

    }

    //edit Assign
    public static void editAssign(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = event.getChannel();
        HashSet<String> cats = new HashSet<String>(); 

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while ( rs.next() ) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }
        
        //doing embeds with each category

        String msg = "";

        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        ArrayList<String> categ = new ArrayList<>();
        LinkedList<String> roles = new LinkedList<>();
        
        for (String strCateg : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.createStatement();

                Guild called = event.getGuild();
                rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES WHERE categories='" + strCateg + "';");
                while ( rs.next() ) {
                    String rcat = rs.getString("ID");
                    String emoteStr = rs.getString("EMOTE");
                    String orig = emoteStr;


                    try {
                        Long.parseLong(emoteStr);
                        try {
                            emoteStr = event.getJDA().getEmoteById(emoteStr).getAsMention();   
                        } catch (Exception e) {
                            emoteStr = "ERROR";
                        }
                    } catch (Exception e) {
                    }


                    msg = emoteStr + " : "+ called.getRoleById(rcat).getAsMention() + "\n";
                    sorting.put(called.getRoleById(rcat), new Object[] {orig, msg});
                }
                rs.close();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            LinkedList<Object[]> sorted = rolesorter(sorting);
            LinkedList<String> tempo = new LinkedList<>();
            msg = "";
            for (Object[] obj : sorted) {
                tempo.add((String) obj[0]);
                msg += (String) obj[1];
            }

            emotes.add(tempo);
            categ.add(strCateg);
            roles.add(msg);
            msg = "";
        }

        ArrayList<EmbedBuilder> emb = new ArrayList<>();
        LinkedList<String> remover = new LinkedList<>();

        for (int i = 0; i < categ.size(); i++) {
            emb.add(embeds(categ.get(i), roles.get(i)));
        }

        for (int k = 0; k < emb.size(); k++) {
            EmbedBuilder eb = emb.get(k);
            LinkedList<String> tempEmo = new LinkedList<>();
            tempEmo.addAll(emotes.remove(0));

            
            ArrayList<Button> butt = new ArrayList<>();
            for (String emoID : tempEmo) {
                
                boolean gemo = false;
                
                try {
                    Long.parseLong(emoID);
                    gemo = true;
                } catch (Exception e) {
                }
                
                try{
                    butt.add(Button.primary(emoID, gemo ? Emoji.fromEmote(event.getJDA().getEmoteById(emoID)): Emoji.fromUnicode(emoID)));
                } catch (Exception e){
                    event.getChannel().sendMessage("Reaction with ID:" + emoID + " is not accesible.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
            }

            

            LinkedList<ActionRow> acR = new LinkedList<>();
            for (int i = 0; i < butt.size(); i +=5) {
                ArrayList<Button> row = new ArrayList<>();
                for (int j = 0; j < 5 && j+i < butt.size(); j++) {
                    row.add(butt.get(i+j));
                }
                acR.add(ActionRow.of(row));
            }

            MessageAction msgAct;

            if(!Data.catToMsg.containsKey(categ.get(k))){
                msgAct = channel.sendMessageEmbeds(eb.build());
                msgAct.setActionRows(acR);
                Message msgs = msgAct.complete();
                Data.msgid.add(msgs.getId());
                ArrayList<String> templist = Data.catToMsg.getOrDefault(categ.get(k), new ArrayList<String>());
                templist.add(msgs.getId());
                Data.catToMsg.put(categ.get(k), templist);
                Data.msgToChan.put(msgs.getId(), msgs.getChannel().getId());
                
                c = null;
                PreparedStatement pstmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection(Data.db);
                    pstmt = c.prepareStatement("INSERT INTO MSGS (GUILDID, CHANNELID, MSGID, CATEGORY) VALUES (?, ?, ?, ?);");
                    pstmt.setString(1, event.getGuild().getId());
                    pstmt.setString(2, event.getChannel().getId());
                    pstmt.setString(3, msgs.getId());
                    pstmt.setString(4, categ.get(k)); 
                    pstmt.executeUpdate();
                    pstmt.close();
                    c.close();
                } catch ( Exception e ) {
                    e.printStackTrace(); 
                    return;
                }
                continue;
            }

            
            for (String msgid : Data.catToMsg.get(categ.get(k))) {
                TextChannel chan = event.getGuild().getTextChannelById(Data.msgToChan.get(msgid));
                try {
                    Message sent = chan.retrieveMessageById(msgid).complete();
                    msgAct = sent.editMessageEmbeds(eb.build());
                } catch (Exception e) {
                    remover.add(msgid);
                    continue;
                }
                
                msgAct.setActionRows(acR);
                msgAct.complete();
            }

        }

        
        for (String oldMsgID : remover) {
            c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                pstmt = c.prepareStatement("DELETE FROM MSGS WHERE MSGID = ?;");
                pstmt.setString(1, oldMsgID);
                pstmt.executeUpdate();
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                e.printStackTrace(); 
            }
            Data.msgid.remove(oldMsgID);
            
            Data.msgToChan.remove(oldMsgID);
        }
        Helper.unhook("Done", failed, hook, event.getUser());
	}
    //helper for editAssign
    private static EmbedBuilder embeds(String title, String msg){
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(1);
        eb.setDescription(msg);
        eb.setFooter("Click on the Emotes to assign yourself Roles.");

        return eb;
    }
    //helper for editAssign
    private static LinkedList<Object[]> rolesorter (HashMap<Role, Object[]> sorting){
        LinkedList<Object[]> res = new LinkedList<>();
        while(sorting.size()!=0){
            Role highest = null;
            for (Role role : sorting.keySet()) {
                if(highest == null || role.getPosition() > highest.getPosition()){
                    highest = role;
                }
            }
            res.add(sorting.get(highest));
            sorting.remove(highest);
        }
        return res;
    }

    //Addrole
    public static void addrole(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {

        String id = event.getOption("role").getAsString();
        String emote = event.getOption("emote").getAsString();
        String categ = event.getOption("category").getAsString();

        if(emote.contains("<")){
            emote = emote.split(":")[2];
            emote.replace(">", "");
        }
        if(categ.equals("")){
            categ = "Other";
        } 
        

        Connection c = null;
        PreparedStatement pstmt = null;
        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            
            String sql = "INSERT INTO ASSIGNROLES (ID,CATEGORIES,EMOTE) VALUES (?, ?, ?);"; 
            pstmt = c.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(id));
            pstmt.setString(2, categ);
            pstmt.setString(3, emote);
            pstmt.executeUpdate();

            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            Helper.unhook(e.getClass().getName() + ": " + e.getMessage(), failed, hook, event.getUser());
            return;
        }

        Data.emoteassign.put(emote, id);
        Data.roles.add(id);
        Helper.unhook("Done", failed, hook, event.getUser());
    }

    //getWarnings
    public static void getWarnings(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        MessageChannel channel = event.getChannel();
        
        boolean ephemeral = event.getOption("ephemeral") == null ? true : event.getOption("ephemeral").getAsBoolean();
        String person = "";

        if(event.getOption("user") == null && event.getOption("userid") == null)  {
            getWarned(event, hook, failed, ephemeral);
            return;
        } else if (event.getOption("user") != null) {
            person = event.getOption("user").getAsString();
        } else {
            person = event.getOption("userid").getAsString();
        }
        

        EmbedBuilder eb = new EmbedBuilder();
        try{
            Member warned = event.getGuild().getMemberById(person);
            eb.setAuthor("Warnings from " + warned.getEffectiveName() + " (" + warned.getUser().getAsTag() + ")", warned.getUser().getAvatarUrl(), warned.getUser().getAvatarUrl());
        } catch (Exception e){
            eb.setAuthor("Warnings from " + person);
            return;
        }
        
        eb.setColor(1);


        Connection c = null;
        PreparedStatement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            
            String sql = "SELECT * FROM WARNINGS WHERE USER = ?;";
            stmt = c.prepareStatement(sql);
            stmt.setLong(1, Long.parseLong(person));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String time = rs.getString("DATE");
                String reason = rs.getString("REASON");
                eb.addField(time, reason, true);
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        
        String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
                : event.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, event.getUser().getAvatarUrl());

        if(ephemeral){
            Helper.unhook(eb.build(), failed, hook, event.getUser());
        } else {
            channel.sendMessageEmbeds(eb.build()).queue();
        }

    }

    //getWarned
    private static void getWarned(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed, boolean ephemeral) {
        MessageChannel channel = event.getChannel();
        HashSet<String> UserIds = new HashSet<>();
        
        Connection c = null;
        Statement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.createStatement();
            String sql = "SELECT USER FROM WARNINGS;";
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) 
                UserIds.add(rs.getString("USER"));
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("All Users with Warnings.", null);
        eb.setColor(1);

        String all = "";
        if(UserIds.size() != 0){
            for (String userID : UserIds){
                try {
                    all += event.getGuild().getMemberById(userID).getAsMention() + "\n";    
                } catch (Exception e) {
                    all += userID + "\n";
                }
                
            }
        }

        eb.setDescription(all);
        String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
                : event.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, event.getUser().getAvatarUrl());
        if(ephemeral){
            Helper.unhook(eb.build(), failed, hook, event.getUser());
        } else {
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
    
    //whois
    public static void whois(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
		MessageChannel channel = event.getChannel();

		Member stalking = null;
        if(event.getOption("user") == null && event.getOption("userid") == null)  {
            stalking = event.getMember();
        } else if (event.getOption("user") != null) {
            stalking = event.getOption("user").getAsMember();
        } else {
            stalking = event.getGuild().getMemberById(event.getOption("userid").getAsString());
        }
        boolean ephemeral = event.getOption("ephemeral") == null ? true : event.getOption("ephemeral").getAsBoolean();

		String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
					: event.getMember().getEffectiveName();
		List<Role> allrolesList = stalking.getRoles();
		
		LinkedList<Role> allroles = new LinkedList<>();
		
		for (Role role : allrolesList) {
			allroles.add(role);
		}
		allroles.add(event.getGuild().getRoleById(event.getGuild().getId()));
		Role highest = allroles.peek();
		Role hoisted = null;
		
		for (Role role : allroles) {
			if(role.isHoisted()){
				hoisted = role;
				break;
			}
		}
	
		DateTimeFormatter jointime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
		DateTimeFormatter createtime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
		
		String rolementions = "";
		
		while(allroles != null && rolementions.length() < 250 && allroles.size() != 0){
			rolementions += allroles.poll().getAsMention() + ", ";
		}
		
		if(allroles.size() == 0){
			if(rolementions.charAt(rolementions.length()-1) == ','){
				rolementions = rolementions.substring(0, rolementions.length()-1);
			} 
		} else {
			rolementions += "` and " + allroles.size() + " more...`";
		}
		OffsetDateTime created = stalking.getUser().getTimeCreated();
		OffsetDateTime now = OffsetDateTime.now();
		int day = now.getDayOfYear() - created.getDayOfYear();
		day = (day<0)? 365+day:day;
		int year =  now.getYear() - created.getYear() + ((now.getDayOfYear()<created.getDayOfYear())?-1:0);

		String multyear = ((year + Math.round(day/365.0)) == 1) ? " year ago" : " years ago";
		String multday = (day== 1) ? " day ago" : " days ago";
		String actualtime = (year >0) ?  (year + Math.round(day/365.0)) + multyear : day + multday;
		
		String addchecks = "Created as: **a " + ((stalking.getUser().isBot()) ? "bot" : "user") + " account** \n Created at: **" + stalking.getUser().getTimeCreated().format(createtime) + "** `(" + actualtime + ")`"; 
		
		LinkedList<String> invID = new LinkedList<>();
		String invitee = "";

		Connection c = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(Data.db);
			
			pstmt = c.prepareStatement("SELECT INVITEE FROM INVITED WHERE INVITED = ?;");
			pstmt.setString(1, stalking.getId());	
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				invID.add(rs.getString("INVITEE"));
			}
			
			pstmt.close();
			c.close();
		} catch ( Exception e ) {	
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		if(invID.size()==0){
			invitee = "NaN";
		}
		for (String userID : invID) {
			try {
				invitee += event.getGuild().getMemberById(userID).getAsMention(); 
			} catch (Exception e) {
			}
		}
		
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
		eb.setColor(highest.getColor());
		
		eb.addField("Nickname", "`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "` " + stalking.getAsMention(), false);
		eb.addField("Joined at", "`" + stalking.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);
		eb.addField("Invited by", invitee, false);
		eb.addField("Highest Role", highest.getAsMention(), true);
		eb.addField("Hoisted Role",(hoisted != null) ? hoisted.getAsMention(): "`Unhoisted`", true);
		eb.addField("Roles obtained (" + (1+allrolesList.size()) + ")" , rolementions, false);
		eb.addField("Additional Checks", addchecks, false);
		eb.setFooter("Summoned by: " + nickname, event.getUser().getAvatarUrl());
		eb.setThumbnail(stalking.getUser().getAvatarUrl());
		
		if(invID.size()!=0) channel.sendMessage("Cache reload").complete().editMessage(invitee).complete().delete().complete();
		
        if(ephemeral) {
            Helper.unhook(eb.build(), failed, hook, event.getUser());
        } else {
            channel.sendMessageEmbeds(eb.build()).queue();
        }

    }
    
    //newRole, Rolebutton
    public static void newRole(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        
        Role newRole = event.getOption("role").getAsRole();

        Connection c = null;
        PreparedStatement stmt = null;
        String emoteStr = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT * FROM ASSIGNROLES WHERE ID=?;");
            stmt.setString(1, newRole.getId());
            ResultSet rs = stmt.executeQuery();

            emoteStr = rs.getString("EMOTE");

        } catch (Exception e) {
            return;
        }
        boolean gemo = false;
        try {
            Long.parseLong(emoteStr);
            gemo = true;
        } catch (Exception e) {
        }
        String msgID = event.getChannel().sendMessage("Get " + newRole.getName() + " with this button:").setActionRow(Button.primary(emoteStr, gemo ? Emoji.fromEmote(event.getJDA().getEmoteById(emoteStr)): Emoji.fromUnicode(emoteStr))).complete().getId();
        Data.buttonid.add(msgID);
    }

    //assign
    public static void roleAssign(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = event.getChannel();
        HashSet<String> cats = new HashSet<String>(); 

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while ( rs.next() ) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }
        
        //doing embeds with each category

        String msg = "";



        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        ArrayList<String> categ = new ArrayList<>();
        LinkedList<String> roles = new LinkedList<>();

        for (String strCats : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.createStatement();

                Guild called = event.getGuild();
                rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES WHERE categories='" + strCats + "';");
                while ( rs.next() ) {
                    String rcat = rs.getString("ID");
                    String emote = rs.getString("EMOTE");
                    String orig = emote;

                    try {
                        Long.parseLong(emote);
                        try {
                            emote = event.getJDA().getEmoteById(emote).getAsMention();   
                        } catch (Exception e) {
                            emote = "ERROR";
                        }
                    } catch (Exception e) {
                    }

                    msg = emote + " : "+ called.getRoleById(rcat).getAsMention() + "\n";
                    sorting.put(called.getRoleById(rcat), new Object[] {orig, msg});
                }
                rs.close();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            LinkedList<Object[]> sorted = rolesorter(sorting);
            LinkedList<String> tempo = new LinkedList<>();
            msg = "";
            for (Object[] obj : sorted) {
                tempo.add((String) obj[0]);
                msg += (String) obj[1];
            }

            emotes.add(tempo);
            categ.add(strCats);
            roles.add(msg);
            msg = "";
        }

        ArrayList<EmbedBuilder> emb = new ArrayList<>();


        for (int i = 0; i < categ.size(); i++) {
            emb.add(embeds(categ.get(i), roles.get(i)));
        }
        for (int k = 0; k < emb.size(); k++) {
            EmbedBuilder eb = emb.get(k);
            LinkedList<String> emoList = new LinkedList<>();
            emoList.addAll(emotes.remove(0));

            
            ArrayList<Button> butt = new ArrayList<>();
            for (String emoID : emoList) {
                boolean gemo = false;
                
                try {
                    Long.parseLong(emoID);
                    gemo = true;
                } catch (Exception e) {
                }
                
                try{
                    butt.add(Button.primary(emoID, gemo ? Emoji.fromEmote(event.getJDA().getEmoteById(emoID)): Emoji.fromUnicode(emoID)));
                } catch (Exception e){
                    event.getChannel().sendMessage("Reaction with ID:" + emoID + " is not accesible.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
            }

            MessageAction msgAct = channel.sendMessageEmbeds(eb.build());
            
            LinkedList<ActionRow> acR = new LinkedList<>();
            for (int i = 0; i < butt.size(); i +=5) {
                ArrayList<Button> row = new ArrayList<>();
                for (int j = 0; j < 5 && j+i < butt.size(); j++) {
                    row.add(butt.get(i+j));
                }
                acR.add(ActionRow.of(row));
            }
            msgAct.setActionRows(acR);
            Message msgs = msgAct.complete();
            Data.msgid.add(msgs.getId());

            ArrayList<String> templist = Data.catToMsg.getOrDefault(categ.get(k), new ArrayList<String>());
            templist.add(msgs.getId());
            Data.catToMsg.put(categ.get(k), templist);
            
            Data.msgToChan.put(msgs.getId(), msgs.getChannel().getId());
            
            c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                pstmt = c.prepareStatement("INSERT INTO MSGS (GUILDID, CHANNELID, MSGID, CATEGORY) VALUES (?, ?, ?, ?);");
                pstmt.setString(1, event.getGuild().getId());
                pstmt.setString(2, event.getChannel().getId());
                pstmt.setString(3, msgs.getId());
                pstmt.setString(4, categ.get(k)); 
                pstmt.executeUpdate();
                pstmt.close();
                c.close();
            } catch ( Exception e ) {
                e.printStackTrace(); 
                return;
            }
        }

        Helper.unhook("Done!", failed, hook,event.getUser());
    } 

    //delrole
    public static void delRole(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String roleid = "";
        if(event.getOption("role") == null && event.getOption("roleid") == null){
            Helper.unhook("You need to use at least one field!", failed, hook, event.getUser());
            return;  
        } else if (event.getOption("role") != null) {
            roleid = event.getOption("role").getAsString();
        } else {
            roleid = event.getOption("roleid").getAsString();
        }
        
        

        if(!Data.roles.contains(roleid)){
            Helper.unhook("Role doesn't exist sry.", failed, hook, event.getUser());
            return;
        }
        Data.roles.remove(roleid);
        for (String emoteID : Data.emoteassign.keySet()) {
            if(Data.emoteassign.get(emoteID).equals(roleid)){
                Data.emoteassign.remove(emoteID);
                break;
            }
        }

        MessageChannel channel = event.getChannel();
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            c.setAutoCommit(false);
    
            stmt = c.createStatement();
            String sql = "DELETE from ASSIGNROLES where ID=" + roleid + ";";
            stmt.executeUpdate(sql);
            c.commit();
                
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        Helper.unhook("Done!", failed, hook, event.getUser());
    }
    
    //roleid
    public static void roleID(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = event.getChannel();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT ID FROM ASSIGNROLES;");
            String result = "";
            while ( rs.next() ) {
                String id = rs.getString("id");

                String rolename = "deleted-role";
                try {
                    rolename = event.getGuild().getRoleById(id).getName();
                } catch (Exception e) {
                }

                result += id + " " + rolename + "\n";
            }
            rs.close();
            stmt.close();
            c.close();
            Helper.unhook(result, failed, hook, event.getUser());
            channel.sendMessage(result).queue();
         } catch ( Exception e ) {
            Helper.unhook(e.getClass().getName() + ": " + e.getMessage(), failed, hook, event.getUser());
            return;
         }
    }

    //updateRole
    public static void updateRole(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        String roleID = event.getOption("roleid").getAsString();

        if(event.getOption("emote") == null && event.getOption("newrole") == null && event.getOption("category") == null) {
            Helper.unhook("Provide at least one of the optional fields.", failed, hook, event.getUser());
            return;
        }

        MessageChannel channel = event.getChannel();
        Connection c = null;
        Statement stmt = null;


        String update = "";
        
        if(event.getOption("emote") != null) {
            update = event.getOption("emote").getAsString();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                update = update.replace("<", "");
                update = update.replace(">", "");
                stmt = c.createStatement();
                String sql= "UPDATE ASSIGNROLES SET EMOTE = '" + update + "' where ID=" + roleID + ";";
                stmt.executeUpdate(sql); 
                
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }
            String oldemo = "";
            for (String emoteID : Data.emoteassign.keySet()) {
                if(Data.emoteassign.get(emoteID).equals(roleID)){
                    oldemo = emoteID;
                    break;
                }
            }
            Data.emoteassign.remove(oldemo);
            Data.emoteassign.put(update, roleID);
        }
        
        if(event.getOption("newrole") != null) {
            update = event.getOption("newrole").getAsString();
            
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.createStatement(); 
                String sql = "UPDATE ASSIGNROLES SET ID = " + update + " where ID=" + roleID + ";";
                stmt.executeUpdate(sql);
        
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }
            Data.roles.remove(update);
            Data.roles.add(roleID);
        }

        if(event.getOption("category") != null) {
            update = event.getOption("category").getAsString();

            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
        
                stmt = c.createStatement();
                String sql = "UPDATE ASSIGNROLES SET categories = '" + update + "' where ID=" + roleID + ";";
                stmt.executeUpdate(sql);
        
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }
        }
        Helper.unhook("Updated.", failed, hook, event.getUser());
    }

}
