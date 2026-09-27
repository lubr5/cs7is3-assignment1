# CS7IS3 Assignment 1: Lucene search engine for the Cranfield collection

## Requirements
- Linux terminal (I used WSL through Windows Powershell)
- Java 21+
- Maven
- gcc and make (to build trec_eval)
- `-std=gnu17` is needed with GCC 15 or newer (my machine). Older compilers can use plain `make`.

## Build
```
mvn package
```

## Run
```
java -cp target/assignment1-1.0.jar ie.tcd.brownlu.Indexer
```
```
java -cp target/assignment1-1.0.jar ie.tcd.brownlu.Searcher
```
## Evaluate (single query)
```
grep '^X ' data/QRelsCorrectedforTRECeval
```
where X is the id number of the query that is being evaluated
## Evaluate (results file - analyser & scorer must be known)
```
trec_eval-9.0.7/trec_eval -m map -m recall.1000,10 -m P.10 data/QRelsCorrectedforTRECeval results/standard_bm25.txt
```

TODO: add evaluation for the dynamic analyser/scorer functionality that I intend to add

## View Full Experimental Log
```
cat workingResults.md
```
