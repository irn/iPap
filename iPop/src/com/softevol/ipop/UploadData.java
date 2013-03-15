package com.softevol.ipop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 14.03.13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class UploadData implements Serializable{

    private String name;

    private String email;

    private String description;

    private List<String> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void addFile(String fileName){
        if (this.files == null){
            files = new ArrayList<String>();
        }
        files.add(fileName);
    }
}
