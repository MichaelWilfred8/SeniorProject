#!/usr/bin/expect -f
spawn ssh user@169.254.1.30 #ssh to OBU
expect "user@169.254.1.30's password:"
sleep 1
send "user\r" #sends user for password of OBU
sleep 1
send "poweroff\r" #poweroff machine
interact 
