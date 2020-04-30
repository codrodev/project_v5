package dm.sime.com.kharetati.pojo;

import java.util.List;

public class GetAreaNamesResponse {
    private AreaResponse service_response;

    public AreaResponse getAreaResponse ()
    {
        return service_response;
    }

    public void setAreaResponse (AreaResponse service_response)
    {
        this.service_response = service_response;
    }
}
