package com.pratham.foundation.async;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.APP_ID_STR;
import static com.pratham.foundation.utility.FC_Constants.DEVICE_ID_STR;
import static com.pratham.foundation.utility.FC_Constants.PI_DOWNLOAD_RESOURCE;
import static com.pratham.foundation.utility.FC_Constants.STUDENT_ID_STR;
import static com.pratham.foundation.utility.FC_Constants.newRootParentId;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;

import java.io.File;
import java.util.concurrent.Executors;

public class API_Content {

    Context mContext;
    API_Content_Result apiContentResult;
    static String responseAPI = "";

    /**
     * @param context
     * @param apiContentResult This contains the fetching of data from API and giving the result.
     *                         Instance from the calling method is used to return the result back.
     * @param apiContentResult is used to return the result back to the calling class.
     *                         if success - requestType and response is sent to receivedContent() method.
     *                         else only requestType is sent to receivedError() method.
     */

    public API_Content(Context context, API_Content_Result apiContentResult) {
        this.mContext = context;
        this.apiContentResult = apiContentResult;
    }

    public void getAPITest(final String requestType, String url, String nodeId) {
        try {
            String url_id;
            url_id = url + nodeId;
            Log.d("API_Content_LOG", "getAPIContent: " + nodeId);
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkServerStatus(final String requestType, String myAPI) {
        try {
            Log.d("API_Content_LOG", "url_id: " + myAPI);
            AndroidNetworking.get(myAPI)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSyncedDetails(final String requestType, String myAPI, String dateFrom, String dateTo, String sId) {
        try {
            String url_id ="";
            url_id = myAPI + dateFrom + " 00:00:00&DateTo="+
                    dateTo + " 23:59:59&StudentId="+ sId;
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSyncedSummary(final String requestType, String myAPI, String dateFrom, String dateTo) {
        try {
            String url_id ="";
            url_id = myAPI + dateFrom + " 00:00:00&DateTo="+
                    dateTo + " 23:59:59&DeviceId="+ FC_Utility.getDeviceID();
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getStudentSyncedData(final String requestType, String startDate, String endDate) {
        try {
            String url_id = "aaa";
//            url_id = url + nodeId;
//            Log.d("API_Content_LOG", "getAPIContent: " + nodeId);
//            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get("http://prathamyouthnet.org/ssmspushdb/dbsummary-group.php")
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncPushedItemDB(final String requestType, String API, String pushId) {
        try {
            String final_URL;
            final_URL = API + pushId;
            Log.d("API_Content_LOG", "syncPushedItemDB: PushID : " + pushId);
            Log.d("API_Content_LOG", "syncPushedItemDB URL : " + final_URL);
            AndroidNetworking.get(final_URL)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Error - Send requestType to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAPIContent(final String requestType, String url, String nodeId) {
        try {
//            String url_id;
//            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            studId = FastSave.getInstance().getString(FC_Constants.CURRENT_API_STUDENT_ID, "");
            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID() + STUDENT_ID_STR + studId;
            Log.d("`API_Content_LOG`", "getAPIContent: " + nodeId);
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAPIContent_PI(final String requestType, String url, String nodeId) {
        try {
//            String url_id;
//            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            url_id = url + nodeId + "&JsonType=Browse";
            Log.d("`API_Content_LOG`", "getAPIContent: " + nodeId);
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAPIContent_PI_V2(final String requestType, String url, String nodeId) {
        try {
//            String url_id;
//            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            url_id = url + nodeId + PI_DOWNLOAD_RESOURCE;
            Log.d("`API_Content_LOG`", "getAPIContent: " + nodeId);
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAPIContent_SubLevel_PI(final String requestType, String url, String nodeId, final int pos, final int size) {
        try {
//            String url_id;
//            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            url_id = url + nodeId + "&JsonType=Browse";
            Log.d("`API_Content_LOG`", "getAPIContent: " + nodeId);
            Log.d("API_Content_LOG", "url_id: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent_PI_SubLevel(requestType, response, pos, size);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBoardAPI(final String requestType, String url) {
        try {
//            String url_id;
//            url_id = url + rootParentId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            studId = FastSave.getInstance().getString(FC_Constants.CURRENT_API_STUDENT_ID, "");
            url_id = url + newRootParentId + DEVICE_ID_STR + FC_Utility.getDeviceID() + STUDENT_ID_STR + studId;
            Log.d("API_Content_LOG", "getAPIContent: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBoardAPI_PI(final String requestType, String url) {
        try {
//            String url_id;
//            url_id = url + rootParentId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id;
            url_id = url + newRootParentId;
            Log.d("API_Content_LOG", "getAPIContent: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInternetTimeApi(final String requestType, String url) {
        try {
            Log.d("API_Content_LOG", "getAPIContent: " + url);
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAPILanguage(final String requestType, String url, String nodeId) {
        try {
//            String url_id;
//            url_id = url + rootParentId + DEVICE_ID_STR + FC_Utility.getDeviceID();
            String url_id, studId;
            studId = FastSave.getInstance().getString(FC_Constants.CURRENT_API_STUDENT_ID, "");
            url_id = url + nodeId + DEVICE_ID_STR + FC_Utility.getDeviceID() + STUDENT_ID_STR + studId;
            Log.d("API_Content_LOG", "getAPIContent: " + url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getStudentByEnrollmentNo(final String requestType, String url) {
        try {
            String url_ = url+APP_ID_STR+newRootParentId;
            Log.d("API_Content_LOG", "getAPIContent: " + url_);
            AndroidNetworking.get(url_)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Success - Send requestType and response to the calling class.
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAuthHeader() {
        String encoded = Base64.encodeToString(("pratham" + ":" + "pratham").getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }

    public void pullFromKolibri(String header, String url) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", getAuthHeader())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Success - Send requestType and response to the calling class.
                        if (apiContentResult != null)
                            apiContentResult.receivedContent(header, response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        //Success - Send requestType and response to the calling class.
                        if (apiContentResult != null)
                            apiContentResult.receivedError(header);
                    }
                });
    }

    public void pullFromInternet(String header, String url) {
        Log.d("pull", "pullFromInternet: "+header+ " : "+url);
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
//                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Success - Send requestType and response to the calling class.
                        if (apiContentResult != null)
                            apiContentResult.receivedContent(header, response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        //Success - Send requestType and response to the calling class.
                        if (apiContentResult != null)
                            apiContentResult.receivedError(header);
                    }
                });
    }

    public static void downloadImage(String url, String filename) {
        File dir = new File(ApplicationClass.foundationPath + "" + App_Thumbs_Path); //Creating an internal dir;
        if (!dir.exists()) dir.mkdirs();
        AndroidNetworking.download(url, dir.getAbsolutePath(), filename)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("image::", "DownloadComplete");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("image::", "Not Downloaded");
                    }
                });
    }

}
