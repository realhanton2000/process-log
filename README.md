# process-log

Start HSQLDB with
```
java -cp ../lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:eventdb --dbname.0 eventdb
```

Build with 
```
mvn clean install
```

Launch with (D:\\dev\\process-log - this is the directory contains logfile.txt)
```
java -jar ./target/process-log-0.0.1.jar D:\\dev\\process-log
```
[application.properties](src/main/resources/application.properties)
```
processlog.dbtype=HSQLDB          <-- Define the data repo type
processlog.streamtype=FILE        <-- Define the stream type
processlog.writeTrigger=100       <-- Use it to reduce the database writing frequence. 
                                  Do writing with batch insert/update when 
                                  here are 100 paired events in memory.
processlog.maxSize=-1             <-- In the extreme case that here are too many unpaired 
                                  events in memory could lead to OOM.
                                  Log the error and clean up those events in memory.
                                  (default -1 means no such size check)
processlog.alertThreshold=4       <-- Decide if this event shall be flagged (default 4 ms)
```

implemented one version to utilize multi-threads, committed to [multi-threads](https://github.com/realhanton2000/process-log/tree/multi-thread) branch.
