import React from "react";
import { Link } from "react-router-dom";
import PlayerWrapper from "../../../components/PlayerWrapper/PlayerWrapper";

function TeamRosters(props) {
    return (
        <div>
            <div className="statistic_team_row" style={{ paddingRight: "5px", width: "638px" }}>
                <Link to={`/team/${props.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                    <div className="statistic_team_row_name">
                        <img src={props.logo} style={{ width: "23px", height: "23px", marginTop: "1px" }}></img>
                        <p>{props.name}</p>
                    </div>
                </Link>
                {props.topPos > 0 ?
                    <Link to={"/top"} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                        <p>{`Позиция в топе: ${props.topPos}`}</p>
                    </Link>
                    :
                    <Link to={"/top"} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer">
                        <p>{"Позиция в топе: -"}</p>
                    </Link>
                }
            </div>
            <div className="match_roster_rect">
                {props.players.map((player, i) =>
                    player.name !== "TBA" ?
                        <Link to={`/player/${player.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" key={player.name}>
                            <PlayerWrapper issuer={"match"} src={player.src} name={player.name} flagPath={player.flagPath} country={player.country} />
                        </Link>
                        :
                        <PlayerWrapper issuer={"match"} src={"../../img/players/NonPhoto.png"} name={"TBA"} key={`${props.name}_TBA${i}`} />
                )
                }
            </div>
        </div>
    );
}

export default TeamRosters;