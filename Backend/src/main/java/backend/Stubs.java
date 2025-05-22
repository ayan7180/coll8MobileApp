package backend;

public interface Stubs {


    interface MajorStubs {

        class Search{
            public Search(){}

            private String abbreviation;

            public String getAbbreviation() {
                return abbreviation;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }
        }

        class New{
            public New() {}

            private String abbreviation;
            private String title;

            public String getAbbreviation(){ return this.abbreviation; }
            public String getTitle(){ return this.title; }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }
        }

    }

    interface UserStubs {

        class Credentials{
            public Credentials(){}

            private String username;
            private String password;

            public String getUsername() {
                return username;
            }

            public String getPassword() {
                return password;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setPassword(String password) {
                this.password = password;
            }
        }

        class AddCourse{
            public AddCourse(){}

            private int userID;
            private int courseID;

            public void setCourseID(int courseID) {
                this.courseID = courseID;
            }

            public void setUserID(int userID) {
                this.userID = userID;
            }

            public int getCourseID() {
                return courseID;
            }

            public int getUserID() {
                return userID;
            }
        }

    }

    interface AnnouncementStubs {

        class New{
            public New() {}

            private String title;
            private String text;

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getText() {
                return text;
            }

        }

    }

    interface FileStubs {

        class Update{
            public Update(){

            }

            private String fileName;

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }
        }

        class Search{
            public Search(){}

            private Integer userID;
            private Integer courseID;

            public Integer getCourseID() {
                return courseID;
            }

            public Integer getUserID() {
                return userID;
            }

            public void setCourseID(Integer courseID) {
                this.courseID = courseID;
            }

            public void setUserID(Integer userID) {
                this.userID = userID;
            }
        }

        class New {
            public New(){}

            private Integer courseID;
            private Integer userID;
            private String fileName;

            public void setCourseID(Integer courseID) {
                this.courseID = courseID;
            }

            public void setUserID(Integer userID) {
                this.userID = userID;
            }

            public void setFileName(String fileName){
                this.fileName = fileName;
            }

            public Integer getCourseID() {
                return courseID;
            }

            public Integer getUserID() {
                return userID;
            }

            public String getFileName() {
                return fileName;
            }
        }

    }

    interface CourseStubs {

        class Search{
            public Search(){

            }

            private int courseNum;
            private String abbreviation;

            public String getAbbreviation() {
                return abbreviation;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }

            public int getCourseNum() {
                return courseNum;
            }

            public void setCourseNum(int courseNum) {
                this.courseNum = courseNum;
            }
        }

        class New {
            public New(){

            }

            private int courseNum;
            private String abbreviation;
            private String title;

            public void setCourseNum(int courseNum) {
                this.courseNum = courseNum;
            }

            public int getCourseNum() {
                return courseNum;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }

            public String getAbbreviation() {
                return abbreviation;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

    }

}
