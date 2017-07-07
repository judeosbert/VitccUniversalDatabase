package com.example.kleptomaniac.vitccuniversaldatabase;

/**
 * Created by kleptomaniac on 23/6/17.
 */

public class Answers {

    private String requestCode;
    private String minQuality,year;
    private String requestPersonName,requestPersonKey;
    private String itemName,category,requestDate,answerDate,answerPersonName,answerPersonKey,answerPersonPhoto,requestPersonPhoto;


    public Answers(){
    }
    public Answers(String itemName,String category,String requestDate,String answerDate,String answerPersonName,String answerPersonKey,String answerPersonPhoto,String requestPersonName,String requestPersonPhoto,String requestPersonKey,String year,String minQuality,String requestCode)
    {
        this.itemName  = itemName;
        this.category = category;
        this.requestDate = requestDate;
        this.answerDate = answerDate;
        this.answerPersonName = answerPersonName;
        this.answerPersonKey = answerPersonKey;
        this.answerPersonPhoto = answerPersonPhoto;
        this.requestPersonPhoto = requestPersonPhoto;
        this.requestPersonName = requestPersonName;
        this.requestPersonKey = requestPersonKey;
        this.year = year;
        this.minQuality = minQuality;
        this.requestCode = requestCode;
    }

    public String getItemName()
    {
        return this.itemName;
    }
    public String getCategory()
    {
        return this.category;
    }
    public String getRequestDate()
    {
        return  this.requestDate;
    }
    public String getAnswerDate()
    {
        return  this.answerDate;
    }
    public String getAnswerPersonName()
    {
        return  this.answerPersonName;
    }
    public String getAnswerPersonKey()
    {
        return this.answerPersonKey;
    }
    public String getAnswerPersonPhoto()
    {
        return  this.answerPersonPhoto;
    }
    public String getRequestPersonPhoto()
    {
        return this.requestPersonPhoto;
    }
    public String getRequestPersonName(){
        return this.requestPersonName;
    }
    public String getRequestPersonKey()
    {
        return this.requestPersonKey;
    }
    public String getYear()
    {
        return this.year;
    }
    public String getMinQuality()
    {
        return this.minQuality;
    }

    public String getRequestCode() {
        return this.requestCode;
    }
}
