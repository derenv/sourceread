package derenvural.sourceread_prototype.data.cards;

import android.os.Bundle;

import java.util.Map;

public interface DataHolder {
    // Serialise
    void loadInstanceState(Bundle bundle);
    void saveInstanceState(Bundle bundle);

    //Database
    Map<String, Object> map_data();

    // GET
    String getImage();
    String getTitle();
    String getText();

    // SET
    void setImage(String image);
    void setTitle(String title);
    void setText(String text);
}
