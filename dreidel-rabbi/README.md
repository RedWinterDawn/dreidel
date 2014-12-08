#Dreidel Rabbi
dreidel-rabbi is the main server that manages all of the instances of different resources.

#Starting dreidel-rabbi locally

##Install postgres 9.3.2
There are lots of ways to do this but my favorite is `brew install postgresql-9.3.2`. If you did the postgres mac app you are on your own from here.

Locate a folder that you want to keep your database.  I am going to choose ~/Postgres for now.

```BASH
mkdir ~/Postgres
initdb -D ~/Postgres
```

To start postgres you now just have to run `postgres -D ~/Postgres`

You now need to make sure you can connect to the database using the postgres user over localhost.

`createuser -s postgres`

You are good to go!

##Starting the service locally

Clone the dreidel project to your machine.

`git clone git@github.com:jive/dreidel`

Import the project into eclipse.

inside the dreidel-rabbi project run `src/test/java/com.jive.qa.dreidel.rabbi.DreidelRabbiTestLauncher`

You are good to go just run the spinnit client against localhost instead of one of the other servers!

Happy Testing!
