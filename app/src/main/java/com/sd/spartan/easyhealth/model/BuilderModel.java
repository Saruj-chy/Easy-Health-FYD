package com.sd.spartan.easyhealth.model;

import java.util.List;

public class BuilderModel {
    private final String diag_id, diag_code, diag_name_eng, diag_name_ban, diag_address_eng, diag_address_ban, diag_contact, diag_website, diag_email ;
    private final String banner_id, banner_img, banner_num ;
    private final String diag_doc_id, doc_time_detail_eng, doc_time_detail_ban, doc_time_input_type, month_name_eng, month_name_ban, week_name_eng, week_name_ban, time_name_from_eng, time_name_from_ban,
            time_from_eng, time_from_ban, time_name_to_eng, time_name_to_ban, time_to_eng, time_to_ban, week_time_txt, extra_week_time_eng, extra_week_time_ban, doc_website, doc_visit_fee, doc_cmnt_eng, doc_cmnt_ban, doc_present, update_num ;
    private final String notify_id, notify_title, notify_msg, notify_msg_show ;
    private final String diag_serial_id, serial_num_eng, serial_num_ban ;
    private final String district_id, district_ban, district_eng ;
    private final String division_id, division_eng, division_ban ;
    private final String subdistrict_id, subdistrict_ban, subdistrict_eng ;
    private final String doc_id, doc_code, doc_diag_total_count, doc_name_eng, doc_name_ban, degree_eng, degree_ban, work_place_eng, work_place_ban ;
    private final String doc_serial_id, doc_serial_num_eng, doc_serial_num_ban ;
    private final String favourite_id, client_id ;
    private final String tutorial_id, tutorial_title, tutorial_details, tutorial_video_id, tutorial_state_num ;
    private final String specialist_id, specialist_name_ban, specialist_name_eng ;
    private final String click_id, total_click_count, current_date ;
    private final String e_id, e_msg, e_reply ;


    private final String reg_user_id;
    private final String user_name;
    private final String user_pass;
    private final String user_phn_num;
    private final String user_email;
    private final String user_state ;
    private final String reg_diag_id;
    private final String diag_username;
    private final String diag_password;
    private final String admin_status ;
    public List<SpecialistModel> spe_list;
    public List<SerialNumModel> ser_list;


