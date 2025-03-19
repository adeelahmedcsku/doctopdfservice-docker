## Build and download all gradle dependencies
# Java Version 17
# Gradle 8.x
# Springboot 3.1
### RUn this gradle project using springboot and open the postman and call the end point int his way.

![image](https://github.com/user-attachments/assets/57b2422e-733c-4738-a414-426ad20931f6)


You can easily convert your docs to pdf using this service.

# In windows:
go to the folder path for instance "C:\Users\adeel\IdeaProjects\doctopdfservice"
then run the following docker command to build and create the docker image.
### docker build -t doc-to-pdf-service .  
then 
### docker compose up -d

then your docker image will be up after 1 mintues approx.
then you have to upload a file like this  through the postman.

set header content type 
![image](https://github.com/user-attachments/assets/aaf1039c-1333-412b-a7a5-6be82a2cbbfe)

Use form data and set body part like this....

![image](https://github.com/user-attachments/assets/4d51482d-06a9-41d4-96a6-775beeebf5fc)


Then it means this service is enabled through the docker.


