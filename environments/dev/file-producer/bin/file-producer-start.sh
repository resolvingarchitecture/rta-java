#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx2G -jar /etl/bin/file-producer.jar partner01 100 kaf-01-i.resolvingarchitecture.io:9092,kaf-02-i.resolvingarchitecture.io:9092,kaf-03-i.resolvingarchitecture.io:9092 /etl/partner01/extracts /etl/partner01/extracts/processed