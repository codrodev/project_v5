package dm.sime.com.kharetati.pojo;

import com.google.gson.annotations.SerializedName;

public class SerializeGetAppRequestModel {
    @SerializedName("inputJson")
    private SerializeGetAppInputRequestModel inputJson;

    public SerializeGetAppInputRequestModel getInputJson() {
        return inputJson;
    }

    public void setInputJson(SerializeGetAppInputRequestModel inputJson) {
        this.inputJson = inputJson;
    }
}
