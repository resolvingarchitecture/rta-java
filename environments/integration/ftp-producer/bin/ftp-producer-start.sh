#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx256M -jar /Library/ftp-producer/bin/ftp-producer.jar 21 10 localhost:9092