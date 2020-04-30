package dm.sime.com.kharetati.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;



/**
 * Created by Imran on 8/30/2017.
 */

public class GetAllSitePlansResponse {
    public List<SitePlan> sitePlanlist;
    public Boolean isError;
    public String message;

    public List<SitePlan> getSitePlanlist() {
        return sitePlanlist;
    }

    public GetAllSitePlansResponse(List<SitePlan> sitePlanlist) {
        this.sitePlanlist = sitePlanlist;
    }

    public GetAllSitePlansResponse() {
    }

    public void setSitePlanlist(List<SitePlan> sitePlanlist) {
        this.sitePlanlist = sitePlanlist;
    }
}
