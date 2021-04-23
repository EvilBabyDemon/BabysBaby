/*
package BabyBaby;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.io.OutputStream;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.ColorType;

import java.sql.*;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


import CryptPart.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;


public class MyListener extends ListenerAdapter {

	static boolean shut = false;
	final static String prefix = "+";
	final static String check = ":checkmark:769279808244809798";
	static boolean cryptdeleter = true;
	static String[] baduser = { "153929916977643521" };
	String ping = "<:pinged:747783377322508290> Pong!";
	static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(11);
	static User author;
	static int firstplace = 999;
	static HashMap<User, ScheduledExecutorService> userMuted = new HashMap<>();
	static Guild guild;
	static String stfuETH = "765542118701400134";
	//static String stfuB = "798924636171141120";
	final static String filePath = "C:\\Users\\Teufi2\\Desktop\\Countdown\\Banners - Kopie\\220.gif";
	final static String filePath2 = "C:\\Users\\Teufi2\\Desktop\\Countdown\\Banners - Kopie\\221.gif";
	static boolean[][] grid = new boolean[10][10];
	static int[][] rgbs;
	static boolean openforcmds = true;
	static File timenow;
	static boolean timerchange;
	static ScheduledExecutorService clock = Executors.newScheduledThreadPool(1);
	static boolean clockused;

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		OffsetDateTime time = event.getUser().getTimeCreated();
		String username = event.getUser().getName().toLowerCase();

		if(username.contains("lengler") || username.contains("emo") || username.contains("welzl")){
			event.getGuild().getTextChannelById("747754931905364000").sendMessage("<@&773908766973624340> Account with name Onur joined. Time of creation of the account:" + time).queue();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		
		
		
		

		


		//if (event.getAuthor().isBot() && !event.getAuthor().getId().equals("778731540359675904"))
		//	return;
		// We don't want to respond to other bot accounts, including ourself
		Message message = event.getMessage();
		String content = message.getContentRaw();
		// getContentRaw() is an atomic getter
		// getContentDisplay() is a lazy getter which modifies the content for e.g.
		// console view (strip discord formatting

		if(content.startsWith(prefix + "clock") && (event.getAuthor().getId().equals("781949572103536650"))){
			message.addReaction("🕰️").queue();
			LocalTime myObj = LocalTime.now();
			
			int clocktime = (15 - myObj.getMinute()%15)*60 + 60 - myObj.getSecond();

			if (clockused) {
				clock.shutdownNow();
				clock = Executors.newScheduledThreadPool(1);
			}
			String minuteString = ((15 + myObj.getMinute())%60 - myObj.getMinute()%15) == 0 ? ""  : "" + ((15 + myObj.getMinute())%60 - myObj.getMinute()%15);


			clockused = true;
			clock.schedule(new clockTower(((myObj.getHour() + ((myObj.getMinute() > 44) ? 1 : 0) )%12) + minuteString, event.getGuild()), clocktime, TimeUnit.SECONDS);
			message.addReaction(check).queue();
			return;
		}
 
		if (content.startsWith("Current value: ")) {
			if ((event.getAuthor().equals(author)) || event.getAuthor().getId().equals("778731540359675904")) {

				content = content.substring(16);

				String[] number = content.split("`");

				int time = Integer.parseInt(number[0]);

				if (time > firstplace) {
					time = 0;
				} else {
					time = firstplace - time;
					time *= 60;
				}

				if (shut) {
					System.out.println("Cancelled");
					scheduler.shutdownNow();
					scheduler = Executors.newScheduledThreadPool(11);
				}

				
				scheduler.schedule(new Later(), time, TimeUnit.SECONDS);
				
				
				System.out.println("In " + time/60 + " minutes at ");

				shut = true;
			}
		}


	


		if(content.equals("Still in timeout") && timerchange){
			message.delete().queue();
			event.getGuild().getTextChannelById("819966095070330950").sendMessage(".place pixelverify 900 720").queueAfter(5, TimeUnit.SECONDS);
		}

		if(content.startsWith("PIXELVERIFY") && content.split(" ")[3].equals("SUCCESS") && event.getAuthor().getId().equals("774276700557148170")){
			

			String[] cmd = content.split(" ");
			
			int xver = Integer.parseInt(cmd[1]);
			int yver = Integer.parseInt(cmd[2]);

			if(xver%100==0 && yver%100==0 && grid[xver/100][yver/100]){
				
				
				message.addReaction(check).queue();
				List<Attachment> test = message.getAttachments();
				Attachment test2 = test.get(0);

				try {
					
					BufferedImage img2 = ImageIO.read(new URL(test2.getUrl()));

					int[][] rgbs2 = new int [img2.getWidth()][img2.getHeight()];

					for (int i = 0; i < img2.getHeight(); i++) {
						for (int j = 0; j < img2.getWidth(); j++) {
							rgbs2[j][i] = img2.getRGB(j, i);
						}
					}

					//PrintStream out = new PrintStream(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\checker" + tmp.getX() + tmp.getY() + ".txt"));
					MessageChannel channel = event.getGuild().getTextChannelById("819966095070330950");

					for (int i = 0; i < img2.getWidth(); i++) {
						for (int j = 0; j < img2.getHeight(); j++) {
							if(rgbs[i+xver][j+yver] != rgbs2[i][j] && rgbs[i+xver][j+yver] != 0){
								String col = Integer.toHexString(rgbs[i+xver][j+yver]);
								col = col.substring(2);
								//String pixelset = (Math.random()<0.5) ? "setpIxel" : "setpixel";
								channel.sendMessage(".place setpIxel " + (i+xver) + " " + (j+yver) + " " + "#" + col).queue();//String.format("#%02x%02x%02x", c1.getRed(),c1.getGreen(), c1.getBlue());
							}	
						}
					}
				

					channel.sendMessage("DONE WITH A GRID").queue(response -> {
						openforcmds = true;
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if(timerchange && xver == 900 && yver == 720){
				timerchange = false;
				message.addReaction(check).queue();
				List<Attachment> test = message.getAttachments();
				Attachment test2 = test.get(0);

				String timename = timenow.getName();

				try {
					
					BufferedImage timer = ImageIO.read(timenow);
					int[][] tim = new int [timer.getWidth()][timer.getHeight()];
					for (int i = 720; i < timer.getHeight(); i++) {
						for (int j = 900; j < timer.getWidth(); j++) {
							tim[j][i] = timer.getRGB(j, i);
						}
					}

					BufferedImage img2 = ImageIO.read(new URL(test2.getUrl()));
					int[][] rgbs2 = new int [img2.getWidth()][img2.getHeight()];
					for (int i = 0; i < img2.getHeight(); i++) {
						for (int j = 0; j < img2.getWidth(); j++) {
							rgbs2[j][i] = img2.getRGB(j, i);
						}
					}

					MessageChannel channel = event.getGuild().getTextChannelById("819966095070330950");
					for (int i = 0; i < img2.getWidth(); i++) {
						for (int j = 0; j < img2.getHeight(); j++) {
							if(tim[i+xver][j+yver] != rgbs2[i][j] && tim[i+xver][j+yver] != 0){
								String col = Integer.toHexString(tim[i+xver][j+yver]);
								col = col.substring(2);
								channel.sendMessage(".place setpIxel " + (i+xver) + " " + (j+yver) + " " + "#" + col + " | " + timename).queue();
							}	
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			return;
		}

		

		
		if (event.getAuthor().isBot())
			return;
		
		if(content.startsWith(prefix + "whois")){
			Member caller = event.getMember();

			List<Role> callroles = caller.getRoles();
			Role admin = event.getGuild().getRoleById("747753814723002500");
			Role ceo = event.getGuild().getRoleById("773908766973624340");
			Role mod = event.getGuild().getRoleById("815932497920917514");

			boolean noaccess = true;
			for (Role var : callroles) {
				if(var.equals(admin) || var.equals(ceo) || var.equals(mod)){
					noaccess = false;
					break;
				}
			} 
			if(noaccess)
				return;

			String [] cmd = content.split(" ");
			Member stalking; 
			MessageChannel channel = event.getChannel();

			if(cmd.length > 1){
				cmd[1] = cmd[1].replaceAll("<", "");
				cmd[1] = cmd[1].replaceAll(">", "");
				cmd[1] = cmd[1].replaceAll("!", "");
				cmd[1] = cmd[1].replaceAll("@", "");
				stalking = event.getGuild().getMemberById(cmd[1]);
			} else{
				stalking = event.getMember();
			}

			

			String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
						: event.getMember().getEffectiveName();
			List<Role> allrolesList = stalking.getRoles();
			
			LinkedList<Role> allroles = new LinkedList<>();
			
			for (Role var : allrolesList) {
				allroles.add(var);
			}
			allroles.add(event.getGuild().getRoleById(event.getGuild().getId()));
			Role highest = allroles.peek();
			Role hoisted = null;
			
			for (Role var : allroles) {
				if(var.isHoisted()){
					hoisted = var;
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
			int year =  now.getYear() - created.getYear();
			String actualtime = (year >0) ?  (year + Math.round(day/365.0)) + " years ago": day + " days ago";
			
			String addchecks = "Created as: **a " + ((stalking.getUser().isBot()) ? "bot" : "user") + " account** \n Created at: **" + stalking.getUser().getTimeCreated().format(createtime) + "** `(" + actualtime + ")`"; 

			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
			eb.setColor(highest.getColor());
			
			
			eb.addField("Nickname", "`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "`", false);
			eb.addField("Joined at", "`" + stalking.getTimeJoined().format(jointime) + "`", false);
			eb.addField("Highest Role", highest.getAsMention(), true);
			eb.addField("Hoisted Role",(hoisted != null) ? hoisted.getAsMention(): "`Unhoisted`", true);
			eb.addField("Roles obtained (" + (1+allrolesList.size()) + ")" , rolementions, false);
			eb.addField("Additional Checks", addchecks, false);
			// eb.addBlankField(false);
			eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());
			// eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png%22);
			eb.setThumbnail(stalking.getUser().getAvatarUrl());
			

			channel.sendMessage(eb.build()).queue();


		}



		if (content.equals(prefix + "rnfgre")) {
			User secret = event.getAuthor();
			MessageChannel channel = event.getChannel();
			channel.deleteMessageById(message.getId()).queue();
			secret.openPrivateChannel().queue((channel1) -> {
				channel1.sendMessage("Hello there you are onto smth! Sadly I didn't finish this easter egg yet really, but still! Dont leak it! Bye!").queue();
			});
			author.openPrivateChannel().queue((channel2) -> {
				channel2.sendMessage(secret + " Is on it.").queue();
			});
		}

		
		


		if (event.getAuthor().equals(author)) {

		
			if(content.equalsIgnoreCase(prefix + "makedb")){
				Connection c = null;
				MessageChannel channel = event.getChannel();
				
				try {
				   Class.forName("org.sqlite.JDBC");
				   c = DriverManager.getConnection("jdbc:sqlite:testone.db");
				} catch ( Exception e ) {
				   channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
				}
				System.out.println("Opened database successfully");
				Statement stmt = null;
				
				try {
				   Class.forName("org.sqlite.JDBC");
				   c = DriverManager.getConnection("jdbc:sqlite:testone.db");
				   System.out.println("Opened database successfully");
		  
				   stmt = c.createStatement();
				   String sql = "CREATE TABLE ASSIGNROLES " +
								  "(ID STRING PRIMARY KEY     NOT NULL," +
								  " ROLE         TEXT    NOT NULL," +
								  " CATEGORIES         TEXT," +  
								  " EMOTE        TEXT, )"; 
				   stmt.executeUpdate(sql);
				   stmt.close();
				   c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
				}
				System.out.println("Table created successfully");

				message.addReaction(check).queue();
			}




			if(content.startsWith(prefix + "removeRoles")){
				Member silenced = event.getMember();
				MessageChannel channel = event.getChannel();
				List<Role> begone = silenced.getRoles();
				LinkedList<Role> rolewithPerm = new LinkedList<>();
				List<Role> bot = event.getGuild().getMemberById("").getRoles();
				Role highestbot = null;
				for (Role var : begone) {
					highestbot = var;
					break;
				}
				String roleIds = "";
				for (Role var : begone) {
					if(var.hasPermission()){
						if(var.getPosition()< highestbot.getPosition()){
							channel.sendMessage("Sry you have a higher Role, than this bot with viewing permissions. Can't take your roles away").queue();
							return;
						}
						rolewithPerm.add(var);
						roleIds += var.getId() + " ";
					}
				}

				Connection c = null;
				Statement stmt = null;
				
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					for (Role var : rolewithPerm) {
						
					}

					stmt = c.createStatement();
					String sql = "INSERT INTO USERHASROLE (USERID, GUILDID, ROLES) " +
									"VALUES ('" + event.getMember().getId() + "', '" + event.getGuild().getId() + "', '"+ content.split(" ")[1] + "');";
					ResultSet rs = stmt.executeQuery(sql);
					
					
					rs.close();
					stmt.close();
					c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				}

			}

			if(content.startsWith(prefix + "setPrefix")){
				Connection c = null;
				Statement stmt = null;
				MessageChannel channel = event.getChannel();

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					stmt = c.createStatement();
					String sql = "INSERT INTO GUILD (ID,PREFIX) " +
									"VALUES (" + event.getGuild().getId() + ", '"+ content.split(" ")[1] + "');";
					ResultSet rs = stmt.executeQuery(sql);
					 
					
					rs.close();
					stmt.close();
					c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				}

			}

			if(content.startsWith(prefix + "reminder")){
				Connection c = null;
				Statement stmt = null;
				MessageChannel channel = event.getChannel();
				String[] cmd = content.split(" ");

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					stmt = c.createStatement();
					String sql = "INSERT INTO REMINDERS (USERID,TEXTS, GUILDID, CHANNELID, TIME) " +
									"VALUES ('" + event.getMember().getId() + "', '" + content.substring(cmd[0].length() + cmd[1].length()+2) + event.getGuild().getId() + "', '" + event.getChannel().getId() +"', " + cmd[1] + "');";
					ResultSet rs = stmt.executeQuery(sql);


					
					rs.close();
					stmt.close();
					c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				}

			}


			
			


			if (content.equalsIgnoreCase(prefix + "getrole")) { // +role name  
				Connection c = null;
				Statement stmt = null;
				MessageChannel channel = event.getChannel();
				HashSet<String> cats = new HashSet<String>(); 

				ResultSet rs;

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					stmt = c.createStatement();

					rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
					while ( rs.next() ) {
						String cat = rs.getString("categories");
						cats.add(cat);
					}
					rs.close();
				} catch ( Exception e ) {
					channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
					e.printStackTrace(); 
					return;
				} finally{
					try {
						stmt.close();
						c.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
				System.out.println("Get done successfully");

				cats.add("Other");



				message.addReaction(check).queue();

				String msg = "";

				String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
						: event.getMember().getEffectiveName();

				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Roles you can assign yourself", null);
				eb.setColor(1);
				// eb.setDescription("Nothing to see here.");
				for (String var : cats) {

					try {
						Class.forName("org.sqlite.JDBC");
						c = DriverManager.getConnection("jdbc:sqlite:testone.db");
						c.setAutoCommit(false);
						System.out.println("Opened database successfully");
						
						stmt = c.createStatement();

						rs = stmt.executeQuery("SELECT ID FROM ASSIGNROLES WHERE categories='" + var + "';");
						while ( rs.next() ) {
							String rcat = rs.getString("ID");
							msg += event.getGuild().getRoleById(rcat).getAsMention() + "\n";
						}
						rs.close();
						stmt.close();
						c.close();
					} catch ( Exception e ) {
						channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
						return;
					}
					System.out.println("Get done successfully");

					eb.addField(var, msg, true);
					msg = "";
				}
				// eb.addBlankField(false);
				eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());
				
				channel.sendMessage(eb.build()).queue(response -> {
					Connection cLam = null;
					Statement stmtLam = null;
					ResultSet rsLam;
					try {
						
						Class.forName("org.sqlite.JDBC");
						cLam = DriverManager.getConnection("jdbc:sqlite:testone.db");
						cLam.setAutoCommit(false);
						System.out.println("Opened database successfully");
						
						stmtLam = cLam.createStatement();

						rsLam = stmtLam.executeQuery("SELECT EMOTE FROM ASSIGNROLES;");
						while (rsLam.next()) {
							String emote = rsLam.getString("EMOTE");
							if(emote == null || emote.length() == 0)
								continue;
							channel.addReactionById(response.getId(), emote).queue();
						}
						rsLam.close();
						stmtLam.close();
						cLam.close();
					} catch ( Exception e ) {
						channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
						return;
					}
				});
				channel.deleteMessageById(message.getId()).queue();

			}

			if (content.equalsIgnoreCase(prefix + "cleartable")) {
				Connection c = null;
				Statement stmt = null;
				MessageChannel channel = event.getChannel();
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					stmt = c.createStatement();

					stmt.executeQuery("DELETE FROM" + content.split(" ")[1] + ";");
					
					stmt.close();
					c.close();
				 } catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				 }
				System.out.println("Get done successfully");
				

				channel.deleteMessageById(message.getId()).queue();
			}
			
			
			if (content.equalsIgnoreCase(prefix + "role")) { // +role name  
				

				Connection c = null;
				Statement stmt = null;
				MessageChannel channel = event.getChannel();
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
					
					stmt = c.createStatement();

					ResultSet rs = stmt.executeQuery("SELECT ID FROM ASSIGNROLES;");
					String result = "";
					while ( rs.next() ) {
						String id = rs.getString("id");
						result = id + "/n";
					}
					rs.close();
					stmt.close();
					c.close();
					channel.sendMessage(result).queue();
				 } catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				 }
				System.out.println("Get done successfully");
				
				channel.deleteMessageById(message.getId()).queue();

			}

			

			if (content.startsWith(prefix + "updaterole")) { // +updaterole ('field' or 'all') name new text 

				String[] cmd = content.split(" ");
				MessageChannel channel = event.getChannel();
				Connection c = null;
				Statement stmt = null;

				switch(cmd[1]){
					case "emote":
					try {
						Class.forName("org.sqlite.JDBC");
						c = DriverManager.getConnection("jdbc:sqlite:testone.db");
						c.setAutoCommit(false);
						System.out.println("Opened database successfully");
				
						stmt = c.createStatement();
						String sql= "UPDATE ASSIGNROLES set EMOTE = '" + cmd[3] + "' where ID=" + cmd[2] + ";";
						stmt.executeUpdate(sql);
						c.commit();
						
						stmt.close();
						c.close();
					} catch ( Exception e ) {
						channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
						return;
					}


						break;
					
					case "id":
						
						try {
							Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:testone.db");
							c.setAutoCommit(false);
							System.out.println("Opened database successfully");
					
							stmt = c.createStatement();
							String sql = "UPDATE ASSIGNROLES set ID = " + cmd[3] + " where ID=" + cmd[2] + ";";
							stmt.executeUpdate(sql);
							c.commit();
					
							stmt.close();
							c.close();
						} catch ( Exception e ) {
							channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
							return;
						}


						break;
					
					case "categories":
						
						try {
							Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:testone.db");
							c.setAutoCommit(false);
							System.out.println("Opened database successfully");
					
							stmt = c.createStatement();
							String sql = "UPDATE ROLES set categories = '" + cmd[3] + "' where ID=" + cmd[2] + ";";
							stmt.executeUpdate(sql);
							c.commit();
					
							stmt.close();
							c.close();
						} catch ( Exception e ) {
							channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
							return;
						}
						
						break;
					
					case "name":

						try {
							Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:testone.db");
							c.setAutoCommit(false);
							System.out.println("Opened database successfully");
					
							stmt = c.createStatement();
							String sql = "UPDATE ASSIGNROLES set NAME = '" + cmd[3] + "' where ID=" + cmd[2] + ";";
							stmt.executeUpdate(sql);
							c.commit();
					
							stmt.close();
							c.close();
						} catch ( Exception e ) {
							channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
							return;
						}

						break;

					default:
						message.addReaction(check).queue();
						return;
				}	
			
				message.addReaction(check).queue();
			}

			if (content.startsWith(prefix + "delrole")) { // +delrole name  

				String[] cmd = content.split(" ");
				MessageChannel channel = event.getChannel();
				Connection c = null;
				Statement stmt = null;

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
		   
					stmt = c.createStatement();
					String sql = "DELETE from ASSIGNROLES where ID=" + cmd[1] + ";";
					stmt.executeUpdate(sql);
					c.commit();
						
					stmt.close();
					c.close();
				 } catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				 }
				 System.out.println("Delete done successfully");


				message.addReaction(check).queue();
			}

			if(content.startsWith(prefix + "sql")){
				
			}

			if (content.startsWith(prefix + "addrole")) { // +addrole name ID ROLE EMOTE
				

				

				String[] cmd = content.split(" ");
				String emote = cmd[4].substring(1, cmd[4].length()-1);
				MessageChannel channel = event.getChannel();
				Connection c = null;
				Statement stmt = null;
				
				try { 	
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");

					stmt = c.createStatement();
					String sql = "INSERT INTO ASSIGNROLES (ID,NAME,CATEGORIES,EMOTE) " +
									"VALUES (" + cmd[1] + ", '"+ cmd[2] + "', '" + cmd[3] + "', '" + emote + "');"; 
					stmt.executeUpdate(sql);

					stmt.close();
					c.commit();
					c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				}
				System.out.println("Added successfully");

				message.addReaction(check).queue();
			}

			// THIS NEXT COMMAND IS ONLY HERE TO HOLD PLACEHOLDER CODE FOR ME TO COPY
			// THIS NEXT COMMAND IS ONLY HERE TO HOLD PLACEHOLDER CODE FOR ME TO COPY
			// THIS NEXT COMMAND IS ONLY HERE TO HOLD PLACEHOLDER CODE FOR ME TO COPY
			
			if (content.startsWith(prefix + "awdadlkajdahfgejkfawkjdhakjhfjlhaefliefggqluifg")) {  
				MessageChannel channel = event.getChannel();
				Connection c = null;
				Statement stmt = null;
				
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");

					stmt = c.createStatement();
					String sql = "INSERT INTO ASSIGNROLES (ID,ROLE,EMOTE) " +
									"VALUES (1, 'Paul', 32, 'California', 20000.00 );"; 
					stmt.executeUpdate(sql);

					sql = "INSERT INTO ASSIGNROLES (ID,NAME,AGE,ADDRESS,SALARY) " +
							"VALUES (2, 'Allen', 25, 'Texas', 15000.00 );"; 
					stmt.executeUpdate(sql);

					sql = "INSERT INTO ASSIGNROLES (ID,NAME,AGE,ADDRESS,SALARY) " +
							"VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );"; 
					stmt.executeUpdate(sql);

					sql = "INSERT INTO ASSIGNROLES (ID,NAME,AGE,ADDRESS,SALARY) " +
							"VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );"; 
					stmt.executeUpdate(sql);

					stmt.close();
					c.commit();
					c.close();
				} catch ( Exception e ) {
					channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
					return;
				}
				System.out.println("Records created successfully");

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
			  
					stmt = c.createStatement();
					String sql = "UPDATE ASSIGNROLES set SALARY = 25000.00 where ID=1;";
					stmt.executeUpdate(sql);
					c.commit();
			  
					ResultSet rs = stmt.executeQuery( "SELECT * FROM ASSIGNROLES;" );
					
					while ( rs.next() ) {
					   int id = rs.getInt("id");
					   String  name = rs.getString("name");
					   int age  = rs.getInt("age");
					   String  address = rs.getString("address");
					   float salary = rs.getFloat("salary");
					   
					   System.out.println( "ID = " + id );
					   System.out.println( "NAME = " + name );
					   System.out.println( "AGE = " + age );
					   System.out.println( "ADDRESS = " + address );
					   System.out.println( "SALARY = " + salary );
					   System.out.println();
					}
					rs.close();
					stmt.close();
					c.close();
				} catch ( Exception e ) {
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);
				}
				  System.out.println("Operation done successfully");
				 




				  // DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE 
				  try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:testone.db");
					c.setAutoCommit(false);
					System.out.println("Opened database successfully");
		   
					stmt = c.createStatement();
					String sql = "DELETE from COMPANY where ID=2;";
					stmt.executeUpdate(sql);
					c.commit();
		   
					ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
					
					while ( rs.next() ) {
					int id = rs.getInt("id");
					String  name = rs.getString("name");
					int age  = rs.getInt("age");
					String  address = rs.getString("address");
					float salary = rs.getFloat("salary");
					
					System.out.println( "ID = " + id );
					System.out.println( "NAME = " + name );
					System.out.println( "AGE = " + age );
					System.out.println( "ADDRESS = " + address );
					System.out.println( "SALARY = " + salary );
					System.out.println();
				 }
				 rs.close();
				 stmt.close();
				 c.close();
				 } catch ( Exception e ) {
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);
				 }
				 System.out.println("Operation done successfully");







 
















			}
			
			
			if (content.startsWith("doit")) {

				ScheduledExecutorService newyear = Executors.newScheduledThreadPool(30);	
				content=content.substring(4);
				
				MessageChannel channel = event.getChannel();
				
				long time = Long.parseLong(content);
				
				long test = 0;

				newyear.schedule(new NewLater((int) time, channel), time + 60, TimeUnit.SECONDS);
				
				test = 75600;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 79200;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 82800;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 84600;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);			
				test = 85200;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 85800;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86100;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86160;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86220;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86280;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86340;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86370;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86380;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86390;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86391;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86392;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86393;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86394;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86395;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86396;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86397;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86398;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86399;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
				test = 86400;
				newyear.schedule(new NewLater((int) test, channel), test - time, TimeUnit.SECONDS);
			
			}

			if (content.equals(prefix + "server")) { 
				for (Guild guild : event.getJDA().getGuilds())
					event.getChannel().sendMessage(guild.getName()).queue();
			}

			if(content.startsWith(prefix + "operationsecret")){
				Long x = Long.parseLong(content.split(" ")[1]);
				Long y = Long.parseLong(content.split(" ")[2]);
				List<Member> everyone = event.getGuild().getMembers();

				// setup code
				char[] c = new char[] {'|', 'I', 'l', '‖'};
				Random rand = new Random();

				for (Member var : everyone) {
					if(x <= Long.parseLong(var.getId()) && Long.parseLong(var.getId()) < y){
						try{
							int len = rand.nextInt(20) + 10;
							String name = "";
							while(len-->0) name += c[rand.nextInt(c.length)];
							var.modifyNickname(name).queue();
						} catch(Exception e){
							event.getChannel().sendMessage(var.getUser().getAsTag()).queue();
							continue;
						}
					}
				}
			}

			if(content.startsWith(prefix + "operationcheck")){
				List<Member> everyone = event.getGuild().getMembers();
				LinkedList<String> oldchars = new LinkedList<>();
				// setup code
				char[] c = new char[] {'|', 'I', 'l', '‖'};
				Random rand = new Random();
				for(int i = 97; i < 123; i++){
					if(i == 108) continue;
					oldchars.add("" + ((char) i));
				}	
 				for (Member var : everyone) {
					boolean notchanged = false;
					for (String stringvar : oldchars) {
						if(var.getNickname() == null || var.getNickname().contains(stringvar)){
							notchanged = true;
							break;
						}
					}
					if(notchanged){
						try{
							int len = rand.nextInt(30) + 2;
							String name = "";
							while(len-->0) name += c[rand.nextInt(c.length)];
							var.modifyNickname(name).queue();
							event.getChannel().sendMessage(var.getAsMention()).queue();
						} catch(Exception e){
							event.getChannel().sendMessage(var.getUser().getAsTag()).queue();
							continue;
						}
					}
				}
				event.getChannel().sendMessage("Done").queue();
			}

			




			if (content.startsWith(prefix + "sort")) { 

				MessageChannel channel = event.getChannel();
				channel.deleteMessageById(message.getId()).queue();

				String[] cmd = content.split(" "); 
				LinkedList<String> sort = new LinkedList<>();
				try {
					Scanner s = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\" + cmd[1] + ".txt"));
					
					while(s.hasNext()){
						sort.add(s.nextLine());
					}
					s.close();
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}

				HashMap<String, ArrayList<String>> adder = new HashMap<>();
				for (String hex : sort) {
					
					String hexString = hex.substring(hex.length()-6, hex.length());
					int[] color_array = {Integer.parseInt(hexString.substring(0,2), 16),Integer.parseInt(hexString.substring(2,4), 16),Integer.parseInt(hexString.substring(4,6), 16)};
					int norm = Math.max(color_array[0]+color_array[1]+color_array[2],1);
					if(norm!=0){
						color_array[0] = (int)Math.round(4.0*color_array[0]/norm);
						color_array[1] = (int)Math.round(4.0*color_array[1]/norm);
						color_array[2] = (int)Math.round(4.0*color_array[2]/norm);
						
						hexString = ""+color_array[0]+color_array[1]+color_array[2];
					}
					//now you have 64 different colors to iterate through.

					ArrayList<String> tmp = adder.getOrDefault(hexString, new ArrayList<String>());
					tmp.add(hex);
					adder.put(hexString, tmp);
				}

				ArrayList<ArrayList<String>> copier = new ArrayList<>();

				for (ArrayList<String> var : adder.values()) {
					
					ArrayList<String> tmp = new ArrayList<>();
					for (String str : var) {
						tmp.add(str);
					}


					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							Comparator<String> comp = new Comparator<String>(){
								@Override
								public int compare(String o1, String o2) {
									return Integer.parseInt(o1.substring(o1.length()-6, o1.length()),16)-Integer.parseInt(o2.substring(o2.length()-6, o2.length()),16);
								}
							};
							tmp.sort(comp);
						}
					});
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					copier.add(tmp);
				}


				try {
					PrintStream out = new PrintStream(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\sort" + cmd[1] + ".txt"));	
					
					for (ArrayList<String> var : copier) {
						for (String var2 : var) {
							out.println(var2);
						}
					}

					out.flush();
					out.close();
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
				channel.sendMessage("Done").queue();

			}

			if(content.startsWith(prefix + "sendollie")){
				ScheduledExecutorService ollie = Executors.newScheduledThreadPool(1);
				User ollieUser = event.getGuild().getMemberById(content.split(" ")[2]).getUser();

				ollie.schedule(new schribollie(ollieUser), Integer.parseInt(content.split(" ")[1]), TimeUnit.SECONDS);
			}

			if(content.equals(prefix + "shutdown")){
				System.exit(0);
			}

			if(content.startsWith(prefix + "tick")){
				LocalTime myObj = LocalTime.now();
				clock = Executors.newScheduledThreadPool(1);
				String minuteString = (myObj.getMinute() - myObj.getMinute()%15) == 0 ? ""  : "" + (myObj.getMinute() - myObj.getMinute()%15);
				clock.schedule(new clockTower(((myObj.getHour())%12) + minuteString, event.getGuild()), 0, TimeUnit.SECONDS);
	
				message.addReaction(check).queue();
				return;
			}

			if(content.startsWith(prefix + "convert")){
				


				boolean onpc = Boolean.parseBoolean(content.split(" ")[1]);

				try {
					
					BufferedImage img;
					if(onpc){
						img = ImageIO.read(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\" + content.split(" ")[2]  + ".png"));
					} else{
						img = ImageIO.read(new URL(content.split(" ")[3]));
					}
					
					int[][] rgbs = new int [img.getWidth()][img.getHeight()];

					for (int i = 0; i < img.getHeight(); i++) {
						for (int j = 0; j < img.getWidth(); j++) {
							rgbs[j][i] = img.getRGB(j, i);
						}
					}
					PrintStream out = new PrintStream(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\con" + content.split(" ")[2] + ".txt"));

					for (int i = 0; i < img.getWidth(); i++) {
						for (int j = 0; j < img.getHeight(); j++) {
							if(rgbs[i][j] != 0){
								String col = Integer.toHexString(rgbs[i][j]);
								col = (col.length() == 7) ? col.substring(1) : col.substring(2);
								out.println(".place setpIxel " + i + " " + j + " " + "#" + col);//String.format("#%02x%02x%02x", c1.getRed(),c1.getGreen(), c1.getBlue());
							}
						}
					}
					
						
					out.flush();
					out.close();

					message.addReaction(check).queue();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			



			if(content.equals(prefix + "ucheck")){
				message.addReaction(check).queue();
				File filecheck = new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\checker.png");

				
				try {
					BufferedImage img = ImageIO.read(filecheck);
					rgbs = new int [img.getWidth()][img.getHeight()];

					for (int i = 0; i < img.getHeight(); i++) {
						for (int j = 0; j < img.getWidth(); j++) {
							rgbs[j][i] = img.getRGB(j, i);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < 1000; i++) {
					for (int j = 0; j < 1000; j++) {
						if(rgbs[i][j] != 0 || grid[i/100][j/100]){
							grid[i/100][j/100] = true;
							j += -j%100 + 100;
						}
					}
				}
				message.removeReaction(check).queueAfter(2, TimeUnit.SECONDS);
				return;
			}
			if(content.equals(prefix + "check")){
				MessageChannel channel = event.getGuild().getTextChannelById("819966095070330950"); 

				int counter = (content.length()>7) ? Integer.parseInt(content.split(" ")[1]) : 0; 
				for(int i = 0; i < 10; i++){
					for(int j = 0; j < 10; j++){
						if(grid[i][j]){
							channel.sendMessage(".place pixelverify " + i*100 + " " + j*100).completeAfter(counter, TimeUnit.SECONDS);
							counter += 55;
						}
					}
				}
				message.addReaction(check).queue();
			}

			
			if(content.startsWith(prefix + "grid")){
				String[] cmd = content.split(" ");
				grid[Integer.parseInt(cmd[1])][Integer.parseInt(cmd[2])] = Boolean.parseBoolean(cmd[3]);
				return;
			}

			if(content.startsWith(prefix + "openup")){
				openforcmds = true;
				return;
			}

			
			if (content.startsWith(prefix + "letsgo")) { 
				MessageChannel channel = event.getChannel(); 
				String[] cmd = content.split(" "); 
				int x = Integer.parseInt(cmd[1]);
				int start = Integer.parseInt(cmd[2]);
				for(int i = start; i < x; i++){
					try {
						Scanner s = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\" + cmd[3] + ".txt"));
						for(int j = 0; j < i; j++){
							if(s.hasNextLine()){
								s.nextLine();
							}
						}
						
						while(s.hasNextLine()){
							channel.sendMessage(s.nextLine()).queue();
							for(int j = 0; j < x-1; j++){
								if(s.hasNextLine()){
									s.nextLine();
								}
							}
						}
						s.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					channel.sendMessage("I did " + (i+1) +"/" + x + ". Lets go on! <@!223932775474921472>" ).queue();
				}
				channel.sendMessage("I am done Boss! Pls start the next else I am bored..... <@!223932775474921472>" ).queue();
			}
			

			if (content.equals(prefix + "setdel")) {

				MessageChannel channel = event.getChannel();

				cryptdeleter = !cryptdeleter;

				channel.deleteMessageById(message.getId()).queue();
				channel.sendMessage("You have set it to " + cryptdeleter).queue();

			} else if (content.startsWith(prefix + "say ")) {
				MessageChannel channel = event.getChannel();

				content = content.substring(5);
				channel.deleteMessageById(message.getId()).queue();
				channel.sendMessage(content).queue();

			} else if (content.startsWith(prefix + "for ")) {
				MessageChannel channel = event.getChannel();

				int x = (int) Integer.parseInt(content.split(" ")[1]);
				content = content.substring(6 + content.split(" ")[1].length());
				channel.deleteMessageById(message.getId()).queue();
				for (int i = 0; i < x; i++) {
					channel.sendMessage(content).queue();
				}
			} else if (content.startsWith(prefix + "set1")) {
				MessageChannel channel = event.getChannel();
				firstplace = Integer.parseInt(content.substring(6));
				channel.deleteMessageById(message.getId()).queue();
			}

		}

		if (content.equals(prefix + "me")) {
			if (event.getAuthor().getId().equals("223932775474921472")) {
				author = event.getAuthor();
				MessageChannel channel = event.getChannel();
				channel.deleteMessageById(message.getId()).queue();
			}
		} else if (content.equals(prefix + "ducky")) {
			MessageChannel channel = event.getChannel();

			content = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fd%2Fd2%2FRubber_Duck_Front_View_in_Fine_Day_20140107.jpg&f=1&nofb=1";
			channel.deleteMessageById(message.getId()).queue();
			channel.sendMessage(content).queue();
		
		} else if (poly(content)) {
			MessageChannel channel = event.getChannel();

			String sender = "https://polybox.ethz.ch/index.php/s/WXf1p3ODpDdpnRH";
			if (!content.equals(prefix + "poly") && !content.equals(prefix + "p")) {
				if (content.equals(prefix + "poly kay") || content.equals(prefix + "p kay")) {
					sender = "https://polybox.ethz.ch/index.php/s/WXf1p3ODpDdpnRH?path=%2F5%20Kay%27s%20Notes";
				} else {

					if (content.startsWith(prefix + "poly")) {
						content = content.substring(6);
					} else {
						content = content.substring(3);
					}
					sender += "?path==%2F1.%20Semester";

					switch (content.charAt(0)) {
					case 'a':
						sender += "1%20AnD";
						break;
					case 'e':
						sender += "3%20EProg";
						break;
					case 'l':
						sender += "4%20Linalg";
						break;
					case 'd':
						sender += "2%20DiskMath";
						break;
					case 'k':
						sender += "5%20Kay%27s%20Notes";
						break;
					default:
						break;
					}
					if (content.length() > 2 && content.charAt(2) == 's') {
						sender += "%2F1%20Skript";
					}
				}
			}
			message.addReaction(check).queue();
			channel.sendMessage("<" + sender + ">").queue();

		} else if (content.startsWith(prefix + "s ")) {
			MessageChannel channel = event.getChannel();
			File suggestions = new File("src\\suggestions.txt");
			content = content.substring(3);
			content = event.getAuthor().getAsTag() + " " + content;
			Message msg = event.getMessage();
			msg.delete().queue();
			try {
				FileWriter fr = new FileWriter(suggestions, true);
				fr.write(content + "\n");
				fr.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			channel.sendMessage("Thx for the suggestion!").queue();


		} else if (content.equals(prefix + "unmute")){
			if(!userMuted.containsKey(event.getAuthor())){
				event.getAuthor().openPrivateChannel().queue((channel) -> {
					channel.sendMessage("You were never muted. Or at least not by this bot. If you were pls report this problem to Lukas").queue();
				});
                return;
            }
            

			Role muteR = guild.getRoleById(stfuETH);

            Member muted = guild.retrieveMember(event.getAuthor()).complete();
            guild.removeRoleFromMember(muted, muteR).queue();

            ScheduledExecutorService mute =  userMuted.get(event.getAuthor());
            mute.shutdownNow();

            userMuted.remove(event.getAuthor());
			
			event.getAuthor().openPrivateChannel().queue((channel) -> {
				channel.sendMessage("You shall be unmuted!").queue();
			});

		} else if (content.startsWith(prefix + "mute")){

			MessageChannel channel = event.getChannel();
			
			String[] muter = content.split(" ");
			int time;
			String sunit;
			User muteUser = event.getAuthor(); 
            ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
            guild = event.getGuild();
			
			if(muter.length == 1){
				channel.sendMessage("The command is +mute <time> [unit] (default unit is minutes)").queue();
				return;
			}


			if(muter[1].length() > 18 || Long.parseLong(muter[1]) > Integer.MAX_VALUE){
				time=Integer.MAX_VALUE;
			} else {
				time = Integer.parseInt(muter[1]);
			}
			
			if(time <= 0){
				channel.sendMessage("Use positive numbers thx!").queue();
				return;
			}			


			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			//LocalDateTime now = LocalDateTime.now(); 
			
			

			if(muter.length <= 2) {
				sunit = "minutes";
				mute.schedule(new GetUnmute(muteUser, event.getGuild()), time, TimeUnit.MINUTES);	
			} else {
				muter[2] = muter[2].toLowerCase();
				if (muter[2].startsWith("h")){
					//now.plus(((long)time), TemporalUnit.class.);
					sunit = "hours";
					mute.schedule(new GetUnmute(muteUser, event.getGuild()), time, TimeUnit.HOURS);
				} else if(muter[2].startsWith("m")){
					sunit = "minutes";
					mute.schedule(new GetUnmute(muteUser, event.getGuild()), time, TimeUnit.MINUTES);
				} else if(muter[2].startsWith("d")){
					mute.schedule(new GetUnmute(muteUser, event.getGuild()), time, TimeUnit.DAYS);
					sunit = "days";
				} else {
					sunit = "seconds";
					mute.schedule(new GetUnmute(muteUser, event.getGuild()), time, TimeUnit.SECONDS);
				}
			}

			

			if(Double.parseDouble(muter[1]) <= 0){
				channel.sendMessage("Use positive numbers thx!").queue();
				return;
			}

			Member gemuted = guild.retrieveMember(event.getAuthor()).complete();

			
			
			




			Role muteR = event.getGuild().getRoleById(stfuETH); 
			guild.addRoleToMember(gemuted, muteR).queue();
			

			channel.sendMessage("You got muted for " + time + " " + sunit + ". Either wait out the timer or write me (the bot) in Private chat \"+unmute\"").queue();
			userMuted.put(muteUser, mute);
		} else if(content.startsWith(prefix + "rolemute")){
			if(true){
				return;
			}

			
		}

		
		
		String channelID = event.getChannel().getId();



		if(!channelID.equals("747776646551175217") && !channelID.equals("768600365602963496") && !channelID.equals("819966095070330950") && !channelID.equals("747768907992924192") && !event.getAuthor().equals(author))
			return;
		
		if(content.equals(prefix + "allroles")){
			List<Role> tmp = event.getGuild().getRoles();

			String mention = "";
			for (Role var : tmp) {
				mention += var.getAsMention() + " (" + var.getId() + ") \n";
			}
			
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Roles");
			eb.setColor(1);
			int dooku = 0;
			while(mention.length() > 1024){
				String submention = mention.substring(0, 1024);
				String[] part = submention.split("\n");
				submention = mention.substring(0, 1024 - part[part.length-1].length());
				eb.addField(""+ dooku, submention, true);
				mention = mention.substring(1024-part[part.length-1].length());
				dooku++;
			}
			eb.addField(""+ dooku, mention, true);
			
			
			String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
					: event.getMember().getEffectiveName();
			eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());
		
			event.getChannel().sendMessage(eb.build()).queue();
			
			message.addReaction(check).queue();



		} else if(content.startsWith(prefix + "sieb")){
			String[] cmd = content.split(" ");
			HashSet<Member> counter = new HashSet<>();

			List<Member> tmp = event.getGuild().getMembers();
			Role role1 = event.getGuild().getRoleById(cmd[1]);
			for (Member var : tmp) {
				if(var.getRoles().contains(role1))
					counter.add(var);
			}

			for(int i = 2; i < cmd.length-1; i +=2){
				Role role = event.getGuild().getRoleById(cmd[i+1]);
				switch (cmd[i]){
					case "!":
						List<Member> removerMembers = event.getGuild().getMembersWithRoles(role);
						for (Member var : removerMembers) {
							if(counter.contains(var)){
								counter.remove(var);
							}
						}
						break;
					case "&":
						LinkedList<Member> save = new LinkedList<>();
						for (Member var : counter) {
							if(!var.getRoles().contains(role))
								save.add(var);
						}
						counter.removeAll(save);
						break;
					case "|":
						List<Member> adderMem = event.getGuild().getMembersWithRoles(role);
						for (Member var : adderMem) 
							counter.add(var);
						break;
				}
			}
			String mention = "";
			for (Member var : counter) {
				mention += var.getAsMention() + "\n";
			}
			String cmdrole = "";
			for(int i = 1; i < cmd.length; i ++){
				if(i%2==1)
					cmdrole += "<@&" + cmd[i] + "> ";
				else
					cmdrole += cmd[i] + " ";
			}


			String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
					: event.getMember().getEffectiveName();

			LinkedList <String> cacherefresh = new LinkedList<>();

			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("People with ");
			eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
			eb.setColor(1);
			if(mention.length() <= 6000 && mention.length() > 5){
				int dooku = 0;
				while(mention.length() > 1024){
					String submention = mention.substring(0, 1024);
					String[] part = submention.split("\n");
					submention = mention.substring(0, 1024 - part[part.length-1].length());
					eb.addField(""+ dooku, submention, true);
					mention = mention.substring(1024-part[part.length-1].length());
					dooku++;
					cacherefresh.add(submention);
				}
				cacherefresh.add(mention);
				eb.addField(""+dooku, mention, true);
			} else {
				event.getChannel().sendMessage("This is over 6000 chars (or empty), can't send this big messages. Sry!").queue();
				return;
			}
			eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());
			

			String lastone = cacherefresh.removeLast();

			for (String var : cacherefresh) {
				event.getChannel().sendMessage("wait a sec").queue(response -> {
					response.editMessage(var).queue(response2 -> {
						response2.delete().queue();
					});
				});		
			}

			event.getChannel().sendMessage("wait a sec").queue(response -> {
				response.editMessage(lastone).queue(response2 -> {
					response2.delete().queue(response3 -> {
						event.getChannel().sendMessage(eb.build()).queue();
					});
				});
			});	


			message.addReaction(check).queue();
		} else if (content.equals(prefix + "learning")){

			MessageChannel channel = event.getChannel();
				int countUsers = 0;
				String userNames = "";
			for (User tempUser : userMuted.keySet()) {
				countUsers++;
				userNames += tempUser.getAsMention() + ", ";
				
			}


			String shouldbeLearning = "<@!223932775474921472>";

			String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname() : event.getMember().getEffectiveName();

			EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("People who are learning or rather should be!", null);
				eb.setColor(1);
				// eb.setDescription("Nothing to see here.");
				eb.addField("" + countUsers, userNames, false);
				eb.addField("people who should be studying stuff right now",  shouldbeLearning, false);
				// eb.addBlankField(false);
				eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());

			channel.sendMessage(eb.build()).queue();
			
			
		} else if (content.equals(prefix + "source")) {
			MessageChannel channel = event.getChannel();
			channel.sendMessage("<https://polybox.ethz.ch/index.php/s/eP3DJRgTQ0i6uNW>").queue();
			
		} else if (content.equals(prefix + "ping") || content.equals(prefix + "pong")) {

			MessageChannel channel = event.getChannel();
			message.addReaction(check).queue();

			if (content.equals(prefix + "pong"))
				ping = "<:pinged:747783377322508290> Ping!";

			long time = System.currentTimeMillis();
			channel.sendMessage(ping).queue(response -> {
				response.editMessageFormat(ping + ": %d ms", System.currentTimeMillis() - time).queue();
			});


		} else if (content.startsWith(prefix + "nokey ")) {
			MessageChannel channel = event.getChannel();
			int x = Character.getNumericValue(content.charAt(7));
			content = content.substring(9);

			message.addReaction(check).queue();
			channel.sendMessage(VWA_MainEntschluesseln.Viginere(content, x)).queue();

		} else if (content.startsWith(prefix + "crypt ")) {
			MessageChannel channel = event.getChannel();

			content = content.substring(7);

			int i = 0;
			for (; i < content.length(); i++) {
				if (content.charAt(i) == ' ') {
					break;
				}
			}

			String key = content.substring(0, i);
			content = content.substring(i + 1);

			if (cryptdeleter)
				channel.deleteMessageById(message.getId()).queue();
			else
				message.addReaction(check).queue();

			channel.sendMessage(VWA_Verschluesseln.encrypter(content, key)).queue();

		} else if (content.startsWith(prefix + "decrypt ")) {
			MessageChannel channel = event.getChannel();

			content = content.substring(9);

			int i = 0;
			for (; i < content.length(); i++) {
				if (content.charAt(i) == ' ') {
					break;
				}
			}

			String key = content.substring(0, i);
			content = content.substring(i + 1);

			if (cryptdeleter)
				channel.deleteMessageById(message.getId()).queue();
			else
				message.addReaction(check).queue();
			channel.sendMessage(KeyDecrypt.decrypter(content, key)).queue();

		} else if (content.equals(prefix + "wp")) {
			MessageChannel channel = event.getChannel();


			File clientDir = new File("BabysBaby\\babysBabyBanners"); // client Directory.
			Random rand = new Random();

			int numberFiles = clientDir.list().length;
			String randomFile = rand.nextInt(numberFiles) + ".png"; // All files are named with their number and a .
			String filePath = clientDir + "/" + randomFile;

			message.addReaction(check).queue();
			channel.sendFile(new File(filePath)).queue();


		} else if (content.equals(prefix + "help")) {

			

			MessageChannel channel = event.getChannel();
			message.addReaction(check).queue();
			Scanner scanner;
			try {
				scanner = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\From_Old_to_NEW\\VSCODE WORKSPACE\\BabysBaby\\Baby\\src\\help.txt"));

				String msg = "";
				while (scanner.hasNext()) {
					msg += "`" + prefix + scanner.next() + "`" + scanner.nextLine() + "\n";
				}

				String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
						: event.getMember().getEffectiveName();

				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Help, powered by Baby's Baby!", null);

				eb.setColor(1);
				// eb.setDescription("Nothing to see here.");
				eb.addField("Things i can do:", msg, false);
				// eb.addBlankField(false);
				eb.setFooter("Summoned by: " + nickname, event.getAuthor().getAvatarUrl());
				// eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png%22);
				// eb.setThumbnail("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png%22);
				

				//channel.sendFile(new File(filePath)).queue();
				channel.sendMessage(eb.build()).queue();
				//channel.sendFile(new File(filePath2)).queue();
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}

		} else if (content.startsWith(prefix + "rock ")) {
			MessageChannel channel = event.getChannel();

			String[] decision = { "rock", "paper", "scissors" };
			content = content.substring(6);
			boolean goodString = false;
			for (int i = 0; i < decision.length; i++) {
				if (decision[i].equals(content)) {
					goodString = true;
				}
			}
			String result = "";

			if (goodString) {

				boolean badUser = false;
				String user = event.getAuthor().getId();

				for (int i = 0; i < baduser.length; i++) {
					if (baduser[i].equals(user))
						badUser = true;
				}

				if (badUser) {
					if (content.equals(decision[0])) {
						result = decision[1];
					} else if (content.equals(decision[1])) {
						result = decision[2];
					} else {
						result = decision[0];
					}
				} else {
					result = decision[(int) (Math.random() * 3)];
				}

				if (content.equals(result)) {
					result = "The bot has chosen: " + result + " Its a draw...";
				} else if (content.equals(decision[0]) && result.equals(decision[1])
						|| content.equals(decision[1]) && result.equals(decision[2])
						|| content.equals(decision[2]) && result.equals(decision[0])) {
					result = "The bot has chosen: " + result + " The bot has won over: " + content;
				} else {
					result = "The bot has chosen: " + result + " The bot has lost over: " + content;
				}

				channel.sendMessage(result).queue();
			} else {
				channel.sendMessage("You have to type rock, paper or scissors correctly!").queue();
			}
		}

	}
	
	
	class NewLater implements Runnable {
		int y;
		MessageChannel channel; 
		
		public NewLater(int x, MessageChannel channel2) {
			y = 86400 - x;
			channel = channel2;
		}

		public void run() {
			System.out.println(y);
			String unit = "";
			if(y/60 > 0) {
				y = y / 60;
				if(y/60 > 0) {
					y = y / 60;
					unit ="hours";
					if(y==1) {
						unit ="hour";
					}
				} else {
					unit ="minutes";
					if(y==1) {
						unit ="minute";
					}
				}
			} else {
				unit ="seconds";
				if(y==1) {
					unit ="second";
				}
			}
			
			channel.sendMessage(y +  " " + unit + " left!").queue();
			if (y == 0) {
				for (int i = 0; i < 5; i++) {
					channel.sendMessage("Happy New Year everyone!").queue();
				}
			}
		}
	}


	class Later implements Runnable {

		public Later() {
		}

		public void run() {
			author.openPrivateChannel().queue((channel) -> {
				channel.sendMessage("Do it now!").queue();
			});
		}
	}

	class clockTower implements Runnable {
		String time;
		Guild eth;

		public clockTower(String s, Guild g) {
			time = s;
			eth = g;
		}

		public void run() {
			timenow  = new File("C:\\Users\\Lukas\\Desktop\\PlacePrint\\Pictures\\clock\\" + time + ".png");
			timerchange = true;
			eth.getTextChannelById("819966095070330950").sendMessage(".place pixelverify 900 720").queue();
			eth.getTextChannelById("819966095070330950").sendMessage("+clock").queue();
		}
	}


	class GetUnmute implements Runnable {
		User muted;
		Guild guild;

		public GetUnmute(User user, Guild tempG) {
			muted = user;
			guild = tempG;
		}

		public void run() {	

			muted.openPrivateChannel().queue((channel) -> {
				channel.sendMessage("You shall be unmuted! Hope this worked...").queue();
			});

			Role muteR = guild.getRoleById(stfuETH);
			guild.removeRoleFromMember(guild.retrieveMember(muted).complete(), muteR).queue();

			userMuted.remove(muted);
		}
	}

	class schribollie implements Runnable {
		User ollie;


		public schribollie(User user) {
			ollie = user;
		}

		public void run() {	
			ollie.openPrivateChannel().queue((channel) -> {
				channel.sendMessage("Dis Mami (This message is sponsored by Modernwarfare Aaron").queue();
			});
		}
	}


	class AddRoles implements Runnable {
		User muted;
		Guild guild;

		public AddRoles(User user, Guild tempG) {
			muted = user;
			guild = tempG;
		}

		public void run() {	


			muted.openPrivateChannel().queue((channel) -> {
				channel.sendMessage("You shall be unmuted! And got all your roles! If there any problems, pls them Lukas").queue();
			});

			Role muteR = guild.getRoleById(stfuETH);
			guild.removeRoleFromMember(guild.retrieveMember(muted).complete(), muteR).queue();

			userMuted.remove(muted);
		}
	}



	public static boolean poly(String content) {

		String[] suffix = { "a", "d", "e", "l", "k", "kay", "a s", "d s", "l s" };

		if (content.equals(prefix + "poly") || content.equals(prefix + "p")) {
			return true;
		}

		for (int i = 0; i < suffix.length; i++) {
			if (content.equals(prefix + "p " + suffix[i]) || content.equals(prefix + "poly " + suffix[i])) {
				return true;
			}
		}
		return false;
	}
	
}

class Pair {
	private int x;
	private int y;

	public void place(int a, int b){
		x=a;
		y=b;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}

}
*/