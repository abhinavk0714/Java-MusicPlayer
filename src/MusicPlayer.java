import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener {
    // need to store song details -> create a song class
    private Song currentSong;
    
    // use JLayer lib to create an AdvancedPlayer obj to handle playing the music
    private AdvancedPlayer advancedPlayer;
    
    // pause bool flag used to indicate whether the player has been paused
    private boolean isPaused;
    
    // stores the last frame when the playback is finished (used for pausing and resuming)
    private int currentFrame;
    
    // constructor
    public MusicPlayer() {
        
    }
    
    public void loadSong(Song song) {
        currentSong = song;
        
        // play the current song if not null
        if(currentSong != null) {
            playCurrentSong();
        }
    }
    
    public void pauseSong() {
        if(advancedPlayer != null) {
            // update isPaused flag
            isPaused = true;
            
            // stop the player
            stopSong();
        }
    }
    
    public void stopSong() {
        if(advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }
    
    public void playCurrentSong() {
        if(currentSong == null)
            return;

        try{
            //read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            
            // create a new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            
            // start music
            startMusicThread();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // create a thread that will handle playing the music
    public void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isPaused) {
                        // resume music from last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }else{
                        // play the music from the beginning
                        advancedPlayer.play();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    @Override
    public void playbackFinished(PlaybackEvent evt) {
        // this gets called when the song finishes or the player gets closed
        System.out.println("Playback Finished");
        
        if(isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }
    }
    
    @Override
    public void playbackStarted(PlaybackEvent evt) {
        // this gets called in the beginning of the song
        System.out.println("Playback Started");
    }
    
    
}
