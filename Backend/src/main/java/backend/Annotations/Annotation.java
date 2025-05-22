package backend.Annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import backend.Files.*;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Annotation {

    // TODO Relationships

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // text: The content of the annotation.
    private String text;

    // TODO Owner

    @OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Annotation> children;
    // Children annotation (comments) made on this annotation

    // The annotation this annotation was made on
    @ManyToOne
    @JoinColumn(name = "parentAnnID")
    @JsonIgnore
    private Annotation parentAnnotation;

    // The file this annotation was made on

    @ManyToOne
    @JoinColumn(name = "parentFileID")
    @JsonIgnore
    private SysFile parentFile;

    // References to other files made by the annotation
    @ManyToMany
    @JoinTable(name = "annotationsFiles_Ref", joinColumns = @JoinColumn(name = "refFileID"), inverseJoinColumns = @JoinColumn(name = "fileID"))
    @JsonIgnore
    private Set<SysFile> references;

    public Annotation(){

    }

    public Annotation(String text, Set<SysFile> references,
                      SysFile parentFile, Annotation parentAnnotation){

        this.text = text;

        this.references = references;
        // No need to know what your children are yet; you've only just been posted, after all.
        this.children = new HashSet<>();

        this.parentFile = parentFile;
        this.parentAnnotation = parentAnnotation;

    }

    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }

    public void deleteReference(SysFile reference){
        references.remove(reference);
    }
    public void addReference(SysFile reference){
        references.add(reference);
    }
    public Set<SysFile> getReferences(){
        return references;
    }

    public void deleteChild(Annotation child){
        children.remove(child);
    }
    public void addChild(Annotation child){
        children.add(child);
    }
    public Set<Annotation> getChildren(){
        return children;
    }

    public void setParentFile(SysFile parentFile){
        this.parentFile = parentFile;
    }
    public SysFile getParentFile(){
        return parentFile;
    }

    public void setParentAnnotation(Annotation parentAnnotation){
        this.parentAnnotation = parentAnnotation;
    }

    public Annotation getParentAnnotation(){
        return parentAnnotation;
    }

    public int getId(){ return id; }



}
