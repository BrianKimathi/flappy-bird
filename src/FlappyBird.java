import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImage;
    Image birdImage;
    Image topObstacleImage;
    Image bottomObstacleImage;

    // Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;

        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }

    }

    // game logic
    Bird bird;
    int velocityY = 0;
    int velocityX = -4;
    int gravity = 1;
    Timer gameLoop;

    ArrayList<Pipe> pipes;

    Random random = new Random();

    Timer placePipesTimer;   

    boolean gameOver = false;

    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topObstacleImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomObstacleImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // bird
        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        // Place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
   
        placePipesTimer.start();

        // game loop
        gameLoop = new Timer(1000/60, this);

        gameLoop.start();

    }

    public void placePipes() {

        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random() * pipeHeight/2);

        int openingSpace = boardHeight/4;


        Pipe topPipe = new Pipe(topObstacleImage);  
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomObstacleImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        
        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for(int i=0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(String.valueOf((int)score), 10, 30);

        if(gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Game Over", boardWidth/2 - 80, boardHeight/2);
        }
        
        // restart button
        if(gameOver) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press space to restart", boardWidth/2 - 120, boardHeight/2 + 50);
        } 

    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for(int i=0; i < pipes.size(); i++) {  
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if(pipe.x + pipe.width < 0) {
                score = score + 1;    
                pipes.remove(i);
            }

            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
        // restart logic
        if(gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            bird.y = birdY;
            pipes.clear();
            score = 0;
            gameOver = false;
            placePipesTimer.start();
            gameLoop.start();
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width 
        && a.x + a.width > b.x 
        && a.y < b.y + b.height 
        && a.y + a.height > b.y;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
      
    @Override
    public void keyReleased(KeyEvent e) {}
    
}
