package backend.CourseLibraries;

import backend.Announcements.Announcement;
import backend.Courses.Course;
import backend.Files.SysFile;
import backend.Users.User;

public class LibraryUtils {
    public static CourseThumbnail generateThumbnail(Course course, User user) {
        Announcement[] announcements;
        SysFile[] sysFiles;
        CourseThumbnail thumbnail;

//        course.getAnnouncements();
        announcements = course.getRecentAnnouncements(user.getNumAnnoucements());
        sysFiles = course.getRecentFiles(user.getNumFiles());

        thumbnail = new CourseThumbnail(course, announcements, sysFiles);

        return thumbnail;
    }
}
