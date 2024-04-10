package com.sd.spartan.easyhealth.model;

public class NotifyModel {
    String  notify_id, notify_title, notify_msg, notify_read, notify_item_id ;

    public NotifyModel(String notify_id, String notify_title, String notify_msg, String notify_read, String notify_item_id) {
        this.notify_id = notify_id;
        this.notify_title = notify_title;
        this.notify_msg = notify_msg;
        this.notify_read = notify_read;
        this.notify_item_id = notify_item_id;
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

    public String getNotify_read() {
        return notify_read;
    }

    public String getNotify_item_id() {
        return notify_item_id;
    }


}
