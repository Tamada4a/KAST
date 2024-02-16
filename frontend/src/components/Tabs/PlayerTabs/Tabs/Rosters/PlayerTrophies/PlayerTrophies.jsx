import React from "react";
import { Link } from "react-router-dom";
import { fillSpaces } from "../../../../../../Utils/Utils";
import "./PlayerTrophies.css"

function PlayerTrophies(props) {
    return (
        <div className="player_trophies_container">
            <div id="player_trophies">
                {props.items.trophies.map((item, i) =>
                    <Link to={`/event/${fillSpaces(item.name)}`} style={{ textDecoration: "none" }} key={`${item.name}/${i}`}>
                        <img key={item.name} src={item.src} alt={item.name} />
                    </Link>
                )}
            </div>
        </div>
    );
}
export default PlayerTrophies;