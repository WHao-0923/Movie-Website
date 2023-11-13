Project 3

Group Members:
 - Weiyu Hao - XML Parsers,
 - Pengpeng Zhang - reCAPTCHA, Encrypted Password, Dashboard using Stored Procedure

Filenames with PreparedStatements:  
all java files under src directory satisfies the sql-safe requirment.

XML Performance Tuning:  
 - Multi-threads Parsing: Use multi-threads for to run the 3 parsers in parallel. Reduce the time for parsing from ~570ms to ~470ms.
 - Batch Execution for SQL: Use batch execution for the sql queries as a whole. Reduce the total time from ~5800ms to ~5200ms.
 - Multi-threads SQL Executions: Use multi-threads to execute the insertion in parallel. Reduce the total time from ~5200ms to ~5000ms.

XML Inconsistent Report:  


DEMO URL: https://54.183.194.96:8443/Fablix-hz/login.html
