package dm.sime.com.kharetati.pojo;

/**
 * Created by Hasham on 8/21/2017.
 */

public class ParcelDetail {
  private String Address;
  private String Area;
  private String Emirate;
  private boolean IsOwner;
  private String NationalId;
  private String PlotNumber;
  private String LandSize;
  private String Phone;

  public String getAddress() {
    return Address;
  }

  public void setAddress(String address) {
    Address = address;
  }

  public String getArea() {
    return Area;
  }

  public void setArea(String area) {
    Area = area;
  }

  public String getEmirate() {
    return Emirate;
  }

  public void setEmirate(String emirate) {
    Emirate = emirate;
  }

  public boolean isOwner() {
    return IsOwner;
  }

  public void setOwner(boolean owner) {
    IsOwner = owner;
  }

  public String getNationalId() {
    return NationalId;
  }

  public void setNationalId(String nationalId) {
    NationalId = nationalId;
  }

  public String getPlotNumber() {
    return PlotNumber;
  }

  public void setPlotNumber(String plotNumber) {
    PlotNumber = plotNumber;
  }

  public String getLandSize() {
    return LandSize;
  }

  public void setLandSize(String landSize) {
    LandSize = landSize;
  }

  public String getPhone() {
    return Phone;
  }

  public void setPhone(String phone) {
    Phone = phone;
  }
}
