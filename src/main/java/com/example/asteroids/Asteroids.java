package com.example.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;

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
        int score;
    Button yes = new Button("YES!");
    Button no = new Button("NO!");

    boolean gameOver = false;
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ASTEROIDS");
        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);

        Canvas canvas = new Canvas(1000, 900);
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

        final int[] numberOfAsteroids = {6};

        score = 0;
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
                    //laserList.size()=numberOfAsteroids*3;
                    keyJustPressedList.clear();

                }

                spaceship.update(1/60.0);
                for(Sprite asteroid : asteroidList)
                    asteroid.update(1/60.0);

                //laser remove after 2 seconds
                for(int n=0; n< laserList.size();n++)
                {
                    Sprite laser = laserList.get(n);
                    laser.update(1/60.0);
                    if(laser.elapsedTime>2)
                        laserList.remove(n);
                }

                //remove laser and asteroid if overlaps
                for(int laserNum=0;laserNum<laserList.size();laserNum++)
                {
                    Sprite laser = laserList.get(laserNum);
                    for(int asteroidNum=0; asteroidNum<asteroidList.size();asteroidNum++){
                        Sprite asteroid = asteroidList.get(asteroidNum);
                        if(laser.overlaps(asteroid)){
                            laserList.remove(laserNum);
                            asteroidList.remove(asteroidNum);
                            score+=100;
                        }
                    }
                }
                //ammo laser 3 * asteroids (8 asteroids ; 24 lasershots)



                //spawn another wave of asteroids (asteroidNum ++)
                if (asteroidList.isEmpty()) {
                    numberOfAsteroids[0]++; // increase the number of asteroids
                    for (int i = 0; i < numberOfAsteroids[0]; i++) {
                        // spawn new asteroids as before
                        Sprite asteroid = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rock.png");
                        double x = 500 * Math.random() + 300;
                        double y = 400 * Math.random() + 100;
                        asteroid.position.set(x, y);
                        double angle = 360 * Math.random();
                        asteroid.velocity.setLength(Math.random() + 100);
                        asteroid.velocity.setAngle(angle);
                        asteroidList.add(asteroid);
                    }
                }


                //game over if asteroid overlaps spaceship
                for (int asteroidHit=0;asteroidHit<asteroidList.size();asteroidHit++)
                {
                    Sprite asteroid = asteroidList.get(asteroidHit);
                    if (spaceship.overlaps(asteroid)){
                        gameOver = true;
                    }
                }

                background.render(context);

                if (gameOver) {

                    Text gameOverText = new Text("Game Over...\nSCORE: " + score + "\nPlay again?");
                    gameOverText.setFont(Font.font("Arial Black", 30));

                    Font buttonFont = Font.font("Arial", 30);
                    yes.setFont(buttonFont);
                    no.setFont(buttonFont);

                    VBox vbox = new VBox(20);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(new Insets(20));
                    vbox.getChildren().addAll(gameOverText, yes, no);

                    root.setCenter(vbox);
                    vbox.setStyle("-fx-background-color: red; -fx-border-color: darkred;");



                    spaceship.position.set(200,400);
                    spaceship.velocity.setLength(0);
                    spaceship.velocity.setAngle(0);


                    yes.setOnAction(actionEvent -> {
                        primaryStage.close(); // Schließe das aktuelle Fenster
                        startNewGame(); // Starte ein neues Spiel
                    });
                    no.setOnAction(actionEvent -> primaryStage.close()); // Beende das Spiel, wenn "NO!" geklickt wird
                    primaryStage.setOnCloseRequest(windowEvent -> System.exit(0)); // Beende das Spiel, wenn das Fenster geschlossen wird

                }

                spaceship.render(context);

                for(Sprite laser : laserList)
                    laser.render(context);
                for(Sprite asteroid : asteroidList)
                    asteroid.render(context);

                context.setFill(Color.WHITESMOKE);
                context.setStroke(Color.GREEN);
                context.setFont(new Font("Arial Black",30));
                context.setLineWidth(3);
                String text="SCORE: "+score;
                int textX=700;
                int textY=30;
                context.fillText(text,textX,textY);
                context.strokeText(text,textX,textY);
            }
        };
        looper.start();

        primaryStage.show();
    }
    private void startNewGame() {
        Stage newStage = new Stage(); // Erstelle ein neues Stage-Objekt für das neue Spiel
        Asteroids newGame = new Asteroids(); // Erstelle eine neue Instanz der Asteroids-Klasse
        newGame.start(newStage); // Starte das neue Spiel mit dem neuen Stage-Objekt
    }
}