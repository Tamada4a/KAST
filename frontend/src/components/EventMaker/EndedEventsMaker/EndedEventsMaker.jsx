import React from "react";
import { NavLink } from "react-router-dom";
import FlagName from "../../FlagName/FlagName";
import { fillSpaces, getEventDate } from "../../../Utils/Utils";
import './EndedEventsMaker.css';
import '../../ResultMaker/ResultMaker.css';

function EndedEventsMaker(props) {
    const getName = (country, city, type) => {
        if (type === "Lan") {
            if (city === "") {
                return country;
            }
            return `${country}, ${city}`;
        }
        return country;
    }


    return (
        <div className="events_date_wrapper">
            {props.events.map((ev) =>
                <NavLink to={`/event/${fillSpaces(ev.event)}`} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none" }} key={ev.event}>
                    <div className="event_rect_match">
                        <div className="past_event_info">
                            <div className="tournament_logo"><img src={ev.src} alt={ev.event} /></div>
                            <div className="sub_up_info_wrapper">
                                <div className="event_name"><p>{ev.event}</p></div>
                                <div className="sub_past_info">
                                    <FlagName flagPath={`../${ev.flagPath}`} country={ev.country} name={getName(ev.country, ev.city, ev.type)} height="10px" />
                                    <p>|</p>
                                    <p>{getEventDate(ev.date)}</p>
                                </div>
                            </div>
                        </div>
                        <div className="event_spec">
                            <div className="main_info_wrapper">
                                <p>{ev.registred}/{ev.total}</p>
                                <div className="main_sub_info_wrapper"><p>Команд</p></div>
                            </div>
                            <div className="main_info_wrapper">
                                <p>{ev.prize}</p>
                                <div className="main_sub_info_wrapper"><p>Приз</p></div>
                            </div>
                            <div className="main_info_wrapper">
                                <p>{ev.fee}</p>
                                <div className="main_sub_info_wrapper"><p>Взнос</p></div>
                            </div>
                            <div className="main_info_wrapper">
                                <p>{ev.type}</p>
                                <div className="main_sub_info_wrapper"><p>Тип</p></div>
                            </div>
                            <div className="main_info_wrapper">
                                <p>{ev.format}</p>
                                <div className="main_sub_info_wrapper"><p>Формат</p></div>
                            </div>
                        </div>
                    </div>
                </NavLink>
            )}
        </div>
    );
}

export default EndedEventsMaker;