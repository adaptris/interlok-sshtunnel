# interlok-sshtunnel [![Codacy Badge](https://api.codacy.com/project/badge/Grade/0287037a13ba414b903080f2654c605a)](https://www.codacy.com/app/adaptris/interlok-sshtunnel)
`friendly-happiness` was the suggested project name

## Why ![Interlok Hammer](https://img.shields.io/badge/certified-interlok%20hammer-red.svg)

Sometimes, you're trying to access a database to do some selects, but they won't open the firewall to let you through. But they will let you login via SSH...

## How to configure

It's a management component, so everything is configured in bootstrap.properties where your configuration consists of a number of  `sshtunnel.tunnel.<identifier>.XXX=` properties. Easiest to explain with an example. In this example the identifier for grouping purposes is _hammertime_. 

You can of course define multiple identifiers for additional SSH tunnels.

```
managementComponents=jmx:sshtunnel:jetty
# The default port is 22
sshtunnel.tunnel.hammertime.host=10.1.2.3:22
sshtunnel.tunnel.hammertime.tunnel.1=3306:3306
sshtunnel.tunnel.hammertime.tunnel.2=2506:2506
sshtunnel.tunnel.hammertime.user=trex
sshtunnel.tunnel.hammertime.privateKey=/path/to/my/privatekey/id_rsa
sshtunnel.tunnel.hammertime.privateKeyPassword=%env{MY_SECRET_PRIVATEKEY_PASSWORD}
```

* You connect to 10.1.2.3:22 as the user _trex_. 
* Your private key is stored in `/path/to/my/privatekey/id_rsa`
* Your private key password is stored against the environment variable `MY_SECRET_PRIVATEKEY_PASSWORD`
    * If you leave this undefined, then a java null is used as the password.
* A tunnel is opened from the local port 3306 -> 10.1.2.3:3306
* A tunnel is opened from the local port 2506 -> 10.1.2.3:2506

So now you can use `jdbc:mysql://localhost:3306/` even though you haven't got mysql installed locally.

Other settings that you could use

```
sshtunnel.tunnel.hammertime.password=PW:XXXX or %env{PASSWORD} or %sysprop{my.property}
sshtunnel.tunnel.hammertime.keepAlive.seconds=60
sshtunnel.tunnel.hammertime.proxy=localhost:3128 or %env{HTTP_Proxy} or %sysprop{my.proxy}
sshtunnel.tunnel.hammertime.proxy.user=lchan or  %env{PROXY_USERNAME} or %sysprop(my.username}
sshtunnel.tunnel.hammertime.proxy.password=PW:XXXX or %env{PASSWORD} or %sysprop{my.property}
```

* _password_ : If you're using private keys, then you won't need this.
* _keepAlive.seconds_ : The default is 60 seconds.
* _proxy*_ : We include support for HTTP proxies because if you need to tunnel, then you probably need to proxy...

