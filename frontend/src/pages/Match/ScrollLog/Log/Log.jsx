import React from "react";
import "./Log.css"

function Log({ type, children }) {

    function colorBorder(event) {
        switch (event) {
            case "roundBegin": case "logout": case "roundBegin": return "1px solid #FFFFFF";
            case "login": return "1px solid #00A621";
            case "kill":  case "bombDeath": return "1px solid #EC0101";
            case "t_win": case "bomb_planted": case "t_suicide": return "1px solid #F44E1C";
            case "ct_win": case "defuse": case "ct_suicide": return "1px solid #1CBCFF";
        }
    }
    return (
        <div className="log" style={{
            border: colorBorder(type),
            opacity: (type === "login") || (type === "logout") ? "0.5" : "1",
            marginBottom: type === "roundBegin" ? "25px" : null,
        }}>
            {children}
        </div>
    )
}

export default Log;