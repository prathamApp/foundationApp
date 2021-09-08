package com.pratham.foundation.ui.admin_panel.PullData;

import android.content.Context;
import android.util.Base64;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Crl;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.ModalProgram;
import com.pratham.foundation.database.domain.ModalStates;
import com.pratham.foundation.database.domain.RaspCrl;
import com.pratham.foundation.database.domain.RaspGroup;
import com.pratham.foundation.database.domain.RaspProgram;
import com.pratham.foundation.database.domain.RaspStudent;
import com.pratham.foundation.database.domain.RaspVillage;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.utility.APIs;
import com.pratham.foundation.utility.FC_Constants;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by PEF on 20/11/2018.
 */

public class PullDataPresenterImp implements PullDataContract.PullDataPresenter, API_Content_Result {
    Context context;
    PullDataContract.PullDataView pullDataView;
    String selectedBlock;
    String selectedProgram;
    int count = 0;
    int groupCount = 0;
    ArrayList<Village> villageList = new ArrayList<>();
//    ArrayList<RaspVillage> raspVillageList = new ArrayList<>();

    List<Crl> crlList = new ArrayList<>();
    List<Student> studentList = new ArrayList();
    List<Groups> groupList = new ArrayList();
    List<String> villageIDList = new ArrayList();
    List<ModalStates> modalStates = new ArrayList();
    Boolean isConnectedToRasp = false;
    API_Content api_content;

    public PullDataPresenterImp(Context context, PullDataContract.PullDataView pullDataView) {
        this.context = context;
        this.pullDataView = pullDataView;
        checkConnectivity();
        api_content = new API_Content(context, this);
    }

    List<ModalProgram> prgrmList = new ArrayList<>();
    Gson gson = new Gson();

