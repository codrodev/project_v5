package dm.sime.com.kharetati.layout;



import dm.sime.com.kharetati.network.MyApiService;
import dm.sime.com.kharetati.pojo.SerializableParcelDetails;
import dm.sime.com.kharetati.pojo.SerializeGetAppRequestModel;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UAEAccessTokenResponse;
import dm.sime.com.kharetati.pojo.UaePassConfig;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class UserRepository {
    private MyApiService api;
    public UserRepository(MyApiService client) {
        this.api = client;

    }
    public Observable<UaePassConfig> uaePassConfig(String url){
        return api.uaePassConfig(url);
    }

    public Observable<SessionUaePassResponse> getSessionUAEPass(String url) {
        return api.getSessionUAEPass(url);
    }

    public Observable<SerializableParcelDetails> getParcelDetails(String url, SerializeGetAppRequestModel model) {
        return api.getParcelDetails(url,model);
    }
    public Observable<UAEAccessTokenResponse> getUAEAccessToken(String accessToken){
        return api.getUAEAccessToken(accessToken);
    }
}
