package com.sd.spartan.easyhealth.model;

import java.util.ArrayList;
import java.util.List;

public class ClassBuilder {
    private String diag_id, diag_code, diag_name_eng, diag_name_ban, diag_address_eng, diag_address_ban, diag_contact, diag_website, diag_email ;
    private String banner_id, banner_img, banner_num ;
    private String diag_doc_id, doc_time_detail_eng, doc_time_detail_ban, doc_time_input_type, month_name_eng, month_name_ban, week_name_eng, week_name_ban, time_name_from_eng, time_name_from_ban,
            time_from_eng, time_from_ban, time_name_to_eng, time_name_to_ban, time_to_eng, time_to_ban, week_time_txt, extra_week_time_eng, extra_week_time_ban, doc_website, doc_visit_fee, doc_cmnt_eng, doc_cmnt_ban, doc_present, update_num ;
    private String notify_id, notify_title, notify_msg, notify_msg_show ;
    private String diag_serial_id, serial_num_eng, serial_num_ban ;
    private String district_id, district_ban, district_eng ;
    private String division_id, division_eng, division_ban ;
    private String subdistrict_id, subdistrict_ban, subdistrict_eng ;
    private String doc_id, doc_code, doc_diag_total_count, doc_name_eng, doc_name_ban, degree_eng, degree_ban, work_place_eng, work_place_ban ;
    private String doc_serial_id, doc_serial_num_eng, doc_serial_num_ban ;
    private String favourite_id, client_id ;
    private String tutorial_id, tutorial_title, tutorial_details, tutorial_video_id, tutorial_state_num ;
    private String specialist_id, specialist_name_ban, specialist_name_eng ;
    private String click_id, total_click_count, current_date ;
    private String e_id, e_msg, e_reply ;


    private String reg_user_id, user_name, user_pass, user_phn_num, user_email, user_state ;
    private String reg_diag_id, diag_username, diag_password, admin_status ;
    public List<SpecialistModel> spe_list = new ArrayList<>() ;
    public List<SerialNumModel> ser_list = new ArrayList<>() ;

    public ClassBuilder setDiag_id(String diag_id) {
        this.diag_id = diag_id;
        return this;
    }



    public ClassBuilder setDiag_code(String diag_code) {
        this.diag_code = diag_code;
        return this;
    }

    public ClassBuilder setDiag_name_eng(String diag_name_eng) {
        this.diag_name_eng = diag_name_eng;
        return this;
    }

    public ClassBuilder setDiag_name_ban(String diag_name_ban) {
        this.diag_name_ban = diag_name_ban;
        return this;
    }

    public ClassBuilder setDiag_address_eng(String diag_address_eng) {
        this.diag_address_eng = diag_address_eng;
        return this;
    }

    public ClassBuilder setDiag_address_ban(String diag_address_ban) {
        this.diag_address_ban = diag_address_ban;
        return this;
    }

    public ClassBuilder setDiag_contact(String diag_contact) {
        this.diag_contact = diag_contact;
        return this;
    }

    public ClassBuilder setDiag_website(String diag_website) {
        this.diag_website = diag_website;
        return this;
    }

    public ClassBuilder setDiag_email(String diag_email) {
        this.diag_email = diag_email;
        return this;
    }

    public ClassBuilder setBanner_id(String banner_id) {
        this.banner_id = banner_id;
        return this;
    }

    public ClassBuilder setBanner_img(String banner_img) {
        this.banner_img = banner_img;
        return this;
    }

    public ClassBuilder setBanner_num(String banner_num) {
        this.banner_num = banner_num;
        return this;
    }

    public ClassBuilder setDiag_doc_id(String diag_doc_id) {
        this.diag_doc_id = diag_doc_id;
        return this;
    }

    public ClassBuilder setDoc_time_detail_eng(String doc_time_detail_eng) {
        this.doc_time_detail_eng = doc_time_detail_eng;
        return this;
    }

    public ClassBuilder setDoc_time_detail_ban(String doc_time_detail_ban) {
        this.doc_time_detail_ban = doc_time_detail_ban;
        return this;
    }

