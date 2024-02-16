import React, { useState } from "react";
import "./PlayerRow.css";
import "../TeamRow/TeamRow.css"

function PlayerRow({ props, team, status }) {
    const [mouseOutCard, setMouseOutCard] = useState(true); //Для ховера игрока
    const [mouseOnCard, setMouseOnCard] = useState(false); //Для ховера игрока

    function teamColor(side) {
        if (side === "CT")
            return "rgba(28, 188, 255, 0.4)";
        else return "rgba(244, 78, 28, 0.4)";
    }


    function weaponImg(weapon) {
        return `/img/scrollLog/weapons/${weapon}.svg`;
    }


    function armorImg(armor) {
        let strArmor;
        switch (armor) {
            case 1:
                strArmor = "Kevlar"; break;
            case 2:
                strArmor = "Assaultsuit"; break;
        }
        return `/img/scrollLog/accessories/${strArmor}.svg`;
    }


    function hp_bar_color(hp) {
        if (hp <= 100 && hp >= 40) return "var(--base-05)";
        else {
            if (hp <= 39 && hp >= 21) return "var(--hp-medium)";
            else {
                if (hp <= 20 && hp >= 1) return "var(--base-12)";
            }
        }
    }


    function hp_bar_width(hp) {
        return hp * 40 / 100;
    }


    return (
        <div className="player_row" style={{ background: teamColor(team.side), opacity: props.hp === 0 && mouseOutCard && status !== "ended" ? "0.4" : "1" }} onMouseOut={() => { setMouseOutCard(true); setMouseOnCard(false) }} onMouseOver={() => { setMouseOutCard(false); setMouseOnCard(true) }}>
            <div className="player_row_nick"><p>{props.nick}</p></div>
            <div style={{ display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center" }}>
                {props.hp !== 0 && status !== "ended" ?
                    <img className="defuse_kit" src={(props.defuseKit && team.side === "CT") ? "/img/scoreboard/BombDefused.svg" : null} />
                    :
                    <></>
                }
                <div className="weapon">
                    {props.hp !== 0 && props.weapon !== "" && status !== "ended" ?
                        <img src={weaponImg(props.weapon)} />
                        :
                        <></>
                    }
                </div>

                {status !== "ended" ?
                    <div className="states">
                        <div className="HP_block">
                            <div className="HP" style={{ width: `${hp_bar_width(props.hp)}px`, background: hp_bar_color(props.hp) }}></div>
                            <p>{props.hp}</p>
                        </div>
                        <div className="armor">
                            {(props.hp === 0 || props.armor === 0) ? null
                                :
                                <img src={armorImg(props.armor)} />
                            }
                        </div>
                        <div className="money">
                            <p style={{ fontSize: "13px" }}>${props.money}</p>
                        </div>
                    </div>
                    :
                    <></>
                }
                <div className="kda_block">
                    <div style={{ display: "flex", flexDirection: "row", gap: "4px" }}>
                        <div className="player_stats_col">
                            <p style={{ fontSize: "13px" }}>{props.kills}</p>
                        </div>
                        <div className="player_stats_col">
                            <p style={{ fontSize: "13px" }}>{props.assists}</p>
                        </div>
                        <div className="player_stats_col">
                            <p style={{ fontSize: "13px" }}>{props.deaths}</p>
                        </div>
                    </div>
                    <div className="player_avg">
                        <p style={{ fontSize: "13px" }}>{props.avg}</p>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default PlayerRow;