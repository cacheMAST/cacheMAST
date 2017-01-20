#!/bin/bash

# Launch GUI
cd cache-mast
mvn exec:java -Dexec.mainClass='cacheMAsT.GraphicalInterface'

# When GUI closed return to project root
cd ..
