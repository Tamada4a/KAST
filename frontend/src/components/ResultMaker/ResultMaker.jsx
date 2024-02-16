import React from "react";
import { Link } from "react-router-dom";
import { matchUrlMaker, setTier } from "../../Utils/Utils";
import "../Tabs/Match/Match.css";
import "./ResultMaker.css";

function ResultMaker(props) {
    return (
        <div className="col_center_gap10">
            <div className="results_date"><p>{`Результаты за ${props.day.date}`}</p></div>
            {props.day.matches.map((match) =>
                <Link to={matchUrlMaker(match.matchId, match.leftTeam, match.rightTeam, match.event, props.day.date)} style={{ textDecoration: "none" }} key={match.matchId}>
                    <div className="match_rect_full">
                        <div className="match">

                            <div className="match_team" style={{ opacity: parseInt(match.leftScore) < parseInt(match.rightScore) ? 0.3 : 1 }}>
                                <div className="left_team_tag"><p>{match.leftTag}</p></div>
                                <img src={match.images.leftTeamSrc} alt={match.leftTeam} />
                            </div>

                            <div className="row_center_gap3">
                                <div className="left_team_score"><p style={{ opacity: parseInt(match.leftScore) < parseInt(match.rightScore) ? 0.3 : 1, color: parseInt(match.rightScore) < parseInt(match.leftScore) ? "green" : "white" }}>{match.leftScore}</p></div>
                                <p>:</p>
                                <div className="right_team_score"><p style={{ opacity: parseInt(match.rightScore) < parseInt(match.leftScore) ? 0.3 : 1, color: parseInt(match.leftScore) < parseInt(match.rightScore) ? "green" : "white" }}>{match.rightScore}</p></div>
                            </div>

                            <div className="match_team" style={{ opacity: parseInt(match.rightScore) < parseInt(match.leftScore) ? 0.3 : 1 }}>
                                <img src={match.images.rightTeamSrc} alt={match.rightTeam} />
                                <div className="right_team_tag"><p id="right_match_team">{match.rightTag}</p></div>
                            </div>
                        </div>
                        <div className="row_center_gap3">
                            <div className="tournament_logo"><img src={match.images.eventSrc} alt={match.event} /></div>
                            <div className="tournament_name"><p>{match.event}</p></div>
                        </div>
                        <div className="top_tier">
                            <div className="row_center_gap3">
                                {setTier(match.tier, "../../")}
                            </div>
                            {match.maps.length === 1 ? <p>{match.maps[0].mapName}</p> : <p>{`bo${match.maps.length}`}</p>}
                        </div>
                    </div>
                </Link>
            )}
        </div>
    );
}

export default ResultMaker;
