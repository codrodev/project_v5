package dm.sime.com.kharetati.pojo;

/**
 * Created by hasham on 9/13/2017.
 */

public class UpdateRegisteredUserResponse {
  public boolean isError;
  public String message;
  public int userID;
  public String deviceID;

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }
}