    public BuilderModel(String diag_id, String diag_code, String diag_name_eng, String diag_name_ban, String diag_address_eng, String diag_address_ban, String diag_contact, String diag_website, String diag_email, String banner_id, String banner_img, String banner_num, String diag_doc_id, String doc_time_detail_eng, String doc_time_detail_ban, String doc_time_input_type, String month_name_eng, String month_name_ban, String week_name_eng, String week_name_ban, String time_name_from_eng, String time_name_from_ban, String time_from_eng, String time_from_ban, String time_name_to_eng, String time_name_to_ban, String time_to_eng, String time_to_ban, String week_time_txt, String extra_week_time_eng, String extra_week_time_ban, String doc_website, String doc_visit_fee, String doc_cmnt_eng, String doc_cmnt_ban, String doc_present, String update_num, String notify_id, String notify_title, String notify_msg, String notify_msg_show, String diag_serial_id, String serial_num_eng, String serial_num_ban, String district_id, String district_ban, String district_eng, String division_id, String division_eng, String division_ban, String subdistrict_id, String subdistrict_ban, String subdistrict_eng, String doc_id, String doc_code, String doc_diag_total_count, String doc_name_eng, String doc_name_ban, String degree_eng, String degree_ban, String work_place_eng, String work_place_ban, String doc_serial_id, String doc_serial_num_eng, String doc_serial_num_ban, String favourite_id, String client_id, String tutorial_id, String tutorial_title, String tutorial_details, String tutorial_video_id, String tutorial_state_num, String specialist_id, String specialist_name_ban, String specialist_name_eng, String click_id, String total_click_count, String current_date, String reg_user_id, String user_name, String user_pass, String user_phn_num, String user_email, String user_state, String reg_diag_id, String diag_username, String diag_password, String admin_status, String e_id, String e_msg, String e_reply, List<SpecialistModel> spe_list, List<SerialNumModel> ser_list) {

        this.diag_id = diag_id;
        this.diag_code = diag_code;
        this.diag_name_eng = diag_name_eng;
        this.diag_name_ban = diag_name_ban;
        this.diag_address_eng = diag_address_eng;
        this.diag_address_ban = diag_address_ban;
        this.diag_contact = diag_contact;
        this.diag_website = diag_website;
        this.diag_email = diag_email;
        this.banner_id = banner_id;
        this.banner_img = banner_img;
        this.banner_num = banner_num;
        this.diag_doc_id = diag_doc_id;
        this.doc_time_detail_eng = doc_time_detail_eng;
        this.doc_time_detail_ban = doc_time_detail_ban;
        this.doc_time_input_type = doc_time_input_type;
        this.month_name_eng = month_name_eng;
        this.month_name_ban = month_name_ban;
        this.week_name_eng = week_name_eng;
        this.week_name_ban = week_name_ban;
        this.time_name_from_eng = time_name_from_eng;
        this.time_name_from_ban = time_name_from_ban;
        this.time_from_eng = time_from_eng;
        this.time_from_ban = time_from_ban;
        this.time_name_to_eng = time_name_to_eng;
        this.time_name_to_ban = time_name_to_ban;
        this.time_to_eng = time_to_eng;
        this.time_to_ban = time_to_ban;
        this.week_time_txt = week_time_txt;
        this.extra_week_time_eng = extra_week_time_eng;
        this.extra_week_time_ban = extra_week_time_ban;
        this.doc_website = doc_website;
        this.doc_visit_fee = doc_visit_fee;
        this.doc_cmnt_eng = doc_cmnt_eng;
        this.doc_cmnt_ban = doc_cmnt_ban;
        this.doc_present = doc_present;
        this.update_num = update_num;
        this.notify_id = notify_id;
        this.notify_title = notify_title;
        this.notify_msg = notify_msg;
        this.notify_msg_show = notify_msg_show;
        this.diag_serial_id = diag_serial_id;
        this.serial_num_eng = serial_num_eng;
        this.serial_num_ban = serial_num_ban;
        this.district_id = district_id;
        this.district_ban = district_ban;
        this.district_eng = district_eng;
        this.division_id = division_id;
        this.division_eng = division_eng;
        this.division_ban = division_ban;
        this.subdistrict_id = subdistrict_id;
        this.subdistrict_ban = subdistrict_ban;
        this.subdistrict_eng = subdistrict_eng;
        this.doc_id = doc_id;
        this.doc_code = doc_code;
        this.doc_diag_total_count = doc_diag_total_count;
        this.doc_name_eng = doc_name_eng;
        this.doc_name_ban = doc_name_ban;
        this.degree_eng = degree_eng;
        this.degree_ban = degree_ban;
        this.work_place_eng = work_place_eng;
        this.work_place_ban = work_place_ban;
        this.doc_serial_id = doc_serial_id;
        this.doc_serial_num_eng = doc_serial_num_eng;
        this.doc_serial_num_ban = doc_serial_num_ban;
        this.favourite_id = favourite_id;
        this.client_id = client_id;
        this.tutorial_id = tutorial_id;
        this.tutorial_title = tutorial_title;
        this.tutorial_details = tutorial_details;
        this.tutorial_video_id = tutorial_video_id;
        this.tutorial_state_num = tutorial_state_num;
        this.specialist_id = specialist_id;
        this.specialist_name_ban = specialist_name_ban;
        this.specialist_name_eng = specialist_name_eng;
        this.click_id = click_id;
        this.total_click_count = total_click_count;
        this.current_date = current_date;
        this.reg_user_id = reg_user_id;
        this.user_name = user_name;
        this.user_pass = user_pass;
        this.user_phn_num = user_phn_num;
        this.user_email = user_email;
        this.user_state = user_state;
        this.reg_diag_id = reg_diag_id;
        this.diag_username = diag_username;
        this.diag_password = diag_password;
        this.admin_status = admin_status;
        this.e_id = e_id;
        this.e_msg = e_msg;
        this.e_reply = e_reply;
        this.spe_list = spe_list;
        this.ser_list = ser_list;
    }

    public String getDiag_id() {
        return diag_id;
    }

    public String getDiag_code() {
        return diag_code;
    }

    public String getDiag_name_eng() {
        return diag_name_eng;
    }

    public String getDiag_name_ban() {
        return diag_name_ban;
    }

    public String getDiag_address_eng() {
        return diag_address_eng;
    }

    public String getDiag_address_ban() {
        return diag_address_ban;
    }

    public String getDiag_contact() {
        return diag_contact;
    }

    public String getDiag_website() {
        return diag_website;
    }

    public String getDiag_email() {
        return diag_email;
    }

    public String getBanner_id() {
        return banner_id;
    }

    public String getBanner_img() {
        return banner_img;
    }

    public String getBanner_num() {
        return banner_num;
    }

    public String getDiag_doc_id() {
        return diag_doc_id;
    }

    public String getDoc_time_detail_eng() {
        return doc_time_detail_eng;
    }

