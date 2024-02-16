import React from "react";
import { NavLink } from "react-router-dom";
import FlagName from "../../FlagName/FlagName";
import { fillSpaces, getEventDate } from "../../../Utils/Utils";
import './OngoingEventsMaker.css'

function OngoingEventsMaker(props) {
    function getName(type, country, city) {
        if (type === "Lan") {
            if (city === "") {
                return country;
            }
            return city;
        }
        return country;
    }


    function getEventName(event, type) {
        if (type === "Lan") {
            return event;
        }
        return `${event} (${type})`;
    }


    return (
        <div className="events_block">
            {props.events !== null ? props.events.map((event) =>
                <NavLink to={`/event/${fillSpaces(event.event)}`} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none" }} key={event.event}>
                    <div>
                        <div className="event_img_header">
                            <div className="crop_event"><img src={event.headerSrc} alt={event.event} /></div>
                        </div>
                        <div className="event_desc">
                            <div className="info_loc_wrapper">
                                <p>{getEventName(event.event, event.type)}</p>
                                <div className="event_sub_info">
                                    <FlagName flagPath={`../${event.flagPath}`} country={event.country} name={getName(event.type, event.country, event.city)} height="11px" />
                                    <p>|</p>
                                    <p>{getEventDate(event.date)}</p>
                                    <p>|</p>
                                    <p>{event.format}</p>
                                </div>
                            </div>
                            <div className="dev_line"></div>
                            <div className="main_event_info">
                                <div className="main_event_info_wrapper">
                                    <p>{event.registred}/{event.total}</p>
                                    <div className="main_sub_info"><p>Команд</p></div>
                                </div>
                                <div className="main_event_info_wrapper">
                                    <p>{event.prize}</p>
                                    <div className="main_sub_info"><p>Приз</p></div>
                                </div>
                                <div className="main_event_info_wrapper">
                                    <p>{event.fee}</p>
                                    <div className="main_sub_info"><p>Взнос</p></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </NavLink>
            ) : <></>}
        </div>
    );
}

export default OngoingEventsMaker;