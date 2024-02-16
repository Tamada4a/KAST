import React from "react";
import "./TeamRow.css"
import "../PlayerRow/PlayerRow.css"
import PlayerWrapper from "../../../../../components/PlayerWrapper/PlayerWrapper";

function TeamRow({ team, partType, status }) {
    function teamColor(side) {
        if (side === "CT")
            return "rgba(28, 188, 255, 0.7)";
        else
            return "rgba(244, 78, 28, 0.7)";
    }


    return (
        <div className="team_row" style={{ background: teamColor(team.side) }}>
            {partType === "team" ?
                <div className="team_row_name">
                    <img src={team.logo} style={{ width: "23px", height: "23px", marginTop: "1px" }} alt={team.name} />
                    <p>{team.name}</p>
                </div>
                :
                <PlayerWrapper issuer={"scoreBoard"} src={team.logo} name={team.name} mainStyle={null} />
            }
            <div style={{ display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center" }}>
                {status !== "ended" ?
                    <div className="weapon">
                        <img src="/img/scrollLog/accessories/Cart.svg" alt="cart" />
                    </div>
                    :
                    <></>
                }
                {status !== "ended" ?
                    <div className="states">
                        <div style={{ width: "40px", display: "flex", justifyContent: "center" }}>
                            <img src="/img/scrollLog/accessories/HP.svg" alt="hp" />
                        </div>
                        <img src="/img/scrollLog/accessories/Assaultsuit.svg" alt="assaultsuit" />
                        <div className="money"><p>$</p></div>
                    </div>
                    :
                    <></>
                }
                <div className="kda_block">
                    <div style={{ display: "flex", flexDirection: "row", gap: "4px" }}>
                        <div className="player_stats_col">
                            <p>У</p>
                        </div>
                        <div className="player_stats_col">
                            <p>П</p>
                        </div>
                        <div className="player_stats_col">
                            <p>С</p>
                        </div>
                    </div>
                    <div className="avg">
                        <p>СУР</p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default TeamRow;