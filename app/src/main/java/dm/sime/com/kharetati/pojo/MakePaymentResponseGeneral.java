package dm.sime.com.kharetati.pojo;

/**
 * Created by Hasham on 8/27/2017.
 */

public class MakePaymentResponseGeneral {

  public boolean isError;
  public MakePayment mkPaymentResponse;

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public MakePayment getMkPaymentResponse() {
    return mkPaymentResponse;
  }

  public void setMkPaymentResponse(MakePayment mkPaymentResponse) {
    this.mkPaymentResponse = mkPaymentResponse;
  }
}
