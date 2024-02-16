import React from "react";
import "./NonFirstTeam.css"
import "../../../../components/ResultMaker/ResultMaker.css"

function NonFirstTeam(props) {

  const color = props.changedPosition > 0 || props.changedPosition <= -999 ? "green" : props.changedPosition < 0 && props.changedPosition > -999 ? "red" : "var(--base-08)";
  const posChanged = props.changedPosition > 0 ? (`+${props.changedPosition}`) : props.changedPosition < 0 && props.changedPosition > -999 ? props.changedPosition : props.changedPosition <= -999 ? "New" : "-";

  return (
    <div className="rectangle_50px_top">
      <div className="row_center_6">
        <div className="rectangle_50px_top_text"><p>#{props.topPos}</p></div>
        <div className="row_center_gap3">
          <div className="tournament_logo"><img src={props.logo} alt={props.name} /></div>
          <div className="col_right_gap3">
            <div className="rectangle_50px_top_text"><p>{props.name}</p></div>
            <div className="row_center_6">
              {props.players.map((item, i) =>
                <div className="rectangle_50px_top_nick" key={`${props.name}_${item.name}${i}`}>
                  <p>{item.name}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      <div className="rectangle_50px_top_text"><p style={{ color: color }}>{posChanged}</p></div>
    </div>
  )
}

export default NonFirstTeam;