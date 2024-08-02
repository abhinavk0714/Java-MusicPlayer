import java.io.File;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

/**
 * Song Class
 * @author abhinavk
 */
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;
    private Mp3File mp3File;
    private double framRatePerMilliseconds;

    /**
     * Constructor that initializes a song from the directory
     * @param filepath of the song
     */
    public Song(String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            framRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            songLength = convertToSongLengthFormat();

            // use jaudiotagger lib to create audiofile obj to reap mp3 file's info
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // read through meta data of audio file
            Tag tag = audioFile.getTag();
            if(tag != null) {
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
            }else {
                // case if mp3 file's meta data can't be read
                songTitle = "N/A";
                songArtist = "N/A";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to convert a song's length into a formatted time
     * @return the formatted time
     */
    private String convertToSongLengthFormat() {
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        
        return formattedTime;
    }

    /**
     * Song title getter
     * @return the song's title
     */
    public String getSongTitle() {
        return songTitle;
    }

    /**
     * Song artist getter
     * @return the song's artist
     */
    public String getSongArtist() {
        return songArtist;
    }

    /**
     * Song length getter
     * @return the song's length
     */
    public String getSongLength() {
        return songLength;
    }

    /**
     * Song file path getter
     * @return the song's file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * MP3 file getter
     * @return the mp3 file
     */
    public Mp3File getMp3File() {
        return mp3File;
    }

    /**
     * Frame rate per milliseconds getter
     * @return the frame rate per milli
     */
    public double getFrameRatePerMilliseconds() {
        return framRatePerMilliseconds;
    }
}
