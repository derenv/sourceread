package derenvural.sourceread_prototype.data.apps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import derenvural.sourceread_prototype.data.DataHolder;

public class App extends DataHolder {
    // extended functionality eg onclick, links, disclaimer
    public App(@Nullable int newImage, @NonNull String newTitle, @Nullable String newText){
        super(newImage, newTitle, newText);
    }
}
