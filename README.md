# SENG302 Project Team 700
This is Travel EA, a website for storing and organising destinations and trips.
It can be packaged to a deliverable form using the `sbt dist` command.

## Dedicated server instance

We have two instances of the project running on services in our virtual machine.

#### Production (port 443)

Hosts tagged commits only. Uses the prod database `seng302-2019-team700-prod`

<http://csse-s302g7.canterbury.ac.nz:443/>

#### Development (port 8443)

Hosts all commits. Uses the test database `seng302-2019-team700-test`

<http://csse-s302g7.canterbury.ac.nz:8443/>


## How to build and deploy locally
Run
```bash
sbt dist
```

Once complete, navigate to /team-700/target/universal and locate the created snapshot.zip for the team project

Extract (Unzip) all the snapshot.zip at your current directory.

Open the snapshot folder and navigate to the /bin folder and open the terminal here.

Run
```bash
chmod +x seng302-team-700
```

and then:
```bash
./seng302-team-700 -Dplay.http.secret.key='zHEyLENJwfBB88RsrMBW'
```

And open <http://localhost:9000/>

### Local commands

Build a snapshot of all the files you need to execute the program.
```bash
sbt dist
```

Run the project on `localhost:9000`
```bash
sbt run
```

Run all tests in the project
```bash
sbt test
```




### References
* [Play documentation](https://playframework.com/documentation/latest/Home](https://playframework.com/documentation/latest/Home)
* [EBean](https://www.playframework.com/documentation/latest/JavaEbean) is a Java ORM library that uses SQL.The documentation can be found [here](https://ebean-orm.github.io/).
* For Java Forms, Please see [here](<https://playframework.com/documentation/latest/JavaForms>).

