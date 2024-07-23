import java.io.File;
import java.lang.reflect.Field;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/*
 * Class used to describe a song
 */
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;

    public Song(String filePath) {
        this.filePath = filePath;
        try {
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

    // getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getFilePath() {
        return filePath;
    }
    
}
