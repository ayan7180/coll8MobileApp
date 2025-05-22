package backend.CourseLibraries;

public class CourseLibraryStubs {
    public static class Update{
        public Update() {}

        private String username;
        private int numAnnouncements;
        private int numFiles;

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public int getNumFiles() {
            return numFiles;
        }

        public int getNumAnnouncements() {
            return numAnnouncements;
        }

        public void setNumAnnouncements(int numAnnouncements) {
            this.numAnnouncements = numAnnouncements;
        }

        public void setNumFiles(int numFiles) {
            this.numFiles = numFiles;
        }
    }
}
