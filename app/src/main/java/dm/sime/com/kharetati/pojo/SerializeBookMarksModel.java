package dm.sime.com.kharetati.pojo;

import com.google.gson.annotations.SerializedName;

public class SerializeBookMarksModel {
    @SerializedName("UserID")
    private int UserID;
    @SerializedName("ParcelNumber")
    private int ParcelNumber;
    @SerializedName("CommunityAr")
    private String CommunityAr;
    @SerializedName("Community")
    private String Community;
    @SerializedName("Area")
    private double Area;

    public double getArea() {
        return Area;
    }

    public void setArea(double area) {
        Area = area;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getParcelNumber() {
        return ParcelNumber;
    }

    public void setParcelNumber(int parcelNumber) {
        ParcelNumber = parcelNumber;
    }

    public String getCommunityAr() {
        return CommunityAr;
    }

    public void setCommunityAr(String communityAr) {
        CommunityAr = communityAr;
    }

    public String getCommunity() {
        return Community;
    }

    public void setCommunity(String community) {
        Community = community;
    }


}
