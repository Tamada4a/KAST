import React from "react";
import EndedEventsMaker from "../../../components/EventMaker/EndedEventsMaker/EndedEventsMaker";
import './EndedEvents.css';
import "../OngoingEvents/OngoingEvents.css";
import Preloader from "../../../components/Preloader/Preloader";

function EndedEvents(props) {
    return (
        <>
            {
                props.ended !== null ?
                    <div className="events_past">
                        {props.ended.map((month) =>
                            <div className="events_date_wrapper" key={month.date}>
                                <div className="events_date"><p>{month.date}</p></div>
                                <EndedEventsMaker events={month.events} />
                            </div>)
                        }
                    </div>
                    :
                    <Preloader />
            }
        </>
    );
}

export default EndedEvents;