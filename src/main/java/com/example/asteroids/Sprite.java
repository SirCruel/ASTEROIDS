package com.example.asteroids;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Sprite {
    public myVector position;
    public myVector velocity;
    public double rotation; //degree
    public Rectangle boundary;
    public Image image;
    public double elapsedTime;
    private double width;
    private double height;

    public Sprite(){
        this.position = new myVector();
        this.velocity = new myVector();
        this.rotation = 0;
        this.boundary = new Rectangle();
        this.elapsedTime=0;
    }
    public Sprite(String imageFileName){
        this();
        setImage(imageFileName);
    }

    public void setImage(String imageFileName){
        this.image = new Image(imageFileName);
        this.boundary.setSize(this.image.getWidth(), this.image.getHeight());
    }

    public Rectangle getBoundary(){
        this.boundary.setPosition(this.position.x,this.position.y);
        return this.boundary;
    }

    public boolean overlaps(Sprite other){
        return this.getBoundary().overlaps(other.getBoundary());
    }
    public void wrap(double screenWidth, double screenHeight)
    {
        double halfWidth = this.image.getWidth()/2;
        double halfHeight =  this.image.getHeight()/2;

        if(this.position.x + halfWidth< 0)
            this.position.x= screenWidth +halfWidth;
        if (this.position.x > screenWidth +halfWidth)
            this.position.x = -halfWidth;
        if(this.position.y + halfHeight < 0)
            this.position.y = screenHeight + halfHeight;
        if (this.position.y > screenHeight + halfHeight)
            this.position.y = -halfHeight;
    }
    public void update (double deltaTime){
        //update position according to velocity
        this.position.add(this.velocity.x * deltaTime, this.velocity.y *deltaTime);
        this.wrap(1000,800);
        //increase elapsed time
        this.elapsedTime+=deltaTime;

    }
    public void render(GraphicsContext context){

        context.save();

        context.translate(this.position.x, this.position.y);
        context.rotate(this.rotation);
        context.translate(-this.image.getWidth()/2,-this.image.getHeight()/2);
        context.drawImage(this.image,0,0);

        context.restore();
    }
}
