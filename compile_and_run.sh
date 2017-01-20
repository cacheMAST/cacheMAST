#!/bin/bash

# Compile
bash compile.sh

# Launch GUI
mvn exec:java -Dexec.mainClass='cacheMAsT.GraphicalInterface