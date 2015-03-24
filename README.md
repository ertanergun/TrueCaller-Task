# TrueCaller-Task

* To package the example run.

        mvn package

* To setup the h2 database run.

        java -jar target/TrueCaller-Task.jar db migrate truecaller.yml

* To run the server run.

        java -jar target/TrueCaller-Task.jar server truecaller.yml
