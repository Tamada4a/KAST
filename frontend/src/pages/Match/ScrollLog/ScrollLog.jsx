import React, { useState, useEffect } from "react";
import "./ScrollLog.css"
import Log from "./Log/Log";
import { applHeaders, request } from "../../../Utils/MyAxios";
import { getServerUrl } from "../../../Utils/HostData";
import Stomp from "stompjs";
import SockJS from "sockjs-client";


function ScrollLog({ matchId, event }) {
    const [logs, setLogs] = useState(null);

    const [mapName, setMapName] = useState("TBA");


    useEffect(() => {
        getLogs();
        getCurrentMap();
    }, []);


    useEffect(() => {
        const socket1 = new SockJS(`${getServerUrl()}/ws`);
        const client1 = Stomp.over(socket1);

        client1.debug = null;

        client1.connect({}, () => {
            client1.subscribe(`/match/logs/${matchId}`, (message) => {
                setLogs(JSON.parse(message.body));
            });
        });

        return () => {
            client1.disconnect();
        }
    }, []);


    useEffect(() => {
        const socket2 = new SockJS(`${getServerUrl()}/ws`);
        const client2 = Stomp.over(socket2);

        client2.debug = null;

        client2.connect({}, () => {
            client2.subscribe(`/match/logsMapName/${matchId}`, (message) => {
                setMapName(message.body);
            });
        });

        return () => {
            client2.disconnect();
        }
    }, []);


    async function getLogs() {
        let logs_ = await request("GET", `getLogs/${event}/${matchId}`, {}, applHeaders);
        setLogs(logs_.data);
    }

    async function getCurrentMap(){
        let currentMap = await request("GET", `getCurrentMap/${event}/${matchId}`, {}, applHeaders);
        setMapName(currentMap.data);
    }


    function colorNick(nick) {
        if (nick === "CT")
            return "var(--ct-color)";
        else
            return "var(--t-color)";
    }


    function weaponImg(weapon) {
        return `/img/scrollLog/weapons/${weapon}.svg`;
    }


    function addInfoKill(info) {
        return `/img/scrollLog/howKilled/${info}.svg`;
    }


    function thingsImg(thing) {
        return `/img/scrollLog/accessories/${thing}.svg`;
    }


    return (
        <div className="scroll_logs" style={{ backgroundImage: `url(../../img/maps/scoreboard/${mapName}.png)` }}>
            <div className="logs_container">
                {logs !== null ?
                    logs.map((log, i) => {
                        switch (log.type) {
                            case "login":
                                return (
                                    <Log type={"login"} key={`${log.type}/${i}`}>
                                        <div className="event_text" style={{ color: "white" }}>{log.nick}</div><p>зашёл на сервер</p>
                                    </Log>
                                );
                            case "logout":
                                return (
                                    <Log type={"logout"} key={`${log.type}/${i}`}>
                                        <div className="event_text" style={{ color: colorNick(log.side) }}>{log.nick}</div><p>вышел с сервера</p>
                                    </Log>
                                );
                            case "roundStarted":
                                return (
                                    <Log type={"roundBegin"} key={`${log.type}/${i}`}>
                                        <p>Раунд начался</p>
                                    </Log>
                                );
                            case "suicide":
                                return (
                                    <Log type={log.side === "TERRORIST" ? "t_suicide" : "ct_suicide"} key={`${log.type}/${i}`}>
                                        <div className="event_text" style={{ color: colorNick(log.side) }}>{log.nick}</div>
                                        <p>совершил суицид</p>
                                    </Log>
                                );
                            case "bombDeath":
                                return (
                                    <Log type={"bombDeath"} key={`${log.type}/${i}`}>
                                        <img src={"/img/scrollLog/howKilled/bombDeath.svg"} alt="bombDeath" />
                                        <div className="event_text" style={{ color: colorNick(log.side) }}>{log.nick}</div>
                                    </Log>
                                );
                            case "roundEnd":
                                return (
                                    <Log type={log.winner === "T" ? "t_win" : "ct_win"} key={`${log.type}/${i}`}>
                                        <p>Раунд завершен - Победитель:</p>
                                        <div className="event_text" style={{ color: colorNick(log.winner) }}>{log.winner}</div>
                                        <div className="display-row-center">
                                            <p>(</p>
                                            <div className="event_text" style={{ color: "var(--t-color)" }}>{log.scoreT}</div>
                                            <p>&nbsp;-&nbsp;</p>
                                            <div className="event_text" style={{ color: "var(--ct-color)" }}>{log.scoreCT}</div>
                                            <p>)&nbsp;-&nbsp;</p>
                                            <div className="event_text" style={{ color: colorNick(log.winner) }}>{log.how}</div>
                                        </div>
                                    </Log>
                                );
                            case "bombDefused":
                                return (
                                    <Log type={"defuse"} key={`${log.type}/${i}`}>
                                        <div className="event_text" style={{ color: "var(--ct-color)" }}>{log.nick}</div>
                                        <img src="/img/scoreboard/BombDefused.svg" alt="DefuseKit" />
                                        <p>разминировал бомбу</p>
                                    </Log>
                                );
                            case "bombPlanted":
                                return (
                                    <Log type={"bomb_planted"} key={`${log.type}/${i}`}>
                                        <div className="event_text" style={{ color: "var(--t-color)" }}>{log.nick}</div>
                                        <img src={thingsImg("Bomb")} alt="Bomb" />
                                        <p>поставил бомбу на {log.plant}</p>
                                        <div className="display-row-center">
                                            <p>(</p>
                                            <div className="event_text" style={{ color: "var(--t-color)" }}>{log.talive}</div>
                                            <p>&nbsp;в&nbsp;</p>
                                            <div className="event_text" style={{ color: "var(--ct-color)" }}>{log.ctAlive}</div>
                                            <p> )</p>
                                        </div>
                                    </Log>
                                );
                            case "kill":
                                return (
                                    <Log type={"kill"} key={`${log.type}/${i}`}>
                                        {log.attackerblind && <img src={addInfoKill("attackerblind")} alt="attackerblind" />}
                                        <div className="event_text" style={{ color: colorNick(log.side) }}>{log.nick}</div>
                                        {log.assisted !== "" && <p>+</p>}
                                        {log.assisted !== "" && <div className="event_text" style={{ color: colorNick(log.assisterSide) }}>{log.assisted}</div>}
                                        {log.flashAssisted !== "" && <p>+</p>}
                                        {log.flashAssisted !== "" && <img src={addInfoKill("flashassist")} alt="flashassist" />}
                                        {log.flashAssisted !== "" && <div className="event_text" style={{ color: colorNick(log.flashAssistedSide) }}>{log.flashAssisted}</div>}
                                        <img src={weaponImg(log.gun)} alt={log.gun} style={{ height: "14px" }} />
                                        {log.noscope && <img src={addInfoKill("noscope")} alt="noscope" />}
                                        {log.penetrated && <img src={addInfoKill("penetrated")} alt="penetrated" />}
                                        {log.throughsmoke && <img src={addInfoKill("throughsmoke")} alt="throughsmoke" />}
                                        {log.headshot && <img src={addInfoKill("headshot")} alt="headshot" />}
                                        <div className="event_text" style={{ color: colorNick(log.victimSide) }}>{log.victim}</div>
                                    </Log>
                                );
                        }
                    })
                    :
                    <></>
                }
                {logs !== null ? <div className="log" style={{ padding: "4px", width: "100%", background: "none" }}></div> : <></>}
            </div>
        </div>
    )
}

export default ScrollLog;