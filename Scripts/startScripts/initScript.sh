#! /usr/bin/bash
if [ $# -lt 2 ]; then
	echo "Not enough arguments"
	echo "Usage: ./initScript.sh sourceJarPath destJarPath"
	exit 10
fi
# Grab username
echo -n "Username: "
read -s username
echo "Read Username"
# Grab password
echo -n "Password: "
read -s password
echo "Read Password"
# Copy local jar file to its desination on the remote server 
sshpass -p $password scp $1 $username@coms-3090-069.class.las.iastate.edu:$2
# Kill any processes running out of 8080/tcp
sshpass -p $password ssh $username@coms-3090-069.class.las.iastate.edu "sudo fuser -k 8080/tcp"
# Start the newly-copied program from its jar file
sshpass -p $password ssh $username@coms-3090-069.class.las.iastate.edu "java -jar $2"
