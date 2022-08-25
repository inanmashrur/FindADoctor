package black.project.finddoctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Doctor implements Serializable {

    String docName, speciality, fee, picURL, about, chamber, location, contactNo, id;
    List<String> schedule;
    int visited;

    public Doctor(){}

    public Doctor(String id,String picURL, String docName, String speciality, String fee ) {
        this.id = id;
        this.docName = docName;
        this.speciality = speciality;
        this.fee = fee;
        this.picURL = picURL;
    }

    public Doctor(String docName, String speciality, String fee, String picURL, String about, String chamber,
                  String location, String contactNo, List<String> schedule, int visited) {
        this.docName = docName;
        this.speciality = speciality;
        this.fee = fee;
        this.picURL = picURL;
        this.about = about;
        this.chamber = chamber;
        this.location = location;
        this.contactNo = contactNo;
        this.schedule = schedule;
        this.visited = visited;
    }

    protected Doctor(Parcel in) {
        docName = in.readString();
        speciality = in.readString();
        fee = in.readString();
        picURL = in.readString();
        about = in.readString();
        chamber = in.readString();
        location = in.readString();
        contactNo = in.readString();
        id = in.readString();
        schedule = in.createStringArrayList();
        visited = in.readInt();
    }

    public int getVisited() {
        return visited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public List<String> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<String> schedule) {
        this.schedule = schedule;
    }

}
