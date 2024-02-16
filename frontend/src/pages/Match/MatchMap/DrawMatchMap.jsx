import React from "react";
import Map from "../../../components/Map/Map";

function DrawMatchMap(props) {
    return (
        <div style={{ height: "30px", objectPosition: "center", position: "relative" }}>
            <Map map={props.mapName} maps={props.eventMapPool} />
            <div style={{ position: "absolute", top: "50%", left: "50%", transform: "translate(-50%, -50%)" }}>
                <p style={{ margin: "0", fontFamily: "var(--text-regular-lcg)" }}>{props.mapName}</p>
            </div>
        </div>
    );
}

export default DrawMatchMap;