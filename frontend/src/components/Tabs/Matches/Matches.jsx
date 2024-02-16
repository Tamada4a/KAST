import React from "react";
import Match from "../Match/Match";
import { NavLink } from 'react-router-dom';
import "./Matches.css"

function Matches(props) {
    const buttonText = props.type === "team" ? "Все результаты команды" : "Все результаты игрока";
    const link = props.type === "team" ? (`/team-results/${props.param}`) : (`/player-results/${props.param}`);


    function isUpcoming() {
        if (props.matches_upcoming !== null && props.matches_upcoming.length > 0) {
            return (
                <div>
                    <div className="time_header"><p>Ближайшие матчи</p></div>
                    <div className="match_col">
                        <p>Дата</p>
                        <p>Матч</p>
                    </div>
                    <div className="tournaments">
                        {props.matches_upcoming.map((ev) => <Match event={ev} key={ev.event} />)}
                    </div>
                </div>
            );
        }
    }


    function isPast() {
        if (props.matches_ended !== null && props.matches_ended.length > 0) {
            const ended = [];
            if (props.matches_ended.length === 1) {
                ended.push(props.matches_ended[0]);
            } else if (props.matches_ended.length > 1) {
                ended.push(props.matches_ended[0]);
                ended.push(props.matches_ended[1]);
            }
            return (
                <div>
                    <div className="time_header"><p>Прошедшие матчи</p></div>
                    <div className="match_col">
                        <p>Дата</p>
                        <p>Матч</p>
                    </div>
                    <div className="tournaments">
                        {ended.map((ev) => <Match event={ev} key={ev.event} />)}
                    </div>
                </div>
            );
        }
    }


    return (
        <div className="tab_matches">
            {isUpcoming()}

            {isPast()}

            <div className="full_grey_button_gap15">
                <NavLink to={link} target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none" }}>
                    <input type="submit" id="loginsubmit" value={buttonText} />
                </NavLink>
            </div>
        </div>
    );
}

export default Matches;