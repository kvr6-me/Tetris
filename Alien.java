import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Alien extends JPanel implements ActionListener, KeyListener {


    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; //used for aliens
        boolean used = false; //used for bullets
        Block(int x, int y, int width, int height, Image img){
            this.x = x;
            this.y = y;
            this.width =width;
            this.height = height;
            this.img = img;
        }
    }

    //board
    int tileSize =32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns; //32 * 16 = 512px
    int boardHeight = tileSize * rows; //32 * 16 = 512px

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //ship
    int shipWidth = tileSize*2; //64px
    int shipHeight = tileSize; //32px
    int shipx = tileSize*columns/2 - tileSize;
    int shipy = boardHeight - tileSize*2;
    int shipVelocityx = tileSize; //ship moving speed
    Block ship;

    //aliens
    ArrayList<Block> alienArray;
    int alienWidth= tileSize*2;
    int alienHeight= tileSize;
    int alienx = tileSize;
    int alieny = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0; //number of alien to defeat
    int alienVelocityx = 1; //alien moving speed

    //bullets
    ArrayList<Block> bulletArray;
    int bulletwidth = tileSize/8;
    int bulletheight = tileSize/2;
    int bulletVelocityy = -10; //bullet moving speed

    Timer gameLoop;
    int score =0;
    boolean GameOver = false;

    Alien(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        //load images
        shipImg= new ImageIcon(getClass().getResource("./ship.jpg")).getImage();
        alienImg= new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg= new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg= new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg= new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray= new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipx, shipy, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        //game timer
        gameLoop =new Timer(1000/60, this); //1000/60 = 16.7
        createAliens();
        gameLoop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //ship
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        //aliens
        for (int i=0; i<alienArray.size(); i++){
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }
        //bullets
        g.setColor(Color.red);
        for (int i = 0; i<bulletArray.size(); i++){
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
                //g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        //score
        g.setColor(Color.pink);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (GameOver) {
            g.drawString("Game Over: "+ String.valueOf(score), 10, 35);
        }
        else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    public void move() {
        //aliens
        for ( int i=0; i<alienArray.size(); i++){
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x +=alienVelocityx;

                //if alien touches the borders
                if (alien.x + alien.width >= boardWidth || alien.x <=0){
                    alienVelocityx *= -1;
                    alien.x += alienVelocityx*2;

                    //move all aliens down by one row
                    for (int j = 0; j<alienArray.size(); j++){
                        alienArray.get(j).y +=alienHeight;
                    }
                }
                if (alien.y >= ship.y){
                    GameOver = true;
                }
            }
        }

        //bullets
        for (int i = 0; i<bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityy;

            //bullet collision with aliens
            for (int j = 0; j< alienArray.size(); j++){
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score +=10;

                }
            }
        }
        //clear bullets
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)){
            bulletArray.remove(0); //removes the first element of the array
        }

        // next level
        if (alienCount == 0) {
            //increase the number of aliens in columns and rows by 1
            score +=alienColumns * alienRows * 10; //bonus points for clearing the level :)
            alienColumns = Math.min(alienColumns + 1, columns/2 - 2); //cap column at 16/2 - 2 = 6
            alienRows = Math.min(alienRows + 1, rows - 6); //cap rows at 16 - 6 = 10
            alienArray.clear();
            bulletArray.clear();
            alienVelocityx = 1;
            createAliens();
        }
    }

    public void createAliens(){
        Random random = new Random();
        for (int r = 0; r< alienRows; r++){
            for ( int c = 0; c < alienColumns; c++){
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienx + c*alienWidth,
                        alieny + r*alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (GameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (GameOver) { //any key to restart
            ship.x = shipx;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityx = 1;
            alienColumns = 3;
            alienRows = 2;
            GameOver = false;
            createAliens();
            gameLoop.start();
         }
        if (e.getKeyCode()== KeyEvent.VK_LEFT && ship.x - shipVelocityx >=0){
            ship.x -=shipVelocityx; //move left one tile
        }
        else if (e.getKeyCode()== KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityx <= boardWidth){
            ship.x +=shipVelocityx; //move right one tile
        }
        else if (e. getKeyCode()== KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth*15/32, ship.y,bulletwidth, bulletheight, null);
            bulletArray.add(bullet);
        }

    }
}
