import React from "react";
import { Link } from "react-router-dom";
import PlayerTrophies from "./PlayerTrophies/PlayerTrophies";
import { fillSpaces } from "../../../../../Utils/Utils";
import "./Rosters.css";

function Rosters(props) {
    return (
        <div className="roster">
            <div className="teams_stats">
                <div className="stat_box">
                    <div className="stat_box_wrapper">
                        <p>{(props.rosters !== "undefined" && props.rosters !== null) ? props.rosters.length : "-"}</p>
                        <div className="stat_down"><p>Команд</p></div>
                    </div>
                </div>

                <div className="stat_box">
                    <div className="stat_box_wrapper">
                        <p>{(props.curTeamDays !== "undefined" && props.curTeamDays !== null) ? props.curTeamDays : "-"}</p>
                        <div className="stat_down"><p>Дней в текущей команде</p></div>
                    </div>
                </div>

                <div className="stat_box">
                    <div className="stat_box_wrapper">
                        <p>{(props.allTeamsDays !== "undefined" && props.allTeamsDays !== null) ? props.allTeamsDays : "-"}</p>
                        <div className="stat_down"><p>Дней в командах</p></div>
                    </div>
                </div>
            </div>
            <div className="teams_history">
                <div className="col_names">
                    {props.rosters !== null && props.rosters.length > 0 ? <p>Временной период</p> : <></>}
                    {props.rosters !== null && props.rosters.length > 0 ? <div className="col_names_team"><p>Команда</p></div> : <></>}
                    {props.rosters !== null && props.rosters.length > 0 ? <div className="col_names_trophies"><p>Трофеи</p></div> : <></>}
                </div>
                <div className="teams_wrapper">
                    {props.rosters !== null ?
                        props.rosters.map((team, i) =>
                            <div className="rect_team" key={`${team.team}/${i}`}>
                                <div className="period_wrapper"><p>{team.period}</p></div>
                                <Link to={`/team/${fillSpaces(team.team)}`} style={{ textDecoration: "none" }}>
                                    <div className="team_wrapper">
                                        <img src={team.teamLogo} alt={team.team} />
                                        <p>{team.team}</p>
                                    </div>
                                </Link>
                                <PlayerTrophies items={team} />
                            </div>
                        )
                        :
                        <></>
                    }
                </div>
            </div>
        </div>
    );
}

export default Rosters;