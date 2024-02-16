import React, { useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import TeamTabs from "../../components/Tabs/TeamTabs/TeamTabs";
import Trophies from "../../components/Trophies/Trophies";
import Player from "./Player/Player"
import "./Team.css"
import InfoContainer from "./InfoContainer/InfoContainer";
import Login from "../../components/Login/Login";
import { useState } from "react";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getImage, showNotification, getStoredPlayerNick } from "../../Utils/Utils";
import Preloader from "../../components/Preloader/Preloader";

function Team() {

  const params = useParams();
  const navigate = useNavigate();

  const [trophies, setTrophies] = useState(null);
  const [teamInfo, setTeamInfo] = useState(null);

  const [matchesUpcoming, setMatchesUpcoming] = useState(null);
  const [matchesEnded, setMatchesEnded] = useState(null);

  const [ongoingEvents, setOngoingEvents] = useState(null);
  const [endedEvents, setEndedEvents] = useState(null);

  const [lanEvents, setLanEvents] = useState(null);
  const [onlineEvents, setOnlineEvents] = useState(null);

  const [exPlayers, setExPlayers] = useState(null);


  const [isAdmin, setIsAdmin] = useState(false); // true false
  const [isParticipant, setIsParticipant] = useState(false); // true false
  const [isCaptain, setIsCaptain] = useState(false); // true false

  const [teamLogo, setTeamLogo] = useState(null);

  async function setTeam(teamInfoData) {
    setTeamLogo(await getImage(params.id));

    setTeamInfo({
      ...teamInfoData,
      players: await Promise.all(teamInfoData.players.map(async player => ({
        ...player,
        photo: await getImage(player.name)
      })))
    });

    if (teamInfoData.players.length === 0) {
      setTeamInfo(teamInfoData);
    }
  }


  async function setTeamMatches(setMatches, matchesData) {

    setMatches(await Promise.all(matchesData.map(async event => ({
      ...event,
      matches: await Promise.all(event.matches.map(async match => ({
        ...match,
        leftTeamSrc: await getImage(match.leftTeam),
        rightTeamSrc: await getImage(match.rightTeam),
      })))
    }))));
  }


  async function setTeamEvents(type, setEvents, eventData) {
    if (type !== "ended") {
      setEvents(await Promise.all(eventData.map(async event => ({
        ...event,
        logo: await getImage(event.name),
        participants: await Promise.all(event.participants.map(async part => ({
          ...part,
          src: await getImage(part.teamName)
        })))
      }))))
    } else {
      setEvents(await Promise.all(eventData.map(async event => ({
        ...event,
        logo: await getImage(event.name)
      }))))
    }
  }


  async function setTeamAchievements(setAchievements, achievementsData) {
    setAchievements(await Promise.all(achievementsData.map(async event => ({
      ...event,
      logo: await getImage(event.name)
    }))))
  }


  async function setExPlayersFunc(exPlayersData) {
    setExPlayers(await Promise.all(exPlayersData.map(async player => ({
      ...player,
      photo: await getImage(player.name)
    }))))
  }


  async function setTeamTrophies(trophies) {
    setTrophies(await Promise.all(trophies.map(async trophy => ({
      ...trophy,
      src: await getImage(trophy.name, "/trophy")
    }))));
  }


  async function getIsAdmin() {
    let resp = await request("GET", `/isAdmin/${getStoredPlayerNick()}`, {}, applHeaders);
    setIsAdmin(resp.data);
  }


  async function getFullTeam() {
    try {
      let resp = await request("GET", `/getFullTeam/${params.id}/${getStoredPlayerNick()}`, {}, applHeaders);
      setIsParticipant(resp.data.participant);

      setIsCaptain(resp.data.captain);

      setTeamMatches(setMatchesUpcoming, resp.data.upcomingMatches);
      setTeamMatches(setMatchesEnded, resp.data.endedMatches);

      setTeamEvents("upcoming", setOngoingEvents, resp.data.upcomingEvents);
      setTeamEvents("ended", setEndedEvents, resp.data.endedEvents);

      setTeamAchievements(setLanEvents, resp.data.lanAchievements);
      setTeamAchievements(setOnlineEvents, resp.data.onlineAchievements);

      setExPlayersFunc(resp.data.exPlayers);

      setTeam(resp.data.teamInfo);

      setTeamTrophies(resp.data.trophies);
    } catch (err) {
      navigate("/notfoundpage");
    }
  }


  useEffect(() => {
    getIsAdmin();
    getFullTeam();
  }, []);


  function drawPlayers() {
    let content = [];

    teamInfo.players.map((player) =>
      !isCaptain ?
        content.push(
          <Link to={`/player/${player.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" key={player.name}>
            <Player
              player={player}
              isCap={false}
              teamInfo={teamInfo}
              updateTeamInfo={setTeamInfo}
              ex_players={exPlayers}
              updateExPlayers={setExPlayers}
            />
          </Link>
        )
        :
        content.push(
          <Player
            player={player}
            isCap={true}
            teamInfo={teamInfo}
            updateTeamInfo={setTeamInfo}
            ex_players={exPlayers}
            updateExPlayers={setExPlayers}
            curPlayer={getStoredPlayerNick()}
            key={player.name}
          />
        )
    )

    if (teamInfo.players.length < 5) {

      let emptyPlayers = 5 - teamInfo.players.length;

      for (let i = 0; i < emptyPlayers; ++i) {
        content.push(
          <Player player={{ name: "?", photo: "../../img/players/NonPhoto.png" }} isCap={isCaptain} teamInfo={teamInfo} key={`emptyPlayers/${i}`} />
        )
      }
    }

    return content;
  }

  const [leaveWindowActive, setLeaveWindowActive] = useState(false);


  async function handleApprove(nick) {
    try {
      await request("POST", "/leftTeam",
        {
          nick: nick,
          team: params.id,
          isKick: false
        });
      let tempTeam = teamInfo;

      for (let i = 0; i < tempTeam.players.length; ++i) {
        if (tempTeam.players[i].name === nick) {
          setExPlayers(exPlayers === null ? [tempTeam.players[i]] : [...exPlayers, tempTeam.players[i]]);
        }
      }

      tempTeam.players = tempTeam.players.filter(player => !(player.name.includes(nick)));
      setTeamInfo(tempTeam);
      setIsParticipant(false);
      setIsCaptain(false);
      showNotification("Вы успешно покинули команду", "ok");
    } catch (err) {
      showNotification(err.response.data.message, "warn");
    }
    setLeaveWindowActive(!leaveWindowActive);
  };


  return (
    <div >
      {teamInfo !== null && trophies !== null ?
        <>
          <div>
            <div className="team_rectangle">
              {drawPlayers()}
            </div>

            <div className="devider_line"></div>

            <InfoContainer {...teamInfo} isCapAdmin={isAdmin || isCaptain} setTeamLogo={setTeamLogo} teamLogo={teamLogo} />

            <div className="devider_line"></div>

            <Trophies items={trophies} />

          </div>

          <TeamTabs
            isCapAdmin={isAdmin || isCaptain}
            description={teamInfo.description}
            players={teamInfo.players}
            matches_upcoming={matchesUpcoming}
            matches_ended={matchesEnded}
            ongoing_events={ongoingEvents}
            ended_events={endedEvents}
            lan_events={lanEvents}
            online_events={onlineEvents}
            ex_players={exPlayers}
            team={params.id}
          />
          
          {
            isParticipant &&
            <div className="leave_team" onClick={() => setLeaveWindowActive(true)}>
              <p>Покинуть команду</p>
            </div>
          }
        </>
        :
        <Preloader />
      }

      {
        isParticipant ?
          <Login active={leaveWindowActive} setActive={setLeaveWindowActive}>
            <div className="header_splash_window">
              <div className="logo_splash_window"></div>
            </div>
            <div className="info_text">
              {teamInfo !== null ? <p>Вы уверены, что хотите покинуть команду {teamInfo.name}?</p> : <></>}
            </div>
            <div className="small_buttons_wrapper">
              <div className="small_dark_button">
                <input type="submit" value="Нет" onClick={() => leaveWindowActive ? setLeaveWindowActive(!leaveWindowActive) : null} />
              </div>
              <div className="small_grey_button">
                <input type="submit" value="Да" onClick={() => handleApprove(getStoredPlayerNick())} />
              </div>
            </div>
          </Login>
          :
          <></>
      }
    </div >
  )

};

export default Team;
