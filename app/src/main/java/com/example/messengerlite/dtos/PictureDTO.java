package com.example.messengerlite.dtos;

public class PictureDTO
{
    private int width;
    private int height;
    private String path;

    public PictureDTO(int width, int height, String path)
    {
        this.width = width;
        this.height = height;
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPath() {
        return path;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPath(String path) {
        this.path = path;
    }
}