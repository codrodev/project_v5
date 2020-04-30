package dm.sime.com.kharetati.pojo;

/**
 * Created by hasham on 9/13/2017.
 */

public class ParcelBlockedResponse {
  private boolean isError;
  private boolean isBlocked;

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public boolean isBlocked() {
    return isBlocked;
  }

  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
  }



}