    public ClassBuilder setDoc_time_input_type(String doc_time_input_type) {
        this.doc_time_input_type = doc_time_input_type;
        return this;
    }

    public ClassBuilder setMonth_name_eng(String month_name_eng) {
        this.month_name_eng = month_name_eng;
        return this;
    }

    public ClassBuilder setMonth_name_ban(String month_name_ban) {
        this.month_name_ban = month_name_ban;
        return this;
    }

    public ClassBuilder setWeek_name_eng(String week_name_eng) {
        this.week_name_eng = week_name_eng;
        return this;
    }

    public ClassBuilder setWeek_name_ban(String week_name_ban) {
        this.week_name_ban = week_name_ban;
        return this;
    }

    public ClassBuilder setTime_name_from_eng(String time_name_from_eng) {
        this.time_name_from_eng = time_name_from_eng;
        return this;
    }

    public ClassBuilder setTime_name_from_ban(String time_name_from_ban) {
        this.time_name_from_ban = time_name_from_ban;
        return this;
    }

    public ClassBuilder setTime_from_eng(String time_from_eng) {
        this.time_from_eng = time_from_eng;
        return this;
    }

    public ClassBuilder setTime_from_ban(String time_from_ban) {
        this.time_from_ban = time_from_ban;
        return this;
    }

    public ClassBuilder setTime_name_to_eng(String time_name_to_eng) {
        this.time_name_to_eng = time_name_to_eng;
        return this;
    }

    public ClassBuilder setTime_name_to_ban(String time_name_to_ban) {
        this.time_name_to_ban = time_name_to_ban;
        return this;
    }

    public ClassBuilder setTime_to_eng(String time_to_eng) {
        this.time_to_eng = time_to_eng;
        return this;
    }

    public ClassBuilder setTime_to_ban(String time_to_ban) {
        this.time_to_ban = time_to_ban;
        return this;
    }

    public ClassBuilder setWeek_time_txt(String week_time_txt) {
        this.week_time_txt = week_time_txt;
        return this;
    }

    public ClassBuilder setExtra_week_time_eng(String extra_week_time_eng) {
        this.extra_week_time_eng = extra_week_time_eng;
        return this;
    }

    public ClassBuilder setExtra_week_time_ban(String extra_week_time_ban) {
        this.extra_week_time_ban = extra_week_time_ban;
        return this;
    }

    public ClassBuilder setDoc_website(String doc_website) {
        this.doc_website = doc_website;
        return this;
    }

    public ClassBuilder setDoc_visit_fee(String doc_visit_fee) {
        this.doc_visit_fee = doc_visit_fee;
        return this;
    }

    public ClassBuilder setDoc_cmnt_eng(String doc_cmnt_eng) {
        this.doc_cmnt_eng = doc_cmnt_eng;
        return this;
    }

    public ClassBuilder setDoc_cmnt_ban(String doc_cmnt_ban) {
        this.doc_cmnt_ban = doc_cmnt_ban;
        return this;
    }

    public ClassBuilder setDoc_present(String doc_present) {
        this.doc_present = doc_present;
        return this;
    }

    public ClassBuilder setUpdate_num(String update_num) {
        this.update_num = update_num;
        return this;
    }

    public ClassBuilder setNotify_id(String notify_id) {
        this.notify_id = notify_id;
        return this;
    }

    public ClassBuilder setNotify_title(String notify_title) {
        this.notify_title = notify_title;
        return this;
    }

    public ClassBuilder setNotify_msg(String notify_msg) {
        this.notify_msg = notify_msg;
        return this;
    }

    public ClassBuilder setNotify_msg_show(String notify_msg_show) {
        this.notify_msg_show = notify_msg_show;
        return this;
    }

    public ClassBuilder setDiag_serial_id(String diag_serial_id) {
        this.diag_serial_id = diag_serial_id;
        return this;
    }

    public ClassBuilder setSerial_num_eng(String serial_num_eng) {
        this.serial_num_eng = serial_num_eng;
        return this;
    }

    public ClassBuilder setSerial_num_ban(String serial_num_ban) {
        this.serial_num_ban = serial_num_ban;
        return this;
    }

