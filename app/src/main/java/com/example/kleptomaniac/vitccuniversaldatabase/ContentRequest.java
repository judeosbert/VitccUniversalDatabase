package com.example.kleptomaniac.vitccuniversaldatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kleptomaniac on 12/6/17.
 */

public class ContentRequest {

    private String requestingUserFullName;
    private  String key;
    private String requestType,requestingUser,RequestingUserPic,movieName,year,minQuality,fileLanguage,requestTime;
    private List<String> peers = new ArrayList<String>();

    public ContentRequest()
    {

    }
    public ContentRequest(String key,String requestType,String requestingUser,String requestingUserFullName, String RequestingUserPic, String movieName, String year, String minQuality, String fileType,String peer)
    {
        this.requestType = requestType;
        this.requestingUser = requestingUser;
        this.RequestingUserPic = RequestingUserPic;
        this.movieName = movieName;
        this.year = year;
        this.minQuality = minQuality;
        this.fileLanguage = fileType;
        Date date = new Date();
        this.requestTime = date.toString();
        this.peers.add(peer);
        this.key = key;
        this.requestingUserFullName = requestingUserFullName;

    }


    public ContentRequest(ContentRequest copy)
    {
        this.requestType = copy.requestType;
        this.requestingUser = copy.requestingUser;
        this.RequestingUserPic = copy.RequestingUserPic;
        this.movieName = copy.movieName;
        this.year = copy.year;
        this.minQuality = copy.minQuality;
        this.fileLanguage = copy.fileLanguage;
        this.requestTime = copy.requestTime;
        this.peers = copy.peers;
        this.key = copy.key;
        this.requestingUserFullName = copy.requestingUserFullName;
    }

    public String getRequestType(){return  requestType;}
    public void setRequestType(String requestType){this.requestType  = requestType;}
    public String getRequestingUser()
    {
        return requestingUserFullName;
    }
    public void setRequestingUser(String requestingUser)
    {
        this.requestingUserFullName = requestingUser;
    }

    public String getRequestingUserPic()
    {
        return RequestingUserPic;
    }
    public void setRequestingUserPic(String picUrl)
    {
        this.RequestingUserPic  = picUrl;
    }
    public String getMovieName()
    {
        return this.movieName;
    }
    public void setMovieName(String name)
    {
        this.movieName = name;

    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    public String getMinQuality() {
        return minQuality;
    }

    public void setMinQuality(String minQuality) {
        this.minQuality = minQuality;
    }
    public String getFileLanguage()
    {
        return fileLanguage;
    }
    public void setFileType(String type)
    {
        this.fileLanguage  = type;
    }
    public List<String> getPeers(){
        return this.peers;
    }

    public void addPeer(String peer)
    {
        this.peers.add(peer);
    }
    public String getKey()
    {
        return this.key;
    }
    public void setKey(String key)
    {
        this.key  = key;
    }
    public String getRequestTime()
    {
        return  this.requestTime;
    }
    public void setRequestTime(String requestTime)
    {
        this.requestTime = requestTime;
    }
    public void removePeer(String peer) {
        if(this .peers.size() > 0 && this.peers.contains(peer))
            this.peers.remove(peer);
    }

    public String getRequestingUserEmail()
    {
        return  this.requestingUser;
    }

}
