import React from "react";
import "../../pages/Matches/Matches.css";
import "../Tabs/Match/Match.css"
import "../ResultMaker/ResultMaker.css"
import { Link } from "react-router-dom";
import { matchUrlMaker, setTier, fixTagLength } from "../../Utils/Utils";

function UpcomingMatchMaker(props) {
    return (
        <div className="col_center_gap10">
            <div className="results_date"><p>Матчи {props.date}</p></div>
            {props.matches.map((match) =>
                <Link to={matchUrlMaker(match.matchId, match.leftTeam, match.rightTeam, match.event, props.date)} style={{ textDecoration: "none" }} key={match.matchId}>
                    <div className="match_frame">
                        <div className="status_match_wrapper">
                            <div className="ongoing"><p>{match.time}</p></div>
                            <div className="matches_frame">
                                <div className="match_team">
                                    <div className="left_team_tag"><p>{fixTagLength(match.leftTag)}</p></div>
                                    <div className="match_frame_img_wrapper">
                                        <img src={match.images.leftTeamSrc} alt={match.leftTeam} key={match.leftTeam} />
                                    </div>
                                </div>
                                <div className="match_score">
                                </div>
                                <div className="match_team">
                                    <div className="match_frame_img_wrapper">
                                        <img src={match.images.rightTeamSrc} alt={match.rightTeam} key={match.rightTeam} />
                                    </div>
                                    <div className="right_team_tag"><p>{fixTagLength(match.rightTag)}</p></div>
                                </div>
                            </div>
                        </div>
                        <div className="row_center_gap3">
                            <div className="tournament_logo">
                                <img src={match.images.eventSrc} alt={match.event} key={match.event} />
                            </div>
                            <div className="event">
                                <p>{match.event}</p>
                            </div>
                        </div>
                        <div className="match_tier">
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

export default UpcomingMatchMaker;