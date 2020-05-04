package derenvural.sourceread_prototype.data.functions.graph;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import derenvural.sourceread_prototype.data.functions.Function;

public class Graph extends Function {
    // Constructors
    public Graph(@NonNull CharSequence title, @Nullable CharSequence text, @Nullable Integer imageID) {
        super(title, text, imageID);
    }
}
