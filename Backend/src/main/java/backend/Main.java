package backend;

import backend.Annotations.AnnotationRepository;
import backend.Announcements.Announcement;
import backend.Announcements.AnnouncementRepository;
//import backend.FileLibrary.FileLibraryRepository;
import backend.Profile.Profile;
import backend.Profile.ProfileRepository;
import backend.Calendar.CalendarRepository;
import backend.Calendar.EventRepository;
import backend.Courses.ArchiveRepository;
import backend.Users.User;
import backend.Courses.CourseRepository;
import backend.Files.SysFileRepository;
import backend.Majors.MajorRepository;
import backend.Users.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

/**
 *
 * @author Vivek Bengre
 *
 */

@SpringBootApplication
@EnableJpaRepositories
//@ComponentScan(basePackages = {"backend.Annotations"}) // TODO Breaks code. Will only look in Annotations for anything, including controllers.
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create 3 users with their machines
    /**
     *
     *
     */
    @Bean
   // CommandLineRunner initUser(AnnouncementRepository announcementRepository, UserRepository userRepository, ProfileRepository profileRepository) {
    CommandLineRunner initClass(
        MajorRepository majorRepository,
        CourseRepository courseRepository,
        AnnouncementRepository announcementRepository,
        UserRepository userRepository,
        SysFileRepository sysFileRepository,
        AnnotationRepository annotationRepository,
        ArchiveRepository archiveRepository,
        CalendarRepository calendarRepository,
        EventRepository eventRepository
    ) {
        return args -> {
            Optional<User> opUser = userRepository.findByUsername("Admin");
            if(opUser.isEmpty()){
                User defaultAdmin = new User();
                defaultAdmin.setSuperuser(true);
                defaultAdmin.setUsername("Admin");
                defaultAdmin.setPassword("Admin");
                userRepository.save(defaultAdmin);
            }

        };
    }

}
