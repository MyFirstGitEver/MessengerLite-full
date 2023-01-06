package com.example.messengerlite.dtos;

public class InfoDTO
{
    public static final int HOME_TOWN = 1;
    public static final int WORKING_CITY = 2;
    public static final int BIRTH_DAY = 3;

    private int type;
    private String content;

    public InfoDTO(int type, String content) {
        this.type = type;

        if(content == null)
            this.content = "Rỗng";
        else
            this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent()
    {
        return content;
    }
}