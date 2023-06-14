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
        // insert sounds
        engineMediaPlayer = createMediaPlayer("random.wav");
        engineMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        laserMediaPlayer = createMediaPlayer("laserSound.mp3");
        collisionMediaPlayer = createMediaPlayer("explosion.wav");
        gameOverMediaPlayer = createMediaPlayer("gameOverSound.wav");
        restartMediaPlayer = createMediaPlayer("ah shit here we go again.wav");
        exitMediaPlayer = createMediaPlayer("Was zitterst denn so..wav");
        // create canvas
        Canvas canvas = new Canvas(1000, 900);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        //continuous input
        ArrayList<String> keyPressedList = new ArrayList<>();
        //discrete input
        ArrayList<String> keyJustPressedList = new ArrayList<>();

        //action on keys
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
        //background
        Sprite background = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\background.jpg");
        background.position.set(500,400);
        background.render(context);

        //spaceship
        Sprite spaceship = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\Spaceship.png");
        spaceship.position.set(100,400);
        spaceship.render(context);

        //powerup
        Sprite powerup = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\powerup.png");

        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> asteroidList = new ArrayList<Sprite>();
        ArrayList<Sprite> asteroidsplitList = new ArrayList<Sprite>();

        final int[] numberOfAsteroids = {6};
        final double[] maxLaserShots = {numberOfAsteroids[0] * 4};
        final double[] remainingLaserShots = {maxLaserShots[0]};
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

                if(keyJustPressedList.contains("F")&& remainingLaserShots[0] > 0) {
                    Sprite laser = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\laser.png");
                    laser.position.set(spaceship.position.x,spaceship.position.y);
                    laser.velocity.setLength(420);
                    laser.velocity.setAngle(spaceship.rotation);
                    laserList.add(laser);
                    remainingLaserShots[0]--;
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

                // check collsision laser : asteroids
                for (int laserNum = 0; laserNum < laserList.size(); laserNum++) {
                    Sprite laser = laserList.get(laserNum);
                    boolean laserOverlaps = false;

                    for (int asteroidNum = 0; asteroidNum < asteroidList.size(); asteroidNum++) {
                        Sprite asteroid = asteroidList.get(asteroidNum);
                        if (laser.overlaps(asteroid)) {
                            // save positions
                            double collisionX = asteroid.position.x;
                            double collisionY = asteroid.position.y;
                            asteroidList.remove(asteroidNum);
                            collisionMediaPlayer.seek(Duration.ZERO);
                            collisionMediaPlayer.play();
                            score += 100;
                            // create asteroidsplits
                            for (int asteroidsplitNum = 0; asteroidsplitNum < 3; asteroidsplitNum++) {
                                Sprite asteroidSplit = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rockpiece.png");
                                asteroidSplit.position.set(collisionX, collisionY);
                                asteroidSplit.velocity.setLength(Math.random() + 100);
                                double angle = 360 * Math.random();
                                asteroidSplit.velocity.setAngle(angle);
                                asteroidSplit.rotationSpeed = Math.random() * 2 - 1;
                                asteroidSplit.velocity.multiply(Math.random() + 0.5);
                                asteroidsplitList.add(asteroidSplit);
                            }
                            laserOverlaps = true;
                            break;
                        }
                    }
                    // remove if overlaps with asteroid
                    if (laserOverlaps) {
                        laserList.remove(laserNum);
                        laserNum--;
                    }
                }

                // remove laser and asteroid split if overlaps
                for (int laserNum = 0; laserNum < laserList.size(); laserNum++) {
                    Sprite laser = laserList.get(laserNum);
                    boolean laserOverlaps = false;

                    for (int asteroidsplitNum = 0; asteroidsplitNum < asteroidsplitList.size(); asteroidsplitNum++) {
                        Sprite asteroidSplit = asteroidsplitList.get(asteroidsplitNum);
                        if (laser.overlaps(asteroidSplit)) {
                            laserOverlaps = true;
                            asteroidsplitList.remove(asteroidsplitNum);
                            collisionMediaPlayer.seek(Duration.ZERO);
                            collisionMediaPlayer.play();
                            score += 150;
                            break;
                        }
                    }
                    // remove if overlaps with asteroidsplits
                    if (laserOverlaps) {
                        laserList.remove(laserNum);
                        laserNum--;
                    }
                }





                //spawn another wave of asteroids (asteroidNum ++)
                if (asteroidList.isEmpty()) {
                    numberOfAsteroids[0]++;
                    maxLaserShots[0] = numberOfAsteroids[0] * 4;
                    remainingLaserShots[0] = maxLaserShots[0];
                    wave ++;
                    for (int i = 0; i < numberOfAsteroids[0]; i++) {
                        // spawn new asteroids
                        Sprite asteroid = new Sprite("C:\\Users\\Soner\\Desktop\\2. Semester\\Algorithmen und Datenstrukturen\\Asteroids\\src\\rock.png");
                        double x , y ;
                        //create safeplace for asteroidspawn
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

                    }
                }


                //game over if asteroid overlaps spaceship
                for (int asteroidHit=0;asteroidHit<asteroidList.size();asteroidHit++)
                {
                    Sprite asteroid = asteroidList.get(asteroidHit);
                    if (spaceship.overlaps(asteroid)) {
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
                for (Sprite asteroidsplit : asteroidsplitList) {
                    asteroidsplit.update(1 / 60.0);
                    asteroidsplit.render(context);
                    asteroidsplit.rotation += asteroidsplit.rotationSpeed;

                    if (spaceship.overlaps(asteroidsplit)) {
                        gameOver = true;
                    }
                }
                if (wave%3==0){
                    powerUp();
                }



                context.setFill(Color.WHITESMOKE);
                context.setStroke(Color.GREEN);
                context.setFont(new Font("Arial Black",30));
                context.setLineWidth(3);
                String text = "WAVE: "+ wave +"     LASERS: " + Math.round(remainingLaserShots[0])+"     SCORE: " + score ;
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
    private void powerUp(){

    }
}