    public ClassBuilder setDistrict_id(String district_id) {
        this.district_id = district_id;
        return this;
    }

    public ClassBuilder setDistrict_ban(String district_ban) {
        this.district_ban = district_ban;
        return this;
    }

    public ClassBuilder setDistrict_eng(String district_eng) {
        this.district_eng = district_eng;
        return this;
    }

    public ClassBuilder setDivision_id(String division_id) {
        this.division_id = division_id;
        return this;
    }

    public ClassBuilder setDivision_eng(String division_eng) {
        this.division_eng = division_eng;
        return this;
    }

    public ClassBuilder setDivision_ban(String division_ban) {
        this.division_ban = division_ban;
        return this;
    }

    public ClassBuilder setSubdistrict_id(String subdistrict_id) {
        this.subdistrict_id = subdistrict_id;
        return this;
    }

    public ClassBuilder setSubdistrict_ban(String subdistrict_ban) {
        this.subdistrict_ban = subdistrict_ban;
        return this;
    }

    public ClassBuilder setSubdistrict_eng(String subdistrict_eng) {
        this.subdistrict_eng = subdistrict_eng;
        return this;
    }

    public ClassBuilder setDoc_id(String doc_id) {
        this.doc_id = doc_id;
        return this;
    }

    public ClassBuilder setDoc_code(String doc_code) {
        this.doc_code = doc_code;
        return this;
    }

    public ClassBuilder setDoc_diag_total_count(String doc_diag_total_count) {
        this.doc_diag_total_count = doc_diag_total_count;
        return this;
    }

    public ClassBuilder setDoc_name_eng(String doc_name_eng) {
        this.doc_name_eng = doc_name_eng;
        return this;
    }

    public ClassBuilder setDoc_name_ban(String doc_name_ban) {
        this.doc_name_ban = doc_name_ban;
        return this;
    }

    public ClassBuilder setDegree_eng(String degree_eng) {
        this.degree_eng = degree_eng;
        return this;
    }

    public ClassBuilder setDegree_ban(String degree_ban) {
        this.degree_ban = degree_ban;
        return this;
    }

    public ClassBuilder setWork_place_eng(String work_place_eng) {
        this.work_place_eng = work_place_eng;
        return this;
    }

    public ClassBuilder setWork_place_ban(String work_place_ban) {
        this.work_place_ban = work_place_ban;
        return this;
    }

    public ClassBuilder setDoc_serial_id(String doc_serial_id) {
        this.doc_serial_id = doc_serial_id;
        return this;
    }

    public ClassBuilder setDoc_serial_num_eng(String doc_serial_num_eng) {
        this.doc_serial_num_eng = doc_serial_num_eng;
        return this;
    }

    public ClassBuilder setDoc_serial_num_ban(String doc_serial_num_ban) {
        this.doc_serial_num_ban = doc_serial_num_ban;
        return this;
    }

    public ClassBuilder setFavourite_id(String favourite_id) {
        this.favourite_id = favourite_id;
        return this;
    }

    public ClassBuilder setClient_id(String client_id) {
        this.client_id = client_id;
        return this;
    }

    public ClassBuilder setTutorial_id(String tutorial_id) {
        this.tutorial_id = tutorial_id;
        return this;
    }

    public ClassBuilder setTutorial_title(String tutorial_title) {
        this.tutorial_title = tutorial_title;
        return this;
    }

    public ClassBuilder setTutorial_details(String tutorial_details) {
        this.tutorial_details = tutorial_details;
        return this;
    }

    public ClassBuilder setTutorial_video_id(String tutorial_video_id) {
        this.tutorial_video_id = tutorial_video_id;
        return this;
    }

    public ClassBuilder setTutorial_state_num(String tutorial_state_num) {
        this.tutorial_state_num = tutorial_state_num;
        return this;
    }

    public ClassBuilder setSpecialist_id(String specialist_id) {
        this.specialist_id = specialist_id;
        return this;
    }

    public ClassBuilder setSpecialist_name_ban(String specialist_name_ban) {
        this.specialist_name_ban = specialist_name_ban;
        return this;
    }

