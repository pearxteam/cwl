# CWL
Custom Whitelist plugin for Sponge-based Minecraft servers.

# Custom Message
CWL allows you to change the default message for non-whitelisted players in its config file.

# Autogen
For config, see the "Config file" section.

CWL allows you to automatically generate the whitelist.json file using a remote SQL database. This will be useful if you have your own Minecraft server with a 
website with registration and whitelist requests, for example.

To generate it, execute a command "/cwl whitelist".

# Config file 
```
main {
  whitelist="Send a whitelist request firstly!"
  db_connection="jdbc:mysql://localhost/lc?user=YOUR_USERNAME&password=YOUR_PASSWORD"
  db_query="SELECT `username`, `uuid` from `players` WHERE `access` = 2;"
}
```

The "whitelist" field is a custom message that will be shown to the not whitelisted player when he joins the server. See the `Custom Message` section.

The "db_connection" field is a JDBC connection string. The format is "jdbc:CONNECTOR://HOST/TABLE?user=USERNAME&password=PASSWORD". See the `Autogen` section.

The "db_query" field is a SQL query string that will be used to get list of whitelisted users. Should return two columns: username and UUID. See the `Autogen` section.