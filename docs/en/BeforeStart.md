# What is the file about?
This file describes the steps that must be done before launching the application.

# Instructions

1. Create a keystore file: `keytool -genkeypair -alias tomcat -keyalg RSA -keysize 4096 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass <PASSWORD>`, where instead of `<PASSWORD>` you must specify the password for the vault. Move the resulting file to `/backend/src/main/resources/keystore`. In `application.properties`, in the `server.ssl.key-store-password` field, specify the `<PASSWORD>` you entered.
2. When running on `localhost`, to avoid the error `NET::ERR_CERT_AUTHORITY_INVALID`, you must enable these settings in the browser: if the browser is an older version - <a href="https://stackoverflow.com/a/60368471/14478725">this</a>, otherwise <a href="https://stackoverflow.com/a/77443547/14478725">this</a>.
3. Use the path `/backend/src/main/resources` to edit the file `config.properties`. In this <a href="https://github.com/Tamada4a/KAST/blob/main/docs/en/Auth.md ">file</a> describes which keys need to be added and how to get the corresponding values.
4. Create and configure a CS2 server - <a href="https://github.com/Tamada4a/KAST/blob/main/docs/en/CS2ServerCreate.md">instructions</a>.
5. Check if your addresses to the client and server match those specified in the following files:
   1. The value of the <b>client_url</b> key in `/backend/src/main/resources/config.properties`.
   2. The return values in the `getServerUrl()` and `getClientUrl()` methods in `/frontend/src/Utils/HostData.jsx`.
