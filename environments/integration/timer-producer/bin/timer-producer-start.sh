#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

#exec java -Xmx2G -cp /Library/timer-producer/bin/timer-producer.jar ra.rta.producers.timer.TimerProducer localhost:9092 summary Daily 4 0 localhost
exec java -Xmx2G -cp /Library/timer-producer/bin/timer-producer.jar ra.rta.producers.timer.TimerProducer localhost:9092 summary Minutely 10 localhost