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

This repo contains 2 applications:

- A simple up-and-running example, taken from [Abhinav's blog](http://blog.abhinav.ca/blog/2015/02/19/scaling-with-akka-streams/).
- A more elaborate example, described [in my blog](http://al333z.github.io/2015/02/28/TheDress/).
