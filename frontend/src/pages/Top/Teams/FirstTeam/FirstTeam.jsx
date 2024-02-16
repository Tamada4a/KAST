import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces } from "../../../../Utils/Utils";
import "./FirstTeam.css"
import PlayerWrapper from "../../../../components/PlayerWrapper/PlayerWrapper";
import "../../../../components/ResultMaker/ResultMaker.css"

function FirstTeam(props) {

  const color = props.changedPosition > 0 || props.changedPosition <= -999 ? "green" : props.changedPosition < 0 && props.changedPosition > -999 ? "red" : "var(--base-08)";
  const posChanged = props.changedPosition > 0 ? (`+${props.changedPosition}`) : props.changedPosition < 0 && props.changedPosition > -999 ? props.changedPosition : props.changedPosition <= -999 ? "New" : "-";

  return (
    <div>
      <div className="rectangle_50px_top" style={{ cursor: "default", pointerEvents: "none" }}>
        <div className="row_center_6">
          <div className="rectangle_50px_top_text"><p>#1</p></div>
          <div className="row_center_gap3">
            <div className="tournament_logo"><img src={props.logo} alt={props.name} /></div>
            <div className="rectangle_50px_top_text">
              <p>{props.name}</p>
            </div>
          </div>
        </div>
        <div className="rectangle_50px_top_text"><p style={{ color: color }}>{posChanged}</p></div>
      </div>
      <div className="top1_team_rectangle">
        <div className="row_left_gap20">
          {props.players.map((item, i) =>
            item.name !== "TBA" ?
              <Link to={`/player/${item.name}`} style={{ textDecoration: "none" }} key={item.name}>
                <PlayerWrapper issuer={"top"} src={item.photo} name={item.name} flagPath={item.flagPath} country={item.country} />
              </Link>
              :
              <PlayerWrapper issuer={"top"} src={item.photo} name={item.name} key={`${props.name}_TBA${i}`}/>
          )}
        </div>
        <Link to={`/team/${fillSpaces(props.name)}`}>Профиль команды</Link>
      </div>
    </div>
  )
}

export default FirstTeam;