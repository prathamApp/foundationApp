package com.pratham.foundation.ui.admin_panel.andmin_login_new.pull_and_asign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

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
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.ModalProgram;
import com.pratham.foundation.database.domain.ModalStates;
import com.pratham.foundation.database.domain.RaspGroup;
import com.pratham.foundation.database.domain.RaspProgram;
import com.pratham.foundation.database.domain.RaspStudent;
import com.pratham.foundation.database.domain.RaspVillage;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.utility.APIs;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@EBean
public class PullAndAssign_Presenter implements PullAndAssign_Contract.PullAndAssignPresenter, API_Content_Result {

    Context mContext;
    PullAndAssign_Contract.PullAndAssignView assignView;
    boolean isConnectedToRasp = false;
    List<ModalProgram> prgrmList = new ArrayList<>();
    //    List<Crl> crlList = new ArrayList<>();
    List<Student> studentList = new ArrayList();
    List<Groups> groupList = new ArrayList();
    List<String> villageIDList = new ArrayList();
    List<ModalStates> modalStates = new ArrayList();
    ArrayList<Village> villageList = new ArrayList<>();
    Gson gson = new Gson();
    API_Content api_content;
    String selectedBlock, selectedProgram,myBlock;
    int count, groupCount;

    public PullAndAssign_Presenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(PullAndAssign_Contract.PullAndAssignView assignView) {
        this.assignView = assignView;
        api_content = new API_Content(mContext, this);
    }

    @Override
    public void checkConnectivity() {
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork()) {
            loadProgramsSpinner();
            //callOnlineContentAPI(contentList, parentId);
        } else if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                //  if (FastSave.getInstance().getString(FC_Constants.FACILITY_ID, "").isEmpty())
                isConnectedToRasp = checkConnectionForRaspberry();
                loadProgramsSpinner();
                //callKolibriAPI(contentList, parentId);
            } else {
                isConnectedToRasp = false;
                loadProgramsSpinner();
                //callOnlineContentAPI(contentList, parentId);
            }
        } else {
            assignView.showNoConnectivity();
        }
    }

    private String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
    }

    @Background
    public void loadProgramsSpinner() {
        if (isConnectedToRasp) {
            AndroidNetworking.get(FC_Constants.URL.DATASTORE_RASPBERY_PROGRAM_STATE_URL.toString())
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham")).build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
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
                                assignView.showProgram(prgrmList);
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                        }
                    });
        } else {
            String url = FC_Constants.URL.PULL_PROGRAMS.toString();
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json").build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
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
                                assignView.showProgram(prgrmList);
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("PullAndAssign", "onError: "+error.getMessage());
                            assignView.closeProgressDialog();
                            assignView.showErrorToast();
                            // handle error
                        }
                    });
        }
    }

    @Override
    public void loadStateSpinner(String selectedProgramId) {
        if (isConnectedToRasp) {
            String[] states = mContext.getResources().getStringArray(R.array.india_states);
            String[] codes = mContext.getResources().getStringArray(R.array.india_states_shortcode);
            modalStates.clear();
            for (int i = 0; i < mContext.getResources().getStringArray(R.array.india_states).length; i++) {
                modalStates.get(i).setProgramId(Integer.parseInt(selectedProgramId));
                modalStates.get(i).setStateCode(codes[i]);
                modalStates.get(i).setStateName(states[i]);
            }
            assignView.showStatesSpinner(modalStates);
        } else {
            String url = FC_Constants.URL.PULL_STATES.toString() + "" + selectedProgramId;
            AndroidNetworking.get(url)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    // do anything with response
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ModalStates>>() {
                    }.getType();
                    ArrayList<ModalStates> modalStatesTemp = gson.fromJson(response.toString(), listType);
                    modalStates.clear();
                    modalStates.addAll(modalStatesTemp);
                    assignView.closeProgressDialog();
                    assignView.showStatesSpinner(modalStates);
                }

                @Override
                public void onError(ANError error) {
                    Log.d("PullAndAssign", "onError: "+error.getMessage());
                    // handle error
                    assignView.closeProgressDialog();
                    assignView.showErrorToast();
                }
            });
        }
    }

    @Override
    public void loadBlockSpinner(int pos, String selectedProgram) {

        assignView.showProgressDialog("loading Blocks");
        selectedBlock = modalStates.get(pos).getStateCode();
        this.selectedProgram = selectedProgram;
        String url;
        if (isConnectedToRasp) {
            url = APIs.pullVillagesKolibriURL + selectedProgram + APIs.KOLIBRI_STATE + selectedBlock;
            api_content.pullFromKolibri(FC_Constants.KOLIBRI_BLOCK, url);
        } else {
            url = APIs.pullVillagesServerURL + selectedProgram + APIs.SERVER_STATE + selectedBlock;
            api_content.pullFromInternet(FC_Constants.SERVER_BLOCK, url);
        }
    }

