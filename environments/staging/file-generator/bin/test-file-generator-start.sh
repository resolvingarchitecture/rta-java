#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx3G -cp /etl/bin/file-producer.jar ra.rta.models.utilities.FinancialTransactionFileGenerator /etl/incoming/group/test/ 20000000 720