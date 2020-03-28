package derenvural.sourceread_prototype.data.storage;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import derenvural.sourceread_prototype.data.login.LoggedInUser;

import static android.content.Context.MODE_PRIVATE;

public class storageSaver {
    public static boolean write(Context context, String uid, LoggedInUser user){
        try {
            // Create streams
            FileOutputStream fos = context.openFileOutput(uid, MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            // Write object
            user.writeObject(os);

            // Close streams
            os.close();
            fos.close();

            return true;
        }catch(IOException error){
            Log.e("STORAGE", "save failed: " + error.getMessage());
            return false;
        }
    }
    public static boolean read(Context context, String uid, LoggedInUser user){
        try {
            // Find directory
            File directory = context.getFilesDir();
            File file = new File(directory.getAbsolutePath() + "/" + uid);

            if(file.exists()){
                // Create streams
                FileInputStream fis = context.openFileInput(uid);
                ObjectInputStream is = new ObjectInputStream(fis);

                // Read object
                user.readObject(is);

                // Close streams
                is.close();
                fis.close();

                return true;
            }else{
                throw new IOException("No object exists! check write/read permissions?");
            }
        }catch(IOException error){
            Log.e("STORAGE", "load failed: " + error.getMessage());
            return false;
        }catch(ClassNotFoundException error){
            Log.e("STORAGE", "load failed: " + error.getMessage());
            return false;
        }
    }
}
