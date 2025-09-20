import javax.swing.*;
public class aliensgame {
    public static void main(String[]args){
        //window variables
        int tileSize =32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; //32 * 16 = 512px
        int boardHeight = tileSize * rows; //32 * 16 = 512px
        JFrame ff = new JFrame("AliensGame");
        //ff.setVisible(true);
        ff.setSize(boardWidth, boardHeight);
        ff.setLocationRelativeTo(null);
        ff.setResizable(true);
        ff.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Alien alien = new Alien();
        ff.add(alien);
        ff.pack();
        alien.requestFocus();
        ff.setVisible(true);

    }
}
