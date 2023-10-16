package core.utils;


public class Vector2D {
    public double x, y;

    public Vector2D(){
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D vec){
        this.x = vec.x;
        this.y = vec.y;
    }

    public void add(Vector2D vec){
        this.x += vec.x;
        this.y += vec.y;
    }

    public void sub(Vector2D vec){
        this.x -= vec.x;
        this.y -= vec.y;
    }

    public double distance(Vector2D vec){
        Vector2D vector2d = new Vector2D(this);
        vector2d.sub(vec);
        return vector2d.length();
    }

    public double length(){
        return Math.sqrt(
            x * x + y * y
        );
    }

    public void normalize(){
        double length = length();
        if(length != 0){
            x /= length;
            y /= length;
        }
    }

    public void rotate(double angle){
        double tempX = x;
        double tempY = y;

        x = tempX * Math.cos(angle) - tempY * Math.sin(angle);
        y = tempX * Math.sin(angle) + tempY * Math.cos(angle);
    }

    @Override
    public String toString(){
        return String.format("x: %.3f y: %.3f", x,y);
    }
}
