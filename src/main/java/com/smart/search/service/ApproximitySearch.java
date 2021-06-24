package com.smart.search.service;

import com.smart.search.model.Searchable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;

public final class ApproximitySearch {

  private static final double EXACT_MATCH = 1d;
  private static final double STARTSWITH_SCORE = 0.95d;
  private static final String SPECIAL_CHARS = "[/\\-+.^:,]";

  private ApproximitySearch() {}

  /**
   * This method will update or/and insert order entry in database with latest data.
   * @param phrase The text typed by the user for searching results
   * @param searchable Set of inputs against which the search should be done
   * @return List<Searchable> list of searchable objects / results along with their id, names and scores sorted descending by score.
   * @throws
   */
  public static List<Searchable> getResults(String phrase, Set<Searchable> searchable) {
    return getResults(phrase, searchable, getJWValue());
  }

  /**
   * This method will update or/and insert order entry in database with latest data.
   * @param phrase The text typed by the user for searching results
   * @param searchable Set of inputs against which the search should be done
   * @param jw value of accuracy, i you want to modify it, default is 0.75, range is between 0 and 1
   * @return List<Searchable> list of searchable objects / results along with their id, names and scores sorted descending by score.
   * @throws
   */
  public static List<Searchable> getResults(String phrase, Set<Searchable> searchable, double jw) {

    if (jw < 0.01)
      jw = 0.75;

    List<Searchable> res = new ArrayList<>();
    if (StringUtils.isEmpty(phrase)) return res;

    boolean isStartsWithSearch = phrase.length() <= 2;
    StopWatch sw = new StopWatch();
    sw.start();
    phrase = phrase.trim().toUpperCase();
    if (isStartsWithSearch) { // starts with check
      for (Searchable s : searchable) {
        String text = s.getText();
        if (text != null
            && (text.toUpperCase().startsWith(phrase) || text.toUpperCase().endsWith(phrase))) {
          Searchable s2 = new Searchable(s.getId(), s.getText());
          s2.setScore(STARTSWITH_SCORE);
          res.add(s2);
        }
      }
    } else {
      for (Searchable r : searchable) {
        String s = r.getText();
        if (s != null) {
          double val = smartSearchJW(phrase, s, jw);
          val = checkAllSubStrings(phrase, jw, s, val);
          val = checkAllSubStrings(s, jw, phrase, val);
          if (val >= jw) {
            Searchable s2 = new Searchable(r.getId(), r.getText());
            s2.setScore(val);
            res.add(s2);
          }
        }
      }
    }
    Collections.sort(res); // sort the results
    System.out.println("Time for Reason search : " + sw.getTime() + ", resp size : " + res.size());
    sw.stop();
    return res;
  }

  private static double checkAllSubStrings(String text1, double jw, String text2, double val) {
    if (val < jw) {
      if (StringUtils.isBlank(text2)) return 0d;
      text2 = text2.toUpperCase().replaceAll(SPECIAL_CHARS, " ").trim();
      String[] textArray = text2.split(" ");
      if (textArray.length > 1) {
        double valArray = 0d;
        double jwEach = 0.9d;
        for (int i = 0; i < textArray.length; i++) {
          valArray = smartSearchJW(text1, textArray[i], jwEach);
          if (valArray >= jwEach) {
            val = valArray;
            break;
          }
        }
      }
    }
    return val;
  }

  private static double getJWValue() {
    return 0.75;
  }

  private static double smartSearchJW(String first, String sec, double jw) {
    double jwValue = 0.0;
    if (sec == null) return jwValue;
    first = first.toUpperCase().replaceAll(SPECIAL_CHARS, " ");
    sec = sec.toUpperCase().replaceAll(SPECIAL_CHARS, " ");
    if (sec.equals(first) || StringUtils.reverseDelimited(sec, ' ').equals(first)) {
      return EXACT_MATCH;
    }
    if (sec.startsWith(first) || sec.endsWith(first)) {
      return STARTSWITH_SCORE;
    }
    try {
      jwValue = StringUtils.getJaroWinklerDistance(first, sec);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jwValue;
  }
}
