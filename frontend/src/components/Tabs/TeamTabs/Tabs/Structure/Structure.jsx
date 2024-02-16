import React from "react";
import { Link } from "react-router-dom";
import PlayerWrapper from "../../../../PlayerWrapper/PlayerWrapper";
import "./Structure.css";
import '../../../../../pages/Top/Teams/FirstTeam/Player/Player.css';
import '../../../Events/Events.css'

function Structure(props) {
    return (
        <div className="tabcontent_tournaments">
            {props.roster !== null && props.roster.length > 0 ? <div className="head_header"><p>Текущий состав</p></div> : <></>}
            {props.roster !== null && props.roster.length > 0 ?
                <div className="players_list">
                    {props.roster.map((player) =>
                        <Link to={`/player/${player.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" key={`current_${player.name}`}>
                            <PlayerWrapper issuer={"structure"} src={player.photo} name={player.name} flagPath={player.flagPath} country={player.country} />
                        </Link>
                    )}
                </div>
                :
                <></>
            }

            {props.ex_players !== null && props.ex_players.length > 0 ? <div className="ex_header" style={props.roster === null || props.roster.length === 0 ? { paddingTop: "0px" } : null}><p>Бывшие игроки</p></div> : <></>}
            {props.ex_players !== null && props.ex_players.length > 0 ?
                <div className="ex_players_list">
                    <div className="ex_players">
                        {props.ex_players.map((ex) =>
                            <Link to={`/player/${ex.name}`} style={{ textDecoration: "none" }} target="_blank" rel="noopener noreferrer" key={`ex_${ex.name}`}>
                                <PlayerWrapper issuer={"structure"} src={ex.photo} name={ex.name} flagPath={ex.flagPath} country={ex.country} />
                            </Link>
                        )}
                    </div>
                </div>
                :
                <></>
            }
        </div>
    );
}

export default Structure;