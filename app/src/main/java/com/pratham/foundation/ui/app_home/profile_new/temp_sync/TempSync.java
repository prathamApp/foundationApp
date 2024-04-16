package com.pratham.foundation.ui.app_home.profile_new.temp_sync;


import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_temp_sync)
public class TempSync extends BaseActivity {

    private static final String TAG = "TextProcessing";
    private static final String checklistFilePath = "checklist.json";

    @AfterViews
    public void initiate() {
    }


    @Click(R.id.goto_results)
    public void button1Click() {
        String originalText = "गिर रात हो गई। चाँद दिख रहा था। तेरे भी तारे भी चमक रहे हैं। सब लोग सो गए ह";
        String recognitionOutput = "रात हो गई, चांद दिख रहा था। तेरे भी तारे भी चमक रहे हैं। सब लोग सो गए हैं। ऐसे मत भी गल";

        Map<String, Object> result = new HashMap<>();
        result = TextProcessing.identifyMistakes(originalText,recognitionOutput);
/*        String[] originalWords = originalText.split("\\s+");
        String[] recognitionWords = recognitionOutput.split("\\s+");
*//*        int[] result = calculateLevenshteinDistance(originalWords, recognitionWords);
        int insertions = result[0];
        int substitutions = result[1];
        int deletions = result[2];
        System.out.println("Insertions: " + insertions);
        System.out.println("Substitutions: " + substitutions);
        System.out.println("Deletions: " + deletions);*//*
        String[] words1 = "hello world".split("\\s+");
        String[] words2 = "hola world".split("\\s+");

        int distance = calculateWordLevelLevenshteinDistance(originalWords, recognitionWords);
        System.out.println("Word Level Levenshtein Distance: " + distance);*/

    }

    public static int calculateWordLevelLevenshteinDistance(String[] words1, String[] words2) {
        int lenWords1 = words1.length + 1;
        int lenWords2 = words2.length + 1;

        // Create a matrix to store the distances between words
        int[][] matrix = new int[lenWords1][lenWords2];

        // Initialize the matrix with initial values
        for (int i = 0; i < lenWords1; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j < lenWords2; j++) {
            matrix[0][j] = j;
        }

        // Fill in the matrix with minimum edit distances
        for (int i = 1; i < lenWords1; i++) {
            for (int j = 1; j < lenWords2; j++) {
                int cost = words1[i - 1].equals(words2[j - 1]) ? 0 : 1;
                matrix[i][j] = Math.min(
                        matrix[i - 1][j] + 1,          // Deletion
                        Math.min(
                                matrix[i][j - 1] + 1,      // Insertion
                                matrix[i - 1][j - 1] + cost // Substitution
                        )
                );
            }
        }

        return matrix[lenWords1 - 1][lenWords2 - 1];
    }


    private static int[] calculateLevenshteinDistance(String[] s1, String[] s2) {
        int len1 = s1.length + 1;
        int len2 = s2.length + 1;
        int[][] matrix = new int[len1][len2];
        for (int i = 0; i < len1; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j < len2; j++) {
            matrix[0][j] = j;
        }
        for (int i = 1; i < len1; i++) {
            for (int j = 1; j < len2; j++) {
                int cost = (s1[i - 1].equals(s2[j - 1])) ? 0 : 1;
                matrix[i][j] = Math.min(matrix[i - 1][j] + 1,           // Deletion
                        Math.min(matrix[i][j - 1] + 1,                   // Insertion
                                matrix[i - 1][j - 1] + cost));          // Substitution
            }
        }
        int[] result = new int[3];
        result[0] = matrix[len1 - 1][len2 - 1];  // Insertions
        result[1] = countSubstitutions(matrix, s1, s2);  // Substitutions
        result[2] = countDeletions(matrix, s1, s2);  // Deletions
        return result;
    }
    private static int countSubstitutions(int[][] matrix, String[] s1, String[] s2) {
        int count = 0;
        int i = matrix.length - 1;
        int j = matrix[0].length - 1;
        while (i > 0 && j > 0) {
            int cost = (s1[i - 1].equals(s2[j - 1])) ? 0 : 1;
            if (matrix[i][j] == matrix[i - 1][j - 1] + cost) {
                count += cost;
            }
            i--;
            j--;
        }
        return count;
    }
    private static int countDeletions(int[][] matrix, String[] s1, String[] s2) {
        int count = 0;
        int i = matrix.length - 1;
        int j = matrix[0].length - 1;
        while (i > 0) {
            if (matrix[i][j] == matrix[i - 1][j] + 1) {
                count++;
            }
            i--;
        }
        return count;
    }

}
