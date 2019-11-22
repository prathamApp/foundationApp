package com.pratham.foundation.utility;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.ZoomImageDialog;
import com.pratham.foundation.ui.admin_panel.AdminControlsActivity_;
import com.pratham.foundation.ui.admin_panel.group_selection.SelectGroupActivity_;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.splash_activity.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class FC_Utility {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public static String loadJSONFromStorage(String readingContentPath, String fileName) {
        JSONArray jsonObj = null;
        try {
            InputStream is = null;
            is = new FileInputStream(readingContentPath + fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            jsonObj = new JSONArray(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return jsonObj.toString();
    }
    private static int[] gradiant_bg = new int[]{
            R.drawable.gradient_one, R.drawable.gradient_two, R.drawable.gradient_three,
            R.drawable.gradient_four,R.drawable.gradient_five};

    public static int getRandomDrawableGradiant(){
        int bg_grad = gradiant_bg[new Random().nextInt(gradiant_bg.length)];
        return bg_grad;
    }

    public static StateListDrawable createStateListDrawable(Drawable drawable, @ColorInt int drawableColor) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] defaultStateSet = {-android.R.attr.state_pressed, -android.R.attr.state_focused, android.R.attr.state_enabled};
        stateListDrawable.addState(defaultStateSet, drawable);

        int[] focusedStateSet = {-android.R.attr.state_pressed, android.R.attr.state_focused};
        Drawable focusedDrawable = darkenDrawable(drawable, drawableColor, 0.7f);
        stateListDrawable.addState(focusedStateSet, focusedDrawable);

        int[] pressedStateSet = {android.R.attr.state_pressed};
        Drawable pressedDrawable = darkenDrawable(drawable, drawableColor, 0.6f);
        stateListDrawable.addState(pressedStateSet, pressedDrawable);

        int[] disableStateSet = {-android.R.attr.state_enabled};
        Drawable disableDrawable = darkenDrawable(drawable, drawableColor, 0.4f);
        stateListDrawable.addState(disableStateSet, disableDrawable);

        return stateListDrawable;
    }

    private static Drawable darkenDrawable(Drawable drawable, @ColorInt int drawableColor, float factor) {
        int color = darkenColor(drawableColor, factor);
        Drawable d = drawable.getConstantState().newDrawable().mutate();
        d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return d;
    }

    @ColorInt
    private static int darkenColor(@ColorInt int color, float factor) {
        if (factor < 0 || factor > 1) {
            return color;
        }
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha,
                Math.max((int) (red * factor), 0),
                Math.max((int) (green * factor), 0),
                Math.max((int) (blue * factor), 0));
    }

    public static String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }

    public static String getLocalHostName() {
        String str1 = Build.MODEL;
//        String str2 = getRandomNumStr(3);
        return str1 /*+ "_" + str2*/;
    }

    public static int dp2px(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    public static boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void showAlertDialogue(Context act, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static boolean checkIfPermissionGranted(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(permission, context.getPackageName());
        return hasPerm == PackageManager.PERMISSION_GRANTED;
    }

    public static String getCurrentVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(context.getPackageName(), 0);

        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        String currentVersion = pInfo.versionName;
        return currentVersion;
    }

    public static String getRandomNumStr(int NumLen) {
        Random random = new Random(System.currentTimeMillis());
        StringBuffer str = new StringBuffer();
        int i, num;
        for (i = 0; i < NumLen; i++) {
            num = random.nextInt(10); // 0-10的随机数
            str.append(num);
        }
        return str.toString();
    }

    public static int generateRandomNum(int NumLen) {
        int no = 0;
        Random rand = new Random();
        no = rand.nextInt(NumLen);
        return no;
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / (context.getResources().getDimension(R.dimen._160sdp)));
        return noOfColumns;
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getVersion() {
        Context context = ApplicationClass.getInstance();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            Log.d("Unable to find", "the name " + packageName + " in the package");
            return null;
        }
    }

    static int Display_Year = 0;
    static int Dont_Disclose = 0;
    private static String TAG = "Utility";
    static Dialog mDateTimeDialog = null;

    public static final Pattern otp_pattern = Pattern.compile("(|^)\\d{4}");

    /**
     * Method to Hide Soft Input Keyboard
     *
     * @param act
     */
    public static void HideInputKeypad(Activity act) {

        InputMethodManager inputManager = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (act.getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(act.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static String getInternalPath(Context context) {
        File[] intDir = context.getExternalFilesDirs("");
        File mydir = null;

        mydir = new File(intDir[0].getAbsolutePath() + "/.FCA/App_Thumbs");
        if (mydir.exists())
            return intDir[0].getAbsolutePath();
        else if (intDir.length > 1) {
            try {
                File file = new File(intDir[1].getAbsolutePath(), "hello.txt");
                if (!file.exists())
                    file.createNewFile();
                file.delete();
                FC_Constants.STORING_IN = "SD-Card";
                return intDir[1].getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                FC_Constants.STORING_IN = "Internal Storage";
                return intDir[0].getAbsolutePath();
            }
        } else {
            FC_Constants.STORING_IN = "Internal Storage";
            return intDir[0].getAbsolutePath();
        }
    }

    /**
     * Function to show Fragment
     *
     * @param mActivity
     * @param mFragment
     * @param mBundle
     * @param TAG
     */
    public static void showFragment(Activity mActivity, Fragment mFragment, int frame,
                                    Bundle mBundle, String TAG) {

        if (mBundle != null)
            mFragment.setArguments(mBundle);

/*        if (mActivity instanceof PictureAssessmentActivity) {
            ((PictureAssessmentActivity) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        } else if (mActivity instanceof SelectWordGameActivity && TAG.equals("AllWordsFragment")) {
            ((SelectWordGameActivity) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        } else if (mActivity instanceof SelectWordGameActivity) {
            ((SelectWordGameActivity) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        } else */
        //todo remove#
       /* if (mActivity instanceof ReadingStoryActivity) {
            ((ReadingStoryActivity) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        } else*/ if (mActivity instanceof AdminControlsActivity_) {
            ((AdminControlsActivity_) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
//        } else if (mActivity instanceof Student_profile_activity) {
//            ((Student_profile_activity) mActivity).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(frame, mFragment, TAG)
//                    .addToBackStack(TAG)
//                    .commit();
        } else if (mActivity instanceof SelectGroupActivity_) {
            ((SelectGroupActivity_) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        } else if (mActivity instanceof SplashActivity) {
            ((SplashActivity) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        }else if (mActivity instanceof ContentPlayerActivity_) {
            ((ContentPlayerActivity_) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        }
        //todo remove#
        /*else if (mActivity instanceof ActivityShareReceive_) {
            ((ActivityShareReceive_) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        }
*/
        /*else if (mActivity instanceof Activity_DietForm) {
            ((Activity_DietForm) mActivity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(frame, mFragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        }*/

//        if (mActivity instanceof CW_Tab_One) {
//            DEBUG_LOG(1, TAG, "From " + mActivity.getLocalClassName());
//            ((CW_Tab_One) mActivity).getSupportFragmentManager()
//                    .beginTransaction().replace(R.id.cw_container, mFragment, TAG)
//                    .addToBackStack(TAG).commit();
//        } else if (mActivity instanceof CW_Tab_Two) {
//            ((CW_Tab_Two) mActivity).getSupportFragmentManager()
//                    .beginTransaction().replace(R.id.cw_container, mFragment, TAG)
//                    .addToBackStack(TAG).commit();
//
//        } else if (mActivity instanceof CW_Tab_Three) {
//            ((CW_Tab_Three) mActivity).getSupportFragmentManager()
//                    .beginTransaction().replace(R.id.cw_container, mFragment, TAG)
//                    .addToBackStack(TAG).commit();
//
//        } else if (mActivity instanceof CW_Authentication) {
//            DEBUG_LOG(1, TAG, "From " + mActivity.getLocalClassName());
//
//            ((CW_Authentication) mActivity).getSupportFragmentManager()
//                    .beginTransaction().replace(R.id.auth_container, mFragment, TAG)
//                    .addToBackStack(TAG).commit();
//        }
//
//
    }

    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    public static void setLocale(Context context, String lang) {

        if (lang.equalsIgnoreCase("Hindi"))
            lang = "hi";
        if (lang.equalsIgnoreCase("Marathi"))
            lang = "mr";
        if (lang.equalsIgnoreCase("Kannada"))
            lang = "kn";
        if (lang.equalsIgnoreCase("Telugu"))
            lang = "te";
        if (lang.equalsIgnoreCase("Bengali"))
            lang = "bn";
        if (lang.equalsIgnoreCase("Gujarati"))
            lang = "gu";
        if (lang.equalsIgnoreCase("Punjabi"))
            lang = "pa";
        if (lang.equalsIgnoreCase("Tamil"))
            lang = "ta";
        if (lang.equalsIgnoreCase("Odiya"))
            lang = "or";
        if (lang.equalsIgnoreCase("Malayalam"))
            lang = "ml";
        if (lang.equalsIgnoreCase("Assamese"))
            lang = "as";

        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
/*
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
*/
        /*Configuration conf = context.getResources().getConfiguration();
        conf.setLocale(myLocale);
        context.createConfigurationContext(conf);
        context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());*/
    }

    /**
     * Method to Show Log
     *
     * @param Type
     * @param TAG
     * @param Message
     */
    public static void DEBUG_LOG(int Type, String TAG, String Message) {

        switch (Type) {
            case 0:
                Log.i(TAG, Message);
                break;
            case 1:
                Log.d(TAG, Message);
                break;
            case 2:
                Log.e(TAG, Message);
                break;
            case 3:
                Log.v(TAG, Message);
                break;
            case 4:
                Log.w(TAG, Message);
                break;
            default:
                break;
        }

    }


    /**
     * Function to show toast
     *
     * @param mContext
     * @param str_Message
     */
    public static void ShowToast(Context mContext, String str_Message) {

        Toast.makeText(mContext, str_Message, Toast.LENGTH_SHORT).show();

    }

    public static String GetCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(cal.getTime());
    }

    public static int convertDpToPixels(float dp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int convertDpToSp(float dp, Context context) {
        int sp = (int) (convertDpToPixels(dp, context) / (float) convertSpToPixels(dp, context));
        return sp;
    }

    public static String getExternalPath(Context context) {
        String sdCardPath = null;
        ArrayList<String> sdcard_path = SDCardUtil.getExtSdCardPaths(context);
        for (String path : sdcard_path) {
            String final_sd_path = path;
            if (new File(final_sd_path + "/.AOP_External").exists()) {
                sdCardPath = final_sd_path + "/.AOP_External/";
                break;
            }
        }
        return sdCardPath;
    }

    public static void showLog(String message, String tag) {
        Log.d(tag, message + "");
    }


    public static int getRandomMaleAvatar(Context context) {
        Integer[] drawables = {R.drawable.b1, R.drawable.b2, R.drawable.b3};
        int a = drawables[new Random().nextInt(drawables.length)];
        return a;
    }

    public static int getRandomFemaleAvatar(Context context) {
        Integer[] drawables = {R.drawable.g1, R.drawable.g2, R.drawable.g3};
        int a = drawables[new Random().nextInt(drawables.length)];
        return a;
    }

    public static String getDeviceSerialID() {
        return Build.SERIAL;
    }

    public static String getDeviceID() {
        return Settings.Secure.getString(ApplicationClass.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Function to get scalled bitmap
     *
     * @param mBitmap
     * @return
     */
    public static Bitmap GetScalledImage(Bitmap mBitmap) {

        Bitmap resultBitmap;
        if (mBitmap.getWidth() >= mBitmap.getHeight()) {

            resultBitmap = Bitmap.createBitmap(mBitmap, mBitmap.getWidth() / 2
                            - mBitmap.getHeight() / 2, 0, mBitmap.getHeight(),
                    mBitmap.getHeight());

        } else {

            resultBitmap = Bitmap.createBitmap(mBitmap, 0, mBitmap.getHeight()
                            / 2 - mBitmap.getWidth() / 2, mBitmap.getWidth(),
                    mBitmap.getWidth());
        }

        return resultBitmap;

    }

    /**
     * Function to calculate user age
     *
     * @param DATE
     * @return
     */
    public static int CalculateAge(String DATE) {

        try {

            SimpleDateFormat DOB_SDF = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.US);

            Date mDOB = DOB_SDF.parse(DATE);

            Calendar dob = Calendar.getInstance();
            dob.setTime(mDOB);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < dob
                    .get(Calendar.DAY_OF_MONTH)) {
                age--;
            }

            return age;

            // return Time_SDF.format(_24HourDt).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Method to generate HMAC-SHA256 signature.
     *
     * @param msg       - Message
     * @param keyString - Key according to which signature will be generated.
     * @return - HMAC - SHA256 signature
     */

    public static String getHMACSignature(String msg, String keyString) {

        DEBUG_LOG(3, TAG, "Msg is--->" + msg);
        DEBUG_LOG(3, TAG, "Key is--->" + keyString);

        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec(
                    (keyString).getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.US_ASCII));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

    /**
     * Method to convert Input stream into string.
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Method to check Internet availability, returns true if Device has an
     * Internet connection.
     *
     * @param mContext
     * @return
     */

    public static boolean isInternetAvailable(Context mContext) {

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();

    }

    public static Bitmap getThumbnail(Uri uri, Context mContext)
            throws IOException {
        InputStream input = mContext.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;// optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1)
                || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
                : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 160) ? (originalSize / 160) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;// optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    /**
     * Function to get byte array from file
     *
     * @param mFile
     * @return
     */
    public static byte[] getByteArrayfromFile(File mFile) {
        // File file = new File(Path);
        byte[] b = new byte[(int) mFile.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(mFile);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char) b[i]);
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }

    /**
     * Function to get file from byte array
     *
     * @param args
     */
    public static void getFilefromByteArray(String[] args) {

        String strFilePath = "Your path";
        try {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            String strContent = "Write File using Java ";

            fos.write(strContent.getBytes());
            fos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
        }
    }

    /**
     * Function to rotate image according to angle
     *
     * @param angle
     * @param mBitmap
     * @return
     */
    public static Bitmap RotateImage(int angle, Bitmap mBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
                mBitmap.getHeight(), matrix, true);
        return mBitmap;
    }

    public static String getImagePath(Uri uri, Context mContext) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    /**
     * Function to get Hash Map
     *
     * @param mContext
     */
    public static void GetHashMap(Context mContext) {

        // Add code to print out the key hash
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    "com.coffeewink", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * Function to check position is even or odd
     *
     * @param position
     * @return
     */
    public static boolean isOdd(int position) {
        return (position % 2) != 0;
    }

    /**
     * Function to get real path of image
     *
     * @param contentURI
     * @param mContext
     * @return
     */
    public static String getRealPathFromURI(Uri contentURI, Context mContext) {
        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /**
     * Function to check google play services are installed or not
     *
     * @param mContext
     * @return
     */
//    public static boolean isGooglePlayServicesAvailable(Context mContext) {
//        // Check that Google Play services is available
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(mContext);
//        // If Google Play services is available
//        if (ConnectionResult.SUCCESS == resultCode) {
//            // In debug mode, log the status
//            Log.d("Location Updates", "Google Play services is available.");
//            return true;
//        } else {
//
//            return false;
//        }
//    }

    /**
     * Function to check whether the service is running or not
     *
     * @param serviceClass
     * @param mContext
     * @return
     */
    public static boolean isMyServiceRunning(Class<?> serviceClass,
                                             Context mContext) {
        ActivityManager manager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                DEBUG_LOG(1, TAG, "Service Running");
                return true;
            }
        }

        DEBUG_LOG(1, TAG, "Service Not Running");
        return false;
    }

    public static int getCurrentDPI(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_XXXHIGH:
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                break;
            case DisplayMetrics.DENSITY_HIGH:
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                break;
            case DisplayMetrics.DENSITY_LOW:
                break;
            default:
                break;
        }
        return 0;
    }

    public static String getRootNode(String currentSelectedLanguage) {
        String rootNodeId = "4030";
        switch (currentSelectedLanguage) {
            case FC_Constants.HINDI:
                rootNodeId = "4030";
                break;
            case FC_Constants.ENGLISH:
                rootNodeId = "4030";
                break;
            case FC_Constants.MARATHI:
                rootNodeId = "25030";
                break;
            case FC_Constants.GUJARATI:
                rootNodeId = "27030";
                break;
            case FC_Constants.TELUGU:
                rootNodeId = "33030";
                break;
            case FC_Constants.KANNADA:
                rootNodeId = "35030";
                break;
            case FC_Constants.BENGALI:
                rootNodeId = "37030";
                break;
            case FC_Constants.TAMIL:
                rootNodeId = "42030";
                break;
            case FC_Constants.ODIYA:
                rootNodeId = "44030";
                break;
            case FC_Constants.ASSAMESE:
                rootNodeId = "40030";
                break;
        }
        return rootNodeId;
    }

    public static long getTimeDifference(String start, String end) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            Date date1 = simpleDateFormat.parse(start);
            Date date2 = simpleDateFormat.parse(end);

            long difference = date2.getTime() - date1.getTime();
//        days = (int) (difference / (1000 * 60 * 60 * 24));
//        hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
//        min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
//        hours = (hours < 0 ? -hours : hours);
            Log.i("time_diff", " :: " + difference);
            return difference;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

/*    public static void setLanguage() {
        if(FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)
                .equalsIgnoreCase("English") ||
                FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)
                        .equalsIgnoreCase("Hindi"))
        FC_Constants.currentSelectedLanguage= FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI);
    }*/

    /**
     * calculates age from birth date
     *
     * @param DOB
     */
    public boolean isAdult(String DOB) {

        try {
            SimpleDateFormat mSDF = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.US);
            Date dateOfBirth = mSDF.parse(DOB);
            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                age--;
            } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < dob
                    .get(Calendar.DAY_OF_MONTH)) {
                age--;
            }

            return age >= 18;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

//
//    /**
//     * Function to update Header (Heading) of active activity
//     *
//     * @param activity
//     * @param str_Heading
//     */
//    public static void SetHeading(FragmentActivity activity, String str_Heading) {
//        TextView txt_Heading = (TextView) activity.findViewById(R.id.txt_heading);
//        txt_Heading.setText(str_Heading.toUpperCase());
//    }


    /**
     * Function to get Byte array fom bitmap
     *
     * @param mBitmap
     * @return
     */
    public static byte[] getByteArray(Bitmap mBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] mArray = stream.toByteArray();
        return mArray;
    }


    /**
     * Function to get formatted meeting date "EEE dd, yyyy"
     *
     * @param str_Date
     * @return
     */
    public static String getFormattedDate(String str_Date) {

        SimpleDateFormat Meeting_Date_SDF = new SimpleDateFormat("dd-MM-yyyy",
                Locale.US);
        SimpleDateFormat Meeting_Date_SDF_Formatted = new SimpleDateFormat("MMM dd, yyyy",
                Locale.US);
        try {
            Date mDate = Meeting_Date_SDF.parse(str_Date);
            return Meeting_Date_SDF_Formatted.format(mDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return str_Date;
        }

    }

    /**
     * Function to get formatted meeting date "EEE dd, yyyy"
     *
     * @param str_Time
     * @return
     */
    public static String getFormattedTime(String str_Time) {

        SimpleDateFormat Meeting_Time_SDF = new SimpleDateFormat("HH:mm",
                Locale.US);
        SimpleDateFormat Meeting_Time_SDF_Formatted = new SimpleDateFormat("hh:mm a",
                Locale.US);
        try {
            Date mDate = Meeting_Time_SDF.parse(str_Time);
            return Meeting_Time_SDF_Formatted.format(mDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return str_Time;
        }

    }

    /**
     * Function to get full address from lat and long coordinates
     *
     * @param lat,lng
     * @return
     */
    public static String getAddressFromLocation(Context context, double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String result = null;
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();
                return result;
            } else {
                return "Getting Location....";
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
            return "Location not detected";
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void showTryAgainDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Please check your network connectivity and try again.");
        dialog.show();
    }
//    /**
//     * Function to get name and email address from contacts
//     *
//     * @param mContext
//     * @return
//     */
//    public static ArrayList<Contact> getContacts(Context mContext) {
//
//
//        try {
//            ContentResolver cr = mContext.getContentResolver(); // Activity/Application
//            // android.content.Context
//            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                    null, null, null);
//            ArrayList<Contact> al_Contacts = new ArrayList<Contact>();
//            if (cursor.moveToFirst()) {
//                do {
//                    String id = cursor.getString(cursor
//                            .getColumnIndex(ContactsContract.Contacts._ID));
//
//                    if (Integer
//                            .parseInt(cursor.getString(cursor
//                                    .getColumnIndex(ContactsContract.Contacts._ID))) > 0) {
//                        Cursor pCur = cr.query(
//                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                                null,
//                                ContactsContract.CommonDataKinds.Email.CONTACT_ID
//                                        + " = ?", new String[]{id}, null);
//                        while (pCur.moveToNext()) {
//                            String contactEmail = pCur
//                                    .getString(pCur
//                                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                            String contactName = pCur
//                                    .getString(pCur
//                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//
//                            Contact mContact = new Contact();
//                            mContact.setContact_name(contactName);
//                            mContact.setContact_email(contactEmail);
//                            al_Contacts.add(mContact);
//                            CW_Utility.DEBUG_LOG(1, TAG, "Email :" + contactEmail);
//                            break;
//                        }
//                        pCur.close();
//                    }
//                } while (cursor.moveToNext());
//            }
//            return al_Contacts;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public static Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    public static Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

//    public static AlertDialog showLoader(Context context) {
//        final View dialogView = View.inflate(context, R.layout.sunbaby_dialog, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setView(dialogView);
//        final AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        return dialog;
//    }

//    private void revealShow(View rootView, boolean reveal, final AlertDialog dialog) {
//        final View view = rootView.findViewById(R.id.filter_popup);
//        int w = view.getRight();
//        int h = view.getTop();
//        float maxRadius = (float) Math.sqrt(w * w + h * h);
//        if (reveal) {
//            Animator revealAnimator = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                revealAnimator = ViewAnimationUtils.createCircularReveal(view,
//                        w, h, 0, maxRadius);
//                revealAnimator.setDuration(500);
//            }
//            view.setVisibility(View.VISIBLE);
//            if (revealAnimator != null) {
//                revealAnimator.start();
//            }
//        } else {
//            Animator anim = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                anim = ViewAnimationUtils.createCircularReveal(view, w, h, maxRadius, 0);
//                anim.setDuration(500);
//            }
//            if (anim != null) {
//                anim.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        dialog.dismiss();
//                        view.setVisibility(View.INVISIBLE);
//                    }
//                });
//                anim.start();
//            } else {
//                dialog.dismiss();
//                view.setVisibility(View.INVISIBLE);
//            }
//        }
//    }

    public static int dpToPx(Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static final int ACCELERATE_DECELERATE_INTERPOLATOR = 0;
    public static final int ACCELERATE_INTERPOLATOR = 1;
    public static final int ANTICIPATE_INTERPOLATOR = 2;
    public static final int ANTICIPATE_OVERSHOOT_INTERPOLATOR = 3;
    public static final int BOUNCE_INTERPOLATOR = 4;
    public static final int DECELERATE_INTERPOLATOR = 5;
    public static final int FAST_OUT_LINEAR_IN_INTERPOLATOR = 6;
    public static final int FAST_OUT_SLOW_IN_INTERPOLATOR = 7;
    public static final int LINEAR_INTERPOLATOR = 8;
    public static final int LINEAR_OUT_SLOW_IN_INTERPOLATOR = 9;
    public static final int OVERSHOOT_INTERPOLATOR = 10;

    public static TimeInterpolator createInterpolator(@IntRange(from = 0, to = 10) final int interpolatorType) {
        switch (interpolatorType) {
            case ACCELERATE_DECELERATE_INTERPOLATOR:
                return new AccelerateDecelerateInterpolator();
            case ACCELERATE_INTERPOLATOR:
                return new AccelerateInterpolator();
            case ANTICIPATE_INTERPOLATOR:
                return new AnticipateInterpolator();
            case ANTICIPATE_OVERSHOOT_INTERPOLATOR:
                return new AnticipateOvershootInterpolator();
            case BOUNCE_INTERPOLATOR:
                return new BounceInterpolator();
            case DECELERATE_INTERPOLATOR:
                return new DecelerateInterpolator();
            case FAST_OUT_LINEAR_IN_INTERPOLATOR:
                return new FastOutLinearInInterpolator();
            case FAST_OUT_SLOW_IN_INTERPOLATOR:
                return new FastOutSlowInInterpolator();
            case LINEAR_INTERPOLATOR:
                return new LinearInterpolator();
            case LINEAR_OUT_SLOW_IN_INTERPOLATOR:
                return new LinearOutSlowInInterpolator();
            case OVERSHOOT_INTERPOLATOR:
                return new OvershootInterpolator();
            default:
                return new LinearInterpolator();
        }
    }

    /*This is some working code for downloading a given URL to a given File object.
    The File object (outputFile) has just been created using new File(path),
    I haven't called createNewFile or anything.*/
    private static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public static void toggleHideyBar(AppCompatActivity activity) {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public static String getYouTubeID(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "nothing";
        }
    }

    public static String getDisplayMetrics(AppCompatActivity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (metrics.densityDpi == DisplayMetrics.DENSITY_LOW) {
            return "ldpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
            return "mdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
            return "hdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
            return "xhdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXHIGH) {
            return "xxhdpi";
        } else if (metrics.densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
            return "xxxhdpi";
        } else {
            return "mdpi";
        }
    }

    /*show loader */
    public static void showDialogInApiCalling(Dialog dialog, Context context, String msg) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
        dialog.setTitle(msg);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /* Set Message */
    public static void setMessage(Dialog dialog, String message) {
        if (dialog != null)
            dialog.setTitle(message);
    }

    /*Dismiss loader */
    public static void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static long folderSize(File directory) {
        long length = 0;
        File[] f = directory.listFiles();
        for (File file : f) {
            if (file != null) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
        }
        return length;
    }

    public static <T> T jsonToBean(String jsonResult, Class<T> clz) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonResult, clz);
        return t;
    }

    public static <T> String beanToJson(T clz) {
        Gson gson = new Gson();
        return gson.toJson(clz);
    }

    public static boolean isProfile(String name) {
        switch (name) {
            case "villages.json":
                return true;
            case "groups.json":
                return true;
            case "crls.json":
                return true;
            case "students.json":
                return true;
            default:
                return false;
        }
    }

    public static boolean isUsages(String name) {
        switch (name) {
            case "sessions.json":
                return true;
            case "logs.json":
                return true;
            case "attendance.json":
                return true;
            case "status.json":
                return true;
            case "scores.json":
                return true;
            case "supervisordata.json":
                return true;
            case "contentprogress.json":
                return true;
            case "learntwords.json":
                return true;
            case "assessment.json":
                return true;
            default:
                return false;
        }
    }

    public static String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(cal.getTime());
    }
    public static void showZoomDialog(Context context,String path, String localPath) {
        path=path.replace(" ","");
        localPath=localPath.replace(" ","");
        ZoomImageDialog zoomImageDialog = new ZoomImageDialog(context, path, localPath);
        zoomImageDialog.show();
    }
}