    public String getDoc_time_detail_ban() {
        return doc_time_detail_ban;
    }

    public String getDoc_time_input_type() {
        return doc_time_input_type;
    }

    public String getMonth_name_eng() {
        return month_name_eng;
    }

    public String getMonth_name_ban() {
        return month_name_ban;
    }

    public String getWeek_name_eng() {
        return week_name_eng;
    }

    public String getWeek_name_ban() {
        return week_name_ban;
    }

    public String getTime_name_from_eng() {
        return time_name_from_eng;
    }

    public String getTime_name_from_ban() {
        return time_name_from_ban;
    }

    public String getTime_from_eng() {
        return time_from_eng;
    }

    public String getTime_from_ban() {
        return time_from_ban;
    }

    public String getTime_name_to_eng() {
        return time_name_to_eng;
    }

    public String getTime_name_to_ban() {
        return time_name_to_ban;
    }

    public String getTime_to_eng() {
        return time_to_eng;
    }

    public String getTime_to_ban() {
        return time_to_ban;
    }

    public String getWeek_time_txt() {
        return week_time_txt;
    }

    public String getExtra_week_time_eng() {
        return extra_week_time_eng;
    }

    public String getExtra_week_time_ban() {
        return extra_week_time_ban;
    }

    public String getDoc_website() {
        return doc_website;
    }

    public String getDoc_visit_fee() {
        return doc_visit_fee;
    }

    public String getDoc_cmnt_eng() {
        return doc_cmnt_eng;
    }

    public String getDoc_cmnt_ban() {
        return doc_cmnt_ban;
    }

    public String getDoc_present() {
        return doc_present;
    }

    public String getUpdate_num() {
        return update_num;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public String getNotify_title() {
        return notify_title;
    }

    public String getNotify_msg() {
        return notify_msg;
    }

    public String getNotify_msg_show() {
        return notify_msg_show;
    }

    public String getDiag_serial_id() {
        return diag_serial_id;
    }

    public String getSerial_num_eng() {
        return serial_num_eng;
    }

    public String getSerial_num_ban() {
        return serial_num_ban;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public String getDistrict_ban() {
        return district_ban;
    }

    public String getDistrict_eng() {
        return district_eng;
    }

    public String getDivision_id() {
        return division_id;
    }

    public String getDivision_eng() {
        return division_eng;
    }

    public String getDivision_ban() {
        return division_ban;
    }

    public String getSubdistrict_id() {
        return subdistrict_id;
    }

    public String getSubdistrict_ban() {
        return subdistrict_ban;
    }

    public String getSubdistrict_eng() {
        return subdistrict_eng;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public String getDoc_diag_total_count() {
        return doc_diag_total_count;
    }

    public String getDoc_name_eng() {
        return doc_name_eng;
    }

    public String getDoc_name_ban() {
        return doc_name_ban;
    }

    public String getDegree_eng() {
        return degree_eng;
    }

    public String getDegree_ban() {
        return degree_ban;
    }

    public String getWork_place_eng() {
        return work_place_eng;
    }

    public String getWork_place_ban() {
        return work_place_ban;
    }

    public String getDoc_serial_id() {
        return doc_serial_id;
    }

    public String getDoc_serial_num_eng() {
        return doc_serial_num_eng;
    }

    public String getDoc_serial_num_ban() {
        return doc_serial_num_ban;
    }

    public String getFavourite_id() {
        return favourite_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getTutorial_id() {
        return tutorial_id;
    }

    public String getTutorial_title() {
        return tutorial_title;
    }

    public String getTutorial_details() {
        return tutorial_details;
    }

    public String getTutorial_video_id() {
        return tutorial_video_id;
    }

    public String getTutorial_state_num() {
        return tutorial_state_num;
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

    public String getClick_id() {
        return click_id;
    }

    public String getTotal_click_count() {
        return total_click_count;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public String getReg_user_id() {
        return reg_user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public String getUser_phn_num() {
        return user_phn_num;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_state() {
        return user_state;
    }

    public String getReg_diag_id() {
        return reg_diag_id;
    }

    public String getDiag_username() {
        return diag_username;
    }

    public String getDiag_password() {
        return diag_password;
    }

    public String getAdmin_status() {
        return admin_status;
    }

    public String getE_id() {
        return e_id;
    }

    public String getE_msg() {
        return e_msg;
    }

    public String getE_reply() {
        return e_reply;
    }

    public List<SpecialistModel> getSpe_list() {
        return spe_list;
    }

    public List<SerialNumModel> getSer_list() {
        return ser_list;
    }
}
