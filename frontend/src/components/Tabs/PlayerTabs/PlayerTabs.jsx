import React from "react";
import { Routes, Route, NavLink, Navigate } from 'react-router-dom'
import Stats from "./Tabs/Stats/Stats";
import Matches from "../Matches/Matches";
import Rosters from "./Tabs/Rosters/Rosters";
import Events from "../Events/Events";
import Achievments from "../Achievments/Achievments";
import "./PlayerTabs.css"

function PlayerTabs(props) {
    return (
        <div>
            <ul className="playerTab">
                <li className="tab_link" key="PlayerTabs Статистика">
                    <NavLink to="stats" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Статистика
                    </NavLink>
                </li>
                <li className="tab_link" key="PlayerTabs Матчи">
                    <NavLink to="matches" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Матчи
                    </NavLink>
                </li>
                <li className="tab_link" key="PlayerTabs Турниры">
                    <NavLink to="events" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Турниры
                    </NavLink>
                </li>
                <li className="tab_link" key="PlayerTabs Достижения">
                    <NavLink to="achievements" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Достижения
                    </NavLink>
                </li>
                <li className="tab_link" key="PlayerTabs Команды">
                    <NavLink to="teams" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Команды
                    </NavLink>
                </li>
            </ul>
            <Routes>
                <Route index element={<Navigate replace to={`/player/${props.nick}/stats`} />} />
                <Route path="stats" element={<Stats stats={props.stat} />} />
                <Route path="matches" element={<Matches matches_upcoming={props.matches_upcoming} matches_ended={props.matches_ended} type="player" param={props.nick} />} />
                <Route path="events" element={<Events ongoing={props.ongoing_events} ended={props.ended_events} type="player" param={props.nick} />} />
                <Route path="achievements" element={<Achievments lan={props.lan_events} online={props.online_events} />} />
                <Route path="teams" element={<Rosters rosters={props.rosters} curTeamDays={props.curTeamDays} allTeamsDays={props.allTeamsDays} />} />
            </Routes>
        </div>
    );
}

export default PlayerTabs;