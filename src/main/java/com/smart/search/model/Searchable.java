package com.smart.search.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author abhishek
 * Generic bean for search and comparison. To make any bean searchable, extend this class.
 * 
 */
public class Searchable implements Comparable<Searchable> {
  

  private String id;
  private String text;
  protected double score;

  public static final double EXACT_MATCH = 1d;
  public static final double STARTSWITH_SCORE = 0.95d;
  private static final String SPECIAL_CHARS = "[/\\-+.^:,]";

  
  public Searchable(String id, String text) {
    this.id = id;
    this.text = text;
  }
  
  public Searchable() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  @Override
  public String toString() {
    return "Searchable [id=" + id + ", text=" + text + ", score=" + score + "]";
  }

  @Override
  public int compareTo(Searchable o) {
    if (this.score < o.getScore()) {
      return 1;
    } else if (this.score > o.getScore()) {
      return -1;
    } else
      return 0;
  }
  
  public List<Searchable> smartSearchGeneric(List<Searchable> data, String phrase) {
    List<Searchable> res = new ArrayList<>();
    List<Searchable> inputList = new ArrayList<>(data);
    double thresholdJW = getJWValue();
    boolean isStartsWithSearch = phrase.length() <= 2;
    phrase = phrase.trim().toUpperCase();
    final String phraseP = phrase;
    if (isStartsWithSearch) {
      inputList.forEach(d -> {
        if (d.getText() != null && d.getText().toUpperCase().startsWith(phraseP)) {
          d.setScore(STARTSWITH_SCORE);
          res.add(d);
        }
      });
    } else {
      inputList.forEach(d -> {
        double jwScore = smartSearchJW(phraseP, d.getText(), thresholdJW, null);
        jwScore = checkAllSubStrings(phraseP, null, thresholdJW, d.getText(), jwScore);
        if (jwScore >= thresholdJW) {
          d.setScore(jwScore);
          res.add(d);
        }
      });
    }
    Collections.sort(res); // ranked the results
    return res;
  }
  
  @SuppressWarnings("deprecation")
  public static double smartSearchJW(String first, String sec, double jw, String log) {
    double jwValue = 0.0;
    if(sec == null)
      return jwValue;
    
    sec = sec.toUpperCase().replaceAll(SPECIAL_CHARS, " ");
    if(sec.equals(first) || StringUtils.reverseDelimited(sec, ' ').equals(first)) {
      return EXACT_MATCH;
    }
    if(sec.startsWith(first) || sec.endsWith(first)) {
      return STARTSWITH_SCORE;
    }
    try {
      jwValue = StringUtils.getJaroWinklerDistance(first, sec);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jwValue;
  }
  

  public static double getJWValue() {
    return 0.75;
  }
  
  public static double checkAllSubStrings(String phrase, String log, double jw, String text,
      double val) {
    outerloop : if(val < jw) {
      if(StringUtils.isBlank(text))
        return 0d;
      text = text.toUpperCase().replaceAll(SPECIAL_CHARS, " ").trim();
      String[] textArray = text.split(" ");
      if(textArray.length>1) {
        double valArray = 0d;
        double jwEach = 0.9d;
        for (int i = 0; i < textArray.length; i++) {
          valArray = smartSearchJW(phrase, textArray[i], jwEach, log);
          if(valArray >=jwEach) {
            val = valArray;
            break outerloop;
          }
        }
      }
    }
    return val;
  }
}