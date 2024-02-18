# О чем файл?
В данном файле описаны пункты, которые необходимо сделать перед запуском приложения.

# Инструкция

1. Создать файл хранилища ключей: `keytool -genkeypair -alias tomcat -keyalg RSA -keysize 4096 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass <PASSWORD>`, где вместо `<PASSWORD>` необходимо указать пароль для хранилища. Полученный файл переместить в `/backend/src/main/resources/keystore`. В `application.properties` в поле `server.ssl.key-store-password` указать введенный вами `<PASSWORD>`.
2. При запуске на `localhost` для избежания ошибки `NET::ERR_CERT_AUTHORITY_INVALID` необходимо включить в браузере данные настройки: если браузер старой версии - <a href="https://stackoverflow.com/a/60368471/14478725">эту</a>, иначе <a href="https://stackoverflow.com/a/77443547/14478725">эту</a>.
3. По пути `/backend/src/main/resources` создайте файл `config.properties`. В данном <a href="https://github.com/Tamada4a/KAST/blob/main/docs/ru/Auth.md">файле</a> описано какие ключи необходимо добавить и как получить соответствующие значения.
4. Создать и настроить сервер CS2 - <a href="https://github.com/Tamada4a/KAST/blob/main/docs/ru/CS2ServerCreate.md">инструкция</a>.
5. Проверьте, совпадают ли ваши адреса до клиента и сервера с указанными в следующих файлах:
   1. Значение по ключу <b>client_url</b> в `/backend/src/main/resources/config.properties`.
   2. Возвращаемые значения в методах `getServerUrl()` и `getClientUrl()` в `/frontend/src/Utils/HostData.jsx`.
