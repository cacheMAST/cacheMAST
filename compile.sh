#!/bin/bash
# Run all Maven installation routines
cd system-metrics
mvn install
mvn compile 
cd ../network-graph
mvn install
mvn compile
cd ../cache-management
mvn install
mvn compile 
cd ../cache-mast
mvn install
mvn compile
cd ..