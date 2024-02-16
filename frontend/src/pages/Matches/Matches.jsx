import React, { useState, useEffect } from "react";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getMatchesWithImg, getImage, getStoredPlayerNick } from "../../Utils/Utils";
import "./Matches.css";
import MatchesGenerator from "../../components/MatchHelper/MatchesGenerator";
import Preloader from "../../components/Preloader/Preloader";

function Matches() {
  const [isAdmin, setIsAdmin] = useState(false);

  const [ongoingMatches, setOngoingMatches] = useState(null);
  const [upcomingMatches, setUpcomingMatches] = useState(null);

  const [allEvents, setAllEvents] = useState(null);

  const [teams, setTeams] = useState(null);


  async function getOngoingMatches(ongoingMatches) {
    setOngoingMatches(await getMatchesWithImg(ongoingMatches));
  }


  async function getUpcomingMatches(upcomingMatches) {
    setUpcomingMatches(
      await Promise.all(upcomingMatches.map(async day => ({
        ...day,
        matches: await getMatchesWithImg(day.matches)
      })))
    );
  }


  async function getTeams(teams) {
    const data = await Promise.all(
      teams.map(async team => ({
        ...team,
        logo: await getImage(team.name)
      }))
    );

    setTeams(data);
  }


  async function getAllEvents(allEvents) {
    setAllEvents(await Promise.all(
      allEvents.map(async event => ({
        ...event,
        eventSrc: await getImage(event.name)
      }))
    ))
  }


  async function getFullMatches() {
    const matches = await request("GET", `/getFullMatches/${getStoredPlayerNick()}`, {}, applHeaders);
    getAllEvents(matches.data.allEvents);
    getTeams(matches.data.teams);

    setIsAdmin(matches.data.isAdmin);
    getOngoingMatches(matches.data.ongoingMatches);
    getUpcomingMatches(matches.data.upcomingMatches);
  }


  useEffect(() => {
    getFullMatches();
  }, []);


  return (
    <div>
      {ongoingMatches && upcomingMatches && teams && allEvents ?
        <MatchesGenerator
          ongoing_matches={ongoingMatches}
          upcoming_matches={upcomingMatches}
          isAdmin={isAdmin}
          part={teams}
          event={allEvents}
        />
        :
        <Preloader />
      }
    </div>
  );
}

export default Matches;