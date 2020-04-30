package dm.sime.com.kharetati.pojo;

import java.util.List;

/**
 * Created by Hasham on 8/21/2017.
 */

public class OwnerPlotResponse {

  private List<ParcelDetail> ParcelsInfo;

  public List<ParcelDetail> getParcelsInfo() {
    return ParcelsInfo;
  }

  public void setParcelsInfo(List<ParcelDetail> parcelsInfo) {
    ParcelsInfo = parcelsInfo;
  }
}
