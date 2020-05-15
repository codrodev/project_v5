package dm.sime.com.kharetati.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hasham on 7/5/2017.
 */

public class AccessTokenResponse {

  public String CurrentAndroidVersion ;
  private String scope;
  private String token_type;
  private String refresh_token;
  private String id_token;
  private int expires_in;
  private String access_token;
  private String error;
  private String error_description;
  private String arcgis_token;
  public String gis_token_url;
  public String gis_user_name;
  public String gis_pwd;
  public String community_layerid;
  public String url_plotfinder;
  public String gis_layer_url;
  @SerializedName("map_hidden_layers")
  @Expose
  private String[] mapHiddenLayers;

  public String parcelLayerExportUrl_en;
  public String parcelLayerExportUrl_ar;
  public String plot_layerid;
  public String noctemplateUrl;
  public String forceUserToUpdateBuild_msg_en;
  public String forceUserToUpdateBuild_msg_ar;
  public boolean forceUserToUpdateBuild;
  public String baseurl_smartsiteplanWs;
  public String smartsiteplanWs_token;
  public boolean show_landreg_in_menu;
  public boolean show_landreg_popup;
  public String landreg_popup_msg_en;
  public String landreg_popup_msg_ar;
  public String landreg_popup_msg_heading_en;
  public String landreg_popup_msg_heading_ar;
  public String landreg_url;
  public String aboutus_ar_url;
  public String aboutus_en_url;
  public String terms_ar_url;
  public String terms_en_url;
  private AppMsg appMsg;

  public String[] getMapHiddenLayers() {
    return mapHiddenLayers;
  }

  public AppMsg getAppMsg() {
    return appMsg;
  }

  public void setAppMsg(AppMsg appMsg) {
    this.appMsg = appMsg;
  }



  public String getAboutus_ar_url() {
    return aboutus_ar_url;
  }

  public void setAboutus_ar_url(String aboutus_ar_url) {
    this.aboutus_ar_url = aboutus_ar_url;
  }

  public String getAboutus_en_url() {
    return aboutus_en_url;
  }

  public void setAboutus_en_url(String aboutus_en_url) {
    this.aboutus_en_url = aboutus_en_url;
  }

  public String getTerms_ar_url() {
    return terms_ar_url;
  }

  public void setTerms_ar_url(String terms_ar_url) {
    this.terms_ar_url = terms_ar_url;
  }

  public String getTerms_en_url() {
    return terms_en_url;
  }

  public void setTerms_en_url(String terms_en_url) {
    this.terms_en_url = terms_en_url;
  }

  public String getBaseurlSmartsiteplanWs() {
    return baseurl_smartsiteplanWs;
  }

  public void setBaseurlSmartsiteplanWs() {
    this.baseurl_smartsiteplanWs = baseurl_smartsiteplanWs;
  }

  public String getSmartsiteplanWsToken() {
    return smartsiteplanWs_token;
  }

  public void setSmartsiteplanWsToken() {
    this.smartsiteplanWs_token = smartsiteplanWs_token;
  }

  public String getArcgis_token() {
    return arcgis_token;
  }

  public void setArcgis_token(String arcgis_token) {
    this.arcgis_token = arcgis_token;
  }


  public String getError_description() {
    return error_description;
  }

  public void setError_description(String error_description) {
    this.error_description = error_description;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public AccessTokenResponse() {

  }

  public int getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(int expires_in) {
    this.expires_in = expires_in;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public String getId_token() {
    return id_token;
  }

  public void setId_token(String id_token) {
    this.id_token = id_token;
  }

  public String getAccess_token() {
    return access_token;
  }
  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
  public String getNoctemplateUrl() {
    return noctemplateUrl;
  }

  public void setNoctemplateUrl(String noctemplateUrl) {
    this.noctemplateUrl = noctemplateUrl;
  }

  public String getForceUserToUpdateBuild_msg_en() {
    return forceUserToUpdateBuild_msg_en;
  }

  public void setForceUserToUpdateBuild_msg_en(String forceUserToUpdateBuild_msg_en) {
    this.forceUserToUpdateBuild_msg_en = forceUserToUpdateBuild_msg_en;
  }

  public String getForceUserToUpdateBuild_msg_ar() {
    return forceUserToUpdateBuild_msg_ar;
  }

  public void setForceUserToUpdateBuild_msg_ar(String forceUserToUpdateBuild_msg_ar) {
    this.forceUserToUpdateBuild_msg_ar = forceUserToUpdateBuild_msg_ar;
  }

  public boolean isForceUserToUpdateBuild() {
    return forceUserToUpdateBuild;
  }

  public void setForceUserToUpdateBuild(boolean forceUserToUpdateBuild) {
    this.forceUserToUpdateBuild = forceUserToUpdateBuild;
  }

  public boolean isShowLandregInMenu() {
    return show_landreg_in_menu;
  }

  public void setShowLandregInMenu(boolean show_landreg_in_menu) {
    this.show_landreg_in_menu = show_landreg_in_menu;
  }

  public boolean isShowLandregPopup() {
    return show_landreg_popup;
  }

  public void setShowLandregPopup(boolean show_landreg_popup) {
    this.show_landreg_popup = show_landreg_popup;
  }

  public String getLandregPopupMsgEn() {
    return landreg_popup_msg_en;
  }

  public void setLandregPopupMsgEn(String landreg_popup_msg_en) {
    this.landreg_popup_msg_en = landreg_popup_msg_en;
  }

  public String getLandregPopupMsgAr() {
    return landreg_popup_msg_ar;
  }

  public void setLandregPopupMsgAr(String landreg_popup_msg_ar) {
    this.landreg_popup_msg_ar = landreg_popup_msg_ar;
  }

  public String getLandregPopupMsgHeadingEn() {
    return landreg_popup_msg_heading_en;
  }

  public void setLandregPopupMsgHeadingEn(String landreg_popup_msg_heading_en) {
    this.landreg_popup_msg_heading_en = landreg_popup_msg_heading_en;
  }

  public String getLandregPopupMsgHeadingAr() {
    return landreg_popup_msg_heading_ar;
  }

  public void setLandregPopupMsgHeadingAr(String landreg_popup_msg_heading_ar) {
    this.landreg_popup_msg_heading_ar = landreg_popup_msg_heading_ar;
  }

  public String getLandregUrl() {
    return landreg_url;
  }

  public void setLandregUrl(String landreg_url) {
    this.landreg_url = landreg_url;
  }
}
