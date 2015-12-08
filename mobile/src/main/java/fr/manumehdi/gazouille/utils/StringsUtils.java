package fr.manumehdi.gazouille.utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to handle Kotlin Extension StringExtension.kt.
 * Created by emmanuelperu on 23/11/2015.
 */
public class StringsUtils {

    public static List<String> getHashTags(String s) {
        if(s == null){
            return null;
        }
        // FIXME externalise pattern
        Pattern pattern = Pattern.compile("#(\\w+|\\W+)");
        Matcher matcher = pattern.matcher(s);
        List<String> hashTags = new ArrayList<String>();
        while (matcher.find()) {
            hashTags.add(matcher.group(1));
        }
        return hashTags.isEmpty() ? null : hashTags;
    }

    public static List<Pair<Integer, Integer>> getHashTagsForSpanPosition(String s) {
        List<Pair<Integer, Integer>> spans = new ArrayList<Pair<Integer, Integer>>();
        Pattern pattern = Pattern.compile("#(\\w+|\\W+)");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            spans.add(new Pair(matcher.start(), matcher.end()));
        }
        return spans.isEmpty() ? null : spans;
    }
}
