package com.sd.spartan.easyhealth.model;

public class SpecialistModel {
    public String specialist_id, specialist_name_ban, specialist_name_eng, doc_id ;

    public SpecialistModel() {
    }

    public SpecialistModel(String specialist_id, String specialist_name_ban, String specialist_name_eng, String doc_id) {
        this.specialist_id = specialist_id;
        this.specialist_name_ban = specialist_name_ban;
        this.specialist_name_eng = specialist_name_eng;
        this.doc_id = doc_id;
    }

    public String getSpecialist_id() {
        return specialist_id;
    }

    public String getSpecialist_name_ban() {
        return specialist_name_ban;
    }

    public String getSpecialist_name_eng() {
        return specialist_name_eng;
    }

    public String getDoc_id() {
        return doc_id;
    }

}
