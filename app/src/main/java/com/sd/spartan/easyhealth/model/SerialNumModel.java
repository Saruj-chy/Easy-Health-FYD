package com.sd.spartan.easyhealth.model;

public class SerialNumModel {
    public String diag_serial_id, serial_num_ban, serial_num_eng, diag_id ;


    public SerialNumModel(String serial_num_ban, String serial_num_eng, String diag_id) {
        this.serial_num_ban = serial_num_ban;
        this.serial_num_eng = serial_num_eng;
        this.diag_id = diag_id;
    }

    public SerialNumModel(String diag_serial_id, String serial_num_ban, String serial_num_eng, String diag_id) {
        this.diag_serial_id = diag_serial_id;
        this.serial_num_ban = serial_num_ban;
        this.serial_num_eng = serial_num_eng;
        this.diag_id = diag_id;
    }

    public String getDiag_serial_id() {
        return diag_serial_id;
    }

    public String getSerial_num_ban() {
        return serial_num_ban;
    }

    public String getSerial_num_eng() {
        return serial_num_eng;
    }

    public String getDiag_id() {
        return diag_id;
    }
}
