import React from "react";
import './HalfMatchSideCT.css'

function HalfMatchSideCT({ rounds }) {
    return (
        <div className="round_history_line_sideCT">
            {rounds.map(round => round)}
        </div>
    )
}

export default HalfMatchSideCT;