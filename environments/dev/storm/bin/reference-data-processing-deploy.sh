#!/bin/sh
# -----------------------------------------------------------------------------
# Start Script for the Reference Data Processing Topology
# -----------------------------------------------------------------------------
# bin/storm : process
# jar : use a jar
# jar location
# Main class
#
bin/storm jar ra-rta-0.5.0.jar ra.rta.topologies.Main /usr/local/apache-storm-0.9.4/conf/storm.yaml referencedata