import React from "react";
import { Link } from "react-router-dom";
import "../PlayerRow/PlayerRow.css"
import PlayerWrapper from "../../../../../components/PlayerWrapper/PlayerWrapper";

function TeamRow({ team, partType }) {
    return (
        <div className="statistic_team_row">
            {partType === "team" ?
                <Link to={`/team/${team.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                    <div className="statistic_team_row_name">
                        <img src={team.logo} style={{ width: "23px", height: "23px", marginTop: "1px" }} alt={team.name} />
                        <p>{team.name}</p>
                    </div>
                </Link>
                :
                <Link to={`/player/${team.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                    <PlayerWrapper issuer={"statistic"} src={team.logo} name={team.name} mainStyle={null} />
                </Link>
            }
            <div className="statistic_team_row_stats">
                <div className="text" style={{ width: "61px" }}>
                    <p>У-С</p>
                </div>
                <div className="text" style={{ width: "28px" }}>
                    <p>+/-</p>
                </div>
                <div className="text" style={{ width: "39px", paddingRight: "5px" }}>
                    <p>СУР</p>
                </div>
                <div className="text" style={{ width: "28px" }}>
                    <p>У/С</p>
                </div>
            </div>
        </div>
    )
}

export default TeamRow;