#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------
exec java -Xmx256m -cp /etl/bin/timer-producer.jar ra.rta.producers.timer.TimerProducer localhost:9092 summary Minutely 10 0 localhost