    @Override
    public void loadPrgramsSpinner() {
        //fetch programs and add to list
        if (isConnectedToRasp) {
            //fetch from raspberry pie device
            AndroidNetworking.get(FC_Constants.URL.DATASTORE_RASPBERY_PROGRAM_STATE_URL.toString())
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham")).build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Success - Add programs to list
                            prgrmList.clear();
                            Type listType = new TypeToken<List<RaspProgram>>() {
                            }.getType();
                            List<RaspProgram> prgm = gson.fromJson(response.toString(), listType);
                            if (prgm != null) {
                                for (RaspProgram prg : prgm) {
                                    ModalProgram mp = new ModalProgram();
                                    mp.setProgramId(prg.getData().getKolibriProgramId());
                                    mp.setProgramName(prg.getData().getKolibriProgramName());
                                    prgrmList.add(mp);
                                }
                                ModalProgram mp = new ModalProgram();
                                mp.setProgramId("-1");
                                mp.setProgramName("Select Program");
                                LinkedHashSet hs = new LinkedHashSet(prgrmList);//to remove redundant values
                                prgrmList.clear();
                                prgrmList.addAll(hs);
                                prgrmList.add(0, mp);
                                pullDataView.showProgram(prgrmList);
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                        }
                    });
        } else {
            //fetch from server
            AndroidNetworking.get(FC_Constants.URL.PULL_PROGRAMS.toString())
                    .addHeaders("Content-Type", "application/json").build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Success - Add programs to list
                            prgrmList.clear();
                            Type listType = new TypeToken<List<ModalProgram>>() {
                            }.getType();
                            prgrmList = gson.fromJson(response.toString(), listType);
                            if (prgrmList != null) {
                                ModalProgram modalProgram = new ModalProgram();
                                modalProgram.setProgramId("-1");
                                modalProgram.setProgramName("Select Program");
                                LinkedHashSet hs = new LinkedHashSet(prgrmList);//to remove redundant values
                                prgrmList.clear();
                                prgrmList.addAll(hs);
                                prgrmList.add(0, modalProgram);
                                pullDataView.showProgram(prgrmList);
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                        }
                    });
        }
    }

    @Override
    public void loadSpinner(String selectedProgramId) {
        //fetch states and add it to list
        if (isConnectedToRasp) {
            //fetch from raspberry pie device
            String[] states = context.getResources().getStringArray(R.array.india_states);
            String[] codes = context.getResources().getStringArray(R.array.india_states_shortcode);
            modalStates.clear();
            for (int i = 0; i < context.getResources().getStringArray(R.array.india_states).length; i++) {
                modalStates.get(i).setProgramId(Integer.parseInt(selectedProgramId));
                modalStates.get(i).setStateCode(codes[i]);
                modalStates.get(i).setStateName(states[i]);
            }
            pullDataView.showStatesSpinner(modalStates);
        } else {
            //fetch from server as per selected program
            AndroidNetworking.get(FC_Constants.URL.PULL_STATES.toString() + "" + selectedProgramId).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    //Success - Add programs to list
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ModalStates>>() {
                    }.getType();
                    ArrayList<ModalStates> modalStatesTemp = gson.fromJson(response.toString(), listType);
                    modalStates.clear();
                    modalStates.addAll(modalStatesTemp);
                    pullDataView.closeProgressDialog();
                    pullDataView.showStatesSpinner(modalStates);
                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                }
            });
        }
    }

    @Override
    public void proccessVillageData(String block) {
        ArrayList<Village> villageName = new ArrayList();
/*
        if (isConnectedToRasp) {
            for (RaspVillage raspVillage : raspVillageList) {
                Village village = raspVillage.getData();
                if (block.equalsIgnoreCase(village.getBlock().trim()))
                    villageName.add(new Village(village.getVillageId(), village.getVillageName()));
            }
        } else {
*/
        for (Village village : villageList) {
            if (block.equalsIgnoreCase(village.getBlock().trim()))
                villageName.add(new Village(village.getVillageId(), village.getVillageName()));
        }
//        }
        if (!villageName.isEmpty()) {
            pullDataView.showVillageDialog(villageName);
        }
    }

    @Override
    public void loadBlockSpinner(int pos, String selectedProgram) {
        //fetch blocks and add it to list
        pullDataView.showProgressDialog("loading Blocks");
        selectedBlock = modalStates.get(pos).getStateCode();
        this.selectedProgram = selectedProgram;
        String url;
        if (isConnectedToRasp) {
            //fetch from raspberry device as per selected program and selected state
            url = APIs.pullVillagesKolibriURL + selectedProgram + APIs.KOLIBRI_STATE + selectedBlock;
            api_content.pullFromKolibri(FC_Constants.KOLIBRI_BLOCK, url);
        } else {
            //fetch from server as per selected program and selected state
            url = APIs.pullVillagesServerURL + selectedProgram + APIs.SERVER_STATE + selectedBlock;
            api_content.pullFromInternet(FC_Constants.SERVER_BLOCK, url);
        }
    }

    private String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
    }

    @Override
    public void downloadStudentAndGroup(ArrayList<String> villageIDList1) {
        //download Student groups and CRL
        // 1 download crl
        pullDataView.showProgressDialog("loading..");
        villageIDList.clear();
        villageIDList.addAll(villageIDList1);
        studentList.clear();
        count = 0;
        String url;
        if (isConnectedToRasp) {
            //fetch student from raspberry device as per selected program and selected village
            for (String id : villageIDList) {
                url = APIs.pullStudentsKolibriURL + selectedProgram + APIs.KOLIBRI_VILLAGE + id;
                api_content.pullFromKolibri(FC_Constants.KOLIBRI_STU, url);
            }
        } else {
            //fetch student from server as per selected program and selected village
            for (String id : villageIDList) {
                url = APIs.pullStudentsServerURL + selectedProgram + APIs.SERVER_VILLAGE + id;
                api_content.pullFromInternet(FC_Constants.SERVER_STU, url);
            }
        }
    }

    private void loadStudent(String url) {
        if (isConnectedToRasp) {
            AndroidNetworking.get(url).addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham")).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    count++;
                    String json = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<RaspStudent>>() {
                    }.getType();
                    List<RaspStudent> studentListTemp = gson.fromJson(json, listType);
                    for (RaspStudent raspStudent : studentListTemp) {
                        for (Student student : raspStudent.getData()) {
                            studentList.add(student);
                        }
                    }
                    loadGroups();
                    //dismissDialog();
                }

                @Override
                public void onError(ANError error) {
                    studentList.clear();
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                    // dismissDialog();
                }
            });
        } else {
            AndroidNetworking.get(url).addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham")).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    count++;
                    String json = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Student>>() {
                    }.getType();
                    List<Student> studentListTemp = gson.fromJson(json, listType);
                    if (studentListTemp != null) {
                        studentList.addAll(studentListTemp);
                    }
                    loadGroups();
                    //dismissDialog();
                }

                @Override
                public void onError(ANError error) {
                    studentList.clear();
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                    // dismissDialog();
                }
            });
        }
    }

    private void loadGroups() {
        //fetch groups and add it to list
        if (count >= villageIDList.size()) {
            groupCount = 0;
            groupList.clear();
            String urlgroup;
            if (isConnectedToRasp) {
                //fetch groups from raspberry pie device
                for (String id : villageIDList) {
                    urlgroup = APIs.pullGroupsKolibriURL + selectedProgram + APIs.KOLIBRI_VILLAGE + id;
                    api_content.pullFromKolibri(FC_Constants.KOLIBRI_GRP, urlgroup);
                }
            } else {
                //fetch groups from server
                for (String id : villageIDList) {
                    urlgroup = APIs.pullGroupsServerURL + selectedProgram + APIs.SERVER_VILLAGE + id;
                    api_content.pullFromInternet(FC_Constants.SERVER_GRP, urlgroup);
                }
            }
        }
    }

    private void downloadGroups(String url) {
        if (isConnectedToRasp) {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            groupCount++;
                            String json = response.toString();
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<RaspGroup>>() {
                            }.getType();
                            List<RaspGroup> groupListTemp = gson.fromJson(json, listType);
                            for (RaspGroup raspGroup : groupListTemp) {
                                for (Groups modal_groups : raspGroup.getData()) {
                                    groupList.add(modal_groups);
                                }

                            }
                            loadCRL();
                        }

                        @Override
                        public void onError(ANError error) {
                            studentList.clear();
                            pullDataView.closeProgressDialog();
                            pullDataView.showErrorToast();
                            // dismissDialog();
                        }
                    });
        } else {
            AndroidNetworking.get(url).addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham")).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    groupCount++;
                    String json = response.toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Groups>>() {
                    }.getType();
                    List<Groups> groupListTemp = gson.fromJson(json, listType);
                    if (groupListTemp != null) {
                        groupList.addAll(groupListTemp);
                    }
                    loadCRL();
                }

                @Override
                public void onError(ANError error) {
                    studentList.clear();
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                    // dismissDialog();
                }
            });
        }
    }

    private void loadCRL() {
        //fetch crl and add it to list
        if (groupCount >= villageIDList.size()) {
            String crlURL;
            if (crlList != null) {
                crlList.clear();
            }
            if (isConnectedToRasp) {
                //fetch crl's from raspberry pie device
                crlURL = APIs.pullCrlsKolibriURL + selectedProgram + APIs.KOLIBRI_STATE + selectedBlock;
                api_content.pullFromKolibri(FC_Constants.KOLIBRI_CRL, crlURL);
            } else {
                //fetch crl's from server
                crlURL = APIs.pullCrlsServerURL + selectedProgram + APIs.SERVER_STATECODE + selectedBlock;
                api_content.pullFromInternet(FC_Constants.SERVER_CRL, crlURL);
            }
        }
    }

    private void downloadCRL(String url) {
        if (isConnectedToRasp) {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // do anything with response
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<RaspCrl>>() {
                            }.getType();
                            ArrayList<RaspCrl> crlListTemp = gson.fromJson(response.toString(), listType);
                            crlList.clear();
                            for (RaspCrl raspCrl : crlListTemp) {
                                for (Crl modal_crl : raspCrl.getData()) {
                                    crlList.add(modal_crl);
                                }
                            }
                            pullDataView.closeProgressDialog();
                            pullDataView.enableSaveButton();
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            pullDataView.closeProgressDialog();
                            pullDataView.showErrorToast();
                        }
                    });
        } else {
            AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    // do anything with response
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Crl>>() {
                    }.getType();
                    ArrayList<Crl> crlListTemp = gson.fromJson(response.toString(), listType);
                    crlList.clear();
                    crlList.addAll(crlListTemp);
                    pullDataView.closeProgressDialog();
                    pullDataView.enableSaveButton();
                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    pullDataView.closeProgressDialog();
                    pullDataView.showErrorToast();
                }
            });
        }
    }

    @Override
    public void saveData() {
       /* BaseActivity.crLdao.insertAllCRL(crlList);
        BaseActivity.studentDao.insertAllStudents(studentList);
        BaseActivity.groupDao.insertAllGroups(groupList);
        BaseActivity.villageDao.insertAllVillages(villageList.get(0).getData());
       */

        //insert data to database
        AppDatabase.getDatabaseInstance(context).getCrlDao().insertAll(crlList);
        Iterator<Student> i = studentList.iterator();
        while (i.hasNext()) {
            Student stu = i.next(); // must be called before you can call i.remove()
            if (stu.getGender().equalsIgnoreCase("deleted"))
                i.remove();
        }
        AppDatabase.getDatabaseInstance(context).getStudentDao().insertAll(studentList);
        Iterator<Groups> gi = groupList.iterator();
        while (gi.hasNext()) {
            Groups stu = gi.next(); // must be called before you can call i.remove()
            if (stu.getDeviceId().equalsIgnoreCase("deleted"))
                gi.remove();
        }
        AppDatabase.getDatabaseInstance(context).getGroupsDao().insertAllGroups(groupList);
        //if (isConnectedToRasp) {
        saveDownloadedVillages();
        AppDatabase.getDatabaseInstance(context).getStatusDao().updateValue("programId", selectedProgram);
        // } else
        // AppDatabase.getDatabaseInstance(context).getVillageDao().insertAllVillages(villageList);
        BackupDatabase.backup(context);
        pullDataView.openLoginActivity();
    }

    private void saveDownloadedVillages() {
//        List<Modal_Village> allStudent = villageList.get(0).getData();
        for (Village vill : villageList) {
            if (villageIDList.contains(String.valueOf(vill.getVillageId())))
                AppDatabase.getDatabaseInstance(context).getVillageDao().insertVillage(vill);
        }
    }


    @Override
    public void clearLists() {
        if (crlList != null) {
            crlList.clear();
        }
        if (studentList != null) {
            studentList.clear();
        }
        if (groupList != null) {
            groupList.clear();
        }
//        if (isConnectedToRasp) {
//            if (raspVillageList != null) {
//                raspVillageList.clear();
//            }
//        } else {
        if (villageList != null) {
            villageList.clear();
        }
//        }
        if (villageIDList != null) {
            villageIDList.clear();
        }
        pullDataView.clearStateSpinner();
        pullDataView.clearBlockSpinner();
        pullDataView.disableSaveButton();
    }

    @Override
    public void onSaveClick() {
//        if (isConnectedToRasp)
//            pullDataView.shoConfermationDialog(crlList.size(), studentList.size(), groupList.size(), raspVillageList.size());
//        else
        pullDataView.shoConfermationDialog(crlList.size(), studentList.size(), groupList.size(), villageList.size());
    }

    @Override
    public void checkConnectivity() {
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork()) {
            //callOnlineContentAPI(contentList, parentId);
        } else if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                //  if (FastSave.getInstance().getString(FC_Constants.FACILITY_ID, "").isEmpty())
                isConnectedToRasp = checkConnectionForRaspberry();
                //callKolibriAPI(contentList, parentId);
            } else {
                isConnectedToRasp = false;
                //callOnlineContentAPI(contentList, parentId);
            }
        } else {
            pullDataView.showNoConnectivity();
        }
    }

    public boolean checkConnectionForRaspberry() {
        boolean isRaspberry = false;
        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                try {
                    isRaspberry = true;
                    /*JSONObject object = new JSONObject();
                    object.put("username", "pratham");
                    object.put("password", "pratham");
                    new PD_ApiRequest(context, ContentPresenterImpl.this)
                            .getacilityIdfromRaspberry(FC_Constants.FACILITY_ID, FC_Constants.RASP_IP + "/api/session/", object);*/
                } catch (Exception e) {
                    isRaspberry = false;
                    e.printStackTrace();
                }
            }
        } else isRaspberry = false;
        return isRaspberry;
    }

    //    List<Village> vilageList = new ArrayList<>();
    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Override
    public void receivedContent(String header, String response) {
        Gson gson = new Gson();
        if (header.equalsIgnoreCase(FC_Constants.KOLIBRI_BLOCK)) {
            List<String> blockList = new ArrayList<>();
            Type listType = new TypeToken<List<RaspVillage>>() {
            }.getType();
            List<RaspVillage> raspvilageList = gson.fromJson(response, listType);
            if (raspvilageList != null) {
                if (raspvilageList.isEmpty()) {
                    blockList.add("NO BLOCKS");
                } else {
                    blockList.add("Select block");
                    for (RaspVillage raspVillage : raspvilageList) {
                        //                                    for (Modal_Village village : raspVillage.getData()) {
                        villageList.add(raspVillage.getData());
                        blockList.add(raspVillage.getData().getBlock());
                        //                                    }
                    }
                }
                LinkedHashSet hs = new LinkedHashSet(blockList);
                blockList.clear();
                blockList.addAll(hs);
                LinkedHashSet hs1 = new LinkedHashSet(villageList);
                villageList.clear();
                villageList.addAll(hs1);
                pullDataView.showBlocksSpinner(blockList);
            }
            pullDataView.closeProgressDialog();
        } else if (header.equalsIgnoreCase(FC_Constants.SERVER_BLOCK)) {
            List<String> blockList = new ArrayList<>();
            Type listType = new TypeToken<List<Village>>() {
            }.getType();
            villageList = gson.fromJson(response, listType);
            if (villageList != null) {
                if (villageList.isEmpty()) {
                    blockList.add("NO BLOCKS");
                } else {
                    blockList.add("Select block");
                    for (Village vill : villageList) {
                        blockList.add(vill.getBlock());
                    }
                }
                LinkedHashSet hs = new LinkedHashSet(blockList);
                blockList.clear();
                blockList.addAll(hs);
                pullDataView.showBlocksSpinner(blockList);
            }
            pullDataView.closeProgressDialog();
        } else if (header.equalsIgnoreCase(FC_Constants.KOLIBRI_STU)) {
            count++;
            String json = response;
            gson = new Gson();
            Type listType = new TypeToken<List<RaspStudent>>() {
            }.getType();
            List<RaspStudent> studentListTemp = gson.fromJson(json, listType);
            for (RaspStudent raspStudent : studentListTemp) {
                for (Student student : raspStudent.getData()) {
                    studentList.add(student);
                }
            }
            loadGroups();
        } else if (header.equalsIgnoreCase(FC_Constants.SERVER_STU)) {
            count++;
            String json = response;
            gson = new Gson();
            Type listType = new TypeToken<List<Student>>() {
            }.getType();
            List<Student> studentListTemp = gson.fromJson(json, listType);
            if (studentListTemp != null) {
                studentList.addAll(studentListTemp);
            }
            loadGroups();
        } else if (header.equalsIgnoreCase(FC_Constants.KOLIBRI_GRP)) {
            groupCount++;
            String json = response;
            gson = new Gson();
            Type listType = new TypeToken<List<RaspGroup>>() {
            }.getType();
            List<RaspGroup> groupListTemp = gson.fromJson(json, listType);
            for (RaspGroup raspGroup : groupListTemp) {
                for (Groups modal_groups : raspGroup.getData()) {
                    groupList.add(modal_groups);
                }

            }
            loadCRL();
        } else if (header.equalsIgnoreCase(FC_Constants.SERVER_GRP)) {
            groupCount++;
            String json = response;
            gson = new Gson();
            Type listType = new TypeToken<List<Groups>>() {
            }.getType();
            List<Groups> groupListTemp = gson.fromJson(json, listType);
            if (groupListTemp != null) {
                groupList.addAll(groupListTemp);
            }
            loadCRL();
        } else if (header.equalsIgnoreCase(FC_Constants.KOLIBRI_CRL)) {
            gson = new Gson();
            Type listType = new TypeToken<List<RaspCrl>>() {
            }.getType();
            ArrayList<RaspCrl> crlListTemp = gson.fromJson(response, listType);
            crlList.clear();
            for (RaspCrl raspCrl : crlListTemp) {
                for (Crl modal_crl : raspCrl.getData()) {
                    crlList.add(modal_crl);
                }
            }
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        } else if (header.equalsIgnoreCase(FC_Constants.SERVER_CRL)) {
            gson = new Gson();
            Type listType = new TypeToken<List<Crl>>() {
            }.getType();
            ArrayList<Crl> crlListTemp = gson.fromJson(response, listType);
            crlList.clear();
            crlList.addAll(crlListTemp);
            pullDataView.closeProgressDialog();
            pullDataView.enableSaveButton();
        }
    }

    @Override
    public void receivedError(String header) {
        pullDataView.showErrorToast();
    }
}
