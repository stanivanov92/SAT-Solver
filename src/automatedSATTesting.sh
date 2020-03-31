#!/bin/bash
echo "Welcome to bash scripting"
echo "This script will run Automated testing on the java SAT Solver on files from 965-970"
for i in {965..970}; do
echo "The file is CNF/50Clause/uf50-0$i.cnf"       
	java Main CNF/50Clause/uf50-0$i.cnf; done
echo "The test is completed, Thank you!"

