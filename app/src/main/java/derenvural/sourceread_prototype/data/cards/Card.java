package derenvural.sourceread_prototype.data.cards;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import derenvural.sourceread_prototype.data.DataHolder;

public class Card extends DataHolder {
    // extended functionality eg onclick, links, analysis data
    public Card(@Nullable String newImage, @NonNull String newTitle, @Nullable String newText){
        super(newImage, newTitle, newText);
    }
}
