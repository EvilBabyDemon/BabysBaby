package BabyBaby.data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import BabyBaby.Command.ISlashCMD;

public class Data {
    public static boolean antibamboozle = true;
    // TODO Remove msgid and check on msgToChan
    public static HashSet<String> msgid = new HashSet<>();
    public static HashMap<String, ArrayList<String>> catToMsg = new HashMap<>();
    public static HashMap<String, String> msgToChan = new HashMap<>();
    public static HashMap<String, String> emoteassign = new HashMap<>();
    public static HashSet<String> roles = new HashSet<>();
    public static final String check = ":checkmark:769279808244809798";
    public static final String xmark = ":xmark:769279807916998728";
    public static final String db = "jdbc:sqlite:testone.db";
    public static final String modlog = "774322031688679454";
    public static final String adminlog = "774322847812157450";
    public static final String ADMIN_BOT_ID = "747768907992924192";
    public static final String ADMIN_ID = "747753814723002500";
    public static final String MODERATOR_ID = "815932497920917514";
    public static final String SERVERBOT_ID = "750439532050251778";
    public static final String ETH_ID = "747752542741725244";
    public static final String SPAM_ID = "768600365602963496";
    public static final String BLIND_ID = "844136589163626526";
    public static OffsetDateTime kick;
    public static OffsetDateTime ban;
    public static final String myselfID = "223932775474921472";
    public static final String dcvd = "306523617188118528";
    public static final String ethstudent = "747786383317532823";
    public static final String ethexternal = "767315361443741717";
    public static final String ETH_NEWCOMERS_CH_ID = "815881148307210260";
    public static final String BOTS_BATTROYAL = "783818541849378867";
    public static boolean elthision = false;
    public static boolean marc = false;
    public static HashSet<String> buttonid = new HashSet<>();
    public static int mydel = 0;
    public static int otherdel = 0;
    public static HashMap<String, Integer> cmdUses = new HashMap<>();
    public static HashSet<String> users = new HashSet<>();
    public static OffsetDateTime startUp;
    public static boolean automaticRoleAddThingy = false;
    public static int slashAndButton = 0;
    public static ArrayList<ISlashCMD> slashcmds = new ArrayList<>();

    public static int[] covid = new int[15];

    // Filepaths
    public static final String TOKEN = "token.txt";
    public static final String SUGGESTION = "suggestions.txt";
    public static final String PLACE = "place/";
}