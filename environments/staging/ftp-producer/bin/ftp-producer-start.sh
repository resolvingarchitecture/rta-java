#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx256M -jar /Library/ftp-producer/bin/ftp-producer.jar 21 100 kaf-01-i:9092