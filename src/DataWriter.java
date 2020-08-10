import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class DataWriter {
    BufferedWriter bufferedWriter;
    public DataWriter(String textFileName){
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(textFileName));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeText(String txt) {
        try {
            bufferedWriter.write(txt);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeStream(){
       try {
           bufferedWriter.close();
       }
       catch (IOException e1){
           e1.printStackTrace();
       }
    }

}


