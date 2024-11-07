package com.pratham.foundation.ui.app_home.profile_new.temp_sync;


import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextProcessing {

    private static final String TAG = "TextProcessing";
    private static final String checklistFilePath = "checklist.json";

    // Define a method to remove nuktas from text
    public static String removeNuktas(String text) {
        String updatedText = text;

        updatedText = updatedText.replace("\\u0929", "\\u0928\\u093c");
        updatedText = updatedText.replace("\\u0931", "\\u0930\\u093c");
        updatedText = updatedText.replace("\\u0934", "\\u0933\\u093c");
        updatedText = updatedText.replace("\\u0958", "\\u0915\\u093c");
        updatedText = updatedText.replace("\\u0959", "\\u0916\\u093c");
        updatedText = updatedText.replace("\\u095A", "\\u0917\\u093c");
        updatedText = updatedText.replace("\\u095B", "\\u091C\\u093c");
        updatedText = updatedText.replace("\\u095C", "\\u0921\\u093c");
        updatedText = updatedText.replace("\\u095D", "\\u0922\\u093c");
        updatedText = updatedText.replace("\\u095E", "\\u092B\\u093c");
        updatedText = updatedText.replace("\\u095F", "\\u092F\\u093c");
        updatedText = updatedText.replace("\\u093c", "");

        return updatedText;
    }

    // Define a method to process text using checklist
    public static String processText(String text, Map<String, String> checklist) {
        String updatedText = text.toLowerCase();

        try {
            String checklistJson = FileUtils.readFileToString(new File(Environment.getExternalStorageDirectory(), checklistFilePath), StandardCharsets.UTF_8);
            JSONObject checklistObj = new JSONObject(checklistJson);

            for (Map.Entry<String, String> entry : checklist.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (checklistObj.has(key)) {
                    updatedText = updatedText.replace(key, checklistObj.getString(key));
                }
            }

            String punctMarks = "।'" + Pattern.quote(StringEscapeUtils.escapeJava(string.punctuation));
            for (char punctMark : punctMarks.toCharArray()) {
                updatedText = updatedText.replace(String.valueOf(punctMark), " ");
            }

            updatedText = updatedText.replaceAll(" +", " ");
            updatedText = removeNuktas(updatedText);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return updatedText;
    }

    // Define a method to identify mistakes
    public static Map<String, Object> identifyMistakes(String canonicalText, String readText) {
        Map<String, Object> result = new HashMap<>();

        try {
            String checklistJson = FileUtils.readFileToString(new File(Environment.getExternalStorageDirectory(), checklistFilePath), StandardCharsets.UTF_8);
            JSONObject checklistObj = new JSONObject(checklistJson);

            String processedCanonicalText = processText(canonicalText, checklistObj);
            String processedReadText = processText(readText, checklistObj);

            String[] tokens = processedCanonicalText.split(" ");
            String[] readTokens = processedReadText.split(" ");

            int[][] distance = editDistance(tokens, readTokens);
            char EPS = '*';

            List<String> aligned = align(tokens, readTokens, EPS);
            int idx = -1;
            for (int i = 0; i < aligned.size(); i++) {
                if (aligned.get(i).charAt(0) == tokens[0].charAt(0)) {
                    idx = i;
                    break;
                }
            }

            List<String> alignedTokens = aligned.subList(idx, aligned.size());

            String subs = "";
            String dels = "";

            for (int i = 0; i < alignedTokens.size(); i++) {
                String token = alignedTokens.get(i);
                if (token.charAt(0) != token.charAt(1)) {
                    if (token.charAt(1) == EPS) {
                        dels += i + "-" + token.charAt(0) + ",";
                    } else if (token.charAt(0) != EPS) {
                        subs += i + "-" + token.charAt(0) + ":" + token.charAt(1) + ",";
                    }
                }
            }

            result.put("no_mistakes", distance[0][2] + distance[1][2]);
            result.put("no_del", distance[0][2]);
            result.put("del_details", dels.substring(0, dels.length() - 1));
            result.put("no_sub", distance[1][2]);
            result.put("sub_details", subs.substring(0, subs.length() - 1));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Define a method to calculate duration
    public static double getDuration(String audioPath) {
        double duration = 0.0;

        try {
            File audioFile = new File(audioPath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            duration = (frames + 0.0) / format.getFrameRate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return duration;
    }

    // Define a method to calculate WCPM
    public static double getWCPM(String audioPath, String canText, int noMistakes) {
        String punctMarks = "।" + Pattern.quote(string.punctuation);
        String processedCanText = canText.toLowerCase();

        for (char punctMark : punctMarks.toCharArray()) {
            processedCanText = processedCanText.replace(String.valueOf(punctMark), "");
        }

        String[] canWords = processedCanText.split(" ");
        int noCorrectWords = canWords.length - noMistakes;

        double duration = getDuration(audioPath);
        double wcpm = (noCorrectWords * 60) / duration;

        return wcpm;
    }

    // Define a method to calculate edit distance
    public static int[][] editDistance(String[] s1, String[] s2) {
        int m = s1.length;
        int n = s2.length;

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s1[i - 1].equals(s2[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        int noDel = dp[m][n];
        int noIns = n - m + noDel;
        int noSub = m - noDel;
        int[][] result = {{noDel, noIns, noSub}};

        return result;
    }

    // Define a method to align two strings
    public static List<String> align(String[] s1, String[] s2, char EPS) {
        int[][] dp = editDistance(s1, s2);
        int m = s1.length;
        int n = s2.length;
        List<String> result = new ArrayList<>();
        int i = m;
        int j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s1[i - 1].equals(s2[j - 1])) {
                result.add(s1[i - 1] + s2[j - 1]);
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                result.add(s1[i - 1] + EPS);
                i--;
            } else {
                result.add(EPS + s2[j - 1]);
                j--;
            }
        }

        Collections.reverse(result);
        return result;
    }

}
