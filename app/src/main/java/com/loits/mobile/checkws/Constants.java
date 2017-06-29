package com.loits.mobile.checkws;

/**
 * Created by DumindaW on 20/01/2017.
 */

public class Constants {

    public static final String DEV_SERVICES_BASE_URL = "https://203.189.65.69:8246/services";
    public static final String DEV_IDS_LOGIN_PATH = "https://203.189.65.69:8005/oauth2/token";

    public static final String DEV_IDS_OAUTH2_CLIENT_ID = "ruX10JIaEdEkoUYtG3yRciiqxs4a";
    public static final String DEV_IDS_OAUTH2_CLIENT_SECRET = "GiiPK3Dsf7fCjVb0FtUXLqJx6awa";

    //public static String  DEV_IDS_OAUTH2_CLIENT_ID = "ruX10JIaEdEkoUYtG3yRciiqxs4a";
    //public static String  DEV_IDS_OAUTH2_CLIENT_SECRET = "GiiPK3Dsf7fCjVb0FtUXLqJx6awa";

    public static final String SERVICE_PENDING = "/insuAssessorJobPending1_0Rest";
    public static final String SERVICE_COMPLETED ="/insuAssessorJobCompleted1_0Rest";
    public static final String SERVICE_ACCEPT = "/insuAssessorJobAccept1_0Rest";
    public static final String SERVICE_REJECT = "/insuAssessorJobReject1_0Rest";

    //public static final String SYNC = "http://203.189.65.69:8284/services/rcptActiveReceipt1_0Rest";
    //public static final String SYNC = "https://203.189.65.69:8246/services/rcptActiveReceipt1_0Rest";
    public static final String SYNC = "https://203.189.65.70:8347/services/mockRest";
    //public static final String SYNC = "https://172.16.1.45:8347/services/rcptActiveReceipt1_0Rest";
    //public static final String SYNC = "http://172.16.1.53:8293/services/rcptActiveReceipt1_0Rest";
    //public static final String SYNC = "https://172.16.1.53:8246/services/rcptActiveReceipt1_0Rest";
    //public static final String SYNC = "http://172.16.1.45:8347/services/rcptActiveReceipt1_0Rest";
    //public static final String SYNC = "https://192.100.151.119:8443/RCPT_SERVER2-1/receipt/complete";

    public final static int SOCKET_TIMEOUT_MS = 100000;

}
