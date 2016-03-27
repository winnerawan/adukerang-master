package co.ipb.adukerang.model;

/**
 * Created by winnerawan on 3/26/16.
 */
public class Keluhan {
    public String id_ruang;
    public String id_keluhan;
    public String id_barang;
    public String id_teknisi;
    public int uid;
    public String keluhan;
    public String foto;
    public String status;
    public String keterangan;

    public Keluhan() {

    }

    public Keluhan(String id_ruang, String id_keluhan, String id_barang, String id_teknisi,
                    int uid, String keluhan, String foto, String status, String keterangan) {

        this.id_ruang=id_ruang;
        this.id_keluhan=id_keluhan;
        this.id_barang=id_barang;
        this.id_teknisi=id_teknisi;
        this.uid=uid;
        this.keluhan=keluhan;
        this.foto=foto;
        this.status=status;
        this.keterangan=keterangan;
    }

    public String getId_ruang() {
        return id_ruang;
    }

    public void setId_ruang(String id_ruang) {
        this.id_ruang = id_ruang;
    }

    public String getId_keluhan() {
        return id_keluhan;
    }

    public void setId_keluhan(String id_keluhan) {
        this.id_keluhan = id_keluhan;
    }

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public String getId_teknisi() {
        return id_teknisi;
    }

    public void setId_teknisi(String id_teknisi) {
        this.id_teknisi = id_teknisi;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
