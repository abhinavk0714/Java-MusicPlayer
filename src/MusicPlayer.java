import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Music Player class
 * @author abhinavk
 */
public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private int currentFrame;
    private int currentTimeInMilli;
    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;
    
    /**
     * Current Song getter
     * @return the current song
     */
    public Song getCurrentSong() {
        return currentSong;
    }
    
    /**
     * Current frame setter
     * @param frame the frame
     */
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }
    
    /**
     * Current time in milli setter
     * @param timeInMilli the time in milliseconds
     */
    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }
    
    /**
     * Music Player constructor
     * @param musicPlayerGUI instance of the gui
     */
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }
    
    /**
     * Method that loads a song
     * @param song a song
     */
    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;
        
        // stop the song if needed
        if(!songFinished)
        stopSong();
        
        if(currentSong != null) {
            // reset frame
            currentFrame = 0;
            
            // reset current time in milli
            currentTimeInMilli = 0;
            
            // update gui
            musicPlayerGUI.setPlaybackSliderValue(0);
            
            playCurrentSong();
        }
    }
    
    /**
     * Method that loads a playlist
     * @param playlistFile the playlist
     */
    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        
        // store the paths from the text file into the playlist arraylist
        try {
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // reach each line from the text file and store the text into the songPath variable
            String songPath;
            while((songPath = bufferedReader.readLine()) != null) {
                // create song object based on song path
                Song song = new Song(songPath);
                
                // add to playlist arraylist
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(playlist.size() > 0) {
            // reset playback slider
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;
            
            // update current song to the first song in the playlist
            currentSong = playlist.get(0);
            currentPlaylistIndex = 0;
            
            // start from the beginning frame
            currentFrame = 0;
            
            // update gui
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);
            
            // start song
            playCurrentSong();
        }
    }
    
    /**
     * Method to pause the song
     */
    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }
    
    /**
     * Method to stop the song
     */
    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }
    
    /**
     * Method to skip to the next song
     */
    public void nextSong() {
        // no need to go to the next song if no playlist
        if(playlist == null)
        return;
        
        // if at the end of the playlist return
        if(currentPlaylistIndex == playlist.size() - 1)
        return;
        
        pressedNext = true;
        
        // stop the song if needed
        if(!songFinished)
        stopSong();
        
        // increase current playlist index
        currentPlaylistIndex++;
        
        // update current song
        currentSong = playlist.get(currentPlaylistIndex);
        
        // reset frame
        currentFrame = 0;
        
        // reset current time in milli
        currentTimeInMilli = 0;
        
        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        
        // play the song
        playCurrentSong();
    }
    
    /**
     * Method to go back to the previous song
     */
    public void prevSong() {
        // no need to go to the next song if no playlist
        if(playlist == null)
        return;
        
        // if at the beginning of the playlist return
        if(currentPlaylistIndex == 0)
        return;
        
        pressedPrev = true;
        
        // stop the song if needed
        if(!songFinished)
        stopSong();
        
        // decrease current playlist index
        currentPlaylistIndex--;
        
        // update current song
        currentSong = playlist.get(currentPlaylistIndex);
        
        // reset frame
        currentFrame = 0;
        
        // reset current time in milli
        currentTimeInMilli = 0;
        
        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        
        // play the song
        playCurrentSong();
    }
    
    /**
     * Method to play the current song loaded
     */
    public void playCurrentSong() {
        if (currentSong == null) 
            return;
        
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            startMusicThread();
            startPlaybackSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method that starts a music thread and synchonizes with the player
     */
    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Method that starts the playbackslider thread to also synchronize with the player
     */
    private void startPlaybackSliderThread() {
        new Thread(() -> {
            if (isPaused) {
                try {
                    synchronized (playSignal) {
                        playSignal.wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            while (!isPaused) {
                try {
                    // Only increment currentTimeInMilli when the song is playing
                    currentTimeInMilli++;
                    int calculatedFrame = (int) (currentTimeInMilli * currentSong.getFrameRatePerMilliseconds());
                    musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    /**
     * Method that finishes the playback when called
     */
    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");
        if (isPaused) {
            currentFrame += evt.getFrame();
            return;
        }
        // If the user pressed next or previous, we don't need to execute the rest of the code
        if (pressedNext || pressedPrev) {
            return;
        }
        
        songFinished = true;
        
        if (playlist == null || currentPlaylistIndex == playlist.size() - 1) {
            // Update GUI for single song or last song in the playlist
            musicPlayerGUI.enablePlayButtonDisablePauseButton();
        } else {
            // Go to the next song in the playlist
            nextSong();
        }
    }
    
    /**
     * Method that starts the playback when called
     */
    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }
}

