import java.awt.Color;
import java.awt.image.*;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MusicPlayerGUI extends JFrame {
    
    // color config
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    
    public MusicPlayerGUI() {
        
        // calls JFrame constructor to configure out GUI and set title header to "Music Player"
        super("Music Player");
        
        // set the width and height
        setSize(400, 600);
        
        // end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // launch the app at the center of the screen
        setLocationRelativeTo(null);
        
        // prevent the app from being resized
        setResizable(false);
        
        // set layout to null which allows us to control the (x, y) coords of our components
        // and set height and width
        setLayout(null);
        
        // change the frame color
        getContentPane().setBackground(FRAME_COLOR);
        
        addGuiComponents();
    }
    
    private void addGuiComponents() {
        // add toolbar
        addToolbar();
        
        // load the record image
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);
    }
    
    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        
        // prevent toolbar from being moved
        toolBar.setFloatable(false);
        
        // add dropdown menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);
        
        // add song menu to place the loading song option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);
        
        // add the "load song" item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        songMenu.add(loadSong);
        
        // add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);
        
        // add items in the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);
        
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);
        
        add(toolBar);
    }
    
    private ImageIcon loadImage(String imagePath) {
        try{
            // read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));
            
            // returns an image icon so the component can render the image
            return new ImageIcon(image);
            
        }catch(Exception e){
            e.printStackTrace();
        }

        // could not find resource
        return null;
    } 
}
