package com.example.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.lang.Thread;

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
    boolean isInvincible = false;
    long invincibleStartTime=0;
    Button yes = new Button("YES!");
    Button no = new Button("NO!");
    int numberOfAsteroids;
    double maxLaserShots = numberOfAsteroids * 2;
    double remainingLaserShots = maxLaserShots;
    int wave = 0;
    boolean isGameOverSoundPlayed = false;
    boolean isGamerRestartSoundPlayed = false;
    boolean gameOver = false;
    private MediaPlayer engineMediaPlayer;

    private MediaPlayer laserMediaPlayer;
    private MediaPlayer collisionMediaPlayer;
    private MediaPlayer gameOverMediaPlayer;
    private MediaPlayer restartMediaPlayer;
    private MediaPlayer exitMediaPlayer;
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ASTEROIDS");
        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);

        engineMediaPlayer = createMediaPlayer("random.wav");
        engineMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        laserMediaPlayer = createMediaPlayer("laserSound.mp3");
        collisionMediaPlayer = createMediaPlayer("explosion.wav");
        gameOverMediaPlayer = createMediaPlayer("gameOverSound.wav");
        restartMediaPlayer = createMediaPlayer("ah shit here we go again.wav");
        exitMediaPlayer = createMediaPlayer("Was zitterst denn so..wav");

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
        spaceship.position.set(100,400);
        spaceship.render(context);

        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> asteroidList = new ArrayList<Sprite>();

        final int[] numberOfAsteroids = {5};

        score = 0;
        AnimationTimer looper = new AnimationTimer()
        {

            public void handle (long nanotime){
                //process user input
                if(keyPressedList.contains("LEFT"))
                    spaceship.rotation -=4;
                if(keyPressedList.contains("RIGHT"))
                    spaceship.rotation +=4;


                if(keyPressedList.contains("UP")) {
                    spaceship.velocity.setLength(180);
                    spaceship.velocity.setAngle(spaceship.rotation);
                    engineMediaPlayer.play();
                }
                else //not pressing UP
                {
                    spaceship.velocity.setLength(0);
                    engineMediaPlayer.stop();
                }

                if(keyJustPressedList.contains("F")&& remainingLaserShots > 0) {
                    Sprite laser = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\laser.png");
                    laser.position.set(spaceship.position.x,spaceship.position.y);
                    laser.velocity.setLength(420);
                    laser.velocity.setAngle(spaceship.rotation);
                    laserList.add(laser);
                    remainingLaserShots--;
                    laser.rotation=spaceship.rotation;
                    laserMediaPlayer.seek(Duration.ZERO);
                    laserMediaPlayer.play();
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
                            /*
                            Sprite asteroidsplit = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rockpiece.png");
                            Sprite x= asteroidList.get((int) asteroid.position.x);
                            Sprite y= asteroidList.get((int) asteroid.position.y);
                            asteroidsplit.position.set(x,y);
                            double angle = 360 * Math.random();
                            asteroidsplit.velocity.setLength(Math.random() + 120);
                            asteroidsplit.velocity.setAngle(angle);
                            asteroidsplit.rotationSpeed = Math.random()* 2 - 1;
                            asteroidsplit.asteroidSpeed = Math.random()* 150 - 100;
                            asteroidList.add(asteroidsplit);
                            */
                            laserList.remove(laserNum);
                            asteroidList.remove(asteroidNum);
                            collisionMediaPlayer.seek(Duration.ZERO);
                            collisionMediaPlayer.play();
                            score+=100;
                        }
                    }
                }

                //spawn another wave of asteroids (asteroidNum ++)
                if (asteroidList.isEmpty()) {
                    numberOfAsteroids[0]++;
                    maxLaserShots = numberOfAsteroids[0] * 3.5;
                    remainingLaserShots = maxLaserShots;
                    wave ++;
                    for (int i = 0; i < numberOfAsteroids[0]; i++) {
                        // spawn new asteroids
                        Sprite asteroid = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rock.png");
                        double x , y ;
                        double spacing = 200;

                        do {
                            x = 500 * Math.random() + 300;
                            y = 400 * Math.random() + 100;
                        }
                        while (!((x < spaceship.position.x - spacing || x > spaceship.position.x + spacing) &&
                                (y < spaceship.position.y - spacing || y > spaceship.position.y + spacing))
                        );


                        asteroid.position.set(x, y);

                        double angle = 360 * Math.random();
                        asteroid.velocity.setLength(Math.random() + 100);
                        asteroid.velocity.setAngle(angle);
                        asteroid.rotationSpeed = Math.random()* 2 - 1;
                        asteroid.velocity.multiply(Math.random() + 0.5);

                        asteroidList.add(asteroid);


                        isInvincible = true;
                        invincibleStartTime = System.currentTimeMillis();
                    }
                }


                //game over if asteroid overlaps spaceship
                for (int asteroidHit=0;asteroidHit<asteroidList.size();asteroidHit++)
                {
                    Sprite asteroid = asteroidList.get(asteroidHit);
                    if (!isInvincible() && spaceship.overlaps(asteroid)) {
                        gameOver = true;
                    }
                }

                background.render(context);

                if (gameOver) {
                    if (!isGameOverSoundPlayed) {
                        gameOverMediaPlayer.play();
                        isGameOverSoundPlayed = true;
                    }
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


                    yes.setOnAction(actionEvent -> {
                        primaryStage.close();
                        restartMediaPlayer.play();
                        startNewGame();
                    });
                    if(!isGamerRestartSoundPlayed) {
                        isGamerRestartSoundPlayed = true;


                        no.setOnAction(actionEvent -> {
                            exitMediaPlayer.setOnEndOfMedia(() -> {
                                exitMediaPlayer.stop();
                                primaryStage.close();
                            });
                            exitMediaPlayer.play();

                            primaryStage.setOnCloseRequest(windowEvent -> {
                                exitMediaPlayer.stop();
                            });
                        });
                    }
                        primaryStage.setOnCloseRequest(windowEvent -> {

                        System.exit(0);
                    });
                }

                spaceship.render(context);

                for(Sprite laser : laserList)
                    laser.render(context);
                for(Sprite asteroid : asteroidList) {
                    asteroid.render(context);
                    asteroid.rotation += asteroid.rotationSpeed;
                }

                context.setFill(Color.WHITESMOKE);
                context.setStroke(Color.GREEN);
                context.setFont(new Font("Arial Black",30));
                context.setLineWidth(3);
                String text = "WAVE: "+ wave +"     LASERS: " + Math.round(remainingLaserShots)+"     SCORE: " + score ;
                int textX=200;
                int textY=30;
                context.fillText(text,textX,textY);
                context.strokeText(text,textX,textY);
            }
        };
        looper.start();

        primaryStage.show();
    }
    private boolean isInvincible() {
        return isInvincible && (System.currentTimeMillis() - invincibleStartTime) < 2000;
    }
    private MediaPlayer createMediaPlayer(String fileName) {
        Media sound = new Media(getClass().getResource(fileName).toExternalForm());
        return new MediaPlayer(sound);
    }

    private void startNewGame() {
        Stage newStage = new Stage();
        Asteroids newGame = new Asteroids();
        newGame.start(newStage);
    }
}