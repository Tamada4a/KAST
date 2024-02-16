import React from "react";
import "./PrizePlace.css"
import { getPrizePlaceEnding } from "../../Utils/Utils";

function PrizePlace(props) {
    function getParticipantDraw(prizePlace) {
        if (prizePlace.teamSrc === undefined)
            return (<div className="prize_logo"><img src={prizePlace.logo} alt={prizePlace.teamName} /></div>);

        return (
            <div style={{ position: "absolute", top: "17%", opacity: "0.26" }}>
                {prizePlace.team === "" ? <></> :
                    <div className="prize_logo" style={{ width: "69px", height: "69px", left: "0", top: "0" }}><img src={prizePlace.teamSrc} alt={prizePlace.team} style={{ opacity: "0.5" }} /></div>
                }
                <div className="prize_player">
                    <div className="crop_prize_player"><img src={prizePlace.logo} alt={prizePlace.teamName} /></div>
                </div>
            </div>
        )
    }


    function drawPrizePlace(prizePlace) {
        if (prizePlace.place === "")
            return (<></>);

        return (
            <div className="prize_place" key={prizePlace.place}>
                {prizePlace.teamName === "" || prizePlace.teamName === null ? <></> :
                    getParticipantDraw(prizePlace)
                }
                <div className="prize_wrapper">
                    <p>{getPrizePlaceEnding(prizePlace.place)}</p>
                    <span>{prizePlace.reward}</span>
                </div>
            </div>
        );
    }

    return (
        <div className="item">
            {props.prize.map((prizePlace) =>
                drawPrizePlace(prizePlace)
            )}
        </div>
    );
}

export default PrizePlace;