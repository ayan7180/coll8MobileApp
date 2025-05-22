package backend.Annotations;

import backend.Files.SysFile;

import java.util.Set;

public class VideoAnnotation extends Annotation {

    // startTime: The time that the annotation starts at.
    // endTime: The time that the annotation ends at.
    private double startTime;
    private double endTime;

    public VideoAnnotation(){
        super();
    }

    public VideoAnnotation(double startTime, double endTime,
                          String text, Set<SysFile> references,
                          SysFile parentFile, Annotation parentAnnotation){
        super(text, references, parentFile, parentAnnotation);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setEndTime(double endTime){
        this.endTime = endTime;
    }
    public double getEndTime(){
        return endTime;
    }

    public void setStartTime(double startTime){
        this.startTime = startTime;
    }
    public double getStartTime(){
        return startTime;
    }

}
