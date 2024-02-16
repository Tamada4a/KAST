import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces, matchUrlMaker } from "../../../Utils/Utils";
import "./Match.css"

function Match(props) {
    return (
        <div className="matches_space">
            <div className="match_header">
                <p>{props.event.event + (props.event.type === "upcoming" ? "" : " - ") + props.event.place}</p>
            </div>
            {props.event.matches.map((match) =>
                <div className="match_rect" key={matchUrlMaker(match.matchId, match.leftTeam, match.rightTeam, props.event.event, match.date)}>
                    <div className="match_date_wrapper"><p>{match.date}</p></div>
                    <div className="match">

                        <Link to={`/team/${fillSpaces(match.leftTeam)}`} style={{ textDecoration: "none" }}>
                            <div className="match_team" style={{ opacity: parseInt(match.leftScore) < parseInt(match.rightScore) ? 0.3 : 1 }}>
                                <div className="left_team_tag"><p>{match.leftTag}</p></div>
                                <img src={match.leftTeamSrc} alt={match.leftTeam} />
                            </div>
                        </Link>

                        <div className="row_center_gap3">
                            <div className="left_team_score"><p style={{ opacity: parseInt(match.leftScore) < parseInt(match.rightScore) ? 0.3 : 1 }}>{match.leftScore}</p></div>
                            <p>:</p>
                            <div className="right_team_score"><p style={{ opacity: parseInt(match.rightScore) < parseInt(match.leftScore) ? 0.3 : 1 }}>{match.rightScore}</p></div>
                        </div>

                        <Link to={`/team/${fillSpaces(match.rightTeam)}`} style={{ textDecoration: "none" }}>
                            <div className="match_team" style={{ opacity: parseInt(match.rightScore) < parseInt(match.leftScore) ? 0.3 : 1 }}>
                                <img src={match.rightTeamSrc} alt={match.rightTeam} />
                                <div className="right_team_tag"><p>{match.rightTag}</p></div>
                            </div>
                        </Link>
                    </div>
                    <Link to={matchUrlMaker(match.matchId, match.leftTeam, match.rightTeam, props.event.event, match.date)} style={{ textDecoration: "none" }}>
                        <div className="match_button_wrapper"><div className="button_match display-row-center"><p>Матч</p></div></div>
                    </Link>
                </div>
            )}
        </div>
    );
}

export default Match;