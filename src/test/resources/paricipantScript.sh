#!/bin/zsh
while true
do
	curl http://localhost:8090/question/survey/6
	echo "\n"
	sleep 5
done
