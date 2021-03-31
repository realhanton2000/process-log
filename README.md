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
