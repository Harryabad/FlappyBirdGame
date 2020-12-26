package com.harryabad.flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird implements ActionListener, MouseListener {

    public static FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public ArrayList<Rectangle> columns;

    public int ticks, yMotion, score, bestScore;
    //yMotion is the motion of the bird

    public boolean gameOver, started;

    public Random rand;

    public FlappyBird(){

        JFrame jframe = new JFrame();
        Timer timer = new Timer(18, this);
        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("Flappy Bird Game");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);


        /*
        create a rectangle in centre of the screen, size 20x20
         */
        bird = new Rectangle(WIDTH/2 - 10, HEIGHT/2 - 10, 20,20);

        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void addColumn(boolean start){
        /*
        space between the columns
         */
        int space = 400;

        /*
        Column width
         */
        int width = 100;

        /*
        Randomize height, but minimum 50
         */
        int height = 50 + rand.nextInt(300);


        if(start){
            /* Ground column:
            x: WIDTH means all the way to the right of the screen
            add its actual width, which we have declared to be 300
            columns.size() * 300 says if there are any other columns it
            will move them over.

            y: HEIGHT - height - 150 puts the column on top of the grass

            width and height already declared
            */
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 150, width, height));

            /* Sky Column:
            x: the -1 pairs the columns together. rather than ground column followed by sky column ...
            y: 0 as it start from the sky
            width already declared
            height: HEIGHT - height - space creates the gap between the two columns
            */
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));

        } else{
            /* Ground column:
            4 are prepared when you start the game
            new columns are added when a column in the ArrayList has reached the left side of the screen.
            So when it gets to the left (offscreen), takes that x position and adds one + 600 to the right
            of the last item in the arrayList.
            if size of the array begins with 4 (4 columns). Constantly replace position 0
            */
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 150, width, height));

            /* Sky column:
            Copied the x position of the new column made above
            */
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }


    }

    public void paintColumn(Graphics g, Rectangle column){
        g.setColor(Color.green.darker().darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void jump(){
        if(gameOver){
            /*
            create new bird after gameover
             */
            bird = new Rectangle(WIDTH/2 - 10, HEIGHT/2 - 10, 20,20); // puts  in centre of screen using /2
            /*
            remove columns from last game
             */
            columns.clear();
            /*
            start with zero yMotion bird is stationary. First click should be up
             */
            yMotion = 0;
            /*
            reset score to zero
             */
            score = 0;

            /*
            add new 4 columns to start of a new game
             */
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }
        /*
        on click, start game
         */
        if(!started){
            started = true;

        } else if(!gameOver){
            if(yMotion > 0){
                yMotion = 0;
            }
            yMotion -= 10;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int speed = 10;

        ticks++;

        if (started){

            for(int i = 0; i < columns.size(); i++){
                /*
                Move the columns along the x axis at speed
                 */
                Rectangle column = columns.get(i);
                column.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15){
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++){
                Rectangle column = columns.get(i);
                /*
                if a column has gone off the screen to the left
                remove said column
                 */
                if(column.x + column.width < 0){
                    columns.remove(column);

                    /*
                    if it's the top columns, then add another column to the arrayList
                    creating an infinite loop
                    false tells us it is no longer the start of the game
                     */
                    if (column.y == 0){
                        addColumn(false);
                    }
                }
            }


            /*
            Have our bird fall
             */
            bird.y += yMotion;

            for (Rectangle column : columns){

                /*
                if bird reaches the centre of a column (can only be in the centre once, add 1 to score (made passed column without crashing)
                ## bird must be in-between the speed to get counted.
                column.y == 0, means it only count the score for one column as technically you're going past two at once
                 */
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - speed && bird.x + bird.width / 2 < column.x + column.width / 2 + speed){
                    score ++;
                    if (score > bestScore){
                        bestScore = score;
                    }
                }

                /*
                if a column and bird collide, game over
                 */
                if (column.intersects(bird)){
                    gameOver = true;

                    /*
                    once crashed into column, move with the column offscreen left
                    rather than pass through all oncoming columns
                     */
                    if (bird.x <= column.x){
                        bird.x = column.x - bird.width;
                    }
                }
            }

            /*
            if bird touches floor(grass), game over
             */
            if (bird.y > HEIGHT - 155 || bird.y < 0){
                gameOver = true;
            }

            /*
            when bird collides, land on ground. not go offscreen
             */
            if(bird.y + yMotion >= HEIGHT - 150){
                bird.y = HEIGHT - 150 - bird.height;
            }


        }
        /*
        clears current window and updates
         */
        renderer.repaint();

    }



    public void repaint(Graphics g) {

        /*
        set color background
         */
        g.setColor(Color.cyan.darker());
        g.fillRect(0,0,WIDTH,HEIGHT);

        /*
        set colour ground
         */
        g.setColor(Color.orange.darker());
        g.fillRect(0,HEIGHT - 150, WIDTH, 150 );

        /*
        set colour grass
         */
        g.setColor(Color.green.darker());
        g.fillRect(0,HEIGHT - 150, WIDTH, 20 );

        /*
        set color bird
         */
        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        for (Rectangle column: columns){
            // paint all columns
            paintColumn(g, column);
        }

        /*
        set text colour and font
         */
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 50));

        if(!started){
            g.drawString("CLICK TO START", 155, HEIGHT / 2 - 50);
        }

        if(gameOver){
            g.drawString("GAME OVER", 200, HEIGHT / 2 - 50);
        }

        if(!gameOver && started){
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
        g.drawString("Best score: " + String.valueOf(bestScore), 10, 750);
    }

    public static void main(String[] args){

        /*
        Creates the instance of flappybird game
         */
        flappyBird = new FlappyBird();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    @Override
    public void mousePressed(MouseEvent e) {


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
