## Changes

### 1.0.6
* fix neo4j embedded browser

### 1.0.5
* rx-aws 1.2.0
* neorx 1.3.0 (uses OkHttp 3.2.0)

### 1.0.2
* Expose hook URL path in GitHubWebHookMessage

### 1.0.1
* Reactor event refactoring
* GitHub web hook event cleanup
* Got bored of version <1

### 0.107.0
* Add Project Reactor
* Simplified the Distributed Event Log

### 0.105.0

* vastly improved scheduler functionality
* ScheduledTask nodes can now have scheduledBy=manual set which will prevent the script's cron/enabled flags from being used
* Scanning AWS ELBs now pulls tags properly
* added rapidoid-u

### 0.104.0
* added distributed event log

### 0.92.0

* expose Neo4j through UI with role-based access controls
* refactor role management
* upgrade versions:
  * vaadin 7.4.8 -> 7.5.2
  * spring-boot 1.2.4.RELEASE->1.2.5.RELEASE
  * spring 4.1.6.RELEASE -> 4.1.7.RELEASE

### 0.91.0
* upgrade to jdk 8

### 0.90.5
* fix update timestamp for ComputeInstance updates

### 0.90.4
* basic working cloudstack plugin

### 0.86.1
* add MockWebServer (from Square OkHttp)
* upgrade junit to 4.12
* upgrade slf4j to 1.7.10

### 0.86.0 
* A10 will automatically detect active/standby state
* upgrade vaadin 7.3.10 -> 7.4.0
* add clojure jsr223 support
* upgrade jython and jruby
* upgrade spring 4.1.4.RELEASE -> 4.1.5.RELEASE
* upgrade gradle 2.2.1 -> 2.3
* rename macgyver-jython -> macgyver-plugin-jython
* rename macgyver-jruby -> macgyver-plugin-jruby

### 0.85.0
* add jenkins plugin
* add github webhook support
* add handlebars template MVC support
* remove javax.json
* upgrade versions:
  * aspectjweaver - 1.5.4 -> 1.8.5
  * hazelcast - 3.2.5 -> 3.4.1
  * jackson - 2.4.3 -> 2.5.1
  * joda-time - 2.3 -> 2.7
  * groovy - 2.3.9 -> 2.4.0
  * assertj - 1.7.0 -> 1.7.1
  * okhttp - 2.1.0 -> 2.2.0
  * vaadin - 7.3.7 -> 7.3.10
  * spring - 4.1.4.RELEASE
  * spring boot - 1.1.8.RELEASE -> 1.2.1.RELEASE

### 0.84.0
* #109 - Auto-refresh VIJava ServiceInstance sessions
* #111 - add basic JIRA integration
* add support for Retrofit

### 0.81.1
* CertChecker can match hostnames on wildcard cert

### 0.81.0
* Add CertChecker

### 0.80.2
* Fix broken AppInstance checkIn

### 0.80.1
* NeoRx related fixes

### 0.80.0
* Switched to NeoRx

### 0.76.0
* Added AssertJ for fluent unit test assertions
* Refactor of Leftronic client to add flexibility

### 0.75.0
* Fixed dependency conflicts with bouncycastle 

### 0.74.0
* Upgraded SSHJ to 0.10.0
* Better configuration of SSH authentication




