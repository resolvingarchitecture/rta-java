#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the File Producer
# -----------------------------------------------------------------------------

exec java -Xmx3G -cp /etl/bin/file-producer.jar ra.rta.models.utilities.FinancialTransactionFileGenerator /etl/incoming/cluster/test/ 20000000 720