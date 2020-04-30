package dm.sime.com.kharetati.pojo;

public class FindSitePlansResponse {
    private String request_status;

    private String is_payment_pending;

    private String is_plan_ready;

    private String parcel_id;

    private SiteplanDocument SiteplanDocument;

    private String request_id;

    private String req_created_date;

    public String getRequestStatus ()
    {
        return request_status;
    }

    public void setRequestStatus (String request_status)
    {
        this.request_status = request_status;
    }

    public String getIsPaymentPending ()
    {
        return is_payment_pending;
    }

    public void setIsPaymentPending (String is_payment_pending)
    {
        this.is_payment_pending = is_payment_pending;
    }

    public String getIsPlanReady ()
    {
        return is_plan_ready;
    }

    public void setIsPlanReady (String is_plan_ready)
    {
        this.is_plan_ready = is_plan_ready;
    }

    public String getParcelId ()
    {
        return parcel_id;
    }

    public void setParcelId (String parcel_id)
    {
        this.parcel_id = parcel_id;
    }

    public SiteplanDocument getSiteplanDocument ()
    {
        return SiteplanDocument;
    }

    public void setSiteplanDocument (SiteplanDocument SiteplanDocument)
    {
        this.SiteplanDocument = SiteplanDocument;
    }

    public String getRequestId ()
    {
        return request_id;
    }

    public void setRequestId (String request_id)
    {
        this.request_id = request_id;
    }

    public String getReqCreatedDate ()
    {
        return req_created_date;
    }

    public void setReqCreatedDate (String req_created_date)
    {
        this.req_created_date = req_created_date;
    }
}
