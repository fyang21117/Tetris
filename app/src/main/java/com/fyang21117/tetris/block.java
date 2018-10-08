package com.fyang21117.tetris;

public class block {
    private String orientation;
    private int goal;
    private String color;
    private  String shape;

    public String getOrientation() {
        return orientation;
    }
    public int getGoal() {
        return  goal;
    }
    public String getColor() {
        return color;
    }
    public String getShape() {
        return shape;
    }

    public void setOrientation(String orientation){
        this.orientation=orientation;
    }
    public void setGoal(int goal){
        this.goal=goal;
    }
    public void setColor(String color){
        this.color=color;
    }
    public void setShape(String shape) {
        this.shape = shape;
    }
}
