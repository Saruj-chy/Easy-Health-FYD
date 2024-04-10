package com.sd.spartan.easyhealth.model;

public class InsertModel {
    public String doc_code, doc_name_eng, doc_name_ban, degree_eng, degree_ban, work_place_eng, work_place_ban ;
    public String week_name_eng, week_name_ban, time_name_from_eng, time_name_from_ban, time_from_eng, time_from_ban, time_name_to_eng, time_name_to_ban,
            time_to_eng, time_to_ban, doc_present, subdistrict_id ;

    public String specialist_eng, specialist_ban ;


    public InsertModel(String doc_code, String doc_name_eng, String doc_name_ban, String degree_eng, String degree_ban,
                       String work_place_eng, String work_place_ban, String specialist_eng, String specialist_ban) {
        this.doc_code = doc_code;
        this.doc_name_eng = doc_name_eng;
        this.doc_name_ban = doc_name_ban;
        this.degree_eng = degree_eng;
        this.degree_ban = degree_ban;
        this.work_place_eng = work_place_eng;
        this.work_place_ban = work_place_ban;
        this.specialist_eng = specialist_eng;
        this.specialist_ban = specialist_ban;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
    }

    public String getDoc_name_eng() {
        return doc_name_eng;
    }

    public void setDoc_name_eng(String doc_name_eng) {
        this.doc_name_eng = doc_name_eng;
    }

    public String getDoc_name_ban() {
        return doc_name_ban;
    }

    public void setDoc_name_ban(String doc_name_ban) {
        this.doc_name_ban = doc_name_ban;
    }

    public String getDegree_eng() {
        return degree_eng;
    }

    public void setDegree_eng(String degree_eng) {
        this.degree_eng = degree_eng;
    }

    public String getDegree_ban() {
        return degree_ban;
    }

    public void setDegree_ban(String degree_ban) {
        this.degree_ban = degree_ban;
    }

    public String getWork_place_eng() {
        return work_place_eng;
    }

    public void setWork_place_eng(String work_place_eng) {
        this.work_place_eng = work_place_eng;
    }

    public String getWork_place_ban() {
        return work_place_ban;
    }

    public void setWork_place_ban(String work_place_ban) {
        this.work_place_ban = work_place_ban;
    }

    public String getWeek_name_eng() {
        return week_name_eng;
    }

    public void setWeek_name_eng(String week_name_eng) {
        this.week_name_eng = week_name_eng;
    }

    public String getWeek_name_ban() {
        return week_name_ban;
    }

    public void setWeek_name_ban(String week_name_ban) {
        this.week_name_ban = week_name_ban;
    }

    public String getTime_name_from_eng() {
        return time_name_from_eng;
    }

    public void setTime_name_from_eng(String time_name_from_eng) {
        this.time_name_from_eng = time_name_from_eng;
    }

    public String getTime_name_from_ban() {
        return time_name_from_ban;
    }

    public void setTime_name_from_ban(String time_name_from_ban) {
        this.time_name_from_ban = time_name_from_ban;
    }

    public String getTime_from_eng() {
        return time_from_eng;
    }

    public void setTime_from_eng(String time_from_eng) {
        this.time_from_eng = time_from_eng;
    }

    public String getTime_from_ban() {
        return time_from_ban;
    }

    public void setTime_from_ban(String time_from_ban) {
        this.time_from_ban = time_from_ban;
    }

    public String getTime_name_to_eng() {
        return time_name_to_eng;
    }

    public void setTime_name_to_eng(String time_name_to_eng) {
        this.time_name_to_eng = time_name_to_eng;
    }

    public String getTime_name_to_ban() {
        return time_name_to_ban;
    }

    public void setTime_name_to_ban(String time_name_to_ban) {
        this.time_name_to_ban = time_name_to_ban;
    }

    public String getTime_to_eng() {
        return time_to_eng;
    }

    public void setTime_to_eng(String time_to_eng) {
        this.time_to_eng = time_to_eng;
    }

    public String getTime_to_ban() {
        return time_to_ban;
    }

    public void setTime_to_ban(String time_to_ban) {
        this.time_to_ban = time_to_ban;
    }

    public String getDoc_present() {
        return doc_present;
    }

    public void setDoc_present(String doc_present) {
        this.doc_present = doc_present;
    }

    public String getSubdistrict_id() {
        return subdistrict_id;
    }

    public void setSubdistrict_id(String subdistrict_id) {
        this.subdistrict_id = subdistrict_id;
    }

    public String getSpecialist_eng() {
        return specialist_eng;
    }

    public void setSpecialist_eng(String specialist_eng) {
        this.specialist_eng = specialist_eng;
    }

    public String getSpecialist_ban() {
        return specialist_ban;
    }

    public void setSpecialist_ban(String specialist_ban) {
        this.specialist_ban = specialist_ban;
    }

}
