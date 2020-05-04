package derenvural.sourceread_prototype.data.functions;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class Function implements FunctionHolder {
    private CharSequence title;
    private CharSequence text;
    private Integer image;

    // extended functionality eg onclick, links, analysis data
    public Function(@NonNull CharSequence newTitle, @Nullable CharSequence newText, @Nullable Integer newImage){
        setTitle(newTitle);
        setText(newText);
        setImage(newImage);
    }

    // GET
    public CharSequence getTitle(){
        return title;
    }
    public CharSequence getText(){
        return text;
    }
    public Integer getImage() { return image; }

    // SET
    public void setTitle(CharSequence title){ this.title = title; }
    public void setText(CharSequence text){
        this.text = text;
    }
    public void setImage(Integer image) { this.image = image; }
}
