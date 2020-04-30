package dm.sime.com.kharetati.pojo;

import java.util.List;

/**
 * Created by hasham on 9/19/2017.
 */

public class AttachmentVM {
  private int UserID ;
  private boolean isOwner ;
  private String paymentTransactionNumber;
  private String ParcelId;
  private List<Attachment> attachment;

  public int getUserID() {
    return UserID;
  }

  public void setUserID(int userID) {
    UserID = userID;
  }

  public boolean isOwner() {
    return isOwner;
  }

  public void setOwner(boolean owner) {
    isOwner = owner;
  }

  public String getPaymentTransactionNumber() {
    return paymentTransactionNumber;
  }

  public void setPaymentTransactionNumber(String paymentTransactionNumber) {
    this.paymentTransactionNumber = paymentTransactionNumber;
  }

  public String getParcelId() {
    return ParcelId;
  }

  public void setParcelId(String parcelId) {
    ParcelId = parcelId;
  }

  public List<Attachment> getAttachment() {
    return attachment;
  }

  public void setAttachment(List<Attachment> attachment) {
    this.attachment = attachment;
  }
}
