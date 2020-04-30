package dm.sime.com.kharetati.pojo;

/**
 * Created by hasham on 8/1/2017.
 */

public class ZoneRegulation {
  private String ZONE_CODE;
  private String HEIGHT_REG_EN;
  private String HEIGHT_REG_AR;
  private String GFA;
  private String FAR;
  private String PARKING_REG_EN;
  private String PARKING_REG_AR;
  private String REMARKS_REG_EN;
  private String REMARKS_REG_AR;
  private String SETBACK_REG_EN;
  private String SETBACK_REG_AR;
  private String LANDUSE_REG_EN;
  private String LANDUSE_REG_AR;

  public ZoneRegulation(){

  }

  public ZoneRegulation(String ZONE_CODE, String HEIGHT_REG_EN, String HEIGHT_REG_AR, String GFA, String FAR, String PARKING_REG_EN, String PARKING_REG_AR, String REMARKS_REG_EN, String REMARKS_REG_AR, String SETBACK_REG_EN, String SETBACK_REG_AR, String LANDUSE_REG_EN, String LANDUSE_REG_AR) {
    this.ZONE_CODE = ZONE_CODE;
    this.HEIGHT_REG_EN = HEIGHT_REG_EN;
    this.HEIGHT_REG_AR = HEIGHT_REG_AR;
    this.GFA = GFA;
    this.FAR = FAR;
    this.PARKING_REG_EN = PARKING_REG_EN;
    this.PARKING_REG_AR = PARKING_REG_AR;
    this.REMARKS_REG_EN = REMARKS_REG_EN;
    this.REMARKS_REG_AR = REMARKS_REG_AR;
    this.SETBACK_REG_EN = SETBACK_REG_EN;
    this.SETBACK_REG_AR = SETBACK_REG_AR;
    this.LANDUSE_REG_EN = LANDUSE_REG_EN;
    this.LANDUSE_REG_AR = LANDUSE_REG_AR;
  }


  public String getZONE_CODE() {
    return ZONE_CODE;
  }

  public void setZONE_CODE(String ZONE_CODE) {
    this.ZONE_CODE = ZONE_CODE;
  }

  public String getHEIGHT_REG_EN() {
    return HEIGHT_REG_EN;
  }

  public void setHEIGHT_REG_EN(String HEIGHT_REG_EN) {
    this.HEIGHT_REG_EN = HEIGHT_REG_EN;
  }

  public String getHEIGHT_REG_AR() {
    return HEIGHT_REG_AR;
  }

  public void setHEIGHT_REG_AR(String HEIGHT_REG_AR) {
    this.HEIGHT_REG_AR = HEIGHT_REG_AR;
  }

  public String getGFA() {
    return GFA;
  }

  public void setGFA(String GFA) {
    this.GFA = GFA;
  }

  public String getFAR() {
    return FAR;
  }

  public void setFAR(String FAR) {
    this.FAR = FAR;
  }

  public String getPARKING_REG_EN() {
    return PARKING_REG_EN;
  }

  public void setPARKING_REG_EN(String PARKING_REG_EN) {
    this.PARKING_REG_EN = PARKING_REG_EN;
  }

  public String getPARKING_REG_AR() {
    return PARKING_REG_AR;
  }

  public void setPARKING_REG_AR(String PARKING_REG_AR) {
    this.PARKING_REG_AR = PARKING_REG_AR;
  }

  public String getREMARKS_REG_EN() {
    return REMARKS_REG_EN;
  }

  public void setREMARKS_REG_EN(String REMARKS_REG_EN) {
    this.REMARKS_REG_EN = REMARKS_REG_EN;
  }

  public String getREMARKS_REG_AR() {
    return REMARKS_REG_AR;
  }

  public void setREMARKS_REG_AR(String REMARKS_REG_AR) {
    this.REMARKS_REG_AR = REMARKS_REG_AR;
  }

  public String getSETBACK_REG_EN() {
    return SETBACK_REG_EN;
  }

  public void setSETBACK_REG_EN(String SETBACK_REG_EN) {
    this.SETBACK_REG_EN = SETBACK_REG_EN;
  }

  public String getSETBACK_REG_AR() {
    return SETBACK_REG_AR;
  }

  public void setSETBACK_REG_AR(String SETBACK_REG_AR) {
    this.SETBACK_REG_AR = SETBACK_REG_AR;
  }

  public String getLANDUSE_REG_EN() {
    return LANDUSE_REG_EN;
  }

  public void setLANDUSE_REG_EN(String LANDUSE_REG_EN) {
    this.LANDUSE_REG_EN = LANDUSE_REG_EN;
  }

  public String getLANDUSE_REG_AR() {
    return LANDUSE_REG_AR;
  }

  public void setLANDUSE_REG_AR(String LANDUSE_REG_AR) {
    this.LANDUSE_REG_AR = LANDUSE_REG_AR;
  }


}
