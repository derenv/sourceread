package derenvural.sourceread_prototype.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class DataHolder {
    private String image;
    private String title;
    private String text;

    public DataHolder(@Nullable String newImage, @NonNull String newTitle, @Nullable String newText){
        image = newImage;
        title = newTitle;
        text = newText;
    }

    public String getImage(){
        return image;
    }

    public String getTitle(){
        return title;
    }

    public String getText(){
        return text;
    }
}
