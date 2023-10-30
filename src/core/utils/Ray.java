package core.utils;

import java.awt.*;

public class Ray {
    Vector2D position;
    double length;
    Color color;

    public Ray(){
        this.position = new Vector2D();
        this.length = 0;
        this.color = Color.WHITE;
    }

    public Ray(Vector2D position, double length, Color color) {
        this.position = position;
        this.length = length;
        this.color = color;
    }

    public void calculateDifference(Vector2D position){
        this.length = this.position.sub(position).length();
    }

    public double getX(){ return position.x; }
    public double getY(){ return position.y; }
    public double getLength(){ return length; }
    public Color getColor(){ return color; }
}
