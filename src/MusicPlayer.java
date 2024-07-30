import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private int currentFrame;
    private int currentTimeInMilli;

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;
        if (currentSong != null) {
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong() {
        if (currentSong == null) return;

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

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");
        if (isPaused) {
            currentFrame += evt.getFrame();
        }
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
    }
}

// import java.io.BufferedInputStream;
// import java.io.FileInputStream;

// import javazoom.jl.player.advanced.AdvancedPlayer;
// import javazoom.jl.player.advanced.PlaybackEvent;
// import javazoom.jl.player.advanced.PlaybackListener;

// public class MusicPlayer extends PlaybackListener {
//     // this will be used to update isPaused more synchronously
//     private static final Object playSignal = new Object();
    
//     // need reference so that we can update the GUI in this class
//     private MusicPlayerGUI musicPlayerGUI;
    
//     // need to store song details -> create a song class
//     private Song currentSong;
//     public Song getCurrentSong() {
//         return currentSong;
//     }
    
//     // use JLayer lib to create an AdvancedPlayer obj to handle playing the music
//     private AdvancedPlayer advancedPlayer;
    
//     // pause bool flag used to indicate whether the player has been paused
//     private boolean isPaused;
    
//     // stores the last frame when the playback is finished (used for pausing and resuming)
//     private int currentFrame;
//     public void setCurrentFrame(int frame) {
//         currentFrame = frame;
//     }
    
//     // track how many milliseconds has passed since playing the song (used for updating the slider)
//     private int currentTimeInMilli;
//     public void setCurrentTimeInMilli(int timeInMilli) {
//         currentTimeInMilli = timeInMilli;
//     }
    
//     // constructor
//     public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
//         this.musicPlayerGUI = musicPlayerGUI;
//     }
    
//     public void loadSong(Song song) {
//         currentSong = song;
        
//         // play the current song if not null
//         if(currentSong != null) {
//             playCurrentSong();
//         }
//     }
    
//     public void pauseSong() {
//         if(advancedPlayer != null) {
//             // update isPaused flag
//             isPaused = true;
            
//             // stop the player
//             stopSong();
//         }
//     }
    
//     public void stopSong() {
//         if(advancedPlayer != null) {
//             advancedPlayer.stop();
//             advancedPlayer.close();
//             advancedPlayer = null;
//         }
//     }
    
//     public void playCurrentSong() {
//         if(currentSong == null)
//         return;
        
//         try{
//             //read mp3 audio data
//             FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
//             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            
//             // create a new advanced player
//             advancedPlayer = new AdvancedPlayer(bufferedInputStream);
//             advancedPlayer.setPlayBackListener(this);
            
//             // start music
//             startMusicThread();
            
//             // start playback slider thread
//             startPlaybackSliderThread();
            
//         }catch(Exception e){
//             e.printStackTrace();
//         }
//     }
    
//     // create a thread that will handle playing the music
//     public void startMusicThread() {
//         new Thread(new Runnable() {
//             @Override
//             public void run() {
//                 try{
//                     if(isPaused) {
//                         synchronized(playSignal) {
//                             // update flag
//                             isPaused = false;
                            
//                             // notify the other thread to continue
//                             playSignal.notify();
//                         }
                        
//                         // resume music from last frame
//                         advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
//                     }else{
//                         // play the music from the beginning
//                         advancedPlayer.play();
//                     }
//                 }catch(Exception e){
//                     e.printStackTrace();
//                 }
//             }
//         }).start();
//     }
    
//     // create a thread that will handle updating the slider
//     private void startPlaybackSliderThread() {
//         new Thread(new Runnable() {
//             @Override
//             public void run() {
//                 if(isPaused) {
//                     try{
//                         // wait until it gets notified by other thread to continue
//                         // makes sure that isPaused bool flag updates to false before continuing
//                         synchronized(playSignal){
//                             playSignal.wait();
//                         }
//                     }catch(Exception e) {
//                         e.printStackTrace();
//                     }
//                 }

//                 while(!isPaused) {
//                     try{
//                         // increment current time milli
//                         currentTimeInMilli++;
                        
//                         // calculate into frame value (1.28 works for my pc, may need a diff multiplier for diff pcs)
//                         int calculatedFrame = (int) ((double) currentTimeInMilli * 1.28 * currentSong.getFrameRatePerMilliseconds());
                        
//                         // update the GUI
//                         musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                        
//                         // mimic 1 millisecond using thread.sleep
//                         Thread.sleep(1);
//                     }catch(Exception e) {
//                         e.printStackTrace();
//                     }
//                 }
//             }
//         }).start();
//     }
    
//     @Override
//     public void playbackFinished(PlaybackEvent evt) {
//         // this gets called when the song finishes or the player gets closed
//         System.out.println("Playback Finished");
        
//         if(isPaused) {
//             currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
//         }
//     }
    
//     @Override
//     public void playbackStarted(PlaybackEvent evt) {
//         // this gets called in the beginning of the song
//         System.out.println("Playback Started");
//     }
    
    
// }
