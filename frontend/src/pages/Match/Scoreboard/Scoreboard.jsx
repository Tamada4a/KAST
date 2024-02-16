import React, { useState, useEffect } from "react";
import TeamBlock from "./TeamBlock/TeamBlock";
import HalfMatchSideCT from "./HalfMatchSideCT/HalfMatchSideCT";
import HalfMatchSideT from "./HalfMatchSideT/HalfMatchSideT";
import RoundIcon from "./RoundIcon/RoundIcon";
import "./Scoreboard.css"
import { applHeaders, request } from "../../../Utils/MyAxios";
import { getImage } from "../../../Utils/Utils";
import { getServerUrl } from "../../../Utils/HostData";
import Stomp from "stompjs";
import SockJS from "sockjs-client";


function Scoreboard({ partType, matchId, event, nameFirst }) {
    const [firstTeam, setFirstTeam] = useState(null);

    const [secondTeam, setSecondTeam] = useState(null);

    const [roundsHistory, setRoundsHistory] = useState(null);

    const [round, setRound] = useState(0);

    const [mapName, setMapName] = useState("TBA");

    const [status, setStatus] = useState("playing");


    useEffect(() => {
        const socket = new SockJS(`${getServerUrl()}/ws`);
        const client = Stomp.over(socket);

        client.debug = null;

        client.connect({}, () => {
            client.subscribe(`/match/scoreBoard/${matchId}`, (message) => {
                setScoreBoard(JSON.parse(message.body));
            });
        });

        return () => {
            client.disconnect();
        }
    }, []);


    useEffect(() => {
        getScoreBoard();
    }, []);


    async function getScoreBoard() {
        let stats = await request("GET", `getScoreboard/${event}/${matchId}`, {}, applHeaders);

        setScoreBoard(stats.data);
    }


    async function setScoreBoard(data) {
        let currentRound = data.currentRound;

        setFirstTeam({
            ...data.firstTeam,
            logo: await getImage(data.firstTeam.name),
            players: data.firstTeam.players.map(player => (
                {
                    ...player,
                    avg: currentRound !== 0 ? (player.fullDamage / currentRound).toFixed(1) : player.fullDamage
                }))
        });
        setSecondTeam({
            ...data.secondTeam,
            logo: await getImage(data.secondTeam.name),
            players: data.secondTeam.players.map(player => (
                {
                    ...player, avg: currentRound !== 0 ? (player.fullDamage / currentRound).toFixed(1) : player.fullDamage
                }))
        });

        setRound(currentRound);

        setRoundsHistory(data.roundHistory);

        setStatus(data.status);

        setMapName(data.mapName);
    }


    function getRoundsIcons(half, side) {
        let maxRounds = 24;

        let rounds = [];
        let halfMaxRounds = maxRounds / 2;

        for (let i = 0; i < halfMaxRounds; ++i) {
            rounds[i] = <RoundIcon key={`${side}_${half}/${i}`} />
        }

        let start = half === "first" ? 0 : halfMaxRounds;

        if (start > roundsHistory.length) {
            return rounds;
        }

        let end = half === "first" ? halfMaxRounds : maxRounds;
        end = end > roundsHistory.length ? roundsHistory.length : end;

        for (let i = start; i < end; ++i) {
            if (roundsHistory[i].winner === side) {
                if (i >= halfMaxRounds) {
                    rounds[i - halfMaxRounds] = <RoundIcon props={roundsHistory[i]} key={`${side}_${start}:${end}/${i}`} />
                } else {
                    rounds[i] = <RoundIcon props={roundsHistory[i]} key={`${side}_${start}:${end}/${i}`} />
                }
            }
        }

        return rounds;
    }


    return (
        <div className="scoreboard_back" style={{ backgroundImage: `url(../../img/maps/scoreboard/${mapName}.png)` }}>
            <div className="scoreboard">
                <div style={{
                    display: "inline-flex",
                    alignItems: "flex-start"
                }}>

                    <div className="round">
                        Раунд : {round} - {mapName}
                    </div>


                    {firstTeam !== null && secondTeam !== null ?
                        <div className="scoreboard_score_block">
                            <div className="scoreboard_score">
                                <div className={firstTeam.side === "T" ? "score_wrapper t_score" : "score_wrapper ct_score"} style={{ justifyContent: "flex-end" }}>{firstTeam.score}</div>
                                <p>:</p>
                                <div className={secondTeam.side === "T" ? "score_wrapper t_score" : "score_wrapper ct_score"} style={{ justifyContent: "flex-start" }}>{secondTeam.score}</div>
                            </div>
                        </div>
                        :
                        <></>
                    }
                </div>
                <div style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    padding: "0px",
                    gap: "10px",
                    width: "630px",
                    maxHeight: "546px",
                    height: "fit-content"
                }}>
                    {firstTeam !== null ?
                        <TeamBlock team={firstTeam} partType={partType} status={status} />
                        :
                        <></>
                    }

                    {firstTeam !== null && secondTeam !== null && roundsHistory !== null ?
                        <div className="rounds_history">
                            {firstTeam.name === nameFirst ?
                                <div className="rounds_first_half">
                                    <HalfMatchSideCT rounds={getRoundsIcons("first", firstTeam.side)} />
                                    <HalfMatchSideT rounds={getRoundsIcons("first", secondTeam.side)} />
                                </div>
                                :
                                <div className="rounds_first_half">
                                    <HalfMatchSideCT rounds={getRoundsIcons("first", secondTeam.side)} />
                                    <HalfMatchSideT rounds={getRoundsIcons("first", firstTeam.side)} />
                                </div>
                            }
                            {firstTeam.name !== nameFirst ?
                                <div className="rounds_second_half">
                                    <HalfMatchSideCT rounds={getRoundsIcons("second", firstTeam.side)} />
                                    <HalfMatchSideT rounds={getRoundsIcons("second", secondTeam.side)} />
                                </div>
                                :
                                <div className="rounds_second_half">
                                    <HalfMatchSideCT rounds={getRoundsIcons("second", secondTeam.side)} />
                                    <HalfMatchSideT rounds={getRoundsIcons("second", firstTeam.side)} />
                                </div>
                            }
                        </div>
                        :
                        <></>
                    }

                    {secondTeam !== null ?
                        <TeamBlock team={secondTeam} partType={partType} status={status} />
                        :
                        <></>
                    }
                </div>
            </div>
        </div >
    )
}

export default Scoreboard;