#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

#exec java -Xmx256M -cp /etl/bin/timer-producer.jar ra.rta.producers.timer.TimerProducer kaf-01-i.resolvingarchitecture.io:9092 summary Daily 4 0 172.19.32.121
exec java -Xmx256M -cp /etl/bin/timer-producer.jar ra.rta.producers.timer.TimerProducer kaf-01-i.resolvingarchitecture.io:9092 summary Minutely 10 172.19.32.121