package dm.sime.com.kharetati.pojo;


import ae.sdg.libraryuaepass.business.Environment;
import ae.sdg.libraryuaepass.business.authentication.model.UAEPassAccessTokenRequestModel;

public class CustomUAEPassAccessTokenRequestModel extends UAEPassAccessTokenRequestModel {
    private Environment environment;
    private String redirectUrl;
    private String clientId;
    private String clientSecret;
    private String responseType;
    private String scope;
    private String acrValues;
    private String ui_locales;

    public String getUi_locales() {
        return ui_locales;
    }

    public void setUi_locales(String ui_locales) {
        this.ui_locales = ui_locales;
    }

    public CustomUAEPassAccessTokenRequestModel() {
        this.environment = Environment.PRODUCTION;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAcrValues() {
        return this.acrValues;
    }

    public void setAcrValues(String acrValues) {
        this.acrValues = acrValues;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
