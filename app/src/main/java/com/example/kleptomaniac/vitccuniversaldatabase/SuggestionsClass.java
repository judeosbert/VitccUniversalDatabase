package com.example.kleptomaniac.vitccuniversaldatabase;

/**
 * Created by kleptomaniac on 7/7/17.
 */

public class SuggestionsClass {

    String itemName;
    String keyCode;

    public SuggestionsClass()
    {

    }
    public SuggestionsClass(String itemName,String keyCode)
    {
        this.itemName = itemName;
        this.keyCode  = keyCode;
    }

    public String getItemName()
    {
        return this.itemName;
    }
    public String getKeyCode()
    {
        return this.keyCode;
    }
}
