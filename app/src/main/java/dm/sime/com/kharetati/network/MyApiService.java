package dm.sime.com.kharetati.network;


import dm.sime.com.kharetati.pojo.SerializableParcelDetails;
import dm.sime.com.kharetati.pojo.SerializeGetAppRequestModel;
import dm.sime.com.kharetati.pojo.SessionUaePassResponse;
import dm.sime.com.kharetati.pojo.UAEAccessTokenResponse;
import dm.sime.com.kharetati.pojo.UaePassConfig;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface MyApiService {


    @POST
    Observable<UaePassConfig> uaePassConfig(@Url String url);
    @POST
    Observable<SessionUaePassResponse> getSessionUAEPass(@Url String url);
    @POST
    Observable<SerializableParcelDetails> getParcelDetails(@Url String url, @Body SerializeGetAppRequestModel model);
    @POST
    Observable<UAEAccessTokenResponse> getUAEAccessToken(@Url String url);


    /*//to get accessToken
    @FormUrlEncoded
    @POST(AppUrls.MYID_ACCESS_TOKEN_URL)
    Observable<AccessTokenResponse> getAccessToken(@Field("username") String username, @Field("password") String password,
                                                   @Field("isUAEID") boolean isUAE, @Field("uaeIDToken") String uaeIDToken);

    //register logged in user
    @POST(AppUrls.REGISTER_MYID_USER)
    Observable<UserRegistration> registerLoggedUser(@Body HTTPRequestBody map);

    // to get session
    @POST(AppUrls.MYID_SESSION_ID)
    Observable<SessionResponse> getSession(@Query("accessToken") String accessToken);

    @POST
    Observable<SessionUaePassResponse> getSessionUAEPass(@Url String url);

    @POST
    Observable<UAEAccessTokenResponse> getUAEAccessToken(@Url String url);

    //guest user login
    @POST(AppUrls.REGISTER_GUEST_USER)
    Observable<KharetatiUser> guestLogin(@Body HTTPRequestBody.GuestBody guestBody);

    @POST
    Observable<UaePassConfig> uaePassConfig(@Url String url);

    @POST
    Observable<SerializedUAEAccessTokenAPIModelResponse> uaeAccessToken(@Body SerializedUAEAccessTokenRequestAPIModel requestObject);

    @POST
    Observable<GetConfigResponse> getConfig(@Url String url);

    //to get user details
    @GET
    Observable<User> getUserDetails(@Url String url);

    //to get makani response
   *//* @POST(AppUrls.MYID_MAKANI_TO_DLTM)
    Observable<MakaniToDLTMResponse> getMakaniToDLTM(@Body HTTPRequestBody.MakaniBody makaniBody);*//*

    //to get all Siteplans
    *//*@POST
    Observable<RetrieveMyMapResponse> getAllSitePlans(@Url String url,@Body JSONObject sitePlanBody);*//*

    @POST
    Observable<RetrieveMyMapResponse> getAllSitePlans(@Url String url, @Body SerializedModel model);

    //to save Book mark
    @POST("Bookmark/addBookmark")
    Observable<SerializableSaveBookMarks> saveAsBookMark(@Body SerializeBookMarksModel bookMarkBody);


    // to get parcel Id in land search
    @POST(AppUrls.MYID_PARCEL_ID)
    Observable<ParcelResponse> getParcelID(@Body HTTPRequestBody.ParcelBody parcelBody);

    //to get Area Names In land search
    @POST("KharetatiWebService/getAreaNames")
    Observable<GetAreaNamesResponse> getAreaNames(@Body HTTPRequestBody.AreaBody areaBody);

    //to load Bookmarks

    @POST("Bookmark/getAllBookMark")
    Observable<BookmarksResponse> getAllBookMarks(@Body SerializeBookmarkModel model);

    //to view Siteplans in MyMaps
    @POST
    Observable<RetrieveDocStreamResponse> viewSitePlans(@Url String url, @Body SerializedModel model);

    // to delete BookMark in Bookmarks
    @POST("Bookmark/deleteBookMark")
    Observable<SerializableSaveBookMarks> deleteBookMark(@Body SerializeBookmarkModel model);

    @POST("Bookmark/updateBookMark")
    Observable<GeneralResponse> editBookMark(@Body SerializeBookmarkEditModel model);

    //to get the profile documents if any
    @POST
    Observable<RetrieveProfileDocsResponse> retrieveProfileDocs(@Url String url, @Body SerializedModel model);

    // to download the document stream
    @POST
    Observable<RetrieveDocStreamResponse> retrieveDoc(@Url String url, @Body SerializedModel model);

    //to Create and Update request
    @POST
    Observable<CreateUpdateRequestResponse> createAndUpdateRequest(@Url String url, @Body SerializedCreateAndUpdateModel model);

    @POST
    Observable<GetAppResponse> getApps(@Url String url, @Body SerializeGetAppRequestModel model);

    @POST
    Observable<AppSessionResponse> getAppSession(@Url String url);

    @POST
    Observable<SearchResult> getMapBasedSearchResult(@Url String url, @Body SearchParameterModel model);

    @POST
    Observable<WebSearchResult> getWebBasedSearchResult(@Url String url, @Body SearchParameterModel model);

    @POST
    Observable<LookupResponseModel> getLookupResult(@Url String url, @Body LookupParameterModel model);

    //to Send Feedback mail
    @POST("util/sendFeedBackEmail")
    Observable<GeneralResponse> sendFeedBack(@Body SerializableFeedBackModel model);

    @POST
    Observable<BaseResponseModel> validateParcel(@Url String url, @Body SerializedValidateParcelModel model);

    //To get the Community details in the BookMark
    @POST
    Observable<SerializableParcelDetails> getParcelDetails(@Url String url, @Body SerializeGetAppRequestModel model);
    @POST
    Observable<MakaniToDLTMResponse> getMakaniToDLTM(@Url String url, @Body SerializeGetAppRequestModel model);*/
}
