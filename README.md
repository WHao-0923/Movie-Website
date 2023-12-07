- # General
    - #### Team#:

    - #### Names: Weiyu Hao, Pengpeng Zhang

    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution: Pengpeng Zhang-Connection Pooling&Jmeter


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - configuration: ./WebContent/META_INF/context.xml
    - code using it: all java files under ./src with connection to database

    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    - In the Fabflix code, when a connection is required by servlet, it will try to find an idle connection in the pool and use it. If there is no idle connection, a new connection will be established(not exceeding the maximum defined in context.xml). If total number exceeds the idle connection, the idle connections will be closed. 

    - #### Explain how Connection Pooling works with two backend SQL.
    - With two backend SQL databases, connection pooling typically involves maintaining separate pools for each database. Each pool operates independently, managing connections to its respective database. When an application requires data, it determines the appropriate database, then retrieves a connection from the corresponding pool. This separation ensures that connections to each database are efficiently reused without interference, while still allowing the application to balance load and resources between the two databases as needed.


- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?


- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |