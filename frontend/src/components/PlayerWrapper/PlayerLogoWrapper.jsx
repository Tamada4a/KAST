import React from "react";

function PlayerLogoWrapper(props) {
    function getWrapperClass() {
        switch (props.issuer) {
            case "participant":
                return "participant_wrapper";
            case "matchHeader":
                return "match_header_team";
        }
    }


    function getLogoClass() {
        switch (props.issuer) {
            case "participant":
                return "participant_player_logo";
            case "matchHeader":
                return "match_header_player_logo";
        }
    }


    function getPlayerWrapper() {
        switch (props.issuer) {
            case "participant":
                return "participant_player";
            case "matchHeader":
                return "match_header_player";
        }
    }


    function getPlayerCrop() {
        switch (props.issuer) {
            case "participant":
                return "crop_participant_player";
            case "matchHeader":
                return "match_header_player_crop";
        }
    }


    return (
        <div className={getWrapperClass()}>
            {props.team !== "" ?
                <div className={getLogoClass()}>
                    <img src={props.teamSrc} alt={props.team} />
                </div>
                :
                <></>
            }
            <div className={getPlayerWrapper()}>
                <div className={getPlayerCrop()}>
                    <img src={props.playerSrc} alt={props.player} />
                </div>
            </div>
            <p>{props.player}</p>
        </div >
    );
}

export default PlayerLogoWrapper;