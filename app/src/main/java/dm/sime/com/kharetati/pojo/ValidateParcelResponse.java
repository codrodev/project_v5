package dm.sime.com.kharetati.pojo;

/**
 * Created by hasham on 9/21/2017.
 */

public class ValidateParcelResponse {
  private boolean isError;
  private boolean isValid;
  private String entitlement;

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public String getEntitlement() {
    return entitlement;
  }

  public void setEntitlement(String entitlement) {
    this.entitlement = entitlement;
  }
}
