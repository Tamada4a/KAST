# ![KAST logo](https://github.com/Tamada4a/KAST/blob/main/frontend/public/kast.svg) KAST

## What is KAST?
KAST is a service for viewing CS2 matches, information about tournaments, players and teams. This service was developed as an alternative to HLTV for amateur tournaments.
A demo of all the main pages can be viewed in <a href="https://github.com/Tamada4a/KAST/blob/main/docs/en/PagesDemo.md ">this</a> file.

![Player Profile](https://github.com/Tamada4a/KAST/blob/main/assets/1.%20Player%20profile.png)

### Full list of features:
For all users:<ul>
<li>Viewing tournaments.</li>
<li>Watching matches.</li>
<li>View the top teams.</li>
<li>View the results of all matches.</li>
<li>View the results of all matches of a certain team.</li>
<li>View the results of all matches of a certain player.</li>
<li>Viewing the tournament page.</li>
<li>Viewing the match page.</li>
<li>View player profiles.</li>
<li>Viewing the team page.</li>
<li>If the player is a member of the team playing in the match, he sees the IP address of the server on which the match is being played.</li>
<li>Changing your date of birth.</li>
<li>Linking your social networks: VKontakte, Faceit, Discord, Steam.</li>
<li>Creating your own team.</li>
<li>If the player is the captain of the team, he can register the team for the tournament.</li>
<li>If the player is the captain of the team, he can update the team logo.</li>
<li>If a player is the captain of the team, he can exclude players from the team and invite new ones.</li>
<li>If the player is the captain of the team, he can change the description of the team.</li>
<li>Search for players, tournaments, and teams in the search.</li>
</ul>

For administrators:<ul>
<li>Create, delete, and edit tournaments.</li>
<li>Create, delete, and edit matches.</li>
<li>Editing the top teams.</li>
<li>Managing team requests: reject or accept.</li>
<li>The possibility to exclude a team from the tournament.</li>
<li>To determine the MVP of the tournament.</li>
<li>To allocate prizes at the tournament.</li>
<li>Adding the IP address of the server where the match is being played on the match page.</li>
<li>Adding streams broadcasting the match.</li>
<li>Set the peaks and bans of the match - which team banned what, which one chose, which card is the desider.</li>
<li>Change the description of any command.</li>
<li>Change the nickname of any player.</li>
<li>Change the photo of any player.</li>
<li>Change the age of any player.</li>
<li>Unlink any player's social networks.</li>
<li>Change the logo of any team.</li>
</ul>

## How to launch?
1. Follow the steps from <a href="https://github.com/Tamada4a/KAST/blob/main/docs/en/BeforeStart.md">this</a> file.
2. There are two options:
   1. Using docker from the root folder: `docker-compose up --build -d`.
   2. Everything is separate:
      1. Download and install <a href="https://www.mongodb.com/">MongoDB</a>.
      2. Download the repository.
      3. Go to the `backend` folder and launch the project in any way convenient for you.
      4. Go to the `frontend` folder, open the console in this folder and write `set HTTPS=true&npm start'.
3. Open in the browser `https://localhost:3000` or another domain where you run the server.
4. The project has been launched!

## Authors
1. <a href="https://github.com/Tamada4a">Kirill Simovin</a> - Fullstack developer, author of the idea, web designer.
2. <a href="https://github.com/ugly4">Alexander Fedyakin</a> - Frontend developer, the author of the idea.
3. Victoria Koshevets - Author of a beautiful meerkat.
4. Vadim Saveliev - Laid down the color palette.

## FAQ
<b>Q: Why KAST?</b>  
<i>A: KAST is an abbreviation of Kirill And Sanya Translation.</i><br></br>

<b>Q: What does the logo mean?</b>  
<i>A: The logo doesn't mean anything - it was generated by a neural network.</i><br></br>

<b>In: How did the site come about?</b>  
<i>A: The site appeared as a project for one of the courses at the university: Sasha offered to do something for tournaments at the computer club, and Kirill remembered that he had once planned to create his own HLTV for his tournaments - and it turned out that way.</i><br></br>

<b>Q: So you stole the site?</b>  
<i>A: We hope that this is not the case. We were really inspired by HLTV, as it is the only resource that focuses on watching matches and combines information about tournaments, players and much more. Yes, we have retained the HLTV structure, but at the same time we have brought our vision to the project - the site combines not only the HLTV features described above, but also the ability to create teams and participate in tournaments.</i><br></br>

<b>Q: What is KAST's goal?</b>  
<i>A: We want to enter the amateur tournament market and raise their level. We want amateur players to touch the level of organization of professional tournaments.</i><br></br>

<b>Q: Why are there so few commits in the repository?</b>  
<i>A: This repository is a merger of two old ones: backend and frontend, since initially each part of the service was located in its own repository.</i><br></br>

<b>Q: How do I enable English on the website?</b>  
<i>A: Currently, only Russian is available.</i>

## Something wrong...
If something went wrong, please create an Issue.
