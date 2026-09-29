# CS7IS3 Assignment 1: Lucene search engine for the Cranfield collection

## Requirements
- Linux terminal (I used WSL through Windows Powershell)
- Java 21+
- Maven
- gcc and make (to build trec_eval)
- `-std=gnu17` is needed with GCC 15 or newer (my machine). Older compilers can use plain `make`.

## Setup: trec_eval (once only)
```
wget https://trec.nist.gov/trec_eval/trec_eval_latest.tar.gz
tar -xzf trec_eval_latest.tar.gz
cd trec_eval-9.0.7
make CC="gcc -std=gnu17"
cd ..
```
If the folder name differs, check it with `ls -d trec_eval*/`.

## Build
```
mvn package
```

## Run
```
java -cp target/assignment1-1.0.jar ie.tcd.brownlu.Main
```

## Evaluate
Each run of the Cranfield queries writes `results/<analyser>_<scorer>.txt`, e.g. `results/custom_bm25.txt`.
To list all results files: `ls results/`
```
trec_eval-9.0.7/trec_eval -m map -m recall.1000,100 -m P.10 data/QRelsCorrectedforTRECeval results/custom_bm25.txt
```
Replace `custom_bm25.txt` with the file you want to evaluate.

## View answer key for a single query
```
grep '^X ' data/QRelsCorrectedforTRECeval
```
where X is the id number of the query that is being shown<br>
output read as: queryNumber, 0, relevantDocument, relevanceScore<br>
1, 0, 184, 3<br>
Query 1 was relevant to Document 184, with a high relevance (3)

## View Full Experimental Log
```
cat workingResults.md
```
