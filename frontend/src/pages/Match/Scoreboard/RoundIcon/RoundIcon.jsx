import React from "react";
import './RoundIcon.css'

function RoundIcon({ props }) {
    function getClassName() {
        if (props === undefined)
            return "roundIcon";
        if (props.winner === "T" && props.how !== "BombExploded")
            return "roundIcon t";
        if (props.winner === "CT" && props.how !== "Timer")
            return "roundIcon ct";
        return "roundIcon";
    }


    function resRound() {
        return `/img/scoreboard/${props.how}.svg`;
    }


    return (
        <div className={getClassName()}>
            <img src={props != null ? resRound() : null} alt={props ? props.how : null} />
        </div>
    )
}

export default RoundIcon;