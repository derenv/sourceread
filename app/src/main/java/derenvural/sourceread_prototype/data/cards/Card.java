package derenvural.sourceread_prototype.data.cards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public abstract class Card implements DataHolder, Serializable {
    private String image;
    private String title;
    private String text;
    // Serialisation
    private static final long serialVersionUID = 1L;

    // extended functionality eg onclick, links, analysis data
    public Card(@Nullable String newImage, @NonNull String newTitle, @Nullable String newText){
        setImage(image);
        setTitle(title);
        setText(text);
    }

    // GET
    public String getImage(){
        return image;
    }
    public String getTitle(){
        return title;
    }
    public String getText(){
        return text;
    }

    // SET
    public void setImage(String image){
        this.image = image;
    }
    public void setTitle(String title){ this.title = title; }
    public void setText(String text){
        this.text = text;
    }
}
