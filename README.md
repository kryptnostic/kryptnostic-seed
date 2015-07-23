# kryptnostic-seed
Kryptnostic Gradle Seed Project

This project sets up the required dependencies to get started with Iris. A Java API to interact with api.kryptnostic.com

## Getting Started
	
	git clone git@github.com:kryptnostic/kryptnostic-seed.git
	cd kryptnsotic-seed
	# for eclipse users
	./gradlew eclipse
	# for intellij users
	./gradlew idea
	# at this point, you may import the project to your IDE
	# run the main method to list all the objects available to the krypt,demo user
	# this should output "Hello World!" followed by a list of object ids. Refer to https://kryptnostic.com/docs for more information about using Iris
	./gradlew run 