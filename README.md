Akka streams example
====================

Demonstrate Akka streams in Scala.

Akka Streams is an implementation of [Reactive Streams](http://www.reactive-streams.org/),
which is a standard for asynchronous stream processing with non-blocking backpressure.

This projects only gives you a "thing you can run", now :)
You just have to generate your twitter key and secret add an `application.conf` in the `project` folder, with:
```
appKey = "<your app key>"
appSecret = "<your app secret>"
accessToken = "<your access token>"
accessTokenSecret = "<your access token secret>"
```

and then `sbt run`.


All credits goes to Abhinav Ajgaonkar. You may want to read his original article [here](http://blog.abhinav.ca/blog/2015/02/19/scaling-with-akka-streams/).
