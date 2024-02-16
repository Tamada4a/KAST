import React from "react";
import "./TeamBlock.css"
import TeamRow from "./TeamRow/TeamRow";
import PlayerRow from "./PlayerRow/PlayerRow";

function TeamBlock({ team, partType, status }) {
    return (
        <div className="team_block" >
            <TeamRow team={team} partType={partType} status={status} />
            {team.players.map((item) =>
                <PlayerRow props={item} team={team} status={status} key={item.nick} />
            )}

        </div>
    )
}

export default TeamBlock;