package com.pratham.foundation.async;

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
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;

import java.io.File;
import java.util.concurrent.Executors;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.DEVICE_ID_STR;
import static com.pratham.foundation.utility.FC_Constants.rootParentId;

public class API_Content {

    Context mContext;
    API_Content_Result apiContentResult;
    static String responseAPI = "";

    public API_Content(Context context, API_Content_Result apiContentResult) {
        this.mContext = context;
        this.apiContentResult = apiContentResult;
    }

    public void getAPIContent(final String requestType, String url, String nodeId) {
        try {
            String url_id;
            url_id = url + nodeId +DEVICE_ID_STR+ FC_Utility.getDeviceID();
            Log.d("API_Content_LOG", "getAPIContent: "+nodeId);
            Log.d("API_Content_LOG", "url_id: "+url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
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
                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void getAPIContentTemp(final String requestType, String url, String nodeId) {
//        try {
//            String url_id;
////            url_id = url + nodeId +DEVICE_ID_STR+ FC_Utility.getDeviceID();
//            url_id =  "http://devposapi.prathamopenschool.org/api/content/downloadv3?id=55262&deviceid=1";
//            Log.d("API_Content_LOG", "getAPIContent: "+nodeId);
//            AndroidNetworking.get(url_id)
//                    .addHeaders("Content-Type", "application/json")
//                    .build()
//                    .getAsString(new StringRequestListener() {
//                        @Override
//                        public void onResponse(String response) {
//                            if (apiContentResult != null)
//                                apiContentResult.receivedContent(requestType, response);
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//                            try {
//                                Log.d("Error:", anError.getErrorDetail());
//                                // Log.d("Error::", anError.getResponse().toString());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            if (apiContentResult != null)
//                                apiContentResult.receivedError(requestType);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void getAPILanguage(final String requestType, String url) {
        try {
            String url_id;
            url_id = url + rootParentId +DEVICE_ID_STR+ FC_Utility.getDeviceID();
            Log.d("API_Content_LOG", "getAPIContent: "+url_id);
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
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
                        if (apiContentResult != null)
                            apiContentResult.receivedContent(header, response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        if (apiContentResult != null)
                            apiContentResult.receivedError(header);
                    }
                });
    }

    public void pullFromInternet(String header, String url) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
//                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (apiContentResult != null)
                            apiContentResult.receivedContent(header, response.toString());
                    }

                    @Override
                    public void onError(ANError error) {
                        if (apiContentResult != null)
                            apiContentResult.receivedError(header);
                    }
                });
    }

    public static String getAPIContentResult(String url, String nodeId) {
        try {
            String url_id;
            url_id = url + "" + nodeId;
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            responseAPI = response;
/*                            if (apiContentResult != null)
                                apiContentResult.receivedContent(requestType, response);*/
                        }

                        @Override
                        public void onError(ANError anError) {
                            responseAPI = "";
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                                // Log.d("Error::", anError.getResponse().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
/*                            if (apiContentResult != null)
                                apiContentResult.receivedError(requestType);*/
                        }

                    });
            return responseAPI;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void downloadImage(String url, String filename) {
        File dir = new File(ApplicationClass.foundationPath +  "" + App_Thumbs_Path ); //Creating an internal dir;
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
