package co.ipb.adukerang.controller;

/**
 * Created by winnerawan on 3/23/16.
 */
public class AppConfig {

    public static String URL_REGIST         = "http://api.winnerawan.net/adukerang/";
    public static String URL_GET_KELUHAN    = "http://api.winnerawan.net/adukerang/get/keluhan/";
    //public static String URL_GET_RUANG    = "http://api.winnerawan.net/adukerang/get/ruang/";
    public static String URL_GET_ID_RUANG   = "http://api.winnerawan.net/adukerang/get/ruang/index.php?id_ruang=";
    public static String URL_GET_BARANG     = "http://api.winnerawan.net/adukerang/get/barang/index.php?id_barang=";
    public static String URL_SET_KELUHAN    = "http://api.winnerawan.net/adukerang/set/foto/upload.php";
    public static String URL_GET_KELUHANKU  = "http://api.winnerawan.net/adukerang/get/keluhan/by/user/?unique_id=";
    public static String URL_TEKNISI_LOGIN  = "http://api.winnerawan.net/adukerang/log/teknisi/";
    public static String URL_GET_TEKNISI_ID = "http://api.winnerawan.net/adukerang/get/teknisi/by/barang/?id_barang=";
    public static String URL_SEND_NOTIF     = "http://api.winnerawan.net/adukerang/gcm/notif/?id=";
    public static String URL_TEKNISI_NOTIF  = "http://api.winnerawan.net/adukerang/teknisi/get/keluhan/";
    public static String URL_GET_GCM_USER   = "http://api.winnerawan.net/adukerang/get/gcm/user/?unique_id=";
    public static String URL_UPDATE_KELUHAN = "http://api.winnerawan.net/adukerang/update/keluhan/?status=";
    public static String URL_TUTUP_ADUAN    = "http://api.winnerawan.net/adukerang/tutup/keluhan/";
    public static String URL_SET_AVATAR     = "http://api.winnerawan.net/adukerang/update/user/upload.php";
    public static String URL_HOME_TEKNISI   = "http://api.winnerawan.net/adukerang/get/keluhan/teknisi/?teknisi_id=";
    //get teknisi id by id_barang
    //gcm test
    public static String URL_GCM            = "http://api.winnerawan.net/adukerang/GCM.php";
    public static final String SENDER_ID    = "280597259588";
    public static final String SERVER_URL   = "http://api.winnerawan.net/adukerang/";
}
