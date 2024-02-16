import React, { useState, useEffect } from "react";
import "../../pages/Matches/Matches.css";
import "../Tabs/Match/Match.css"
import "../ResultMaker/ResultMaker.css"
import { Link } from "react-router-dom";
import { matchUrlMaker, setTier, fixTagLength, isInt } from "../../Utils/Utils";
import { getServerUrl } from "../../Utils/HostData";
import Stomp from "stompjs";
import SockJS from "sockjs-client";

function OngoingMatchMaker(props) {
    const [leftUpperColor, setLeftUpperColor] = useState("white");
    const [rightUpperColor, setRightUpperColor] = useState("white");
    const [leftSubColor, setLeftSubColor] = useState("var(--white40)");
    const [rightSubColor, setRightSubColor] = useState("var(--white40)");

    const [leftSubOpacity, setLeftSubOpacity] = useState(1);
    const [rightSubOpacity, setRightSubOpacity] = useState(1);

    const [leftUpperScore, setLeftUpperScore] = useState(0);
    const [rightUpperScore, setRightUpperScore] = useState(0);
    const [leftSubScore, setLeftSubScore] = useState(0);
    const [rightSubScore, setRightSubScore] = useState(0);


    useEffect(() => {
        setMatchScore(isInt(props.leftScore) ? parseInt(props.leftScore) : props.leftScore, isInt(props.rightScore) ? parseInt(props.rightScore) : props.rightScore, parseInt(props.leftMapScore), parseInt(props.rightMapScore));
    }, []);


    useEffect(() => {
        const socket = new SockJS(`${getServerUrl()}/ws`);
        const client = Stomp.over(socket);

        client.debug = null;

        client.connect({}, () => {
            client.subscribe(`/match/score/${props.matchId}`, (message) => {
                let jsonScore = JSON.parse(message.body);
                setMatchScore(jsonScore.leftScore, jsonScore.rightScore, jsonScore.leftMapScore, jsonScore.rightMapScore);
            });
        });

        return () => {
            client.disconnect();
        }
    }, []);


    function setMatchScore(leftScore, rightScore, leftMapScore, rightMapScore) {
        setLeftUpperColor(getColor(leftScore, rightScore));
        setRightUpperColor(getColor(rightScore, leftScore));

        let subColorLeft = getColor(leftMapScore, rightMapScore, "var(--white40)");
        let subColorRight = getColor(rightMapScore, leftMapScore, "var(--white40)");

        setLeftSubColor(subColorLeft);
        setRightSubColor(subColorRight);

        setLeftSubOpacity(subColorLeft === "var(--white40)" ? 1 : 0.9);
        setRightSubOpacity(subColorRight === "var(--white40)" ? 1 : 0.9);

        setLeftUpperScore(leftScore);
        setRightUpperScore(rightScore);
        setLeftSubScore(leftMapScore);
        setRightSubScore(rightMapScore);
    }


    function getColor(score1, score2, defColor = "white") {
        if (score1 === score2) {
            return defColor;
        }

        if (score1 > score2) {
            return "var(--base-11)";
        }

        return "red";
    }


    return (
        <Link to={matchUrlMaker(props.matchId, props.leftTeam, props.rightTeam, props.event, props.date)} style={{ textDecoration: "none" }}>
            <div className="match_frame">
                <div className="status_match_wrapper">
                    <div className="live"><p>LIVE</p></div>
                    <div className="matches_frame">
                        <div className="row_center_gap3">
                            <div className="left_team_tag"><p>{fixTagLength(props.leftTag)}</p></div>
                            <div className="match_frame_img_wrapper">
                                <img src={props.images.leftTeamSrc} alt={props.leftTeam} key={props.leftTeam} />
                            </div>
                        </div>
                        <div className="match_score" style={{ flexDirection: props.maps.length > 1 ? "column" : "row" }}>
                            <div className="upper_score">
                                <div className="left_upper_score"><p style={{ color: leftUpperColor }}>{leftUpperScore}</p></div>
                                <p>:</p>
                                <div className="right_upper_score"><p style={{ color: rightUpperColor }}>{rightUpperScore}</p></div>
                            </div>
                            {props.maps.length > 1 ?
                                <div className="sub_score">
                                    <div className="left_sub_score"><p>(<span style={{ color: leftSubColor, opacity: leftSubOpacity }}>{leftSubScore}</span>)</p></div>
                                    <div className="right_sub_score"><p>(<span style={{ color: rightSubColor, opacity: rightSubOpacity }}>{rightSubScore}</span>)</p></div>
                                </div>
                                :
                                <></>
                            }
                        </div>
                        <div className="row_center_gap3">
                            <div className="match_frame_img_wrapper">
                                <img src={props.images.rightTeamSrc} alt={props.rightTeam} key={props.rightTeam} />
                            </div>
                            <div className="right_team_tag"><p>{fixTagLength(props.rightTag)}</p></div>
                        </div>
                    </div>
                </div>
                <div className="row_center_gap3">
                    <div className="tournament_logo">
                        <img src={props.images.eventSrc} alt={props.event} key={props.event} />
                    </div>
                    <div className="event">
                        <p>{props.event}</p>
                    </div>
                </div>
                <div className="match_tier">
                    <div className="row_center_gap3">
                        {setTier(props.tier, "../../")}
                    </div>
                    {props.maps.length === 1 ? <p>{props.maps[0].mapName}</p> : <p>{`bo${props.maps.length}`}</p>}
                </div>
            </div>
        </Link>
    );
}

export default OngoingMatchMaker;