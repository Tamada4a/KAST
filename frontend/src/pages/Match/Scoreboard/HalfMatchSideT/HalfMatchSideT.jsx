import React from "react";
import './HalfMatchSideT.css'

function HalfMatchSideT({ rounds }) {
    return (
        <div className="round_history_line_sideT">
            {rounds.map(round => round)}
        </div>
    )
}

export default HalfMatchSideT;