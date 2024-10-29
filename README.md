Scanner Rule Engine

A Spring boot application to upload files, scans the file for vulnerability and sends the result in an email.

Requirements :

1. User should be able to upload file
2. User should get the scan report if vulnerabilities are found in the file. 


Technologies used :

Apache Maven 3.3.9
Spring Boot 2.6.8


App Endpoints

To upload a file
API : http://localhost:8080/api/upload
Parameters : 
    1. files : file to upload
    2. repositoryName : any name (string)
    3. releaseName : any name (string)


Steps to run the project :

1. Fork the github repo
2. Run mvn clean install
3. Use the api to upload the file
4. If vulnerabilities exist in the file, an email is sent.