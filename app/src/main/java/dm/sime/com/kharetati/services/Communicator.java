package dm.sime.com.kharetati.services;

import dm.sime.com.kharetati.pojo.BuildingViolationResponse;

/**
 * Created by Hasham on 7/26/2017.
 */

public interface Communicator {

    public void navigateToMap(String parcelId, String dlTm);
    public void navigateToFlowSelect(String flow);
    public void navigateToAttachment(String data);
    public void navigateToAttachmentSelection(String data);

    public void navigateToZoneRegulation(String data);
    public void navigateToPayment(String data);
    public void navigateToPassCode(String mobileNo);
    public void navigateToViewPdf(String data);
    public void hideMainMenuBar();
    public void hideTransitionAppBar();
    public void hidePaymentAppBar();
    public void showMainMenuBar();
    public void showAppBar();
    public void hideAppBar();
    public void homePageAppBar();
    public void paymentAppBar();

    public void navigateToFeedback(String data);
    public void navigateToDownloadedSitePlan();
    public void navigateToHome(boolean addToBackStack);
    public void saveAsBookmark(boolean showMsg);
    public void navigateToContactUs(String data);
    public void setUnreadNotificationCount(int count);
    public void closeNotificationsPanel();
    public void navigateToUpdateProfile();
    public void navigateToMyIDProfile();
    public void navigateToSearchSitePlan();
    public void navigateToViolations(BuildingViolationResponse buildingViolationResponse);
    public void navigateToDisclaimer();
}
