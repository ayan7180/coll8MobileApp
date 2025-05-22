JSON for automatic object returns

"user": 
{
    "id": int,
    "username": String,
    "emailID": String,
    "password": String
}

"major": 
{
    "abbreviation": String
}

"course": 
{
    {
        "courseNum": int,
        "title": String,
        "major": major
    }
}

"sysFile": 
{
    "filePath" : String,
    "creationDate" : String,
    "lastModification" : String,
    "course" : course,
    "user" : user
}

"courseThumbnail": 
{
    "course": course,
    "recentAnnouncements": [
        annoucement,
        annoucement,
        ...
    ],
    "recentFiles": [
        sysFile,
        sysFile,
        ...
    ],
    "numAnnouncements": int,
    "numFiles": int
}

"courseLibrary": 
[
    courseThumbnail,
    courseThumbnail,
    ...

]