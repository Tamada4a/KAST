import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces } from "../../Utils/Utils";
import "./AttendedEvents.css";
import "../Tabs/Events/Events.css";
import "../../pages/Results/Results.css";


function AttendedEvents(props) {
    return (
        <Link to={`/event/${fillSpaces(props.event.name)}`} style={{ textDecoration: "none" }}>
            <div className="event_rect">
                <div className="event_wrapper">
                    <div className="event_info_wrapper">
                        <div className="event_place"><p>{props.event.place}</p></div>
                        <div className="event_information">
                            <div className="event_logo"><img src={props.event.logo} alt={props.event.name} /></div>
                            <div className="tournament_info">
                                <p>{props.event.name}</p>
                                <div className="tournament_sub_info">
                                    <p>{props.event.date}</p>
                                </div>
                            </div>
                        </div>
                        <div className="event_team_space_border">
                            <div className="event_team">
                                <img src={props.event.teamSrc} alt={props.event.team} />
                                <p>{props.event.team}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Link>
    );
}

export default AttendedEvents;