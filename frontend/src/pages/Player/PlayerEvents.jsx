import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { getImage } from "../../Utils/Utils";
import { applHeaders, request } from "../../Utils/MyAxios";
import AttendedEvents from "../../components/AttendedEvents/AttendedEvents";
import "../../components/AttendedEvents/AttendedEvents.css";
import Preloader from "../../components/Preloader/Preloader";


function PlayerEvents() {

    const params = useParams();

    const [events, setEvents] = useState(null);


    async function getEvents() {
        const attendedEvents = await request("GET", `/getPlayerAttendedEvents/${params.id}`, {}, applHeaders);
        setEvents(await Promise.all(attendedEvents.data.map(async event => ({
            ...event,
            logo: await getImage(event.name),
            teamSrc: await getImage(event.team)
        }))));
    }


    useEffect(() => {
        getEvents();
    }, []);


    return (
        <div>
            <div className="results_header"><p>Посещённые турниры</p></div>
            <div className="events_col">
                {events !== null && events.length > 0 ? <div className="events_col_date"><p>Место</p></div> : <></>}
                {events !== null && events.length > 0 ? <div className="events_col_event"><p>Турнир</p></div> : <></>}
                {events !== null && events.length > 0 ? <div className="events_col_team"><p>Команда</p></div> : <></>}
            </div>
            {events !== null ?
                <div className="events">
                    {events.map((ev) =>
                        <AttendedEvents event={ev} key={ev.name}/>
                    )}
                </div>
                :
                <Preloader />
            }
        </div>
    );
}

export default PlayerEvents;