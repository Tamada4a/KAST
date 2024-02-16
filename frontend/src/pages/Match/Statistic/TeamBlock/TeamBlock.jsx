import React from "react";
import './TeamBlock.css'
import PlayerRow from "./PlayerRow/PlayerRow";
import TeamRow from "./TeamRow/TeamRow";

function TeamBlock({ team, players, partType }) {
    return (
        <div className="statistic_team_block">
            <TeamRow team={team} partType={partType} />
            {players.map((item) =>
                <PlayerRow props={item} partType={partType} key={item.nick} />
            )}
        </div>
    )
}

export default TeamBlock;