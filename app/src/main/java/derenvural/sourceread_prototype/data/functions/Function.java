package derenvural.sourceread_prototype.data.functions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class Function implements FunctionHolder {
    private String title;
    private String text;

    // extended functionality eg onclick, links, analysis data
    public Function(@NonNull String newTitle, @Nullable String newText){
        setTitle(newTitle);
        setText(newText);
    }

    // GET
    public String getTitle(){
        return title;
    }
    public String getText(){
        return text;
    }

    // SET
    public void setTitle(String title){ this.title = title; }
    public void setText(String text){
        this.text = text;
    }
}
