#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx3G -cp /Library/file-producer/bin/file-producer.jar ra.rta.models.utilities.FileGenerator /Library/file-producer/data/incoming/partner/staging/ 1 1