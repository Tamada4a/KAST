import React, { useState, useEffect } from "react";
import { Routes, Route, NavLink, Navigate } from 'react-router-dom'
import EndedEvents from "./EndedEvents/EndedEvents";
import OngoingEvents from "./OngoingEvents/OngoingEvents";
import './Tournaments.css'
import { applHeaders, request } from "../../Utils/MyAxios";
import { getImage, getStoredPlayerNick } from "../../Utils/Utils";


function Tournaments() {
    const [isAdmin, setIsAdmin] = useState(false);

    const [ongoingEvents, setOngoingEvents] = useState(null)

    const [featuredEvents, setFeaturedEvents] = useState(null);

    const [endedEvents, setEndedEvents] = useState(null)

    const [countries, setCountries] = useState(null);

    const [eventMapPool, setEventMapPool] = useState(null)


    async function getOngoingEvents(events) {
        setOngoingEvents(await Promise.all(events.map(async event => ({
            ...event,
            headerSrc: await getImage(event.event, "/header"),
            eventSrc: await getImage(event.event),
            teams: await Promise.all(event.teams.map(async team => ({
                ...team,
                src: await getImage(team.name)
            })))
        }))));
    }


    async function getFeaturedEvents(events) {
        setFeaturedEvents(await Promise.all(events.map(async day => ({
            ...day,
            events: await Promise.all(day.events.map(async event => ({
                ...event,
                headerSrc: await getImage(event.event, "/header"),
                eventSrc: await getImage(event.event)
            })))
        }))));
    }


    async function getEndedEvents(events) {
        setEndedEvents(await Promise.all(events.map(async day => ({
            ...day,
            events: await Promise.all(day.events.map(async event => ({
                ...event,
                src: await getImage(event.event)
            })))
        }))));
    }


    async function setFullEvents() {
        let events = await request("GET", `/getFullTournaments/${getStoredPlayerNick()}`, {}, applHeaders);

        setIsAdmin(events.data.isAdmin);

        setCountries(events.data.countries);

        setEventMapPool(events.data.mapPool);

        getOngoingEvents(events.data.ongoingEvents)

        getFeaturedEvents(events.data.featuredEvents);

        getEndedEvents(events.data.endedEvents);
    }


    useEffect(() => {
        setFullEvents();
    }, []);


    return (
        <div>
            <ul className="tab_wrapper">
                <li className="tab_button" key="Tournaments Текущие и будущие">
                    <NavLink to="ongoing" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Текущие и будущие
                    </NavLink>
                </li>
                <li className="tab_link" key="Tournaments Прошедшие">
                    <NavLink to="ended" style={({ isActive }) => ({  // если активна, то текст белый
                        color: isActive ? 'var(--text-01)' : 'var(--text-02)'
                    })}>
                        Прошедшие
                    </NavLink>
                </li>
            </ul>
            <Routes>
                <Route index element={<Navigate replace to="/tournaments/ongoing" />} />
                <Route path="ongoing" element={<OngoingEvents ongoing={ongoingEvents} featured={featuredEvents} isAdmin={isAdmin} setOngoingEvents={setOngoingEvents} setFeaturedEvents={setFeaturedEvents} countries={countries} eventMapPool={eventMapPool} />} />
                <Route path="ended" element={<EndedEvents ended={endedEvents} />} />
            </Routes>
        </div>
    );
}

export default Tournaments;