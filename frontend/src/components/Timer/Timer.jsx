import React, { useState, useEffect } from "react";
import "./Timer.css"
function Timer(props) {
    const [counter, setCounter] = useState(null);

    useEffect(() => {
        let cnt = (props.date.getTime() - new Date()) / 1000
        cnt > 0 && setTimeout(() => setCounter(cnt - 1), 1000);
    }, [counter]);

    return (
        <div className="timer" style={props.style}>
            {counter !== null ?
                <a>{props.text}{counter < 86400 ? null : `${parseInt(counter / 86400)}д.`} {counter < 3600 ? null : `${parseInt(counter / 3600 % 24)}час.`} {counter < 60 ? null : `${parseInt(counter / 60 % 60)}мин.`} {`${parseInt(counter % 60)}сек.`}</a>
                : null
            }
        </div>
    );
}

export default Timer;