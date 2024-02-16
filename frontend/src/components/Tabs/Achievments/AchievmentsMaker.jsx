import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces, placeType } from "../../../Utils/Utils";
import "./Achievments.css";
import "../Events/Events.css";

function AchievmentsMaker(props) {
    return (
        <Link to={`/event/${fillSpaces(props.event.name)}`} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none" }}>
            <div className="tournament_rect">
                <div className="place_event_wrapper">
                    {placeType(props.event.place)}
                    <div className="ev_info">
                        <div className="event_logo"><img src={props.event.logo} /></div>
                        <div className="tournament_info">
                            <p>{props.event.name}</p>
                            <div className="tournament_sub_info">
                                <p>{props.event.date}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Link>
    );
}

export default AchievmentsMaker;