    public ClassBuilder setSpecialist_name_eng(String specialist_name_eng) {
        this.specialist_name_eng = specialist_name_eng;
        return this;
    }

    public ClassBuilder setClick_id(String click_id) {
        this.click_id = click_id;
        return this;
    }

    public ClassBuilder setTotal_click_count(String total_click_count) {
        this.total_click_count = total_click_count;
        return this;
    }

    public ClassBuilder setCurrent_date(String current_date) {
        this.current_date = current_date;
        return this;
    }

    public ClassBuilder setReg_user_id(String reg_user_id) {
        this.reg_user_id = reg_user_id;
        return this;
    }

    public ClassBuilder setUser_name(String user_name) {
        this.user_name = user_name;
        return this;
    }

    public ClassBuilder setUser_pass(String user_pass) {
        this.user_pass = user_pass;
        return this;
    }

    public ClassBuilder setUser_phn_num(String user_phn_num) {
        this.user_phn_num = user_phn_num;
        return this;
    }

    public ClassBuilder setUser_email(String user_email) {
        this.user_email = user_email;
        return this;
    }

    public ClassBuilder setUser_state(String user_state) {
        this.user_state = user_state;
        return this;
    }

    public ClassBuilder setReg_diag_id(String reg_diag_id) {
        this.reg_diag_id = reg_diag_id;
        return this;
    }

    public ClassBuilder setDiag_username(String diag_username) {
        this.diag_username = diag_username;
        return this;
    }

    public ClassBuilder setDiag_password(String diag_password) {
        this.diag_password = diag_password;
        return this;
    }

    public ClassBuilder setAdmin_status(String admin_status) {
        this.admin_status = admin_status;
        return this;
    }

    public ClassBuilder setE_id(String e_id) {
        this.e_id = e_id;
        return this;
    }

    public ClassBuilder setE_msg(String e_msg) {
        this.e_msg = e_msg;
        return this;
    }

    public ClassBuilder setE_reply(String e_reply) {
        this.e_reply = e_reply;
        return this;
    }

    public ClassBuilder setSpe_list(List<SpecialistModel> spe_list) {
        this.spe_list = spe_list;
        return this;
    }

    public ClassBuilder setSer_list(List<SerialNumModel> ser_list) {
        this.ser_list = ser_list;
        return this;
    }

    public BuilderModel build(){
        return new BuilderModel( diag_id,  diag_code,  diag_name_eng,  diag_name_ban,  diag_address_eng,  diag_address_ban,  diag_contact,  diag_website,  diag_email,  banner_id,  banner_img,  banner_num,  diag_doc_id,  doc_time_detail_eng,  doc_time_detail_ban, doc_time_input_type,  month_name_eng,  month_name_ban,  week_name_eng,  week_name_ban,  time_name_from_eng,  time_name_from_ban,  time_from_eng,  time_from_ban,  time_name_to_eng,  time_name_to_ban,  time_to_eng,  time_to_ban,  week_time_txt,  extra_week_time_eng,  extra_week_time_ban,  doc_website,  doc_visit_fee,  doc_cmnt_eng,  doc_cmnt_ban,  doc_present,  update_num,  notify_id,  notify_title,  notify_msg,  notify_msg_show,  diag_serial_id,  serial_num_eng,  serial_num_ban,  district_id,  district_ban,  district_eng,  division_id,  division_eng,  division_ban,  subdistrict_id,  subdistrict_ban,  subdistrict_eng,  doc_id,  doc_code,  doc_diag_total_count,  doc_name_eng,  doc_name_ban,  degree_eng,  degree_ban,  work_place_eng,  work_place_ban,  doc_serial_id,  doc_serial_num_eng,  doc_serial_num_ban,  favourite_id,  client_id,  tutorial_id,  tutorial_title,  tutorial_details,  tutorial_video_id,  tutorial_state_num,  specialist_id,  specialist_name_ban,  specialist_name_eng,  click_id,  total_click_count,  current_date,  reg_user_id,  user_name,  user_pass,  user_phn_num,  user_email,  user_state,  reg_diag_id,  diag_username,  diag_password, admin_status, e_id, e_msg, e_reply, spe_list, ser_list);
    }

}
