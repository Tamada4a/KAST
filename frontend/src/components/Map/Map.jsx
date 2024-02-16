import React from "react";
import "./Map.css"

function Map(props) {
    function isFound(inputMap) {
        return props.maps.some(map => map === inputMap);
    }


    return (
        <img src={`../../img/maps/map_preview/${(isFound(props.map) ? props.map : "TBA")}.png`} alt={props.map} />
    );
}

export default Map;