//    @Override
    public void loadVillageSpinner(String selectedBlock) {
        assignView.showProgressDialog("loading Villages");
        this.selectedProgram = selectedProgram;
        this.selectedBlock = selectedBlock;
        String url;
        if (isConnectedToRasp) {
            url = APIs.pullVillagesKolibriURL + selectedProgram + APIs.KOLIBRI_STATE + selectedBlock;
            api_content.pullFromKolibri(FC_Constants.KOLIBRI_BLOCK, url);
        } else {
            url = APIs.pullVillagesServerURL + selectedProgram + APIs.SERVER_STATE + selectedBlock;
            api_content.pullFromInternet(FC_Constants.SERVER_BLOCK, url);
        }
    }

    @Override
    public void proccessVillageData(String block) {
        myBlock = block;
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
            assignView.showVillageDialog(villageName);
        }
    }

    @Override
    public void downloadStudentAndGroup(ArrayList<String> villageIDList1) {
        //download Student groups and CRL
        // 1 download crl
        assignView.showProgressDialog("loading..");
        villageIDList.clear();
        villageIDList.addAll(villageIDList1);
        studentList.clear();
        count = 0;
        String url;
        if (isConnectedToRasp) {
            for (String id : villageIDList) {
                url = APIs.pullStudentsKolibriURL + selectedProgram + APIs.KOLIBRI_VILLAGE + id;
                api_content.pullFromKolibri(FC_Constants.KOLIBRI_STU, url);
            }
        } else {
            for (String id : villageIDList) {
                url = APIs.pullStudentsServerURL + selectedProgram + APIs.SERVER_VILLAGE + id;
                api_content.pullFromInternet(FC_Constants.SERVER_STU, url);
            }
        }
    }

    private void loadGroups() {
        if (count >= villageIDList.size()) {
            groupCount = 0;
            groupList.clear();
            String urlgroup;
            if (isConnectedToRasp) {
                for (String id : villageIDList) {
                    urlgroup = APIs.pullGroupsKolibriURL + selectedProgram + APIs.KOLIBRI_VILLAGE + id;
                    api_content.pullFromKolibri(FC_Constants.KOLIBRI_GRP, urlgroup);
                }
            } else {
                for (String id : villageIDList) {
                    urlgroup = APIs.pullGroupsServerURL + selectedProgram + APIs.SERVER_VILLAGE + id;
                    api_content.pullFromInternet(FC_Constants.SERVER_GRP, urlgroup);
                }
            }
        }
    }

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
                assignView.showBlocksSpinner(blockList);
            }
            assignView.closeProgressDialog();
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
                assignView.showBlocksSpinner(blockList);
            }
            assignView.closeProgressDialog();
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
            saveData();
//            loadCRL();
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
            assignView.closeProgressDialog();
            saveData();
//            loadCRL();
        } /*else if (header.equalsIgnoreCase(FC_Constants.KOLIBRI_CRL)) {
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
            assignView.closeProgressDialog();
            assignView.enableSaveButton();
        } else if (header.equalsIgnoreCase(FC_Constants.SERVER_CRL)) {
            gson = new Gson();
            Type listType = new TypeToken<List<Crl>>() {
            }.getType();
            ArrayList<Crl> crlListTemp = gson.fromJson(response, listType);
            crlList.clear();
            crlList.addAll(crlListTemp);
            assignView.closeProgressDialog();
            assignView.enableSaveButton();
        }*/
    }

    public void saveData() {
        AppDatabase.getDatabaseInstance(mContext).getStudentDao().insertAll(studentList);
        Iterator<Groups> gi = groupList.iterator();
        while (gi.hasNext()) {
            Groups stu = gi.next(); // must be called before you can call i.remove()
            if (stu.getDeviceId().equalsIgnoreCase("deleted"))
                gi.remove();
        }
        AppDatabase.getDatabaseInstance(mContext).getGroupsDao().insertAllGroups(groupList);
        saveDownloadedVillages();
        AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue("programId", selectedProgram);
        BackupDatabase.backup(mContext);
        fetchVillageData();
    }

    @Background
    public void fetchVillageData() {
        try {
            List<Village> blocksVillagesList = AppDatabase.getDatabaseInstance(mContext).getVillageDao().GetVillages(myBlock);
            assignView.showVillageSpinner(blocksVillagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getAllGroups(int vilID) {
        try {
            List<Groups> dbgroupList = AppDatabase.getDatabaseInstance(mContext).getGroupsDao().GetGroups(vilID);
            assignView.populateGroups(dbgroupList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDownloadedVillages() {
        for (Village vill : villageList) {
            if (villageIDList.contains(String.valueOf(vill.getVillageId())))
                AppDatabase.getDatabaseInstance(mContext).getVillageDao().insertVillage(vill);
        }
    }

    @SuppressLint("HardwareIds")
    @Background
    @Override
    public void updateDBData(String group1, String group2, String group3, String group4, String group5, int vilID) {
        try {
            AppDatabase.getDatabaseInstance(mContext).getStudentDao().deleteDeletedStdRecords();
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue(FC_Constants.GROUPID1, group1);
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue(FC_Constants.GROUPID2, group2);
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue(FC_Constants.GROUPID3, group3);
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue(FC_Constants.GROUPID4, group4);
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue(FC_Constants.GROUPID5, group5);

            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue("village", Integer.toString(vilID));
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue("DeviceId", "" + Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue("ActivatedDate", FC_Utility.getCurrentDateTime());
            AppDatabase.getDatabaseInstance(mContext).getStatusDao().updateValue("ActivatedForGroups", group1 + "," + group2 + "," + group3 + "," + group4 + "," + group5);

            assignView.groupAssignSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedError(String header) {
        assignView.showErrorToast();
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

    @Override
    public void clearLists() {
/*        if (crlList != null) {
            crlList.clear();
        }*/
        if (studentList != null) {
            studentList.clear();
        }
        if (groupList != null) {
            groupList.clear();
        }
        if (villageList != null) {
            villageList.clear();
        }
        if (villageIDList != null) {
            villageIDList.clear();
        }
//        assignView.clearStateSpinner();
//        assignView.clearBlockSpinner();
//        assignView.disableSaveButton();
    }

}