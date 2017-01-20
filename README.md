# cacheMAST
CacheMAST (Cache Management Analysis and Visualisation Tool) is a tool designed to visualise cache management strategies across a content delivery chain for network and communications researchers and engineers. The tool helps simplify the analysis of how caching choices impact the usage of network and cache infrastructure resources, with the goal of giving a visual insight into the design of appropriate strategies for optimised content delivery. 

# Acknowledgments
This work received funding from the Flamingo Network of Excellence project (318488) of the EU Seventh Framework Programme, and the EPSRC KCN project (EP/L026120/1).

# Launching the software
* `mvn install` then `mvn compile` on all subprojects by running the `compile.sh` script
* Launch after compiling Maven projects
`mvn exec:java -Dexec.mainClass='cacheMAsT.GraphicalInterface'
* To compile + execute run `compile_and_run.sh`