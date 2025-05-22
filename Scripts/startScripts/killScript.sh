#! /usr/bin/bash
# Grab Username
echo -n "Username: "
read -s username
echo "Read Username"
# Grab Password
echo -n "Password: "
read -s password
echo "Read Password"
# Kill any process running out of 8080/tcp
sshpass -p $password ssh weyderts@coms-3090-069.class.las.iastate.edu "sudo fuser -k 8080/tcp"
