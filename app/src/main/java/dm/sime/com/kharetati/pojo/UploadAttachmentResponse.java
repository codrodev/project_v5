package dm.sime.com.kharetati.pojo;

/**
 * Created by Hasham on 8/27/2017.
 */

public class UploadAttachmentResponse {
  public boolean isError;
  public String accelacustomId;
  public int status;
  public String errorDetail;


  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public void setErrorDetail(String errorDetail) {
    this.errorDetail = errorDetail;
  }

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public String getAccelacustomId() {
    return accelacustomId;
  }

  public void setAccelacustomId(String accelacustomId) {
    this.accelacustomId = accelacustomId;
  }
}
