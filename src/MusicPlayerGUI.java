import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlayerGUI extends JFrame {
    
    // color config
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    // allow to use file explorer in our app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    
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

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        // set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("src/assets/songs"));

        // filter file chooser to only see .mp3 extension
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
        
        addGuiComponents();
    }
    
    private void addGuiComponents() {
        // add toolbar
        addToolbar();
        
        // load the record image
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);
        
        // song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);
        
        // song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);
        
        // playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // when the user is holding the tick we want to pause the song
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // when the user drops the tick
                JSlider source = (JSlider) e.getSource();

                // get the frame value from where the user wants to playback to
                int frame = source.getValue();

                // update the current frame in the music player to this frame
                musicPlayer.setCurrentFrame(frame);

                // update the current time in milli too
                musicPlayer.setCurrentTimeInMilli((int) (frame / 1.28 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds()));

                // resume the song
                musicPlayer.playCurrentSong();

                // toggle on pause button and toggle off play button
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);
        
        // playback buttons (prev, play, pause, etc.)
        addPlaybackBtns();
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
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // an int is returned to us to let us know what the user did
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                // checking to see if the user pressed the "open" button
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // create a song obj based on selected file
                    Song song = new Song(selectedFile.getPath());

                    // load song in music player
                    musicPlayer.loadSong(song);

                    // update song title and artist
                    updateSongTitleAndArtist(song);

                    // update playback slider
                    updatePlaybackSlider(song);

                    // toggle on pause button and toggle off play button
                    enablePauseButtonDisablePlayButton();
                }
            }
        });
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
    
    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);
        
        // previous button
        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        playbackBtns.add(prevButton);
        
        // play button
        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle on play button and toggle off pause button
                enablePauseButtonDisablePlayButton();

                // resume the song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);
        
        // pause button
        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                // pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);
        
        // next button
        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    // this will be used to update our slider from the music player class
    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    private void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());

    }

    private void updatePlaybackSlider(Song song) {
        // update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        // beginning will be 00:00
        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        // end will vary depending on the song
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    private void enablePauseButtonDisablePlayButton() {
        // retrive reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        // turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    private void enablePlayButtonDisablePauseButton() {
        // retrive reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        // turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
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
