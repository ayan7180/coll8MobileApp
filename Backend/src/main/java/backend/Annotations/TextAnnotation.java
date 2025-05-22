package backend.Annotations;

import backend.Files.SysFile;
import jakarta.persistence.Entity;
import org.w3c.dom.Text;

import java.util.Set;

@Entity
public class TextAnnotation extends Annotation {

    // startChar: The character that the annotation starts on.
    // endChar: The character that the annotation ends on.
    // The first character is character 0, the second is character 1, and so on.
    private int startChar;
    private int endChar;

    public TextAnnotation(){
        super();
    }

    public TextAnnotation(int startChar, int endChar,
                          String text, Set<SysFile> references,
                          SysFile parentFile, Annotation parentAnnotation){
        super(text, references, parentFile, parentAnnotation);
        this.startChar = startChar;
        this.endChar = endChar;
    }

    public void setEndChar(int endChar){
        this.endChar = endChar;
    }
    public int getEndChar(){
        return endChar;
    }

    public void setStartChar(int startChar){
        this.startChar = startChar;
    }
    public int getStartChar(){
        return startChar;
    }

}
