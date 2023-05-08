package com.example.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Random;

public class Asteroids extends Application {
    public static void main (String [] args){
        try{
            launch(args);
        }
        catch (Exception error){
            error.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        mainStage.setTitle("ASTEROIDS");
        BorderPane root=new BorderPane();
        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        Canvas canvas= new Canvas(1000,800);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        //continuous input
        ArrayList<String> keyPressedList = new ArrayList<>();
        //discrete input
        ArrayList<String> keyJustPressedList = new ArrayList<>();

        mainScene.setOnKeyPressed(
                (KeyEvent event)->
                {
                    String keyName = event.getCode().toString();
                    //avoid adding duplicates

                    if (!keyPressedList.contains(keyName))
                    {
                        keyPressedList.add(keyName);
                        keyJustPressedList.add(keyName);
                    }
                }
        );



        mainScene.setOnKeyReleased(
                (KeyEvent event)->{
                    String keyName = event.getCode().toString();
                    //avoid adding duplicates
                    if( keyPressedList.contains(keyName)){
                        keyPressedList.remove(keyName);
                    }
                }
        );

        Sprite background = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\background.jpg");
        background.position.set(500,400);
        background.render(context);


        Sprite spaceship = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\Spaceship.png");
        spaceship.position.set(200,400);
        spaceship.render(context);

        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> asteroidList = new ArrayList<Sprite>();




        AnimationTimer looper = new AnimationTimer()
        {
            public void handle (long nanotime){
                //process user input
                if(keyPressedList.contains("LEFT"))
                    spaceship.rotation -=3;
                if(keyPressedList.contains("RIGHT"))
                    spaceship.rotation +=3;


                if(keyPressedList.contains("UP")) {
                    spaceship.velocity.setLength(150);
                    spaceship.velocity.setAngle(spaceship.rotation);
                }
                else //not pressing UP
                {
                    spaceship.velocity.setLength(0);
                }

                if(keyJustPressedList.contains("F")) {
                    Sprite laser = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\laser.png");
                    laser.position.set(spaceship.position.x,spaceship.position.y);
                    laser.velocity.setLength(400);
                    laser.velocity.setAngle(spaceship.rotation);
                    laserList.add(laser);
                    laser.rotation=spaceship.rotation;

                    keyJustPressedList.clear();

                }

                /*Sprite asteroid = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rock.png");
                Random random = new Random();
                asteroid.position.set(0,0);
                asteroid.velocity.setLength(50);
                asteroid.velocity.setAngle(50);
                asteroidList.add(asteroid);
                */
                int numberOfAsteroids = 4; // specify the number of asteroids you want to create
                Random random = new Random();
                Sprite asteroid = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rock.png");

                for (int i = 0; i < numberOfAsteroids; i++) {
                    // set the position and velocity of the asteroid using random values
                    asteroid.position.set(random.nextInt(800), random.nextInt(1000));
                    asteroid.velocity.setLength(random.nextInt(50));
                    asteroid.velocity.setAngle(random.nextInt(360));
                    asteroidList.add(asteroid);
                }

                for (int i = 0; i < asteroidList.size(); i++) {
                    asteroid = asteroidList.get(i);
                    asteroid.update(1 / 60.0);
                    asteroid.render(context);
                }
                spaceship.update(1/60.0);
                for(int n=0; n< laserList.size();n++)
                {
                    Sprite laser = laserList.get(n);
                    laser.update(1/60.0);
                    if(laser.elapsedTime>2)
                        laserList.remove(n);
                }
                for (int i = 0; i < laserList.size(); i++) {
                    Sprite laser = laserList.get(i);
                    for (int j = 0; j < asteroidList.size(); j++) {
                        asteroid = asteroidList.get(j);
                        if (laser.position.equals(asteroid.position)) {
                            asteroidList.remove(j);
                            break;
                        }
                    }
                }

                background.render(context);
                spaceship.render(context);

                for(Sprite laser : laserList)
                    laser.render(context);

            }
        };
        looper.start();


        mainStage.show();
    }
}