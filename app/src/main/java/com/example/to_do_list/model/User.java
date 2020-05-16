package com.example.to_do_list.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 李木白 on 2018/3/3.
 */

public class User extends BmobObject{
    private String username;
    private String password;
    private List objectIdList = new ArrayList();

    public List<String> getObjectIdList() {
        return objectIdList;
    }

    public void setObjectIdList(List<String> objectIdList) {
        this.objectIdList = objectIdList;
    }

    public void removeTag(String id) {
        objectIdList.remove(id);
    }

    public void addTag(String id) {
        objectIdList.add(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
