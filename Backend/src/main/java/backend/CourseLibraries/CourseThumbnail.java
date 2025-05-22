package backend.CourseLibraries;

import backend.Announcements.Announcement;
import backend.Courses.Course;
import backend.Files.SysFile;

public class CourseThumbnail {


    // Complex-relationships were created in these listed types, instead of this type.
    private Course course;
    private Announcement[] recentAnnouncements;
    private SysFile[] recentFiles;

    public CourseThumbnail() {

    }

    public CourseThumbnail(Course course, int numAnnouncements, int numFiles) {
        this.course = course;
        this.recentAnnouncements = new Announcement[numAnnouncements];
        this.recentFiles = new SysFile[numFiles];
    }

    public CourseThumbnail(Course course, Announcement[] recentAnnouncements, SysFile[] recentFiles) {
        this.course = course;
        this.recentAnnouncements = recentAnnouncements;
        this.recentFiles = recentFiles;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setRecentFiles(SysFile[] recentFiles) {
        this.recentFiles = recentFiles;
    }

    public void setRecentAnnouncements(Announcement[] recentAnnouncements) {
        this.recentAnnouncements = recentAnnouncements;
    }

    public Announcement[] getRecentAnnouncements() {
        return recentAnnouncements;
    }

    public SysFile[] getRecentFiles() {
        return recentFiles;
    }

}
