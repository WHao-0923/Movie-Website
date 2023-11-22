Project 3

Group Members:
 - Weiyu Hao - XML Parsers, HTTPS.
 - Pengpeng Zhang - reCAPTCHA, Encrypted Password, Dashboard using Stored Procedure

Filenames with PreparedStatements:  
all java files under src directory satisfies the sql-safe requirment.

XML Performance Tuning:  
 - Multi-threads Parsing: Use multi-threads for to run the 3 parsers in parallel. Reduce the time for parsing from ~570ms to ~470ms.
 - Batch Execution for SQL: Use batch execution for the sql queries as a whole. Reduce the total time from ~5800ms to ~5000ms.
 - Multi-threads SQL Executions: Use multi-threads to execute the insertion in parallel. Reduce the total time from ~5000ms to ~4000ms.

XML Inconsistent Report:  
 - XML_Report.txt

DEMO URL: https://youtu.be/5NkjyPy1gyM
