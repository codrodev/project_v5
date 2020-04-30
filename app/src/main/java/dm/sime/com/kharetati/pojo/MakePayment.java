package dm.sime.com.kharetati.pojo;

/**
 * Created by Hasham on 8/27/2017.
 */

public class MakePayment {
  public String userID ;
  public String transactionID ;
  public String URI ;
  public String ValidToken ;
  public String Code ;
  public String Message ;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getTransactionID() {
    return transactionID;
  }

  public void setTransactionID(String transactionID) {
    this.transactionID = transactionID;
  }

  public String getURI() {
    return URI;
  }

  public void setURI(String URI) {
    this.URI = URI;
  }

  public String getValidToken() {
    return ValidToken;
  }

  public void setValidToken(String validToken) {
    ValidToken = validToken;
  }

  public String getCode() {
    return Code;
  }

  public void setCode(String code) {
    Code = code;
  }

  public String getMessage() {
    return Message;
  }

  public void setMessage(String message) {
    Message = message;
  }
}
