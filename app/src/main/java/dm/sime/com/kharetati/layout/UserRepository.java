package dm.sime.com.kharetati.layout;



import dm.sime.com.kharetati.network.MyApiService;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UaePassConfig;
import io.reactivex.Observable;

class UserRepository {
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
}
