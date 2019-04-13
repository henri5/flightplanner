# flightplanner

### Requirements
* Docker
* Java 11

### What
* do `GET /api/routes/${source IATA}/${destination IATA}`
* add `GET` parameter `?allowLandConnections=true` for a bus ride
* distances in km (I think)
* data from https://openflights.org/data.html

### Examples
* example request http://localhost:8080/api/routes/AER/GYD outputs
```
{
  "source" : "AER",
  "destination" : "GYD",
  "distance" : 1529,
  "connections" : [ {
    "source" : "AER",
    "destination" : "KRR",
    "distance" : 187,
    "type" : "AIR"
  }, {
    "source" : "KRR",
    "destination" : "SCO",
    "distance" : 955,
    "type" : "AIR"
  }, {
    "source" : "SCO",
    "destination" : "GYD",
    "distance" : 387,
    "type" : "AIR"
  } ]
}
```


* example route that only works with land connection http://localhost:8080/api/routes/GOM/GYD?allowLandConnection=true
```
{
  "source" : "GOM",
  "destination" : "GYD",
  "distance" : 6079,
  "connections" : [ {
    "source" : "GOM",
    "destination" : "KME",
    "distance" : 95,
    "type" : "LAND"
  }, {
    "source" : "KME",
    "destination" : "KGL",
    "distance" : 148,
    "type" : "AIR"
  }, {
    "source" : "KGL",
    "destination" : "DXB",
    "distance" : 4072,
    "type" : "AIR"
  }, {
    "source" : "DXB",
    "destination" : "GYD",
    "distance" : 1764,
    "type" : "AIR"
  } ]
}
```
