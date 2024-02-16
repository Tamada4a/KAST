import React from "react";
import "./FlagName.css"

export default function FlagName(props) {
    return (
        <div className={props.isWrap ? "flag_name text_wrapper" : "flag_name"} style={{ width: props.isWrap ? props.width : null }}>
            <img src={props.flagPath} alt={props.country} style={{ height: props.height }} />
            <p>{props.name}</p>
        </div>
    );
}