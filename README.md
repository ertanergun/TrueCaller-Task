# TrueCaller-Task

* To package the example run.

        mvn package

* To setup the h2 database run.

        java -jar target/truecallertask-1.0.jar db migrate truecaller.yml

* To run the server run.

        java -jar target/truecallertask-1.0.jar server truecaller.yml
		
* To run the application, open any browser and use urls below.

	To save a view action:
        http://localhost:8090/view/viewer={viewerId}&viewing={viewedId}
	
	To list view history for user
	http://localhost:8090/view/listviewerfor={viewedId}
