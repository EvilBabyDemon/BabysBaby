package BabyBaby.Command.commands.Admin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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


    public void editAssign(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
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

    public EmbedBuilder embeds(String title, String msg){
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(1);
        eb.setDescription(msg);
        eb.setFooter("Click on the Emotes to assign yourself Roles.");

        return eb;
    }

    public LinkedList<Object[]> rolesorter (HashMap<Role, Object[]> sorting){
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

    public boolean cont (List<String> c, String s){
        for (String str : c) {
            if(str.contains(s)){
                return true;
            }
        }
        return false;
    }

    //Addrole
    public void addrole(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {

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
    public void getWarnings(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
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
    public void getWarned(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed, boolean ephemeral) {
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

    public void whois(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
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

    public void newRole (SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        
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

    

}
