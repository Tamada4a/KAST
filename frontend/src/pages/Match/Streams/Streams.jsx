import React from "react";
import { Link } from "react-router-dom";
import "./Streams.css"


function Streams(props) {
    return (
        <div className="stream">
            <div className="stream_wrapper" onClick={() => { props.checkStreamLink(props.link) }} >
                <img src={props.flagPath} alt={props.country} />
                <p>{props.name}</p>
            </div>
            <div className="stream_viewers">
                <p>{props.viewers}</p>
                <a href={props.link}></a>
            </div>
        </div>
    )

}

export default Streams;