# What is this file about?
This file contains information for obtaining the keys necessary to authorize the user's social networks.

# Instructions
## Discord
1. Go to <a href="https://discord.com/developers/applications">link</a> and create a new application ("New Application" button).
2. In the "OAuth2/General" tab, add a redirect link - click on the "Add Redirect" button and enter `https://localhost:3000/social-auth/discord/`.
3. In the "General Information" tab, copy the "application id" and add it as the key value <b>client_id_discord</b> in the file `config.properties`, that is: <i>client_id_discord="application id"</i>.

## VK
1. Go to <a href="https://dev.vk.com/en">link</a> and create a new application ("Create App" button).
2. In the "Platform" field, select "Website" and follow the suggested link to the service. Click the "Add Application" button.
3. Enter the name and select "Web" as the platform. Click "Next".
4. In the "Base domain" field, enter `localhost`.
5. In the "Trusted Redirect URL" field, enter `https://localhost:3000/social-auth/vk/`. Click "Next".
6. Copy the value from the "Application ID" field to the file `config.properties` with the key <b>client_id_vk</b>, that is: <i>client_id_vk="Application ID"</i>.
7. Copy the value from the "Protected key" field to the file `config.properties` with the key <b>client_secret_vk</b>, that is: <i>client_secret_vk="Protected key"</i>.

## Faceit
1. You need to enable 2FA (two-factor authentication).
2. Go to <a href="https://developers .face it.com/">the link</a>.
3. Go to the "App Studio" tab and create a new application (the "New" button) by filling in the required fields.
4. On the application page, go to the "OAuth2 Clients" tab and create a new "Client id" in the "Create FACEIT OAuth2 Client Ids" field.
5. In the "Redirect URI" field, insert `https://localhost:3000/social-auth/faceit/` and click "Create".
6. Then click on the pencil button next to the created "Client id" and add the value of the "Client ID" field to the file `config.properties` with the key <b>client_id_faceit</b>, that is: <i>client_id_faceit="Client ID"</i>. Add the value of the "Client secret" field with the key <b>client_secret_faceit</b>.

## Twitch
1. Go to <a href="https://dev.twitch.tv/console">link</a> and create a new application (the "Submit application" button).
2. Enter the name, as a redirect link, enter `https://localhost:3000/`.
3. Select "Web Integration" as the category.
4. Click the "Create" button. If, when you click on the button, you remain on the form filling page, you have entered an already existing application name.
5. After the successful creation of the application, go to the application management by clicking the appropriate button - "Manage".
6. Copy the value from the "Client ID" field to the file `config.properties` with the key <b>client_id_twitch</b>, that is: <i>client_id_twitch="Client ID"</i>.
7. Copy the value from the "Client Secret code" field to the file `config.properties` with the key <b>client_secret_twitch</b>, that is: <i>client_secret_twitch="Client secret code"</i>.

## YouTube
1. Go to <a href="https://console.cloud .google.com/apis">link</a> and create a new project.
2. Go to the "Credentials" tab -> click "Create Credentials" -> select "API key".
3. After creating the key, click on the colon next to it and select "Edit API key".
4. In the "API restrictions" tab, select the "Restrict key" item and select "YouTube Data API v3" in the drop-down list.
5. Save the changes.
6. Click on "Show key" next to the key and copy to the file `config.properties` with the key <b>youtube_api_key</b>, that is: <i>youtube_api_key="Your API key"</i>.

## Client address
1. In the `config.properties` file, you need to add another value - the client's address, that is: <i>client_url="address/client"</i>. In this case, the "address/client" is `https://localhost:3000`.
2. If your domain differs from the one described above, replace `localhost` with the appropriate one everywhere.
