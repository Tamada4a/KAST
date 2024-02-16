import React from "react";
import { Link } from "react-router-dom";
import './PlayerRow.css'

function PlayerRow({ props }) {
    return (
        <div className="statistic_player_row">
            <Link to={`/player/${props.nick}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                <div className="statistic_player_row_nick">
                    <img className="player_row_flag" src={props.flagPath} alt={props.country} />
                    <p>{props.firstName}</p>
                    <p style={{ fontFamily: "var(--text-medium-lcg)" }}>"{props.nick}"</p>
                    <p>{props.lastName}</p>
                </div>
            </Link>
            <div className="statistic_team_row_stats" style={{ paddingRight: "10px" }}>
                <div className="text" style={{ width: "61px" }}>
                    <p>{props.kills}-{props.deaths}</p>
                </div>
                <div className="text" style={{ width: "28px" }}>
                    <p style={{ color: !(props.kills === props.deaths) ? (props.kills > props.deaths) ? "green" : "red" : "white" }}>
                        {(props.kills > props.deaths) ? "+" : null}{props.kills - props.deaths}
                    </p>
                </div>
                <div className="text" style={{ width: "39px", paddingRight: "5px" }}>
                    <p>{props.avg}</p>
                </div>
                <div className="text" style={{ width: "28px" }}>
                    <p>{props.deaths === 0 ? props.kills : (props.kills / props.deaths).toFixed(2)}</p>
                </div>
            </div>
        </div>
    )
}

export default PlayerRow;